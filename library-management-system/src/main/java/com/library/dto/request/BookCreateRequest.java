
package com.library.dto.request;

import lombok.Data;

@Data
public class BookCreateRequest {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String summary;
    private Integer copyCount;
    private String location;
}
