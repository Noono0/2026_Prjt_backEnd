package com.noonoo.prjtbackend.auth.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(JavaMailSender.class)
public class AppMailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@localhost}")
    private String fromAddress;

    public void sendHtml(String to, String subject, String htmlBody) throws Exception {
        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("수신 이메일이 비어 있습니다.");
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }
}
