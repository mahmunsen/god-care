package com.godcare.api.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
@AllArgsConstructor
@Schema(title = "pageable")
public class PageableRequest {

    @ApiModelProperty(value = "커서 100", example = "100")
    @Setter
    private String cursor;

    @ApiModelProperty(value = "페이지 번호 0 ", example = "0")
    private Integer page;

    @ApiModelProperty(value = "한 페이지 크기 20", example = "20")
    private Integer size;

    public Pageable from() {
        if (page == null) page = 0;
        if (size == null) size = 20;
        return PageRequest.of(page, size, Sort.Direction.ASC, "id");
    }

    public String getCursor() {
//        if (cursor == null) return Long.MAX_VALUE;
        if (cursor == null) return null;
        return cursor;
    }

    public Integer getSize() {
        if (size == null) return 20;
        return size;
    }
}
