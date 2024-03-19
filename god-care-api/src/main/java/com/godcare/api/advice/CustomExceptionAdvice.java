package com.godcare.api.advice;


import com.godcare.api.exception.CustomException;
import com.godcare.api.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionAdvice {
    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<?> handleApiRequestException(CustomException e){
        log.info("e = {}", e.getMessage());
        HttpStatus httpStatus = e.getErrorCode().getCode();
        String errorMessage = e.getErrorMessage();
        return new ResponseEntity<>(Response.fail(errorMessage), httpStatus);
    }
}
