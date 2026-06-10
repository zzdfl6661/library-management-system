package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.dto.response.StudentCardInfo;
import com.library.entity.CardRecord;
import com.library.entity.LibraryCard;
import com.library.entity.Student;
import com.library.service.CardRecordService;
import com.library.service.LibraryCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    private LibraryCardService libraryCardService;

    @Autowired
    private CardRecordService cardRecordService;

    @GetMapping("/list")
    public ApiResponse<List<LibraryCard>> getCardList(
            @RequestParam(required = false) String keyword) {
        List<LibraryCard> cards;
        if (keyword != null && !keyword.isEmpty()) {
            cards = libraryCardService.searchCards(keyword);
        } else {
            cards = libraryCardService.getAllCards();
        }
        return ApiResponse.success(cards);
    }

    @GetMapping("/student/{sno}")
    public ApiResponse<Map<String, Object>> getStudentInfo(@PathVariable String sno) {
        Student student = libraryCardService.getStudentBySno(sno);
        if (student == null) {
            return ApiResponse.error(404, "学生不存在");
        }
        
        LibraryCard card = libraryCardService.getCardBySno(sno);
        boolean hasValidCard = libraryCardService.hasValidCard(sno);
        int availableBorrowCount = 0;
        if (hasValidCard && card != null) {
            availableBorrowCount = libraryCardService.getAvailableBorrowCount(card.getCardNo());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("student", student);
        result.put("card", card);
        result.put("hasValidCard", hasValidCard);
        result.put("availableBorrowCount", availableBorrowCount);
        
        return ApiResponse.success(result);
    }

    @PostMapping("/create")
    public ApiResponse<Void> createCard(@RequestBody Map<String, String> request) {
        String sno = request.get("sno");
        String cardNo = request.get("cardNo");
        if (sno == null || cardNo == null) {
            return ApiResponse.error(400, "参数不完整");
        }
        libraryCardService.createCard(sno, cardNo);
        return ApiResponse.success("借书证创建成功", null);
    }

    @PostMapping("/create/batch")
    public ApiResponse<Void> createCards(@RequestBody Map<String, List<String>> request) {
        List<String> snos = request.get("snos");
        List<String> cardNos = request.get("cardNos");
        if (snos == null || cardNos == null || snos.size() != cardNos.size()) {
            return ApiResponse.error(400, "参数不完整或数量不匹配");
        }
        libraryCardService.createCards(snos, cardNos);
        return ApiResponse.success("批量借书证创建成功", null);
    }

    @PutMapping("/report-lost/{sno}")
    public ApiResponse<Void> reportLost(@PathVariable String sno) {
        libraryCardService.reportLost(sno);
        return ApiResponse.success("挂失成功", null);
    }

    @PutMapping("/reissue")
    public ApiResponse<Void> reissueCard(@RequestBody Map<String, String> request) {
        String sno = request.get("sno");
        String newCardNo = request.get("newCardNo");
        if (sno == null || newCardNo == null) {
            return ApiResponse.error(400, "参数不完整");
        }
        libraryCardService.reissueCard(sno, newCardNo);
        return ApiResponse.success("补办成功", null);
    }

    @PutMapping("/cancel/{sno}")
    public ApiResponse<Void> cancelCard(@PathVariable String sno) {
        libraryCardService.cancelCard(sno);
        return ApiResponse.success("注销成功", null);
    }

    @PutMapping("/cancel/batch")
    public ApiResponse<Void> cancelCards(@RequestBody List<String> snos) {
        if (snos == null || snos.isEmpty()) {
            return ApiResponse.error(400, "学号列表不能为空");
        }
        libraryCardService.cancelCards(snos);
        return ApiResponse.success("批量注销成功", null);
    }

    @GetMapping("/records/{sno}")
    public ApiResponse<List<CardRecord>> getCardRecords(@PathVariable String sno) {
        List<CardRecord> records = cardRecordService.getRecordsBySno(sno);
        return ApiResponse.success(records);
    }
}