package com.noonoo.prjtbackend.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 필드 단위 에러 응답 객체
 *
 * <p>예) field = memberId code = DUPLICATE message = 이미 사용 중인 회원 아이디입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorResponse {

    /** 문제 발생 필드명 프론트 input name 과 맞추면 매핑하기 좋음 */
    private String field;

    /** 필드 에러 코드 예) REQUIRED, DUPLICATE, INVALID_VALUE */
    private String code;

    /** 사용자에게 보여줄 메시지 */
    private String message;
}
