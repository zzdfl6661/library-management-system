
package com.library.service.impl;

import com.library.dto.request.BookCreateRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.enums.BookCopyStatus;
import com.library.exception.BusinessException;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import com.library.util.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Override
    public BookDetailResponse getBookDetail(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        List<BookCopy> copies = bookCopyMapper.selectByBookId(id);
        BookDetailResponse response = new BookDetailResponse();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setPublisher(book.getPublisher());
        response.setSummary(book.getSummary());
        response.setCopies(copies.stream().map(copy -> {
            BookDetailResponse.CopyInfo info = new BookDetailResponse.CopyInfo();
            info.setId(copy.getId());
            info.setBarcode(copy.getBarcode());
            info.setLocation(copy.getLocation());
            info.setStatus(copy.getStatus());
            return info;
        }).collect(Collectors.toList()));
        return response;
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookMapper.searchByKeyword(keyword);
    }

    @Override
    @Transactional
    public Book createBook(BookCreateRequest request) {
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
    public Book getByIsbn(String isbn) {
        return bookMapper.selectByIsbn(isbn);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookMapper.selectAll();
    }
}
