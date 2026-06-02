
package com.library.service;

import com.library.dto.request.CardCreateRequest;
import com.library.dto.response.CardResponse;
import com.library.entity.LibraryCard;

import java.util.List;

public interface LibraryCardService {
    LibraryCard createCard(CardCreateRequest request);
    void reportLost(String cardNo);
    LibraryCard reissueCard(String cardNo);
    void cancelCard(String cardNo);
    LibraryCard getByCardNo(String cardNo);
    List<LibraryCard> getAllCards();
    List<LibraryCard> searchCards(String cardNo);
    CardResponse getCardInfoWithAvailableCount(String cardNo);
}
