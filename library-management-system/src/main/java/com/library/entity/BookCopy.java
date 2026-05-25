
package com.library.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookCopy {
    private Long id;
    private Long bookId;
    private String barcode;
    private String location;
    private String status;
    private LocalDateTime createTime;
}
