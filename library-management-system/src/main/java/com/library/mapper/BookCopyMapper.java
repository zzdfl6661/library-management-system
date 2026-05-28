
package com.library.mapper;

import com.library.entity.BookCopy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookCopyMapper {
    BookCopy selectById(@Param("id") Long id);
    BookCopy selectByBarcode(@Param("barcode") String barcode);
    List<BookCopy> selectByBookId(@Param("bookId") Long bookId);
    List<BookCopy> selectByStatus(@Param("status") String status);
    int insert(BookCopy bookCopy);
    int update(BookCopy bookCopy);
    int deleteById(@Param("id") Long id);
    int updateStatusByBookId(@Param("bookId") Long bookId, @Param("status") String status);
}
