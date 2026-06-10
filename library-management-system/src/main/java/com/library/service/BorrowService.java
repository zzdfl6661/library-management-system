package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.dto.response.BorrowRecordResponse;
import com.library.entity.BorrowRecord;
import com.library.entity.LibraryCard;

import java.util.List;
import java.util.Map;

public interface BorrowService {

    /**
     * 验证借书证是否可借
     * @param cardNo 借书证号
     * @return null=可借，否则返回不可借原因
     */
    String canBorrow(String cardNo);

    /**
     * 获取借书证可借数量
     * @param cardNo 借书证号
     * @return 剩余可借数量
     */
    int getAvailableCount(String cardNo);

    /**
     * 获取借书证信息
     * @param cardNo 借书证号
     * @return 借书证信息
     */
    LibraryCard getCardInfo(String cardNo);

    /**
     * 借书（单本）
     * @param cardNo 借书证号
     * @param barCode 图书条码
     */
    void borrowBook(String cardNo, String barCode);

    /**
     * 借书（批量）
     * @param cardNo 借书证号
     * @param barCodes 图书条码列表
     */
    void borrowBooks(String cardNo, List<String> barCodes);

    /**
     * 还书
     * @param barCode 图书条码
     * @return 包含超期天数和罚款金额的Map
     */
    Map<String, Object> returnBook(String barCode);

    /**
     * 按条码查询借阅状态
     * @param barCode 图书条码
     * @return 借阅记录
     */
    BorrowRecord checkBorrowStatus(String barCode);

    /**
     * 按学号查询借阅记录（分页）
     * @param sno 学号
     * @param page 页码
     * @param size 每页大小
     * @return 分页借阅记录
     */
    IPage<BorrowRecordResponse> getBorrowRecordsBySno(String sno, int page, int size);

    /**
     * 按学号查询当前借阅（未归还）
     * @param sno 学号
     * @return 未归还的借阅记录列表
     */
    List<BorrowRecord> getCurrentBorrowsBySno(String sno);

    /**
     * 按条码查询未归还的借阅记录
     * @param barCode 图书条码
     * @return 未归还的借阅记录
     */
    BorrowRecord getUnreturnedByBarCode(String barCode);
}
