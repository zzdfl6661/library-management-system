package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum FineStatus {
    PAID("已缴"),
    UNPAID("未缴");

    @EnumValue
    private final String status;

    FineStatus(String status) {
        this.status = status;
    }
}