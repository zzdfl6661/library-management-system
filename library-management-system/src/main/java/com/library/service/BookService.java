
package com.library.service;

import com.library.dto.request.BookCreateRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.entity.Book;

import java.util.List;

public interface BookService {
    BookDetailResponse getBookDetail(Long id);
    List<Book> searchBooks(String title, String author, String isbn, String publisher);
    Book createBook(BookCreateRequest request);
    Book getByIsbn(String isbn);
    List<Book> getAllBooks();
}
