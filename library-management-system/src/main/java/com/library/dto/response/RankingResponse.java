
package com.library.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RankingResponse {
    private String name;
    private Integer count;
    private BigDecimal amount;
}
