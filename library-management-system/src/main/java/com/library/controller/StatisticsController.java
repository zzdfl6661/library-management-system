package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.dto.response.RankingResponse;
import com.library.dto.response.StatisticsResponse;
import com.library.entity.Book;
import com.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ApiResponse<StatisticsResponse> getDashboard() {
        StatisticsResponse stats = statisticsService.getStatistics();
        return ApiResponse.success(stats);
    }

    @GetMapping("/hot-books")
    public ApiResponse<List<RankingResponse>> getHotBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<RankingResponse> hotBooks = statisticsService.getHotBooks(limit);
        return ApiResponse.success(hotBooks);
    }

    @GetMapping("/top-readers")
    public ApiResponse<List<RankingResponse>> getTopReaders(
            @RequestParam(defaultValue = "10") int limit) {
        List<RankingResponse> topReaders = statisticsService.getTopReaders(limit);
        return ApiResponse.success(topReaders);
    }

    @GetMapping("/new-books")
    public ApiResponse<List<Book>> getNewBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<Book> newBooks = statisticsService.getNewBooks(limit);
        return ApiResponse.success(newBooks);
    }
}