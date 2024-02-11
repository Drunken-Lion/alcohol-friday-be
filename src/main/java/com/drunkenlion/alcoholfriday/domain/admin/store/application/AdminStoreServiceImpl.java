package com.drunkenlion.alcoholfriday.domain.admin.store.application;

import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerRequest;
import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminStoreServiceImpl implements AdminStoreService {
    private final MakerRepository makerRepository;

    public Page<MakerListResponse> getMakers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Maker> makers = makerRepository.findAll(pageable);

        return makers.map(MakerListResponse::of);
    }

    public MakerDetailResponse getMaker(Long id) {
        Maker maker = makerRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MAKER)
                        .build());

        return MakerDetailResponse.of(maker);
    }

    public MakerDetailResponse createMaker(MakerRequest makerRequest) {
        Maker maker = MakerRequest.toEntity(makerRequest);
        makerRepository.save(maker);

        return MakerDetailResponse.of(maker);
    }

    @Transactional
    public MakerDetailResponse modifyMaker(Long id, MakerRequest makerRequest) {
        Maker maker = makerRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MAKER)
                        .build());

        maker = maker.toBuilder()
                .name(makerRequest.getName())
                .address(makerRequest.getAddress())
                .detail(makerRequest.getDetail())
                .region(makerRequest.getRegion())
                .build();

        makerRepository.save(maker);

        return MakerDetailResponse.of(maker);
    }
}
