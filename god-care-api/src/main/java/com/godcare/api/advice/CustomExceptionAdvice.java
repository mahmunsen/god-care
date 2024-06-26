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

    @ExceptionHandler(NotTempStatusException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public Response notTempStatusException() {
        return Response.fail("상품의 현재 상태로는 업로드를 진행할 수 없습니다.");
    }

    @ExceptionHandler(NotCompleteStatusException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public Response notCompleteStatusException() {
        return Response.fail("상품의 현재 상태로는 업데이트를 진행할 수 없습니다.");
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

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response fileAlreadyExistsException() {
        return Response.fail("이미 해당 이미지 파일 이름이 존재합니다.");
    }

    @ExceptionHandler(AtLeastOneImageRequiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response atLeastOneImageRequiredException() {
        return Response.fail("적어도 한 개 이상의 이미지 파일이 있어야 합니다.");
    }

    @ExceptionHandler(ProductSortTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response ProductSortTypeNotFoundException() {
        return Response.fail("물품의 정렬 타입을 찾을 수 없습니다.");
    }

    @ExceptionHandler(ProductPhotoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response productPhotoNotFoundException() {
        return Response.fail("물품의 이미지를 찾을 수 없습니다.");
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

