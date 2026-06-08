
package com.library.service.impl;

import com.library.dto.request.PaymentRequest;
import com.library.entity.BorrowRecord;
import com.library.entity.FineRecord;
import com.library.entity.PaymentRecord;
import com.library.entity.Student;
import com.library.exception.BusinessException;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.FineRecordMapper;
import com.library.mapper.PaymentRecordMapper;
import com.library.mapper.StudentMapper;
import com.library.service.FineService;
import com.library.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FineServiceImpl implements FineService {

    @Autowired
    private FineRecordMapper fineRecordMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    private static final BigDecimal FINE_PER_DAY = new BigDecimal("0.10");

    @Override
    @Transactional
    public BigDecimal getUnpaidFineByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        generateFinesForOverdueBorrows(studentNo);
        return fineRecordMapper.selectUnpaidTotalByStudentId(student.getId());
    }

    @Override
    @Transactional
    public List<FineRecord> getFineRecordsByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        generateFinesForOverdueBorrows(studentNo);
        return fineRecordMapper.selectByStudentId(student.getId());
    }

    @Override
    @Transactional
    public void payFine(PaymentRequest request) {
        Student student = studentMapper.selectByStudentNo(request.getStudentNo());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        BigDecimal unpaidFine = fineRecordMapper.selectUnpaidTotalByStudentId(student.getId());
        if (unpaidFine.compareTo(request.getAmount()) < 0) {
            throw new BusinessException("缴纳金额超过未缴罚款");
        }
        PaymentRecord payment = new PaymentRecord();
        payment.setStudentId(student.getId());
        payment.setAmount(request.getAmount());
        payment.setOperatorId(request.getOperatorId());
        payment.setCreateTime(LocalDateTime.now());
        paymentRecordMapper.insert(payment);
        List<FineRecord> unpaidFines = fineRecordMapper.selectUnpaidByStudentId(student.getId());
        BigDecimal remaining = request.getAmount();
        for (FineRecord fine : unpaidFines) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            if (remaining.compareTo(fine.getAmount()) >= 0) {
                fine.setIsPaid(1);
                fineRecordMapper.update(fine);
                remaining = remaining.subtract(fine.getAmount());
            } else {
                fine.setAmount(fine.getAmount().subtract(remaining));
                fineRecordMapper.update(fine);
                remaining = BigDecimal.ZERO;
            }
        }
    }

    @Override
    public List<PaymentRecord> getPaymentRecordsByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return paymentRecordMapper.selectByStudentId(student.getId());
    }

    @Override
    @Transactional
    public void repayFine(Long fineId) {
        FineRecord fine = fineRecordMapper.selectById(fineId);
        if (fine == null) {
            throw new BusinessException("罚款记录不存在");
        }
        if (fine.getIsPaid() == 1) {
            throw new BusinessException("该罚款已缴纳");
        }
        fine.setIsPaid(1);
        fineRecordMapper.update(fine);
    }

    @Override
    @Transactional
    public void generateFinesForOverdueBorrows(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            return;
        }
        List<BorrowRecord> unreturnedBorrows = borrowRecordMapper.selectUnreturnedByStudentId(student.getId());
        LocalDate today = LocalDate.now();
        
        for (BorrowRecord record : unreturnedBorrows) {
            LocalDate dueDate = record.getDueDate();
            if (today.isAfter(dueDate)) {
                FineRecord existingFine = fineRecordMapper.selectByBorrowRecordId(record.getId());
                if (existingFine != null) {
                    continue;
                }
                long days = DateUtil.daysBetween(dueDate, today);
                BigDecimal amount = FINE_PER_DAY.multiply(new BigDecimal(days));
                FineRecord fine = new FineRecord();
                fine.setStudentId(student.getId());
                fine.setBorrowRecordId(record.getId());
                fine.setAmount(amount);
                fine.setDays((int) days);
                fine.setIsPaid(0);
                fine.setCreateTime(LocalDateTime.now());
                fineRecordMapper.insert(fine);
                record.setIsOverdue(1);
                borrowRecordMapper.update(record);
            }
        }
    }
}
