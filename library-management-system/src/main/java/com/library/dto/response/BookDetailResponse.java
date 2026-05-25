
package com.library.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BookDetailResponse {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String summary;
    private List<CopyInfo> copies;

    @Data
    public static class CopyInfo {
        private Long id;
        private String barcode;
        private String location;
        private String status;
    }
}
