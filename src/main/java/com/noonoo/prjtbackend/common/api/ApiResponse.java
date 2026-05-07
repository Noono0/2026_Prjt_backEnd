package com.noonoo.prjtbackend.common.api;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 API 응답 객체
 *
 * @param <T> 실제 응답 데이터 타입
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** 요청 성공 여부 true : 정상 처리 false : 실패 */
    private boolean success;

    /** 업무/시스템 코드 예) SUCCESS, VALIDATION_ERROR, DUPLICATE_MEMBER_ID */
    private String code;

    /** 사용자/프론트에게 보여줄 메시지 */
    private String message;

    /** 실제 응답 데이터 */
    private T data;

    /** 필드 단위 에러 목록 지금 당장은 안 써도 나중 확장 위해 미리 둠 */
    private List<FieldErrorResponse> errors;

    /** 성공 응답 - 기본 메시지 */
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message("정상 처리되었습니다.")
                .data(data)
                .errors(Collections.emptyList())
                .build();
    }

    /** 성공 응답 - 메시지 직접 지정 */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .errors(Collections.emptyList())
                .build();
    }

    /** 실패 응답 - 단순 실패 */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .errors(Collections.emptyList())
                .build();
    }

    /** 실패 응답 - 필드 에러 포함 */
    public static <T> ApiResponse<T> fail(
            String code, String message, List<FieldErrorResponse> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .errors(errors)
                .build();
    }
}
