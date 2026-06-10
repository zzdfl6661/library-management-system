package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BorrowCheckResponse;
import com.library.dto.response.BorrowRecordResponse;
import com.library.dto.response.ReturnResponse;
import com.library.entity.BorrowRecord;
import com.library.entity.LibraryCard;
import com.library.service.BorrowService;
import com.library.service.LibraryCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private LibraryCardService libraryCardService;

    @PostMapping("/card-check")
    public ApiResponse<BorrowCheckResponse> checkCard(@RequestBody Map<String, String> request) {
        String cardNo = request.get("cardNo");
        if (cardNo == null) {
            return ApiResponse.error(400, "借书证号不能为空");
        }
        
        BorrowCheckResponse response = new BorrowCheckResponse();
        String reason = borrowService.canBorrow(cardNo);
        LibraryCard card = borrowService.getCardInfo(cardNo);
        
        if (reason != null) {
            response.setCanBorrow(false);
            response.setReason(reason);
            response.setAvailableCount(0);
            response.setCard(card);
        } else {
            response.setCanBorrow(true);
            response.setReason(null);
            response.setAvailableCount(borrowService.getAvailableCount(cardNo));
            response.setCard(card);
        }
        
        return ApiResponse.success(response);
    }

    @PostMapping("/borrow")
    public ApiResponse<Void> borrowBooks(@RequestBody Map<String, Object> request) {
        String cardNo = (String) request.get("cardNo");
        @SuppressWarnings("unchecked")
        List<String> barCodes = (List<String>) request.get("barCodes");
        
        if (cardNo == null || barCodes == null || barCodes.isEmpty()) {
            return ApiResponse.error(400, "参数不完整");
        }
        
        borrowService.borrowBooks(cardNo, barCodes);
        return ApiResponse.success("借书成功", null);
    }

    @PostMapping("/return")
    public ApiResponse<ReturnResponse> returnBook(@RequestBody Map<String, String> request) {
        String barCode = request.get("barCode");
        if (barCode == null) {
            return ApiResponse.error(400, "条码不能为空");
        }
        
        Map<String, Object> result = borrowService.returnBook(barCode);
        
        ReturnResponse response = new ReturnResponse();
        response.setSuccess(true);
        response.setOvdDays(result.get("ovdDays") != null ? ((Number) result.get("ovdDays")).intValue() : 0);
        response.setFineMoney(result.get("fineMoney") != null ? (java.math.BigDecimal) result.get("fineMoney") : java.math.BigDecimal.ZERO);
        response.setMessage("还书成功");
        
        return ApiResponse.success(response);
    }

    @GetMapping("/check/{barCode}")
    public ApiResponse<BorrowRecord> checkBorrowStatus(@PathVariable String barCode) {
        BorrowRecord record = borrowService.checkBorrowStatus(barCode);
        if (record == null) {
            return ApiResponse.error(404, "未找到借阅记录");
        }
        return ApiResponse.success(record);
    }

    @GetMapping("/student/{sno}")
    public ApiResponse<IPage<BorrowRecordResponse>> getBorrowRecords(
            @PathVariable String sno,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<BorrowRecordResponse> records = borrowService.getBorrowRecordsBySno(sno, page, size);
        return ApiResponse.success(records);
    }

    @GetMapping("/student/{sno}/current")
    public ApiResponse<List<BorrowRecord>> getCurrentBorrows(@PathVariable String sno) {
        List<BorrowRecord> records = borrowService.getCurrentBorrowsBySno(sno);
        return ApiResponse.success(records);
    }
}