
package com.library.controller;

import com.library.dto.request.BorrowRequest;
import com.library.dto.request.ReturnRequest;
import com.library.dto.response.ApiResponse;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/card/{cardNo}")
    public ApiResponse<List<BorrowRecord>> getBorrowRecordsByCardNo(@PathVariable String cardNo) {
        List<BorrowRecord> records = borrowService.getBorrowRecordsByCardNo(cardNo);
        return ApiResponse.success(records);
    }

    @GetMapping("/student/{studentNo}")
    public ApiResponse<List<BorrowRecord>> getBorrowRecordsByStudentNo(@PathVariable String studentNo) {
        List<BorrowRecord> records = borrowService.getBorrowRecordsByStudentNo(studentNo);
        return ApiResponse.success(records);
    }

    @GetMapping("/canBorrow/{cardNo}")
    public ApiResponse<Boolean> canBorrow(@PathVariable String cardNo) {
        boolean canBorrow = borrowService.canBorrow(cardNo);
        return ApiResponse.success(canBorrow);
    }
}
