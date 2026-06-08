
package com.library.service;

import com.library.dto.request.PaymentRequest;
import com.library.entity.FineRecord;
import com.library.entity.PaymentRecord;

import java.math.BigDecimal;
import java.util.List;

public interface FineService {
    BigDecimal getUnpaidFineByStudentNo(String studentNo);
    List<FineRecord> getFineRecordsByStudentNo(String studentNo);
    void payFine(PaymentRequest request);
    List<PaymentRecord> getPaymentRecordsByStudentNo(String studentNo);
    void repayFine(Long fineId);
    void generateFinesForOverdueBorrows(String studentNo);
}
