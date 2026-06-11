package com.library.mapper;

import com.library.dto.response.RankingResponse;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BorrowRecordMapper {
    BorrowRecord selectBySerNum(@Param("serNum") Integer serNum);

    List<BorrowRecord> selectBySno(@Param("sno") String sno);

    List<BorrowRecord> selectUnreturnedBySno(@Param("sno") String sno);

    List<BorrowRecord> selectByBarCode(@Param("barCode") String barCode);

    List<BorrowRecord> selectByBarCodeAndUnreturned(@Param("barCode") String barCode);

    int insert(BorrowRecord record);

    int updateBySerNum(BorrowRecord record);

    int countBySno(@Param("sno") String sno);

    List<BorrowRecord> selectUnpaidBySno(@Param("sno") String sno);

    List<BorrowRecord> selectAllBySno(@Param("sno") String sno);

    @Select("SELECT b.id, b.bname as name, COUNT(*) as count " +
            "FROM borrowrec br " +
            "JOIN bookcopy bc ON br.barCode = bc.barCode " +
            "JOIN book b ON bc.ISBN = b.ISBN " +
            "WHERE b.bookStatus = 1 " +
            "GROUP BY b.id, b.bname, b.author " +
            "ORDER BY count DESC " +
            "LIMIT #{limit}")
    List<RankingResponse> selectHotBooks(@Param("limit") int limit);

    @Select("SELECT s.sno as name, COUNT(*) as count " +
            "FROM borrowrec br " +
            "JOIN student s ON br.sno = s.sno " +
            "GROUP BY s.sno, s.username " +
            "ORDER BY count DESC " +
            "LIMIT #{limit}")
    List<RankingResponse> selectTopReaders(@Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM borrowrec WHERE DATE(borDate) = DATE(#{date})")
    int countByBorDate(@Param("date") LocalDate date);

    @Select("SELECT COUNT(*) FROM borrowrec WHERE DATE(retDate) = DATE(#{date})")
    int countByRetDate(@Param("date") LocalDate date);

    @Select("SELECT COALESCE(SUM(fineMoney), 0) FROM borrowrec WHERE sno = #{sno} AND fineStatus = 'UNPAID'")
    java.math.BigDecimal sumUnpaidFineBySno(@Param("sno") String sno);
}