
package com.library.service;

import com.library.dto.request.BorrowRequest;
import com.library.dto.request.ReturnRequest;
import com.library.entity.BorrowRecord;

import java.util.List;

public interface BorrowService {
    void borrowBooks(BorrowRequest request);
    void returnBook(ReturnRequest request);
    List<BorrowRecord> getBorrowRecordsByCardNo(String cardNo);
    List<BorrowRecord> getBorrowRecordsByStudentNo(String studentNo);
    boolean canBorrow(String cardNo);
}
