package com.library.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnResponse {
    private Boolean success;
    private Integer ovdDays;
    private BigDecimal fineMoney;
    private String message;
}