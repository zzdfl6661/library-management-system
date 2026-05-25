
package com.library.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FineRecord {
    private Long id;
    private Long studentId;
    private Long borrowRecordId;
    private BigDecimal amount;
    private Integer days;
    private Integer isPaid;
    private LocalDateTime createTime;
}
