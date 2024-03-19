package com.godcare.api.exception;

import org.springframework.http.HttpStatus;


public interface ErrorCode {
    HttpStatus getCode();
    String getMessage();
}
