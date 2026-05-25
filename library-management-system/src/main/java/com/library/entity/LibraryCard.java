
package com.library.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LibraryCard {
    private Long id;
    private String cardNo;
    private Long studentId;
    private String status;
    private LocalDate issueDate;
    private LocalDateTime createTime;
}
