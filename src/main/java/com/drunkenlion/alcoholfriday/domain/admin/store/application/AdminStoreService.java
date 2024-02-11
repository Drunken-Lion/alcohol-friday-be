package com.drunkenlion.alcoholfriday.domain.admin.store.application;

import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerListResponse;
import org.springframework.data.domain.Page;

public interface AdminStoreService {
    Page<MakerListResponse> getMakers(int page, int size);

    MakerDetailResponse getMaker(Long id);

    MakerDetailResponse createMaker(MakerCreateRequest makerCreateRequest);
}
