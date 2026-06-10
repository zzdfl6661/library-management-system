package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum BookStatus {
    OFFSHELF(0, "下架"),
    ONSHELF(1, "上架"),
    DELETED(2, "删除");

    @EnumValue
    private final Integer code;
    private final String description;

    BookStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}