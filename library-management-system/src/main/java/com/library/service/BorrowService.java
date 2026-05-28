
package com.library.service;

import com.library.dto.request.BorrowRequest;
import com.library.dto.request.ReturnRequest;
import com.library.dto.response.BorrowRecordResponse;
import com.library.entity.BorrowRecord;

import java.util.List;

public interface BorrowService {
    void borrowBooks(BorrowRequest request);
    void returnBook(ReturnRequest request);
    List<BorrowRecordResponse> getBorrowRecordsByCardNo(String cardNo);
    List<BorrowRecordResponse> getBorrowRecordsByStudentNo(String studentNo);
    boolean canBorrow(String cardNo);
    void borrowBookByStudent(Long bookId, String studentNo);
}
