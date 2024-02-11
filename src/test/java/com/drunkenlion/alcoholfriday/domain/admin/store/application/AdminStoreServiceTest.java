package com.drunkenlion.alcoholfriday.domain.admin.store.application;

import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminStoreServiceTest {
    @InjectMocks
    private AdminStoreServiceImpl adminStoreService;
    @Mock
    private MakerRepository makerRepository;

    private final Long id = 1L;
    private final String name = "test 제조사";
    private final String address = "서울 강동구 아리수로 46";
    private final String detail = "1052호";
    private final String region = "서울특별시";
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();
    private final int page = 0;
    private final int size = 20;

    @Test
    public void getMakersTest() {
        // given
        Mockito.when(this.makerRepository.findAll(any(Pageable.class))).thenReturn(this.getMakers());

        // when
        Page<MakerListResponse> makers = this.adminStoreService.getMakers(page, size);

        // then
        List<MakerListResponse> content = makers.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(id);
        assertThat(content.get(0).getName()).isEqualTo(name);
        assertThat(content.get(0).getRegion()).isEqualTo(region);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    public void getMakerTest() {
        // given
        Mockito.when(this.makerRepository.findById(any())).thenReturn(this.getOne());

        // when
        MakerDetailResponse makerDetailResponse = this.adminStoreService.getMaker(id);

        // then
        assertThat(makerDetailResponse.getId()).isEqualTo(id);
        assertThat(makerDetailResponse.getName()).isEqualTo(name);
        assertThat(makerDetailResponse.getAddress()).isEqualTo(address);
        assertThat(makerDetailResponse.getDetail()).isEqualTo(detail);
        assertThat(makerDetailResponse.getRegion()).isEqualTo(region);
        assertThat(makerDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(makerDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }


    private Page<Maker> getMakers() {
        List<Maker> list = List.of(this.getData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Maker>(list, pageable, list.size());
    }

    private Optional<Maker> getOne() {
        return Optional.of(this.getData());
    }

    private Maker getData() {
        return Maker.builder()
                .id(id)
                .name(name)
                .address(address)
                .detail(detail)
                .region(region)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
