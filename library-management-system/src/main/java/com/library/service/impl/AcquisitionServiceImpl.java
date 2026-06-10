
package com.library.service.impl;

import com.library.dto.request.BookCreateRequest;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.enums.BookCopyStatus;
import com.library.enums.BookStatus;
import com.library.exception.BusinessException;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.service.AcquisitionService;
import com.library.util.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            book.setISBN(request.getIsbn());
            book.setBname(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setPublisher(request.getPublisher());
            book.setIntroduction(request.getSummary());
            book.setBookStatus(BookStatus.ONSHELF);
            bookMapper.insert(book);
        }
        if (request.getCopyCount() != null && request.getCopyCount() > 0) {
            List<BookCopy> existingCopies = bookCopyMapper.selectByIsbn(request.getIsbn());
            int startSequence = existingCopies.size() + 1;
            for (int i = 0; i < request.getCopyCount(); i++) {
                BookCopy copy = new BookCopy();
                copy.setISBN(request.getIsbn());
                copy.setBarCode(BarcodeGenerator.generateBarcode(request.getIsbn(), startSequence + i));
                copy.setPlace(request.getLocation());
                copy.setStatus(BookCopyStatus.AVAILABLE);
                bookCopyMapper.insert(copy);
            }
        }
        return book;
    }

    @Override
    @Transactional
    public void discardBook(String barcode) {
        BookCopy bookCopy = bookCopyMapper.selectByBarCode(barcode);
        if (bookCopy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (bookCopy.getStatus() == BookCopyStatus.CANCELLED) {
            throw new BusinessException("图书副本已注销");
        }
        bookCopy.setOldStatus(bookCopy.getStatus().getCode());
        bookCopy.setStatus(BookCopyStatus.CANCELLED);
        bookCopyMapper.updateByBarCode(bookCopy);
    }

    @Override
    @Transactional
    public void withdrawBook(Long bookId) {
        Book book = bookMapper.selectById(bookId.intValue());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getBookStatus() == BookStatus.DELETED) {
            throw new BusinessException("图书已删除");
        }
        book.setBookStatus(BookStatus.OFFSHELF);
        bookMapper.updateById(book);
    }
}
