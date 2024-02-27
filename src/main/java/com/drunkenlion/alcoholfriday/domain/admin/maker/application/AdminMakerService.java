package com.drunkenlion.alcoholfriday.domain.admin.maker.application;

import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerRequest;
import org.springframework.data.domain.Page;

public interface AdminMakerService {
    Page<MakerListResponse> getMakers(int page, int size);
    MakerDetailResponse getMaker(Long id);
    MakerDetailResponse createMaker(MakerRequest makerRequest);
    MakerDetailResponse modifyMaker(Long id, MakerRequest makerRequest);
    MakerDetailResponse deleteMaker(Long id);
}
