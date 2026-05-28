package com.library.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LibraryCardResponse {
    private Long id;
    private String cardNo;
    private Long studentId;
    private String studentNo;
    private String status;
    private LocalDate issueDate;
    private Integer availableCount;
    private LocalDateTime createTime;
}