package com.noonoo.prjtbackend.common.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    /** {@code false} 로 로컬 부하 테스트 등에서 끌 수 있습니다. */
    private boolean enabled = true;

    /** 1분당 IP당 로그인 시도 */
    private int loginPerMinute = 30;

    /** 1시간당 IP당 비밀번호 재설정 요청(메일 발송) */
    private int passwordResetRequestPerHour = 8;

    /** 1분당 IP당 인증코드 검증 */
    private int passwordResetVerifyPerMinute = 20;

    /** 1분당 IP당 비밀번호 변경 완료 */
    private int passwordResetCompletePerMinute = 10;

    /** 1분당 IP당 OAuth 회원 동기화 */
    private int oauthSyncPerMinute = 40;

    /** 1분당 IP당 OAuth 세션 수립 */
    private int oauthEstablishPerMinute = 40;

    /** 1분당 IP당 방문 하트비트 */
    private int heartbeatPerMinute = 180;
}
