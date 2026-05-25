
package com.library.mapper;

import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BorrowRecordMapper {
    BorrowRecord selectById(@Param("id") Long id);
    List<BorrowRecord> selectByCardId(@Param("cardId") Long cardId);
    List<BorrowRecord> selectByCopyId(@Param("copyId") Long copyId);
    List<BorrowRecord> selectByStudentId(@Param("studentId") Long studentId);
    List<BorrowRecord> selectUnreturnedByCardId(@Param("cardId") Long cardId);
    int selectCountByCardId(@Param("cardId") Long cardId);
    int selectCountByStudentIdAndMonth(@Param("studentId") Long studentId, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    int insert(BorrowRecord record);
    int update(BorrowRecord record);
    int deleteById(@Param("id") Long id);
}
