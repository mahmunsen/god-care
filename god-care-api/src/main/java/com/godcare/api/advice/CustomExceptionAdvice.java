package com.godcare.api.advice;

import com.godcare.api.exception.*;
import com.godcare.api.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionAdvice {
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response productNotFoundException() {
        return Response.fail("요청한 상품을 찾을 수 없습니다.");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException() {
        return Response.fail("해당 카테고리를 찾을 수 없습니다.");
    }

    @ExceptionHandler(ImageFileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response imageFileNotFoundException() {
        return Response.fail("해당 이미지 파일을 찾을 수 없습니다.");
    }

    @ExceptionHandler(ProductSortTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response ProductSortTypeNotFoundException() {
        return Response.fail("물품의 정렬 타입을 찾을 수 없습니다.");
    }

    @ExceptionHandler(FileUploadFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response fileUploadFailedException() {
        return Response.fail("파일 업로드에 실패했습니다.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response Exception() {
        return Response.fail("응답이 실패했습니다.");
    }
}

