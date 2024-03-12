package com.drunkenlion.alcoholfriday.domain.admin.maker.application;

import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerRequest;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMakerServiceImpl implements AdminMakerService {
    private final MakerRepository makerRepository;
    private final ProductRepository productRepository;

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
        Maker maker = makerRepository.findByIdAndDeletedAtIsNull(id)
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

    @Transactional
    public MakerDetailResponse deleteMaker(Long id) {
        Maker maker = makerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MAKER)
                        .build());

        // maker와 관계가 있는 product 중 삭제 상태가 아닌 것이 있는지 확인
        if (productRepository.existsByMakerAndDeletedAtIsNull(maker)) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.MAKER_IN_USE)
                    .build();
        }

        maker = maker.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        makerRepository.save(maker);

        return MakerDetailResponse.of(maker);
    }
}
