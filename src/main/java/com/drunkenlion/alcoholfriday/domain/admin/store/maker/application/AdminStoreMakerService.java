package com.drunkenlion.alcoholfriday.domain.admin.store.maker.application;

import com.drunkenlion.alcoholfriday.domain.admin.store.maker.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.maker.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.maker.dto.MakerRequest;
import org.springframework.data.domain.Page;

public interface AdminStoreMakerService {
    Page<MakerListResponse> getMakers(int page, int size);
    MakerDetailResponse getMaker(Long id);
    MakerDetailResponse createMaker(MakerRequest makerRequest);
    MakerDetailResponse modifyMaker(Long id, MakerRequest makerRequest);
    MakerDetailResponse deleteMaker(Long id);
}
