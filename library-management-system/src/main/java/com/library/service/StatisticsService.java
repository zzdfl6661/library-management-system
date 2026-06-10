package com.library.service;

import com.library.dto.response.RankingResponse;
import com.library.dto.response.StatisticsResponse;
import com.library.entity.Book;

import java.util.List;

public interface StatisticsService {

    List<RankingResponse> getHotBooks(int limit);

    List<RankingResponse> getTopReaders(int limit);

    List<Book> getNewBooks(int limit);

    StatisticsResponse getStatistics();

    int getTodayBorrowCount();

    int getTodayReturnCount();
}