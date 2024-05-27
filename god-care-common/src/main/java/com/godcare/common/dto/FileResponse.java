package com.godcare.common.dto;

import lombok.*;

import java.net.URL;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class FileResponse {
    private String originalFileName;
    private String uploadFileName;
    private String uploadFilePath;
    private String uploadFileUrl;
    private URL presingedUrl;
}
