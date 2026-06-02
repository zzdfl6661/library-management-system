
package com.library.service.impl;

import com.library.dto.request.BorrowRequest;
import com.library.dto.request.ReturnRequest;
import com.library.dto.response.BorrowRecordResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.FineRecord;
import com.library.entity.LibraryCard;
import com.library.entity.Student;
import com.library.enums.BookCopyStatus;
import com.library.enums.CardStatus;
import com.library.exception.BusinessException;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.FineRecordMapper;
import com.library.mapper.LibraryCardMapper;
import com.library.mapper.StudentMapper;
import com.library.service.BorrowService;
import com.library.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private static final int BORROW_DAYS = 30;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("0.1");

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private FineRecordMapper fineRecordMapper;

    @Override
    @Transactional
    public void borrowBooks(BorrowRequest request) {
        LibraryCard card = libraryCardMapper.selectByCardNo(request.getCardNo());
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        if (!CardStatus.NORMAL.name().equals(card.getStatus())) {
            throw new BusinessException("借书证状态异常");
        }
        BigDecimal totalFine = fineRecordMapper.selectUnpaidTotalByStudentId(card.getStudentId());
        if (totalFine.compareTo(new BigDecimal("50")) > 0) {
            throw new BusinessException("累计罚款超过50元，无法借书");
        }
        Student student = studentMapper.selectById(card.getStudentId());
        int borrowedCount = borrowRecordMapper.selectCountByCardId(card.getId());
        if (borrowedCount >= student.getMaxBorrowCount()) {
            throw new BusinessException("已达到最大借书数量限制");
        }
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(BORROW_DAYS);
        for (String barcode : request.getBarcodes()) {
            BookCopy bookCopy = bookCopyMapper.selectByBarcode(barcode);
            if (bookCopy == null) {
                throw new BusinessException("图书副本不存在: " + barcode);
            }
            if (!BookCopyStatus.AVAILABLE.name().equals(bookCopy.getStatus())) {
                throw new BusinessException("图书不可借: " + barcode);
            }
            BorrowRecord record = new BorrowRecord();
            record.setCardId(card.getId());
            record.setCopyId(bookCopy.getId());
            record.setBorrowDate(today);
            record.setDueDate(dueDate);
            record.setIsOverdue(0);
            record.setCreateTime(LocalDateTime.now());
            borrowRecordMapper.insert(record);
            bookCopy.setStatus(BookCopyStatus.BORROWED.name());
            bookCopyMapper.update(bookCopy);
        }
    }

    @Override
    @Transactional
    public void returnBook(ReturnRequest request) {
        BookCopy bookCopy = bookCopyMapper.selectByBarcode(request.getBarcode());
        if (bookCopy == null) {
            throw new BusinessException("图书副本不存在");
        }
        List<BorrowRecord> records = borrowRecordMapper.selectByCopyId(bookCopy.getId());
        if (records.isEmpty()) {
            throw new BusinessException("未找到借阅记录");
        }
        BorrowRecord record = records.get(0);
        LocalDate today = LocalDate.now();
        LocalDate dueDate = record.getDueDate();
        if (today.isAfter(dueDate)) {
            long days = DateUtil.daysBetween(dueDate, today);
            BigDecimal amount = FINE_PER_DAY.multiply(new BigDecimal(days));
            LibraryCard card = libraryCardMapper.selectById(record.getCardId());
            FineRecord fine = new FineRecord();
            fine.setStudentId(card.getStudentId());
            fine.setBorrowRecordId(record.getId());
            fine.setAmount(amount);
            fine.setDays((int) days);
            fine.setIsPaid(0);
            fine.setCreateTime(LocalDateTime.now());
            fineRecordMapper.insert(fine);
            record.setIsOverdue(1);
        }
        record.setReturnDate(today);
        borrowRecordMapper.update(record);
        bookCopy.setStatus(BookCopyStatus.AVAILABLE.name());
        bookCopyMapper.update(bookCopy);
    }

    @Override
    public List<BorrowRecordResponse> getBorrowRecordsByCardNo(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        return convertToBorrowRecordResponseList(borrowRecordMapper.selectByCardId(card.getId()));
    }

    @Override
    public List<BorrowRecordResponse> getBorrowRecordsByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return convertToBorrowRecordResponseList(borrowRecordMapper.selectByStudentId(student.getId()));
    }

    private List<BorrowRecordResponse> convertToBorrowRecordResponseList(List<BorrowRecord> records) {
        return records.stream().map(record -> {
            BorrowRecordResponse response = new BorrowRecordResponse();
            response.setId(record.getId());
            response.setCardNo(libraryCardMapper.selectById(record.getCardId()).getCardNo());
            response.setBorrowDate(record.getBorrowDate());
            response.setDueDate(record.getDueDate());
            response.setReturnDate(record.getReturnDate());
            response.setIsOverdue(record.getIsOverdue());
            response.setCreateTime(record.getCreateTime());
            BookCopy copy = bookCopyMapper.selectById(record.getCopyId());
            if (copy != null) {
                response.setBarcode(copy.getBarcode());
                Book book = bookMapper.selectById(copy.getBookId());
                if (book != null) {
                    response.setBookTitle(book.getTitle());
                }
            }
            return response;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean canBorrow(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null || !CardStatus.NORMAL.name().equals(card.getStatus())) {
            return false;
        }
        BigDecimal totalFine = fineRecordMapper.selectUnpaidTotalByStudentId(card.getStudentId());
        if (totalFine.compareTo(new BigDecimal("50")) > 0) {
            return false;
        }
        Student student = studentMapper.selectById(card.getStudentId());
        int borrowedCount = borrowRecordMapper.selectCountByCardId(card.getId());
        return borrowedCount < student.getMaxBorrowCount();
    }

    @Override
    @Transactional
    public void borrowBookByStudent(Long bookId, Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        List<LibraryCard> cards = libraryCardMapper.selectByStudentId(studentId);
        LibraryCard card = cards.isEmpty() ? null : cards.get(0);
        if (card == null) {
            throw new BusinessException("未办理借书证");
        }
        if (!CardStatus.NORMAL.name().equals(card.getStatus())) {
            throw new BusinessException("借书证状态异常");
        }
        BigDecimal totalFine = fineRecordMapper.selectUnpaidTotalByStudentId(studentId);
        if (totalFine.compareTo(new BigDecimal("50")) > 0) {
            throw new BusinessException("累计罚款超过50元，无法借书");
        }
        int borrowedCount = borrowRecordMapper.selectCountByCardId(card.getId());
        if (borrowedCount >= student.getMaxBorrowCount()) {
            throw new BusinessException("已达到最大借书数量限制");
        }
        List<BookCopy> copies = bookCopyMapper.selectByBookId(bookId);
        BookCopy availableCopy = copies.stream()
                .filter(c -> BookCopyStatus.AVAILABLE.name().equals(c.getStatus()))
                .findFirst()
                .orElse(null);
        if (availableCopy == null) {
            throw new BusinessException("该书暂无可借副本");
        }
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(BORROW_DAYS);
        BorrowRecord record = new BorrowRecord();
        record.setCardId(card.getId());
        record.setCopyId(availableCopy.getId());
        record.setBorrowDate(today);
        record.setDueDate(dueDate);
        record.setIsOverdue(0);
        record.setCreateTime(LocalDateTime.now());
        borrowRecordMapper.insert(record);
        availableCopy.setStatus(BookCopyStatus.BORROWED.name());
        bookCopyMapper.update(availableCopy);
    }

    @Override
    @Transactional
    public void borrowBookByCardNo(Long bookId, String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        if (!CardStatus.NORMAL.name().equals(card.getStatus())) {
            throw new BusinessException("借书证状态异常");
        }
        Long studentId = card.getStudentId();
        BigDecimal totalFine = fineRecordMapper.selectUnpaidTotalByStudentId(studentId);
        if (totalFine.compareTo(new BigDecimal("50")) > 0) {
            throw new BusinessException("累计罚款超过50元，无法借书");
        }
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        int borrowedCount = borrowRecordMapper.selectCountByCardId(card.getId());
        if (borrowedCount >= student.getMaxBorrowCount()) {
            throw new BusinessException("已达到最大借书数量限制");
        }
        List<BookCopy> copies = bookCopyMapper.selectByBookId(bookId);
        BookCopy availableCopy = copies.stream()
                .filter(c -> BookCopyStatus.AVAILABLE.name().equals(c.getStatus()))
                .findFirst()
                .orElse(null);
        if (availableCopy == null) {
            throw new BusinessException("该书暂无可借副本");
        }
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(BORROW_DAYS);
        BorrowRecord record = new BorrowRecord();
        record.setCardId(card.getId());
        record.setCopyId(availableCopy.getId());
        record.setBorrowDate(today);
        record.setDueDate(dueDate);
        record.setIsOverdue(0);
        record.setCreateTime(LocalDateTime.now());
        borrowRecordMapper.insert(record);
        availableCopy.setStatus(BookCopyStatus.BORROWED.name());
        bookCopyMapper.update(availableCopy);
    }

    @Override
    public BorrowRecordResponse checkBorrowStatus(String barcode) {
        BookCopy bookCopy = bookCopyMapper.selectByBarcode(barcode);
        if (bookCopy == null) {
            throw new BusinessException("图书副本不存在");
        }
        List<BorrowRecord> records = borrowRecordMapper.selectByCopyId(bookCopy.getId());
        if (records.isEmpty()) {
            throw new BusinessException("未找到借阅记录");
        }
        BorrowRecord record = records.get(0);
        return convertToBorrowRecordResponse(record);
    }

    private BorrowRecordResponse convertToBorrowRecordResponse(BorrowRecord record) {
        BorrowRecordResponse response = new BorrowRecordResponse();
        response.setId(record.getId());
        response.setCardNo(libraryCardMapper.selectById(record.getCardId()).getCardNo());
        response.setBorrowDate(record.getBorrowDate());
        response.setDueDate(record.getDueDate());
        response.setReturnDate(record.getReturnDate());
        response.setIsOverdue(record.getIsOverdue());
        response.setCreateTime(record.getCreateTime());
        BookCopy copy = bookCopyMapper.selectById(record.getCopyId());
        if (copy != null) {
            response.setBarcode(copy.getBarcode());
            Book book = bookMapper.selectById(copy.getBookId());
            if (book != null) {
                response.setBookTitle(book.getTitle());
            }
        }
        return response;
    }
}
