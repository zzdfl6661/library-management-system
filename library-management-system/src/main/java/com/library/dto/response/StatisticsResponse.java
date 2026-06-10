package com.library.dto.response;

import com.library.entity.Book;
import lombok.Data;

import java.util.List;

@Data
public class StatisticsResponse {
    private List<RankingResponse> hotBooks;
    private List<RankingResponse> topReaders;
    private List<Book> newBooks;
    private Integer todayBorrowCount;
    private Integer todayReturnCount;
}