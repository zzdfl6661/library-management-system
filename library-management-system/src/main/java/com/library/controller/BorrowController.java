
package com.library.controller;

import com.library.dto.request.BorrowRequest;
import com.library.dto.request.ReturnRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BorrowRecordResponse;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "http://localhost:5173")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @PostMapping
    public ApiResponse<Void> borrowBooks(@RequestBody BorrowRequest request) {
        borrowService.borrowBooks(request);
        return ApiResponse.success("借书成功");
    }

    @PostMapping("/return")
    public ApiResponse<Void> returnBook(@RequestBody ReturnRequest request) {
        borrowService.returnBook(request);
        return ApiResponse.success("还书成功");
    }

    @PostMapping("/student")
    public ApiResponse<Void> borrowBookByStudent(@RequestBody Map<String, Long> request) {
        Long bookId = request.get("bookId");
        Long userId = Long.parseLong(request.get("userId").toString());
        borrowService.borrowBookByStudent(bookId, userId);
        return ApiResponse.success("借阅成功");
    }

    @PostMapping("/card")
    public ApiResponse<Void> borrowBookByCardNo(@RequestBody Map<String, Object> request) {
        Long bookId = Long.parseLong(request.get("bookId").toString());
        String cardNo = request.get("cardNo").toString();
        borrowService.borrowBookByCardNo(bookId, cardNo);
        return ApiResponse.success("借阅成功");
    }

    @GetMapping("/check/{barcode}")
    public ApiResponse<BorrowRecordResponse> checkBorrowStatus(@PathVariable String barcode) {
        BorrowRecordResponse response = borrowService.checkBorrowStatus(barcode);
        return ApiResponse.success(response);
    }

    @GetMapping("/card/{cardNo}")
    public ApiResponse<List<BorrowRecordResponse>> getBorrowRecordsByCardNo(@PathVariable String cardNo) {
        List<BorrowRecordResponse> records = borrowService.getBorrowRecordsByCardNo(cardNo);
        return ApiResponse.success(records);
    }

    @GetMapping("/student/{studentNo}")
    public ApiResponse<List<BorrowRecordResponse>> getBorrowRecordsByStudentNo(@PathVariable String studentNo) {
        List<BorrowRecordResponse> records = borrowService.getBorrowRecordsByStudentNo(studentNo);
        return ApiResponse.success(records);
    }

    @GetMapping("/canBorrow/{cardNo}")
    public ApiResponse<Boolean> canBorrow(@PathVariable String cardNo) {
        boolean canBorrow = borrowService.canBorrow(cardNo);
        return ApiResponse.success(canBorrow);
    }
}
