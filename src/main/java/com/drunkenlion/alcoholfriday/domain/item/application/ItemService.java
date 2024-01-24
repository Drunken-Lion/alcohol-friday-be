package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemRequest;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import org.springframework.data.domain.Page;

public interface ItemService {
    Page<SearchItemResponse> search(SearchItemRequest searchItemRequest);
}
