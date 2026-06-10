
package com.library.service;

import com.library.entity.BookCopy;

import java.util.List;
import java.util.Map;

public interface BookCopyService {
    // 按条码查询副本
    BookCopy getCopyByBarCode(String barCode);

    // 按ISBN查询某书的所有副本
    List<BookCopy> getCopiesByIsbn(String isbn);

    // 查询可借副本
    BookCopy getAvailableCopyByIsbn(String isbn);

    // 新增副本
    void createCopy(String isbn, String barCode, String place);

    // 编辑副本信息
    void updateCopy(BookCopy copy);

    // 注销副本（status=2）
    void cancelCopy(String barCode);

    // 恢复副本（将status改回oldStatus）
    void restoreCopy(String barCode);

    // 借出副本（status=0）
    void borrowCopy(String barCode);

    // 归还副本（status=1）
    void returnCopy(String barCode);

    // 获取某书的副本统计（总数/可借数）
    Map<String, Integer> getCopyStats(String isbn);
}
