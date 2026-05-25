
package com.library.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StatisticsResponse {
    private List<RankingResponse> studentRanking;
    private List<RankingResponse> bookRanking;
    private Long todayBorrowCount;
    private Long todayReturnCount;
    private Long totalBooks;
    private Long availableBooks;
    private Long borrowedBooks;
}
