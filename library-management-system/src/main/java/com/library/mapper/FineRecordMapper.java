
package com.library.mapper;

import com.library.entity.FineRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface FineRecordMapper {
    FineRecord selectById(@Param("id") Long id);
    List<FineRecord> selectByStudentId(@Param("studentId") Long studentId);
    List<FineRecord> selectUnpaidByStudentId(@Param("studentId") Long studentId);
    BigDecimal selectUnpaidTotalByStudentId(@Param("studentId") Long studentId);
    int insert(FineRecord record);
    int update(FineRecord record);
    int updatePaidByStudentId(@Param("studentId") Long studentId, @Param("amount") BigDecimal amount);
    FineRecord selectByBorrowRecordId(@Param("borrowRecordId") Long borrowRecordId);
    int deleteById(@Param("id") Long id);
}
