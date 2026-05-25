
package com.library.service.impl;

import com.library.dto.request.PaymentRequest;
import com.library.entity.FineRecord;
import com.library.entity.PaymentRecord;
import com.library.entity.Student;
import com.library.exception.BusinessException;
import com.library.mapper.FineRecordMapper;
import com.library.mapper.PaymentRecordMapper;
import com.library.mapper.StudentMapper;
import com.library.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Override
    public BigDecimal getUnpaidFineByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return fineRecordMapper.selectUnpaidTotalByStudentId(student.getId());
    }

    @Override
    public List<FineRecord> getFineRecordsByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
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
}
