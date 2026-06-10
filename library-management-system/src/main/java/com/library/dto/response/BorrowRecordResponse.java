package com.library.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BorrowRecordResponse {
    private Integer serNum;
    private String sno;
    private String studentName;
    private String barCode;
    private String bookTitle;
    private String author;
    private String publisher;
    private String place;
    private LocalDate borDate;
    private LocalDate retDate;
    private LocalDate realRetDate;
    private String retStatus;
    private Integer ovdDays;
    private BigDecimal fineMoney;
    private String fineStatus;
}
