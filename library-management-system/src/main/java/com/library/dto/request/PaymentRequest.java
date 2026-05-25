
package com.library.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String studentNo;
    private BigDecimal amount;
    private Long operatorId;
}
