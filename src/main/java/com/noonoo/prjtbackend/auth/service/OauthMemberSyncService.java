package com.noonoo.prjtbackend.auth.service;

import com.noonoo.prjtbackend.auth.dto.OauthMemberSyncRequest;
import com.noonoo.prjtbackend.auth.dto.OauthMemberSyncResponse;
import com.noonoo.prjtbackend.auth.oauth.MemberOAuthProvider;
import com.noonoo.prjtbackend.file.mapper.AttachFileMapper;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.mapper.MemberRoleMapper;
import com.noonoo.prjtbackend.member.service.WalletPointGrantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthMemberSyncService {

    private static final String SYNC_ACTOR = "OAUTH_SYNC";

    private final MemberMapper memberMapper;
    private final MemberRoleMapper memberRoleMapper;
    private final AttachFileMapper attachFileMapper;
    private final PasswordEncoder passwordEncoder;
    private final WalletPointGrantService walletPointGrantService;

    @Transactional
    public OauthMemberSyncResponse sync(OauthMemberSyncRequest req, HttpServletRequest request) {
        MemberOAuthProvider provider = MemberOAuthProvider.fromApiCode(req.getProvider());
        String subject = req.getSubject().trim();
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("subject 가 비어 있습니다.");
        }

        String clientIp = resolveClientIp(request);
        MemberDto existing = memberMapper.findMemberByOauth(provider.name(), subject);
        if (existing != null) {
            MemberSaveRequest patch = new MemberSaveRequest();
            patch.setMemberSeq(existing.getMemberSeq());
            patch.setModifyId(SYNC_ACTOR);
            patch.setModifyIp(clientIp);
            if (StringUtils.hasText(req.getEmail())) {
                patch.setEmail(req.getEmail().trim());
            }
            if (StringUtils.hasText(req.getMemberName())) {
                patch.setMemberName(req.getMemberName().trim());
            }
            if (StringUtils.hasText(req.getNickname())) {
                patch.setNickname(req.getNickname().trim());
            }
            if (StringUtils.hasText(req.getProfileImageUrl())) {
                patch.setProfileImageUrl(req.getProfileImageUrl().trim());
            }
            prepareProfileImage(patch);
            memberMapper.updateMemberOauthLoginProfile(patch);
            return OauthMemberSyncResponse.builder()
                    .memberSeq(existing.getMemberSeq())
                    .memberId(existing.getMemberId())
                    .oauthProvider(provider.name())
                    .newlyRegistered(false)
                    .build();
        }

        MemberSaveRequest insert = new MemberSaveRequest();
        insert.setMemberId(resolveUniqueMemberId(provider, subject));
        insert.setMemberName(resolveMemberName(req));
        insert.setNickname(resolveNickname(req, insert.getMemberName()));
        if (StringUtils.hasText(req.getEmail())) {
            insert.setEmail(req.getEmail().trim());
        }
        if (StringUtils.hasText(req.getProfileImageUrl())) {
            insert.setProfileImageUrl(req.getProfileImageUrl().trim());
        }
        insert.setOauthProvider(provider.name());
        insert.setOauthSubject(subject);
        insert.setGradeCode("NORMAL");
        insert.setStatusCode("ACTIVE");
        insert.setCreateId(SYNC_ACTOR);
        insert.setModifyId(SYNC_ACTOR);
        insert.setCreateIp(clientIp);
        insert.setModifyIp(clientIp);
        insert.setMemberPwd(passwordEncoder.encode(UUID.randomUUID().toString()));

        prepareProfileImage(insert);
        int n = memberMapper.insertMember(insert);
        if (n <= 0) {
            throw new IllegalStateException("회원 INSERT 에 실패했습니다.");
        }
        Long seq = insert.getMemberSeq();
        if (seq == null) {
            throw new IllegalStateException("회원 INSERT 후 member_seq 가 없습니다.");
        }
        memberRoleMapper.insertMemberRole(seq, "USER", SYNC_ACTOR, clientIp);
        try {
            walletPointGrantService.grantSignup(seq);
        } catch (Exception e) {
            log.warn("OAuth 회원가입 포인트 지급 실패 memberSeq={}: {}", seq, e.toString());
        }

        return OauthMemberSyncResponse.builder()
                .memberSeq(seq)
                .memberId(insert.getMemberId())
                .oauthProvider(provider.name())
                .newlyRegistered(true)
                .build();
    }

    private static String resolveMemberName(OauthMemberSyncRequest req) {
        if (StringUtils.hasText(req.getMemberName())) {
            return req.getMemberName().trim();
        }
        if (StringUtils.hasText(req.getEmail())) {
            String e = req.getEmail().trim();
            int at = e.indexOf('@');
            if (at > 0) {
                return e.substring(0, at);
            }
            return e;
        }
        return "회원";
    }

    private static String resolveNickname(OauthMemberSyncRequest req, String memberName) {
        if (StringUtils.hasText(req.getNickname())) {
            return req.getNickname().trim();
        }
        return memberName;
    }

    /**
     * 로그인·화면용 {@code member_id}. OAuth subject 는 {@code oauth_subject} 에만 저장하고,
     * 여기서는 제공자 접두 + 무작위 문자열로 짧게 발급해 외부 식별자 노출을 피한다.
     */
    private String resolveUniqueMemberId(MemberOAuthProvider provider, String subject) {
        String prefix = provider.getMemberIdPrefix();
        for (int i = 0; i < 12; i++) {
            String candidate = prefix + UUID.randomUUID().toString().replace("-", "");
            if (memberMapper.findLoginMember(candidate) == null) {
                return candidate;
            }
        }
        return prefix + sha256Prefix24(provider.name() + ":" + subject + ":" + System.nanoTime());
    }

    private static String sha256Prefix24(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d).substring(0, 24);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            String first = xff.split(",")[0].trim();
            if (StringUtils.hasText(first)) {
                return first;
            }
        }
        String ra = request.getRemoteAddr();
        return StringUtils.hasText(ra) ? ra : "127.0.0.1";
    }

    /**
     * {@link com.noonoo.prjtbackend.member.serviceImpl.MemberServiceImpl#prepareProfileImage} 와 동일 정책.
     */
    private void prepareProfileImage(MemberSaveRequest req) {
        Long fileSeq = req.getProfileImageFileSeq();
        if (fileSeq != null) {
            if (attachFileMapper.selectByFileSeq(fileSeq) == null) {
                throw new IllegalArgumentException("프로필 이미지 파일을 찾을 수 없습니다.");
            }
            req.setProfileImageUrl(null);
            return;
        }
        String url = req.getProfileImageUrl();
        if (url != null && url.startsWith("data:")) {
            throw new IllegalArgumentException("프로필 이미지는 base64로 저장할 수 없습니다.");
        }
    }
}