
package com.library.mapper;

import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    Book selectById(@Param("id") Long id);
    Book selectByIsbn(@Param("isbn") String isbn);
    List<Book> search(@Param("title") String title, @Param("author") String author, 
                      @Param("isbn") String isbn, @Param("publisher") String publisher);
    List<Book> selectAll();
    int insert(Book book);
    int update(Book book);
    int deleteById(@Param("id") Long id);
}
