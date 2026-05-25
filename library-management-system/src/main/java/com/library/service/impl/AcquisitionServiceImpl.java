
package com.library.service.impl;

import com.library.dto.request.BookCreateRequest;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.enums.BookCopyStatus;
import com.library.exception.BusinessException;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.service.AcquisitionService;
import com.library.util.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AcquisitionServiceImpl implements AcquisitionService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Override
    @Transactional
    public Book importNewBook(BookCreateRequest request) {
        Book existingBook = bookMapper.selectByIsbn(request.getIsbn());
        Book book;
        if (existingBook != null) {
            book = existingBook;
        } else {
            book = new Book();
            book.setIsbn(request.getIsbn());
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setPublisher(request.getPublisher());
            book.setSummary(request.getSummary());
            book.setCreateTime(LocalDateTime.now());
            bookMapper.insert(book);
        }
        if (request.getCopyCount() != null && request.getCopyCount() > 0) {
            List<BookCopy> existingCopies = bookCopyMapper.selectByBookId(book.getId());
            int startSequence = existingCopies.size() + 1;
            for (int i = 0; i < request.getCopyCount(); i++) {
                BookCopy copy = new BookCopy();
                copy.setBookId(book.getId());
                copy.setBarcode(BarcodeGenerator.generateBarcode(request.getIsbn(), startSequence + i));
                copy.setLocation(request.getLocation());
                copy.setStatus(BookCopyStatus.AVAILABLE.name());
                copy.setCreateTime(LocalDateTime.now());
                bookCopyMapper.insert(copy);
            }
        }
        return book;
    }

    @Override
    @Transactional
    public void discardBook(String barcode) {
        BookCopy bookCopy = bookCopyMapper.selectByBarcode(barcode);
        if (bookCopy == null) {
            throw new BusinessException("图书副本不存在");
        }
        bookCopy.setStatus(BookCopyStatus.DAMAGED.name());
        bookCopyMapper.update(bookCopy);
    }

    @Override
    @Transactional
    public void withdrawBook(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        bookCopyMapper.updateStatusByBookId(bookId, BookCopyStatus.WITHDRAWN.name());
    }
}
