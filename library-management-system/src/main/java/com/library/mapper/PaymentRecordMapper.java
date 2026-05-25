
package com.library.mapper;

import com.library.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentRecordMapper {
    PaymentRecord selectById(@Param("id") Long id);
    List<PaymentRecord> selectByStudentId(@Param("studentId") Long studentId);
    int insert(PaymentRecord record);
    int update(PaymentRecord record);
    int deleteById(@Param("id") Long id);
}
