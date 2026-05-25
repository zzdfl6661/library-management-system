
package com.library.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Student {
    private Long id;
    private String studentNo;
    private String name;
    private String type;
    private Integer maxBorrowCount;
    private LocalDateTime createTime;
}
