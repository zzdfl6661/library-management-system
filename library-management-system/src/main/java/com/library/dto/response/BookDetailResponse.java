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
    private String title;
    private String author;
    private String publisher;
    private String introduction;
    private String summary;
    private LocalDate pubDate;
    private String clcNum;
    private String bookStatus;
    private List<BookCopy> copies;
    private Integer copyCount;
    private Integer availableCount;
    private List<CopyInfo> copyInfoList;

    @Data
    public static class CopyInfo {
        private Long id;
        private String barcode;
        private String location;
        private String status;
    }
}