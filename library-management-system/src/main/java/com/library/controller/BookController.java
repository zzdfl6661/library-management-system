
package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.dto.response.BookDetailResponse;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getBookDetail(@PathVariable Long id) {
        BookDetailResponse response = bookService.getBookDetail(id);
        return ApiResponse.success(response);
    }

    @GetMapping("/search")
    public ApiResponse<List<Map<String, Object>>> searchBooks(
            @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> books = bookService.searchBooksWithStats(keyword);
        return ApiResponse.success(books);
    }

    @GetMapping
    public ApiResponse<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ApiResponse.success(books);
    }
}
