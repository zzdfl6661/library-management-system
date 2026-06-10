package com.library.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookMapper {
    Book selectByIsbn(@Param("ISBN") String ISBN);

    Book selectById(@Param("id") Long id);

    List<Book> selectByKeyword(@Param("keyword") String keyword);

    List<Book> selectAll();

    int insert(Book book);

    int updateById(Book book);

    List<Book> selectOnShelf();

    IPage<Book> selectOnShelfPage(Page<Book> page);

    @Select("SELECT * FROM book WHERE bookStatus = 1 ORDER BY id DESC LIMIT #{limit}")
    List<Book> selectNewBooks(@Param("limit") int limit);
}