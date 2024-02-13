package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemRequest;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.file.application.FileServiceImpl;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final FileServiceImpl fileService;

    @Override
    public Page<SearchItemResponse> search(SearchItemRequest searchItemRequest) {
        Pageable pageable = PageRequest.of(0, searchItemRequest.getSize());
        Page<Item> search = this.itemRepository.search(searchItemRequest.getKeywordType(), searchItemRequest.getKeyword(), pageable);

        List<Long> entityIds = search.getContent()
                .stream()
                .map((BaseEntity::getId))
                .toList();

        List<NcpFileResponse> files = this.fileService.findAllByEntityIds(entityIds, EntityType.ITEM.getEntityName());

        return SearchItemResponse.of(search, files);
    }

    @Override
    public FindItemResponse get(Long id) {
        Item item = this.itemRepository.get(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        NcpFileResponse file = this.fileService.findByEntityId(item.getId(), EntityType.ITEM.getEntityName());

        return FindItemResponse.of(item, file);
    }
}
