package com.library.mapper;

import com.library.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentRecordMapper {
    List<PaymentRecord> selectBySno(@Param("sno") String sno);

    int insert(PaymentRecord record);

    Integer selectMaxSerNum();
}