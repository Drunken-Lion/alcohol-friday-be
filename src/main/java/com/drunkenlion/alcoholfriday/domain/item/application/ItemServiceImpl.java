package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemRequest;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public Page<SearchItemResponse> search(SearchItemRequest searchItemRequest) {
        Pageable pageable = PageRequest.of(0, searchItemRequest.getSize());
        Page<Item> search = this.itemRepository.search(searchItemRequest.getKeywordType(), searchItemRequest.getKeyword(), pageable);

        return SearchItemResponse.of(search);
    }

    @Override
    public FindItemResponse get(Long id) {
        Item item = this.itemRepository.get(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        return FindItemResponse.of(item);
    }
}
