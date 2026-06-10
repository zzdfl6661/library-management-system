package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BookDetailResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.service.BookCopyService;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCopyService bookCopyService;

    @GetMapping("/search")
    public ApiResponse<List<Book>> searchBooks(@RequestParam(required = false) String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return ApiResponse.success(books);
    }

    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getBookDetail(@PathVariable Long id) {
        BookDetailResponse detail = bookService.getBookDetail(id);
        if (detail == null) {
            return ApiResponse.error(404, "图书不存在");
        }
        return ApiResponse.success(detail);
    }

    @GetMapping("/onshelf")
    public ApiResponse<IPage<Book>> getOnShelfBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Book> books = bookService.getOnShelfBooksPage(page, size);
        return ApiResponse.success(books);
    }

    @PostMapping
    public ApiResponse<Void> createBook(@RequestBody Book book) {
        bookService.createBook(book);
        return ApiResponse.success("图书创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateBook(@RequestBody Book book) {
        bookService.updateBook(book);
        return ApiResponse.success("图书更新成功", null);
    }

    @PutMapping("/{id}/offshelf")
    public ApiResponse<Void> offshelfBook(@PathVariable Long id) {
        bookService.offshelfBook(id);
        return ApiResponse.success("图书下架成功", null);
    }

    @PutMapping("/{id}/onshelf")
    public ApiResponse<Void> onshelfBook(@PathVariable Long id) {
        bookService.onshelfBook(id);
        return ApiResponse.success("图书上架成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.success("图书删除成功", null);
    }

    @GetMapping("/{isbn}/copies")
    public ApiResponse<List<BookCopy>> getCopies(@PathVariable String isbn) {
        List<BookCopy> copies = bookCopyService.getCopiesByIsbn(isbn);
        return ApiResponse.success(copies);
    }

    @PostMapping("/{isbn}/copies")
    public ApiResponse<Void> createCopy(
            @PathVariable String isbn,
            @RequestBody Map<String, String> request) {
        String barCode = request.get("barCode");
        String place = request.get("place");
        if (barCode == null || place == null) {
            return ApiResponse.error(400, "参数不完整");
        }
        bookCopyService.createCopy(isbn, barCode, place);
        return ApiResponse.success("副本创建成功", null);
    }

    @PutMapping("/{isbn}/copies/{barCode}")
    public ApiResponse<Void> updateCopy(
            @PathVariable String isbn,
            @PathVariable String barCode,
            @RequestBody BookCopy copy) {
        copy.setISBN(isbn);
        copy.setBarCode(barCode);
        bookCopyService.updateCopy(copy);
        return ApiResponse.success("副本更新成功", null);
    }

    @PutMapping("/{isbn}/copies/{barCode}/cancel")
    public ApiResponse<Void> cancelCopy(
            @PathVariable String isbn,
            @PathVariable String barCode) {
        bookCopyService.cancelCopy(barCode);
        return ApiResponse.success("副本注销成功", null);
    }
}