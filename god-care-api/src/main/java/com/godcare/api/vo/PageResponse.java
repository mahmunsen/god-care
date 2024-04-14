package com.godcare.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private String cursor;
    private int size;
    private boolean isEnd;

    public static <T> PageResponse<T> from(List<T> content, PageableRequest pageable){
        boolean isEnd = content.size() < pageable.getSize();
        return new PageResponse<>(content, pageable.getCursor(), pageable.getSize(), isEnd);
    }
}
