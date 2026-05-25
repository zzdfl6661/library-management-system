
package com.library.service;

import com.library.dto.response.StatisticsResponse;
import com.library.dto.response.RankingResponse;

import java.util.List;

public interface StatisticsService {
    StatisticsResponse getDashboardStatistics();
    List<RankingResponse> getStudentBorrowRanking();
    List<RankingResponse> getBookBorrowRanking();
}
