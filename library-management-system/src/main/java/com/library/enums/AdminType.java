package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum AdminType {
    OFFICE(0, "证件管理"),
    ACQUISITION(1, "采编"),
    CIRCULATION(2, "流通");

    @EnumValue
    private final Integer code;
    private final String description;

    AdminType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}