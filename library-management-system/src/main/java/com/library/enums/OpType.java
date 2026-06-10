package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OpType {
    NEW("新办"),
    LOST("挂失"),
    REISSUE("补办"),
    CANCEL("注销");

    @EnumValue
    private final String operation;

    OpType(String operation) {
        this.operation = operation;
    }
}