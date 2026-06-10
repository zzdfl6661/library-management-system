package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum CardStatus {
    NORMAL("正常"),
    LOST("挂失"),
    CANCELLED("注销");

    @EnumValue
    private final String status;

    CardStatus(String status) {
        this.status = status;
    }
}