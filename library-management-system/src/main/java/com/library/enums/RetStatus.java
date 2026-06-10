package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum RetStatus {
    ONTIME("按时还"),
    OVERDUE("超时还"),
    UNRETURNED("未归还");

    @EnumValue
    private final String status;

    RetStatus(String status) {
        this.status = status;
    }
}