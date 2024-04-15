package com.godcare.api.enums;

public enum FilePath {
    PRODUCT_DIR("product");

    FilePath(String path) {
        this.path = path;
    }

    private final String path;

    public String getPath() {
        return path;
    }
}
