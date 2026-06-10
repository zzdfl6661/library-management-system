package com.library.mapper;

import com.library.entity.CardRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardRecordMapper {
    List<CardRecord> selectBySno(@Param("sno") String sno);

    List<CardRecord> selectByCardNo(@Param("cardNo") String cardNo);

    int insert(CardRecord record);
}