package com.library.mapper;

import com.library.entity.LibraryCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LibraryCardMapper {
    LibraryCard selectByCardNo(@Param("cardNo") String cardNo);

    LibraryCard selectBySno(@Param("sno") String sno);

    List<LibraryCard> selectByStatus(@Param("status") String status);

    List<LibraryCard> selectAll();

    int insert(LibraryCard card);

    int updateByCardNo(LibraryCard card);

    List<LibraryCard> searchByKeyword(@Param("keyword") String keyword);
}