
package com.library.controller;

import com.library.dto.request.CardCreateRequest;
import com.library.dto.response.ApiResponse;
import com.library.entity.LibraryCard;
import com.library.service.LibraryCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "http://localhost:5173")
public class CardController {

    @Autowired
    private LibraryCardService libraryCardService;

    @PostMapping
    public ApiResponse<LibraryCard> createCard(@RequestBody CardCreateRequest request) {
        LibraryCard card = libraryCardService.createCard(request);
        return ApiResponse.success("借书证创建成功", card);
    }

    @PutMapping("/{cardNo}/lost")
    public ApiResponse<Void> reportLost(@PathVariable String cardNo) {
        libraryCardService.reportLost(cardNo);
        return ApiResponse.success("挂失成功");
    }

    @PutMapping("/{cardNo}/reissue")
    public ApiResponse<LibraryCard> reissueCard(@PathVariable String cardNo) {
        LibraryCard card = libraryCardService.reissueCard(cardNo);
        return ApiResponse.success("补办成功", card);
    }

    @PutMapping("/{cardNo}/cancel")
    public ApiResponse<Void> cancelCard(@PathVariable String cardNo) {
        libraryCardService.cancelCard(cardNo);
        return ApiResponse.success("注销成功");
    }

    @GetMapping("/{cardNo}")
    public ApiResponse<LibraryCard> getCardByCardNo(@PathVariable String cardNo) {
        LibraryCard card = libraryCardService.getByCardNo(cardNo);
        return ApiResponse.success(card);
    }

    @GetMapping
    public ApiResponse<List<LibraryCard>> getAllCards() {
        List<LibraryCard> cards = libraryCardService.getAllCards();
        return ApiResponse.success(cards);
    }
}
