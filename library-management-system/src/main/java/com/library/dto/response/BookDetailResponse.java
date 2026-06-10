package com.library.dto.response;

import com.library.entity.BookCopy;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookDetailResponse {
    private Long id;
    private String isbn;
    private String bname;
    private String author;
    private String publisher;
    private String introduction;
    private LocalDate pubDate;
    private String clcNum;
    private String bookStatus;
    private List<BookCopy> copies;
    private Integer copyCount;
    private Integer availableCount;
}