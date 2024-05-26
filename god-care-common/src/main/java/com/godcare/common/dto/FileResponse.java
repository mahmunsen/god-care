package com.godcare.common.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class FileResponse {
    private String uploadFileName;
    private String presingedUrl;
}
