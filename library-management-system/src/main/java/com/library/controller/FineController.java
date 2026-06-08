
package com.library.controller;

import com.library.dto.request.PaymentRequest;
import com.library.dto.response.ApiResponse;
import com.library.entity.FineRecord;
import com.library.entity.PaymentRecord;
import com.library.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fines")
@CrossOrigin(origins = "http://localhost:5173")
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping("/student/{studentNo}")
    public ApiResponse<List<FineRecord>> getFineRecords(@PathVariable String studentNo) {
        List<FineRecord> records = fineService.getFineRecordsByStudentNo(studentNo);
        return ApiResponse.success(records);
    }

    @GetMapping("/student/{studentNo}/total")
    public ApiResponse<BigDecimal> getUnpaidFine(@PathVariable String studentNo) {
        BigDecimal total = fineService.getUnpaidFineByStudentNo(studentNo);
        return ApiResponse.success(total);
    }

    @PostMapping("/pay")
    public ApiResponse<Void> payFine(@RequestBody PaymentRequest request) {
        fineService.payFine(request);
        return ApiResponse.success("缴费成功");
    }

    @GetMapping("/payments/{studentNo}")
    public ApiResponse<List<PaymentRecord>> getPaymentRecords(@PathVariable String studentNo) {
        List<PaymentRecord> records = fineService.getPaymentRecordsByStudentNo(studentNo);
        return ApiResponse.success(records);
    }

    @PostMapping("/{fineId}/repay")
    public ApiResponse<Void> repayFine(@PathVariable Long fineId) {
        fineService.repayFine(fineId);
        return ApiResponse.success("还款成功");
    }
}
