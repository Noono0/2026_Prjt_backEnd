package com.noonoo.prjtbackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Next BFF가 OAuth 동기화 후 Spring 세션을 맞출 때 사용 */
@Getter
@Setter
public class OauthEstablishSessionRequest {

    @NotNull
    private Long memberSeq;

    @NotBlank
    private String memberId;
}
