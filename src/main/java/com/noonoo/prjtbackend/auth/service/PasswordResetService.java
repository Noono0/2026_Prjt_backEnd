package com.noonoo.prjtbackend.auth.service;

import com.noonoo.prjtbackend.auth.dto.PasswordResetCompleteDto;
import com.noonoo.prjtbackend.auth.dto.PasswordResetRequestDto;
import com.noonoo.prjtbackend.auth.dto.PasswordResetVerifyDto;
import com.noonoo.prjtbackend.auth.dto.PasswordResetVerifyResponseDto;
import com.noonoo.prjtbackend.auth.mail.AppMailService;
import com.noonoo.prjtbackend.auth.mail.MailFallbackNotifier;
import com.noonoo.prjtbackend.common.util.IpUtil;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int CODE_TTL_MINUTES = 10;
    private static final int RESET_TOKEN_TTL_MINUTES = 15;
    private static final int REQUEST_COOLDOWN_SECONDS = 60;
    private static final int MIN_NEW_PASSWORD_LEN = 8;

    private final MemberMapper memberMapper;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<AppMailService> appMailService;
    private final MailFallbackNotifier mailFallbackNotifier;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void requestCode(PasswordResetRequestDto dto, HttpServletRequest httpRequest) {
        String memberId = trim(dto == null ? null : dto.getMemberId());
        String email = trim(dto == null ? null : dto.getEmail());
        if (!StringUtils.hasText(memberId) || !StringUtils.hasText(email)) {
            throw new IllegalArgumentException("아이디와 이메일을 입력해 주세요.");
        }

        MemberDto member = memberMapper.findMemberByMemberIdAndEmail(memberId, email);
        String clientIp = IpUtil.getClientIp(httpRequest);

        if (member == null || member.getMemberSeq() == null) {
            // 계정 존재 여부 노출 방지 — 동일 안내
            log.info("password reset request: no member match memberId={} (masked email)", memberId);
            return;
        }

        Long memberSeq = member.getMemberSeq();
        if (cooldownActive(memberSeq)) {
            throw new IllegalArgumentException("잠시 후 다시 요청해 주세요.");
        }

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        String codeHash = passwordEncoder.encode(code);
        LocalDateTime codeExp = LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES);

        jdbcTemplate.update(
                """
                        INSERT INTO password_reset_request (
                            member_seq, code_hash, code_expires_at, create_ip, create_dt
                        ) VALUES (?, ?, ?, ?, NOW())
                        """,
                memberSeq,
                codeHash,
                Timestamp.valueOf(codeExp),
                truncate(clientIp, 100)
        );

        String to = member.getEmail();
        if (!StringUtils.hasText(to)) {
            log.warn("password reset: member has empty email memberSeq={}", memberSeq);
            throw new IllegalArgumentException("등록된 이메일이 없습니다.");
        }

        String greeting =
                StringUtils.hasText(member.getNickname()) ? member.getNickname().trim() : memberId;
        sendCodeEmail(to, memberId, greeting, code);

        log.info("password reset code issued memberSeq={}", memberSeq);
    }

    private boolean cooldownActive(Long memberSeq) {
        Long cnt = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM password_reset_request
                        WHERE member_seq = ?
                          AND create_dt >= DATE_SUB(NOW(), INTERVAL ? SECOND)
                        """,
                Long.class,
                memberSeq,
                REQUEST_COOLDOWN_SECONDS
        );
        return cnt != null && cnt > 0;
    }

    private void sendCodeEmail(String to, String memberId, String greetingName, String code) {
        String subject = "[PRJT] 비밀번호 재설정 인증코드";
        String displayName = StringUtils.hasText(greetingName) ? greetingName : memberId;
        String html = """
                <div style="font-family:sans-serif;line-height:1.6;color:#111;">
                  <p>안녕하세요, %s 님.</p>
                  <p>비밀번호 재설정 인증코드입니다.</p>
                  <p style="font-size:1.5rem;font-weight:bold;letter-spacing:0.2em;">%s</p>
                  <p>코드 유효 시간: %d분</p>
                  <p>본인이 요청하지 않았다면 이 메일을 무시해 주세요.</p>
                </div>
                """.formatted(escapeHtml(displayName), escapeHtml(code), CODE_TTL_MINUTES);

        AppMailService mail = appMailService.getIfAvailable();
        if (mail != null) {
            try {
                mail.sendHtml(to, subject, html);
            } catch (Exception e) {
                log.error("password reset mail send failed: {}", e.toString());
                throw new IllegalArgumentException("인증 메일 발송에 실패했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } else {
            mailFallbackNotifier.notifyPasswordResetCode(to, memberId, code);
        }
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    @Transactional
    public PasswordResetVerifyResponseDto verifyCode(PasswordResetVerifyDto dto) {
        String memberId = trim(dto == null ? null : dto.getMemberId());
        String email = trim(dto == null ? null : dto.getEmail());
        String code = trim(dto == null ? null : dto.getCode());
        if (!StringUtils.hasText(memberId) || !StringUtils.hasText(email) || !StringUtils.hasText(code)) {
            throw new IllegalArgumentException("아이디, 이메일, 인증코드를 입력해 주세요.");
        }
        if (code.length() != 6 || !code.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("인증코드는 6자리 숫자입니다.");
        }

        MemberDto member = memberMapper.findMemberByMemberIdAndEmail(memberId, email);
        if (member == null || member.getMemberSeq() == null) {
            throw new IllegalArgumentException("인증코드가 올바르지 않거나 만료되었습니다.");
        }

        Long rowRequestSeq = jdbcTemplate.query(
                """
                        SELECT request_seq, code_hash, code_expires_at, reset_token, used_at
                        FROM password_reset_request
                        WHERE member_seq = ?
                        ORDER BY request_seq DESC
                        LIMIT 1
                        """,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    if (rs.getTimestamp("used_at") != null) {
                        return null;
                    }
                    if (rs.getString("reset_token") != null) {
                        return null;
                    }
                    if (rs.getTimestamp("code_expires_at") == null
                            || rs.getTimestamp("code_expires_at").before(Timestamp.valueOf(LocalDateTime.now()))) {
                        return null;
                    }
                    String hash = rs.getString("code_hash");
                    if (!passwordEncoder.matches(code, hash)) {
                        return null;
                    }
                    return rs.getLong("request_seq");
                },
                member.getMemberSeq()
        );

        if (rowRequestSeq == null) {
            throw new IllegalArgumentException("인증코드가 올바르지 않거나 만료되었습니다.");
        }

        String resetToken = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime tokenExp = LocalDateTime.now().plusMinutes(RESET_TOKEN_TTL_MINUTES);

        int updated = jdbcTemplate.update(
                """
                        UPDATE password_reset_request
                        SET reset_token = ?, reset_token_expires_at = ?
                        WHERE request_seq = ?
                          AND used_at IS NULL
                          AND reset_token IS NULL
                        """,
                resetToken,
                Timestamp.valueOf(tokenExp),
                rowRequestSeq
        );
        if (updated != 1) {
            throw new IllegalArgumentException("인증 처리에 실패했습니다. 처음부터 다시 시도해 주세요.");
        }

        return PasswordResetVerifyResponseDto.builder().resetToken(resetToken).build();
    }

    @Transactional
    public void completeReset(PasswordResetCompleteDto dto, HttpServletRequest httpRequest) {
        String token = trim(dto == null ? null : dto.getResetToken());
        String rawPassword = dto == null ? null : dto.getNewPassword();
        if (!StringUtils.hasText(token) || !StringUtils.hasText(rawPassword)) {
            throw new IllegalArgumentException("재설정 토큰과 새 비밀번호를 입력해 주세요.");
        }
        String newPwd = Objects.requireNonNull(rawPassword).trim();
        if (newPwd.length() < MIN_NEW_PASSWORD_LEN) {
            throw new IllegalArgumentException("새 비밀번호는 %d자 이상이어야 합니다.".formatted(MIN_NEW_PASSWORD_LEN));
        }

        String clientIp = IpUtil.getClientIp(httpRequest);

        Row row = jdbcTemplate.query(
                """
                        SELECT request_seq, member_seq
                        FROM password_reset_request
                        WHERE reset_token = ?
                          AND used_at IS NULL
                          AND reset_token_expires_at > NOW()
                        LIMIT 1
                        """,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    return new Row(rs.getLong("request_seq"), rs.getLong("member_seq"));
                },
                token
        );

        if (row == null) {
            throw new IllegalArgumentException("재설정 링크가 만료되었거나 이미 사용되었습니다. 처음부터 다시 시도해 주세요.");
        }

        String encoded = passwordEncoder.encode(newPwd);
        int n = memberMapper.updateMemberPassword(row.memberSeq, encoded, "PASSWORD_RESET", clientIp);
        if (n < 1) {
            throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
        }

        jdbcTemplate.update(
                """
                        UPDATE password_reset_request
                        SET used_at = NOW()
                        WHERE request_seq = ?
                        """,
                row.requestSeq
        );
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String truncate(String s, int max) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.trim();
        return t.length() > max ? t.substring(0, max) : t;
    }

    private record Row(long requestSeq, long memberSeq) {}
}
