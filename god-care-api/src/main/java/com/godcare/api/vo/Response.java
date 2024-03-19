package com.godcare.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class Response<T> {
    private final Boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public Response() {
        this.success = null;
        this.message = null;
        this.data = null;
    }

    private Response(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    public static <T> Response<T> success(T data) {
        return new Response<>(true,null, data);
    }

    public static <T> Response<T> fail(String message) {
        return new Response<>(false, message,null);
    }
}
