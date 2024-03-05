package com.godcare.common.util;

import com.godcare.common.vo.Response;
import org.springframework.http.ResponseEntity;

public class ApiUtils {

    public static <T> ResponseEntity<Response<T>> success(Integer status, String message, T data) {
        return ResponseEntity.status(status).body(new Response<>(true, status, message, data));
    }
}
