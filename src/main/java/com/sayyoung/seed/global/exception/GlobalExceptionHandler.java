package com.sayyoung.seed.global.exception;

import com.sayyoung.seed.global.response.ErrorResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직에서 발생한 예외를 처리합니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e
    ) {
        log.warn(
                "BusinessException occurred: code={}, message={}",
                e.getErrorCode().getCode(),
                e.getMessage()
        );

        return ResponseFactory.failure(
                e.getErrorCode()
        );
    }

    /**
     * @RequestBody와 @Valid를 통한 요청 값 검증 실패를 처리합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(CommonErrorCode.VALIDATION_FAILED.getMessage());

        log.warn(
                "Request validation failed: message={}",
                message
        );

        return ResponseFactory.failure(
                CommonErrorCode.VALIDATION_FAILED,
                message
        );
    }

    /**
     * 요청 본문의 JSON 형식이 올바르지 않은 경우를 처리합니다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        log.warn(
                "Request body parsing failed: message={}",
                e.getMessage()
        );

        return ResponseFactory.failure(
                CommonErrorCode.INVALID_REQUEST_BODY
        );
    }

    /**
     * PathVariable 또는 RequestParam의 타입 변환 실패를 처리합니다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        log.warn(
                "Request parameter type mismatch: name={}, value={}",
                e.getName(),
                e.getValue()
        );

        return ResponseFactory.failure(
                CommonErrorCode.INVALID_PARAMETER
        );
    }

    /**
     * 필수 RequestParam이 누락된 경우를 처리합니다.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        log.warn(
                "Required request parameter missing: name={}",
                e.getParameterName()
        );

        return ResponseFactory.failure(
                CommonErrorCode.MISSING_PARAMETER
        );
    }

    /**
     * @Validated를 통한 파라미터 제약 조건 검증 실패를 처리합니다.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException e
    ) {
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse(CommonErrorCode.VALIDATION_FAILED.getMessage());

        log.warn(
                "Constraint validation failed: message={}",
                message
        );

        return ResponseFactory.failure(
                CommonErrorCode.VALIDATION_FAILED,
                message
        );
    }

    /**
     * 존재하지 않는 API 또는 정적 리소스 요청을 처리합니다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException e
    ) {
        log.warn(
                "Requested resource not found: path={}",
                e.getResourcePath()
        );

        return ResponseFactory.failure(
                CommonErrorCode.NOT_FOUND
        );
    }

    /**
     * 별도로 처리되지 않은 예상치 못한 예외를 처리합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e
    ) {
        log.error(
                "Unexpected exception occurred",
                e
        );

        return ResponseFactory.failure(
                CommonErrorCode.INTERNAL_SERVER_ERROR
        );
    }
}