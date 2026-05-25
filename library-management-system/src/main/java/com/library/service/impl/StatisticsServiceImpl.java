
package com.library.service.impl;

import com.library.dto.response.RankingResponse;
import com.library.dto.response.StatisticsResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.LibraryCard;
import com.library.entity.Student;
import com.library.enums.BookCopyStatus;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.LibraryCardMapper;
import com.library.mapper.StudentMapper;
import com.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public StatisticsResponse getDashboardStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        response.setStudentRanking(getStudentBorrowRanking());
        response.setBookRanking(getBookBorrowRanking());
        response.setTodayBorrowCount(getTodayBorrowCount());
        response.setTodayReturnCount(getTodayReturnCount());
        response.setTotalBooks(getTotalBooks());
        response.setAvailableBooks(getAvailableBooks());
        response.setBorrowedBooks(getBorrowedBooks());
        return response;
    }

    @Override
    public List<RankingResponse> getStudentBorrowRanking() {
        List<Student> students = studentMapper.selectAll();
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        Map<String, Integer> countMap = new HashMap<>();
        for (Student student : students) {
            int count = borrowRecordMapper.selectCountByStudentIdAndMonth(student.getId(), startOfMonth, endOfMonth);
            if (count > 0) {
                countMap.put(student.getName(), count);
            }
        }
        return countMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    RankingResponse r = new RankingResponse();
                    r.setName(entry.getKey());
                    r.setCount(entry.getValue());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RankingResponse> getBookBorrowRanking() {
        Map<Long, Integer> bookCountMap = new HashMap<>();
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        List<BorrowRecord> records = new ArrayList<>();
        List<LibraryCard> cards = libraryCardMapper.selectAll();
        for (LibraryCard card : cards) {
            records.addAll(borrowRecordMapper.selectByCardIdAndMonth(card.getId(), startOfMonth, endOfMonth));
        }
        for (BorrowRecord record : records) {
            BookCopy copy = bookCopyMapper.selectById(record.getCopyId());
            if (copy != null) {
                bookCountMap.merge(copy.getBookId(), 1, Integer::sum);
            }
        }
        List<RankingResponse> ranking = new ArrayList<>();
        bookCountMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .forEach(entry -> {
                    RankingResponse r = new RankingResponse();
                    Book book = bookMapper.selectById(entry.getKey());
                    r.setName(book != null ? book.getTitle() : "Book-" + entry.getKey());
                    r.setCount(entry.getValue());
                    ranking.add(r);
                });
        return ranking;
    }

    private Long getTodayBorrowCount() {
        LocalDate today = LocalDate.now();
        return (long) libraryCardMapper.selectAll().stream()
                .flatMap(card -> borrowRecordMapper.selectByCardId(card.getId()).stream())
                .filter(record -> today.equals(record.getBorrowDate()))
                .count();
    }

    private Long getTodayReturnCount() {
        LocalDate today = LocalDate.now();
        return (long) libraryCardMapper.selectAll().stream()
                .flatMap(card -> borrowRecordMapper.selectByCardId(card.getId()).stream())
                .filter(record -> today.equals(record.getReturnDate()))
                .count();
    }

    private Long getTotalBooks() {
        return (long) bookCopyMapper.selectByStatus(BookCopyStatus.AVAILABLE.name()).size()
                + (long) bookCopyMapper.selectByStatus(BookCopyStatus.BORROWED.name()).size();
    }

    private Long getAvailableBooks() {
        return (long) bookCopyMapper.selectByStatus(BookCopyStatus.AVAILABLE.name()).size();
    }

    private Long getBorrowedBooks() {
        return (long) bookCopyMapper.selectByStatus(BookCopyStatus.BORROWED.name()).size();
    }
}
