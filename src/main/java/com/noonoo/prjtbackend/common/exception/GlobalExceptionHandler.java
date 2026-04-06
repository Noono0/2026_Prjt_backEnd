package com.noonoo.prjtbackend.common.exception;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 전역 예외 처리기
 *
 * 컨트롤러/서비스에서 예외가 발생했을 때
 * 공통 응답 형태로 변환해서 내려준다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 잘못된 파라미터, 업무상 잘못된 값 등
     */
    /**
     * 로그인 실패 등 Spring Security 인증 예외
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("인증 실패: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(
                        "AUTH_FAILED",
                        e.getMessage() != null ? e.getMessage() : "로그인에 실패했습니다."
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("잘못된 요청 파라미터/비즈니스 예외 발생: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("multipart 크기 초과: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.fail("PAYLOAD_TOO_LARGE", "업로드 가능한 최대 크기를 초과했습니다."));
    }

    /**
     * DB 제약조건 오류
     * 예: unique key 중복, not null 위반, fk 위반 등
     *
     * DB 원문은 로그에 남기고,
     * 프론트에는 가공된 메시지만 전달한다.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DB 제약조건 오류 발생", e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(
                        "DATA_INTEGRITY_ERROR",
                        "입력값이 올바르지 않거나 중복된 데이터가 있습니다."
                ));
    }

    /**
     * 그 외 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("서버 내부 오류 발생", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(
                        "INTERNAL_SERVER_ERROR",
                        "처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                ));
    }
}
