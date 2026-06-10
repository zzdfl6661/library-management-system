
package com.library.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RankingResponse {
    private Long id;
    private String name;
    private Integer count;
    private BigDecimal amount;
}
