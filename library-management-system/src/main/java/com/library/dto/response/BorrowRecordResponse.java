
package com.library.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BorrowRecordResponse {
    private Long id;
    private String cardNo;
    private String studentName;
    private String bookTitle;
    private String barcode;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Integer isOverdue;
    private LocalDateTime createTime;
}
