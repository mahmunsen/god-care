package com.godcare.api.entity;


import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FileStatus {

    private final String fileKey;
    private final String contentType;
    private String uploadId;
    private int partCounter;
    private int buffered;
    private Map<Integer, CompletedPart> completedParts = new HashMap<>();
    public FileStatus(String contentType, String fileKey) {
        this.contentType = contentType;
        this.fileKey = fileKey;
        this.buffered = 0;
    }

    public void addBuffered(int buffered) {
        this.buffered += buffered;
    }

    public int getAddedPartCounter() {
        return ++this.partCounter;
    }
}
