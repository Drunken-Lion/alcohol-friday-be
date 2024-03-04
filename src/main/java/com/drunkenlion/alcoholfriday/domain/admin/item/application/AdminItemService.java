package com.drunkenlion.alcoholfriday.domain.admin.item.application;

import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemModifyRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminItemService {
    Page<ItemListResponse> getItems(int page, int size);
    ItemDetailResponse getItem(Long id);
    ItemDetailResponse createItem(ItemCreateRequest itemCreateRequest, List<MultipartFile> files);
    ItemDetailResponse modifyItem(Long id, ItemModifyRequest itemModifyRequest, List<Integer> remove, List<MultipartFile> files);
    void deleteItem(Long id);
}
