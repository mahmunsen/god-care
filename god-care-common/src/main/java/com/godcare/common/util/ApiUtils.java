package com.godcare.common.util;

import com.godcare.common.vo.Response;
import org.springframework.http.HttpStatus;

public class ApiUtils {

    public static <T> Response<T> success(Integer status, String message, T data) {
        return new Response<>(true, status, message, data);
    }

    public static <T> Response<T> success(HttpStatus status, String message, T data) {
        return new Response<>(true, status.value(), message, data);
    }

}
