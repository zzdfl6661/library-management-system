package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.entity.BorrowRecord;
import com.library.entity.PaymentRecord;
import com.library.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fine")
@CrossOrigin(origins = "*")
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping("/student/{sno}/unpaid")
    public ApiResponse<List<BorrowRecord>> getUnpaidFines(@PathVariable String sno) {
        List<BorrowRecord> fines = fineService.getUnpaidFinesBySno(sno);
        return ApiResponse.success(fines);
    }

    @GetMapping("/student/{sno}")
    public ApiResponse<List<BorrowRecord>> getAllFines(@PathVariable String sno) {
        List<BorrowRecord> fines = fineService.getAllFinesBySno(sno);
        return ApiResponse.success(fines);
    }

    @GetMapping("/student/{sno}/total")
    public ApiResponse<BigDecimal> getUnpaidTotal(@PathVariable String sno) {
        BigDecimal total = fineService.getUnpaidTotalBySno(sno);
        return ApiResponse.success(total);
    }

    @PostMapping("/pay")
    public ApiResponse<Void> payFines(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> serNums = (List<Integer>) request.get("serNums");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        
        if (serNums == null || serNums.isEmpty()) {
            return ApiResponse.error(400, "罚款记录不能为空");
        }
        
        fineService.payFines(serNums, amount);
        return ApiResponse.success("缴费成功", null);
    }

    @PostMapping("/pay/all/{sno}")
    public ApiResponse<Void> payAllFines(@PathVariable String sno) {
        fineService.payAllFinesBySno(sno);
        return ApiResponse.success("全部缴费成功", null);
    }

    @GetMapping("/payments/{sno}")
    public ApiResponse<List<PaymentRecord>> getPaymentRecords(@PathVariable String sno) {
        List<PaymentRecord> records = fineService.getPaymentRecordsBySno(sno);
        return ApiResponse.success(records);
    }
}