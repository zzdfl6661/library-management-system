
package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.dto.response.RankingResponse;
import com.library.dto.response.StatisticsResponse;
import com.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ApiResponse<StatisticsResponse> getDashboard() {
        StatisticsResponse response = statisticsService.getDashboardStatistics();
        return ApiResponse.success(response);
    }

    @GetMapping("/student-ranking")
    public ApiResponse<List<RankingResponse>> getStudentRanking() {
        List<RankingResponse> ranking = statisticsService.getStudentBorrowRanking();
        return ApiResponse.success(ranking);
    }

    @GetMapping("/book-ranking")
    public ApiResponse<List<RankingResponse>> getBookRanking() {
        List<RankingResponse> ranking = statisticsService.getBookBorrowRanking();
        return ApiResponse.success(ranking);
    }
}
