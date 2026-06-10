
package com.library.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.response.BookDetailResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.enums.BookCopyStatus;
import com.library.enums.BookStatus;
import com.library.exception.BusinessException;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Override
    public Book getBookByIsbn(String isbn) {
        Book book = bookMapper.selectByIsbn(isbn);
        return book;
    }

    @Override
    public Book getBookById(Long id) {
        Book book = bookMapper.selectById(id.intValue());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        return book;
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookMapper.selectByKeyword(keyword);
    }

    @Override
    public List<Book> getOnShelfBooks() {
        return bookMapper.selectOnShelf();
    }

    @Override
    public IPage<Book> getOnShelfBooksPage(int page, int size) {
        Page<Book> pageParam = new Page<>(page, size);
        return bookMapper.selectOnShelfPage(pageParam);
    }

    @Override
    @Transactional
    public void createBook(Book book) {
        Book existingBook = bookMapper.selectByIsbn(book.getISBN());
        if (existingBook != null) {
            throw new BusinessException("图书已存在");
        }
        book.setBookStatus(BookStatus.ONSHELF);
        bookMapper.insert(book);
    }

    @Override
    @Transactional
    public void updateBook(Book book) {
        Book existingBook = bookMapper.selectById(book.getId().intValue());
        if (existingBook == null) {
            throw new BusinessException("图书不存在");
        }
        if (existingBook.getBookStatus() == BookStatus.DELETED) {
            throw new BusinessException("图书已删除，无法编辑");
        }
        bookMapper.updateById(book);
    }

    @Override
    @Transactional
    public void offshelfBook(Long id) {
        Book book = bookMapper.selectById(id.intValue());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getBookStatus() == BookStatus.DELETED) {
            throw new BusinessException("图书已删除，无法下架");
        }
        book.setBookStatus(BookStatus.OFFSHELF);
        bookMapper.updateById(book);
    }

    @Override
    @Transactional
    public void onshelfBook(Long id) {
        Book book = bookMapper.selectById(id.intValue());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getBookStatus() == BookStatus.DELETED) {
            throw new BusinessException("图书已删除，无法上架");
        }
        book.setBookStatus(BookStatus.ONSHELF);
        bookMapper.updateById(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookMapper.selectById(id.intValue());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        book.setBookStatus(BookStatus.DELETED);
        bookMapper.updateById(book);
    }

    @Override
    public BookDetailResponse getBookDetail(Long id) {
        Book book = bookMapper.selectById(id.intValue());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        List<BookCopy> copies = bookCopyMapper.selectByIsbn(book.getISBN());
        
        BookDetailResponse response = new BookDetailResponse();
        response.setId(Long.valueOf(book.getId()));
        response.setIsbn(book.getISBN());
        response.setTitle(book.getBname());
        response.setAuthor(book.getAuthor());
        response.setPublisher(book.getPublisher());
        response.setSummary(book.getIntroduction());
        response.setCopyInfoList(copies.stream()
                .filter(c -> c.getStatus() != BookCopyStatus.CANCELLED)
                .map(copy -> {
                    BookDetailResponse.CopyInfo info = new BookDetailResponse.CopyInfo();
                    info.setId(Long.valueOf(copy.getISBN().hashCode()));
                    info.setBarcode(copy.getBarCode());
                    info.setLocation(copy.getPlace());
                    info.setStatus(copy.getStatus().name());
                    return info;
                }).collect(Collectors.toList()));
        return response;
    }
}
