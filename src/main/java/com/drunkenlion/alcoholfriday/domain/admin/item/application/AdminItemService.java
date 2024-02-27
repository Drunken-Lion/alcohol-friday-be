package com.drunkenlion.alcoholfriday.domain.admin.item.application;

import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemRequest;
import org.springframework.data.domain.Page;

public interface AdminItemService {
    Page<ItemListResponse> getItems(int page, int size);
    ItemDetailResponse getItem(Long id);
    ItemDetailResponse createItem(ItemRequest itemRequest);
    ItemDetailResponse modifyItem(Long id, ItemRequest itemRequest);
    void deleteItem(Long id);
}
