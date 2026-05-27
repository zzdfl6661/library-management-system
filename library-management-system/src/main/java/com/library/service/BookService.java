
package com.library.service;

import com.library.dto.request.BookCreateRequest;
import com.library.dto.response.BookDetailResponse;
import com.library.entity.Book;

import java.util.List;
import java.util.Map;

public interface BookService {
    BookDetailResponse getBookDetail(Long id);
    List<Map<String, Object>> searchBooksWithStats(String keyword);
    Book createBook(BookCreateRequest request);
    Book getByIsbn(String isbn);
    List<Book> getAllBooks();
}
