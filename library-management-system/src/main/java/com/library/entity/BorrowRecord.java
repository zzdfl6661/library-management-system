
package com.library.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BorrowRecord {
    private Long id;
    private Long cardId;
    private Long copyId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Integer isOverdue;
    private LocalDateTime createTime;
}
