package com.flawlessyou.backend.entity.product;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Type {
    OILY, DRY, NORMAL;

    @JsonCreator
    public static Type fromString(String value) {
        return Type.valueOf(value.toUpperCase());
    }
}