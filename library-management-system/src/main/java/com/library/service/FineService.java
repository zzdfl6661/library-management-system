package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.entity.PaymentRecord;

import java.math.BigDecimal;
import java.util.List;

public interface FineService {

    List<BorrowRecord> getUnpaidFinesBySno(String sno);

    List<BorrowRecord> getAllFinesBySno(String sno);

    BigDecimal getUnpaidTotalBySno(String sno);

    void payFine(Integer serNum);

    void payFines(List<Integer> serNums, BigDecimal amount);

    void payAllFinesBySno(String sno);

    List<PaymentRecord> getPaymentRecordsBySno(String sno);
}