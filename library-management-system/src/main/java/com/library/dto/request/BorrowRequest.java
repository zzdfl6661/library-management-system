
package com.library.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BorrowRequest {
    private String cardNo;
    private List<String> barcodes;
}
