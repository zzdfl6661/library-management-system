package com.library.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum StudentType {
    UNDERGRADUATE("本科生", 5),
    GRADUATE("研究生", 10),
    TEACHER("教师", 15);

    @EnumValue
    private final String type;
    private final Integer maxBorrowCount;

    StudentType(String type, Integer maxBorrowCount) {
        this.type = type;
        this.maxBorrowCount = maxBorrowCount;
    }
}