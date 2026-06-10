
package com.library.service.impl;

import com.library.entity.BookCopy;
import com.library.enums.BookCopyStatus;
import com.library.exception.BusinessException;
import com.library.mapper.BookCopyMapper;
import com.library.service.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookCopyServiceImpl implements BookCopyService {

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Override
    public BookCopy getCopyByBarCode(String barCode) {
        BookCopy copy = bookCopyMapper.selectByBarCode(barCode);
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (copy.getStatus() == BookCopyStatus.CANCELLED) {
            throw new BusinessException("图书副本已注销");
        }
        return copy;
    }

    @Override
    public List<BookCopy> getCopiesByIsbn(String isbn) {
        List<BookCopy> copies = bookCopyMapper.selectByIsbn(isbn);
        return copies.stream()
                .filter(c -> c.getStatus() != BookCopyStatus.CANCELLED)
                .toList();
    }

    @Override
    public BookCopy getAvailableCopyByIsbn(String isbn) {
        List<BookCopy> availableCopies = bookCopyMapper.selectAvailableByIsbn(isbn);
        if (availableCopies == null || availableCopies.isEmpty()) {
            throw new BusinessException("没有可借的图书副本");
        }
        return availableCopies.get(0);
    }

    @Override
    @Transactional
    public void createCopy(String isbn, String barCode, String place) {
        BookCopy existingCopy = bookCopyMapper.selectByBarCode(barCode);
        if (existingCopy != null) {
            throw new BusinessException("图书副本已存在");
        }
        BookCopy copy = new BookCopy();
        copy.setIsbn(isbn);
        copy.setBarCode(barCode);
        copy.setPlace(place);
        copy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyMapper.insert(copy);
    }

    @Override
    @Transactional
    public void updateCopy(BookCopy copy) {
        BookCopy existingCopy = bookCopyMapper.selectByBarCode(copy.getBarCode());
        if (existingCopy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (existingCopy.getStatus() == BookCopyStatus.CANCELLED) {
            throw new BusinessException("图书副本已注销，无法编辑");
        }
        bookCopyMapper.updateByBarCode(copy);
    }

    @Override
    @Transactional
    public void cancelCopy(String barCode) {
        BookCopy copy = bookCopyMapper.selectByBarCode(barCode);
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (copy.getStatus() == BookCopyStatus.CANCELLED) {
            throw new BusinessException("图书副本已注销");
        }
        copy.setOldStatus(copy.getStatus().getCode());
        copy.setStatus(BookCopyStatus.CANCELLED);
        bookCopyMapper.updateByBarCode(copy);
    }

    @Override
    @Transactional
    public void restoreCopy(String barCode) {
        BookCopy copy = bookCopyMapper.selectByBarCode(barCode);
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (copy.getStatus() != BookCopyStatus.CANCELLED) {
            throw new BusinessException("图书副本未注销，无需恢复");
        }
        if (copy.getOldStatus() == null) {
            copy.setStatus(BookCopyStatus.AVAILABLE);
        } else {
            copy.setStatus(BookCopyStatus.values()[copy.getOldStatus()]);
        }
        bookCopyMapper.updateByBarCode(copy);
    }

    @Override
    @Transactional
    public void borrowCopy(String barCode) {
        BookCopy copy = bookCopyMapper.selectByBarCode(barCode);
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (copy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new BusinessException("图书副本不可借");
        }
        copy.setOldStatus(copy.getStatus().getCode());
        copy.setStatus(BookCopyStatus.BORROWED);
        bookCopyMapper.updateByBarCode(copy);
    }

    @Override
    @Transactional
    public void returnCopy(String barCode) {
        BookCopy copy = bookCopyMapper.selectByBarCode(barCode);
        if (copy == null) {
            throw new BusinessException("图书副本不存在");
        }
        if (copy.getStatus() != BookCopyStatus.BORROWED) {
            throw new BusinessException("图书副本未借出，无需归还");
        }
        copy.setOldStatus(copy.getStatus().getCode());
        copy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyMapper.updateByBarCode(copy);
    }

    @Override
    public Map<String, Integer> getCopyStats(String isbn) {
        List<BookCopy> copies = bookCopyMapper.selectByIsbn(isbn);
        int total = copies.size();
        int available = (int) copies.stream()
                .filter(c -> c.getStatus() == BookCopyStatus.AVAILABLE)
                .count();
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("available", available);
        return stats;
    }
}
