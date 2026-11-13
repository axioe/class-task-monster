package com.classmonster.exception;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반 시 던지는 예외. GlobalExceptionHandler 가 ErrorResponse 로 변환한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
