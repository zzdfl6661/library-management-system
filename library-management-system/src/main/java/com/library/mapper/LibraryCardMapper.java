
package com.library.mapper;

import com.library.entity.LibraryCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LibraryCardMapper {
    LibraryCard selectById(@Param("id") Long id);
    LibraryCard selectByCardNo(@Param("cardNo") String cardNo);
    List<LibraryCard> selectByStudentId(@Param("studentId") Long studentId);
    List<LibraryCard> selectAll();
    List<LibraryCard> selectByCardNoLike(@Param("cardNo") String cardNo);
    int insert(LibraryCard card);
    int update(LibraryCard card);
    int deleteById(@Param("id") Long id);
}
