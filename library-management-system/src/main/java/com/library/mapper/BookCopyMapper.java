package com.library.mapper;

import com.library.entity.BookCopy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookCopyMapper {
    BookCopy selectByBarCode(@Param("barCode") String barCode);

    List<BookCopy> selectByIsbn(@Param("ISBN") String ISBN);

    List<BookCopy> selectByIsbnAndStatus(@Param("ISBN") String ISBN, @Param("status") Integer status);

    List<BookCopy> selectAvailableByIsbn(@Param("ISBN") String ISBN);

    int insert(BookCopy copy);

    int updateByBarCode(BookCopy copy);
}