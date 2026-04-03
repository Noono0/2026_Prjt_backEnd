package com.noonoo.prjtbackend.auth.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JavaMailSender 가 없을 때(로컬 SMTP 미설정) 개발 편의용 로그 또는 운영 시 안내.
 */
@Slf4j
@Component
public class MailFallbackNotifier {

    @Value("${app.mail.log-code-in-dev:false}")
    private boolean logCodeInDev;

    public void notifyPasswordResetCode(String to, String memberId, String code) {
        if (logCodeInDev) {
            log.warn("[DEV] SMTP 미설정 — 비밀번호 재설정 인증코드 | to={} memberId={} code={}", maskEmail(to), memberId, code);
            return;
        }
        throw new IllegalArgumentException("이메일 발송(SMTP)이 설정되지 않았습니다. application.yml 의 spring.mail.* 를 구성하거나 app.mail.log-code-in-dev=true 로 로컬 테스트 해 주세요.");
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] p = email.split("@", 2);
        String local = p[0];
        String dom = p[1];
        String masked = local.length() <= 2 ? "**" : local.charAt(0) + "***" + local.charAt(local.length() - 1);
        return masked + "@" + dom;
    }
}
