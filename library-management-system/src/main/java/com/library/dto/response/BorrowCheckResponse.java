package com.library.dto.response;

import com.library.entity.LibraryCard;
import lombok.Data;

@Data
public class BorrowCheckResponse {
    private Boolean canBorrow;
    private String reason;
    private Integer availableCount;
    private LibraryCard card;
}