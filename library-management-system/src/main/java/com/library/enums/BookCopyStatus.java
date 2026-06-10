package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum BookCopyStatus {
    BORROWED(0, "借出"),
    AVAILABLE(1, "可借"),
    CANCELLED(2, "注销");

    @EnumValue
    private final Integer code;
    private final String description;

    BookCopyStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}