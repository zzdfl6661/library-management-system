package com.library.service.impl;

import com.library.dto.response.RankingResponse;
import com.library.dto.response.StatisticsResponse;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<RankingResponse> getHotBooks(int limit) {
        return borrowRecordMapper.selectHotBooks(limit);
    }

    @Override
    public List<RankingResponse> getTopReaders(int limit) {
        return borrowRecordMapper.selectTopReaders(limit);
    }

    @Override
    public List<Book> getNewBooks(int limit) {
        return bookMapper.selectNewBooks(limit);
    }

    @Override
    public StatisticsResponse getStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        response.setHotBooks(getHotBooks(10));
        response.setTopReaders(getTopReaders(10));
        response.setNewBooks(getNewBooks(10));
        response.setTodayBorrowCount(getTodayBorrowCount());
        response.setTodayReturnCount(getTodayReturnCount());
        return response;
    }

    @Override
    public int getTodayBorrowCount() {
        return borrowRecordMapper.countByBorDate(LocalDate.now());
    }

    @Override
    public int getTodayReturnCount() {
        return borrowRecordMapper.countByRetDate(LocalDate.now());
    }
}