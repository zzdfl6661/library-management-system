package com.library.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.response.BorrowRecordResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.LibraryCard;
import com.library.entity.Student;
import com.library.enums.BookCopyStatus;
import com.library.enums.FineStatus;
import com.library.enums.RetStatus;
import com.library.enums.StudentType;
import com.library.exception.BusinessException;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.LibraryCardMapper;
import com.library.mapper.StudentMapper;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements BorrowService {

    private static final int BORROW_DAYS = 30;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("0.1");

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private StudentMapper studentMapper;

    private static final BigDecimal MAX_FINE_ALLOWED = new BigDecimal("50.00");

    @Override
    public String canBorrow(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            return "借书证不存在";
        }
        if (!"正常".equals(card.getCardStatus())) {
            return "借书证状态异常：" + card.getCardStatus();
        }
        
        Student student = studentMapper.selectBySno(card.getSno());
        if (student == null) {
            return "学生不存在";
        }
        
        BigDecimal unpaidFine = borrowRecordMapper.sumUnpaidFineBySno(card.getSno());
        if (unpaidFine != null && unpaidFine.compareTo(MAX_FINE_ALLOWED) > 0) {
            return "累计超期罚款（¥" + unpaidFine + "）超过50元，暂时无法借书";
        }
        
        int borrowedCount = borrowRecordMapper.countBySno(card.getSno());
        int maxCount = getMaxBorrowCount(student.getType());
        
        if (borrowedCount >= maxCount) {
            return "已达到最大借书数量限制（" + maxCount + "本）";
        }
        
        return null;
    }

    @Override
    public int getAvailableCount(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            return 0;
        }
        
        Student student = studentMapper.selectBySno(card.getSno());
        if (student == null) {
            return 0;
        }
        
        int borrowedCount = borrowRecordMapper.countBySno(card.getSno());
        int maxCount = getMaxBorrowCount(student.getType());
        
        return Math.max(0, maxCount - borrowedCount);
    }

    @Override
    public LibraryCard getCardInfo(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        return card;
    }

    @Override
    @Transactional
    public void borrowBook(String cardNo, String barCode) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        if (!"正常".equals(card.getCardStatus())) {
            throw new BusinessException("借书证状态异常");
        }
        
        Student student = studentMapper.selectBySno(card.getSno());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        int borrowedCount = borrowRecordMapper.countBySno(card.getSno());
        int maxCount = getMaxBorrowCount(student.getType());
        if (borrowedCount >= maxCount) {
            throw new BusinessException("已达到最大借书数量限制（" + maxCount + "本）");
        }
        
        BookCopy bookCopy = bookCopyMapper.selectByBarCode(barCode);
        if (bookCopy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new BusinessException("图书不可借");
        }
        
        LocalDate today = LocalDate.now();
        LocalDate retDate = today.plusDays(BORROW_DAYS);
        
        BorrowRecord record = new BorrowRecord();
        record.setSno(card.getSno());
        record.setBarCode(barCode);
        record.setBorDate(today);
        record.setRetDate(retDate);
        record.setRetStatus(RetStatus.UNRETURNED);
        record.setOvdDays(0);
        record.setFineMoney(BigDecimal.ZERO);

        borrowRecordMapper.insert(record);

        bookCopy.setOldStatus(bookCopy.getStatus().getCode());
        bookCopy.setStatus(BookCopyStatus.BORROWED);
        bookCopyMapper.updateByBarCode(bookCopy);

        card.setTimes((card.getTimes() != null ? card.getTimes() : 0) + 1);
        libraryCardMapper.updateByCardNo(card);
    }

    @Override
    @Transactional
    public void borrowBooks(String cardNo, List<String> barCodes) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        if (!"正常".equals(card.getCardStatus())) {
            throw new BusinessException("借书证状态异常");
        }
        
        Student student = studentMapper.selectBySno(card.getSno());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        int borrowedCount = borrowRecordMapper.countBySno(card.getSno());
        int maxCount = getMaxBorrowCount(student.getType());
        if (borrowedCount + barCodes.size() > maxCount) {
            throw new BusinessException("借书数量超过限制（剩余可借：" + (maxCount - borrowedCount) + "本）");
        }
        
        LocalDate today = LocalDate.now();
        LocalDate retDate = today.plusDays(BORROW_DAYS);
        
        for (String barCode : barCodes) {
            BookCopy bookCopy = bookCopyMapper.selectByBarCode(barCode);
            if (bookCopy == null) {
                throw new BusinessException("图书副本不存在：" + barCode);
            }
            if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
                throw new BusinessException("图书不可借：" + barCode);
            }
            
            BorrowRecord record = new BorrowRecord();
            record.setSno(card.getSno());
            record.setBarCode(barCode);
            record.setBorDate(today);
            record.setRetDate(retDate);
            record.setRetStatus(RetStatus.UNRETURNED);
            record.setOvdDays(0);
            record.setFineMoney(BigDecimal.ZERO);

            borrowRecordMapper.insert(record);

            bookCopy.setOldStatus(bookCopy.getStatus().getCode());
            bookCopy.setStatus(BookCopyStatus.BORROWED);
            bookCopyMapper.updateByBarCode(bookCopy);
        }

        card.setTimes((card.getTimes() != null ? card.getTimes() : 0) + barCodes.size());
        libraryCardMapper.updateByCardNo(card);
    }

    @Override
    @Transactional
    public Map<String, Object> returnBook(String barCode) {
        List<BorrowRecord> records = borrowRecordMapper.selectByBarCodeAndUnreturned(barCode);
        if (records.isEmpty()) {
            throw new BusinessException("未找到该图书的未归还借阅记录");
        }
        
        BorrowRecord record = records.get(0);
        BookCopy bookCopy = bookCopyMapper.selectByBarCode(barCode);
        if (bookCopy == null) {
            throw new BusinessException("图书副本不存在");
        }
        
        LocalDate today = LocalDate.now();
        LocalDate retDate = record.getRetDate();
        
        int ovdDays = 0;
        BigDecimal fineMoney = BigDecimal.ZERO;
        
        if (today.isAfter(retDate)) {
            ovdDays = (int) java.time.temporal.ChronoUnit.DAYS.between(retDate, today);
            fineMoney = FINE_PER_DAY.multiply(new BigDecimal(ovdDays));
        }
        
        record.setRealRetDate(today);
        record.setRetStatus(ovdDays > 0 ? RetStatus.OVERDUE : RetStatus.ONTIME);
        record.setOvdDays(ovdDays);
        record.setFineMoney(fineMoney);
        record.setFineStatus(fineMoney.compareTo(BigDecimal.ZERO) > 0 ? FineStatus.UNPAID : null);
        
        borrowRecordMapper.updateBySerNum(record);
        
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyMapper.updateByBarCode(bookCopy);
        
        LibraryCard card = libraryCardMapper.selectBySno(record.getSno());
        if (card != null && card.getTimes() > 0) {
            card.setTimes(card.getTimes() - 1);
            libraryCardMapper.updateByCardNo(card);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("ovdDays", ovdDays);
        result.put("fineMoney", fineMoney);
        return result;
    }

    @Override
    public BorrowRecord checkBorrowStatus(String barCode) {
        List<BorrowRecord> records = borrowRecordMapper.selectByBarCode(barCode);
        if (records.isEmpty()) {
            throw new BusinessException("未找到借阅记录");
        }
        return records.get(0);
    }

    @Override
    public IPage<BorrowRecordResponse> getBorrowRecordsBySno(String sno, int page, int size) {
        List<BorrowRecord> allRecords = borrowRecordMapper.selectAllBySno(sno);
        
        int total = allRecords.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<BorrowRecord> pagedRecords = fromIndex < total 
            ? allRecords.subList(fromIndex, toIndex) 
            : List.of();
        
        List<BorrowRecordResponse> responses = pagedRecords.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        Page<BorrowRecordResponse> result = new Page<>(page, size);
        result.setRecords(responses);
        result.setTotal(total);
        
        return result;
    }

    @Override
    public List<BorrowRecord> getCurrentBorrowsBySno(String sno) {
        return borrowRecordMapper.selectUnreturnedBySno(sno);
    }

    @Override
    public BorrowRecord getUnreturnedByBarCode(String barCode) {
        List<BorrowRecord> records = borrowRecordMapper.selectByBarCodeAndUnreturned(barCode);
        if (records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }

    private int getMaxBorrowCount(String studentType) {
        if (studentType == null) {
            return 5;
        }
        for (StudentType type : StudentType.values()) {
            if (type.getType().equals(studentType)) {
                return type.getMaxBorrowCount();
            }
        }
        return 5;
    }

    private BorrowRecordResponse convertToResponse(BorrowRecord record) {
        BorrowRecordResponse response = new BorrowRecordResponse();
        response.setSerNum(record.getSerNum());
        response.setSno(record.getSno());
        response.setBarCode(record.getBarCode());
        response.setBorDate(record.getBorDate());
        response.setRetDate(record.getRetDate());
        response.setRealRetDate(record.getRealRetDate());
        response.setOvdDays(record.getOvdDays());
        response.setFineMoney(record.getFineMoney());
        
        if (record.getRetStatus() != null) {
            response.setRetStatus(record.getRetStatus().getStatus());
        }
        if (record.getFineStatus() != null) {
            response.setFineStatus(record.getFineStatus().getStatus());
        }
        
        LibraryCard card = libraryCardMapper.selectBySno(record.getSno());
        if (card != null) {
            response.setStudentName(card.getSname());
        }
        
        BookCopy bookCopy = bookCopyMapper.selectByBarCode(record.getBarCode());
        if (bookCopy != null) {
            response.setPlace(bookCopy.getPlace());
            Book book = bookMapper.selectByIsbn(bookCopy.getISBN());
            if (book != null) {
                response.setBookTitle(book.getBname());
                response.setAuthor(book.getAuthor());
                response.setPublisher(book.getPublisher());
            }
        }
        
        return response;
    }
}
