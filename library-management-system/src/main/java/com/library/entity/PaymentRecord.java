
package com.library.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRecord {
    private Long id;
    private Long studentId;
    private BigDecimal amount;
    private Long operatorId;
    private LocalDateTime createTime;
}
