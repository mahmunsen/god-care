package com.godcare.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {

    TEMP("TEMP"),
    COMPLETE("COMPLETE");

    private final String status;
}
