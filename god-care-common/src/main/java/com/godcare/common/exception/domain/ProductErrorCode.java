package com.godcare.common.exception.domain;

import com.godcare.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND.value(), "요청하신 상품을 찾을 수 없습니다.");

    private final int code;
    private final String message;

}
