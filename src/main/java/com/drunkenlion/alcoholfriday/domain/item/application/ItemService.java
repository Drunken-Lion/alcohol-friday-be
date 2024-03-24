package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.ItemReviewResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    Page<SearchItemResponse> search(int page, Integer size, String keyword, List<String> keywordType);
    FindItemResponse get(Long id);

    Page<ItemReviewResponse> getReviews(Long id, int page, int size);
}
