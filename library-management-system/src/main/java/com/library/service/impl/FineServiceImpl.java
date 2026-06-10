package com.library.service.impl;

import com.library.entity.BorrowRecord;
import com.library.entity.PaymentRecord;
import com.library.enums.FineStatus;
import com.library.exception.BusinessException;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.PaymentRecordMapper;
import com.library.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class FineServiceImpl implements FineService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public List<BorrowRecord> getUnpaidFinesBySno(String sno) {
        return borrowRecordMapper.selectUnpaidBySno(sno);
    }

    @Override
    public List<BorrowRecord> getAllFinesBySno(String sno) {
        return borrowRecordMapper.selectAllBySno(sno);
    }

    @Override
    public BigDecimal getUnpaidTotalBySno(String sno) {
        List<BorrowRecord> unpaidFines = borrowRecordMapper.selectUnpaidBySno(sno);
        return unpaidFines.stream()
                .map(BorrowRecord::getFineMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public void payFine(Integer serNum) {
        BorrowRecord record = borrowRecordMapper.selectBySerNum(serNum);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (record.getFineStatus() == FineStatus.PAID) {
            throw new BusinessException("该记录已缴罚款");
        }
        BigDecimal fineAmount = record.getFineMoney();

        record.setFineStatus(FineStatus.PAID);
        record.setFineMoney(BigDecimal.ZERO);
        borrowRecordMapper.updateBySerNum(record);

        PaymentRecord payment = new PaymentRecord();
        payment.setSno(record.getSno());
        payment.setPayAmount(fineAmount);
        payment.setPayDate(LocalDate.now());
        paymentRecordMapper.insert(payment);

        Integer paySerNum = paymentRecordMapper.selectMaxSerNum();
        record.setPaySerNum(paySerNum);
        borrowRecordMapper.updateBySerNum(record);
    }

    @Override
    @Transactional
    public void payFines(List<Integer> serNums, BigDecimal amount) {
        if (serNums == null || serNums.isEmpty()) {
            throw new BusinessException("借阅记录列表不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("缴纳金额必须大于0");
        }

        List<Integer> sortedSerNums = serNums.stream()
                .sorted(Comparator.naturalOrder())
                .toList();

        List<BorrowRecord> records = sortedSerNums.stream()
                .map(borrowRecordMapper::selectBySerNum)
                .filter(r -> r != null && r.getFineStatus() == FineStatus.UNPAID)
                .toList();

        BigDecimal totalUnpaid = records.stream()
                .map(BorrowRecord::getFineMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (amount.compareTo(totalUnpaid) > 0) {
            throw new BusinessException("缴纳金额超过未缴罚款总额");
        }

        BigDecimal remaining = amount;
        for (BorrowRecord record : records) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            if (remaining.compareTo(record.getFineMoney()) >= 0) {
                remaining = remaining.subtract(record.getFineMoney());
                record.setFineStatus(FineStatus.PAID);
                record.setFineMoney(BigDecimal.ZERO);
            } else {
                record.setFineMoney(record.getFineMoney().subtract(remaining));
                remaining = BigDecimal.ZERO;
            }
            borrowRecordMapper.updateBySerNum(record);
        }

        PaymentRecord payment = new PaymentRecord();
        payment.setSno(records.get(0).getSno());
        payment.setPayAmount(amount);
        payment.setPayDate(LocalDate.now());
        paymentRecordMapper.insert(payment);

        Integer paySerNum = paymentRecordMapper.selectMaxSerNum();
        for (Integer serNum : sortedSerNums) {
            BorrowRecord record = borrowRecordMapper.selectBySerNum(serNum);
            if (record != null && record.getFineStatus() == FineStatus.PAID) {
                record.setPaySerNum(paySerNum);
                borrowRecordMapper.updateBySerNum(record);
            }
        }
    }

    @Override
    @Transactional
    public void payAllFinesBySno(String sno) {
        List<BorrowRecord> unpaidFines = borrowRecordMapper.selectUnpaidBySno(sno);
        if (unpaidFines.isEmpty()) {
            throw new BusinessException("没有未缴罚款");
        }

        BigDecimal totalAmount = unpaidFines.stream()
                .map(BorrowRecord::getFineMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Integer> serNums = unpaidFines.stream()
                .map(BorrowRecord::getSerNum)
                .sorted()
                .toList();

        payFines(serNums, totalAmount);
    }

    @Override
    public List<PaymentRecord> getPaymentRecordsBySno(String sno) {
        return paymentRecordMapper.selectBySno(sno);
    }
}