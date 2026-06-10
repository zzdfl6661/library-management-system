
package com.library.service;

import com.library.dto.response.BookDetailResponse;
import com.library.entity.Book;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface BookService {
    // 按ISBN查询图书
    Book getBookByIsbn(String isbn);

    // 按ID查询图书
    Book getBookById(Long id);

    // 搜索图书（按关键词模糊搜索书名/作者/ISBN）
    List<Book> searchBooks(String keyword);

    // 获取所有上架图书
    List<Book> getOnShelfBooks();

    // 分页获取上架图书
    IPage<Book> getOnShelfBooksPage(int page, int size);

    // 新增图书
    void createBook(Book book);

    // 编辑图书
    void updateBook(Book book);

    // 下架图书（bookStatus=0）
    void offshelfBook(Long id);

    // 恢复上架（bookStatus=1）
    void onshelfBook(Long id);

    // 删除图书（逻辑删除，bookStatus=2）
    void deleteBook(Long id);

    // 获取图书详情（含副本信息）
    BookDetailResponse getBookDetail(Long id);
}
