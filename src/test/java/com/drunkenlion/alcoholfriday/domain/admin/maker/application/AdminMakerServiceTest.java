package com.drunkenlion.alcoholfriday.domain.admin.maker.application;

import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerRequest;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminMakerServiceTest {
    @InjectMocks
    private AdminMakerServiceImpl adminMakerService;
    @Mock
    private MakerRepository makerRepository;
    @Mock
    private ProductRepository productRepository;

    private final Long id = 1L;
    private final String name = "test 제조사";
    private final String address = "서울 강동구 아리수로 46";
    private final String detail = "1052호";
    private final String region = "서울특별시";
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();
    private final LocalDateTime deletedAt = LocalDateTime.now();
    private final int page = 0;
    private final int size = 20;

    private final String modiftyName = "test 제조사 수정";
    private final String modifyAddress = "test 주소 수정";
    private final String modifyDetail = "test 상세주소 수정";
    private final String modifyRegion = "test 제조지역 수정";

    @Test
    @DisplayName("제조사 목록 조회 성공")
    public void getMakersTest() {
        // given
        Mockito.when(this.makerRepository.findAll(any(Pageable.class))).thenReturn(this.getMakers());

        // when
        Page<MakerListResponse> makers = this.adminMakerService.getMakers(page, size);

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
    @DisplayName("제조사 상세 조회 성공")
    public void getMakerTest() {
        // given
        Mockito.when(this.makerRepository.findById(any())).thenReturn(this.getOne());

        // when
        MakerDetailResponse makerDetailResponse = this.adminMakerService.getMaker(id);

        // then
        assertThat(makerDetailResponse.getId()).isEqualTo(id);
        assertThat(makerDetailResponse.getName()).isEqualTo(name);
        assertThat(makerDetailResponse.getAddress()).isEqualTo(address);
        assertThat(makerDetailResponse.getDetail()).isEqualTo(detail);
        assertThat(makerDetailResponse.getRegion()).isEqualTo(region);
        assertThat(makerDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(makerDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("제조사 상세 조회 실패 - 찾을 수 없는 제조사")
    public void getMakerFailNotFoundTest() {
        // given
        Mockito.when(this.makerRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMakerService.getMaker(id);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제조사 등록 성공")
    public void createMakerTest() {
        // given
        MakerRequest makerRequest = MakerRequest.builder()
                .name(name)
                .address(address)
                .detail(detail)
                .region(region)
                .build();

        Mockito.when(makerRepository.save(any(Maker.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MakerDetailResponse makerDetailResponse = adminMakerService.createMaker(makerRequest);

        // then
        assertThat(makerDetailResponse.getName()).isEqualTo(name);
        assertThat(makerDetailResponse.getAddress()).isEqualTo(address);
        assertThat(makerDetailResponse.getDetail()).isEqualTo(detail);
        assertThat(makerDetailResponse.getRegion()).isEqualTo(region);
    }

    @Test
    @DisplayName("제조사 수정 성공")
    public void modifyMakerTest() {
        // given
        MakerRequest makerRequest = MakerRequest.builder()
                .name(modiftyName)
                .address(modifyAddress)
                .detail(modifyDetail)
                .region(modifyRegion)
                .build();

        Mockito.when(makerRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getOne());
        Mockito.when(makerRepository.save(any(Maker.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MakerDetailResponse makerDetailResponse = adminMakerService.modifyMaker(id, makerRequest);

        // then
        assertThat(makerDetailResponse.getId()).isEqualTo(id);
        assertThat(makerDetailResponse.getName()).isEqualTo(modiftyName);
        assertThat(makerDetailResponse.getAddress()).isEqualTo(modifyAddress);
        assertThat(makerDetailResponse.getDetail()).isEqualTo(modifyDetail);
        assertThat(makerDetailResponse.getRegion()).isEqualTo(modifyRegion);
    }

    @Test
    @DisplayName("제조사 수정 실패 - 찾을 수 없는 제조사")
    public void modifyMakerFailNotFoundTest() {
        // given
        MakerRequest makerRequest = MakerRequest.builder()
                .name(modiftyName)
                .address(modifyAddress)
                .detail(modifyDetail)
                .region(modifyRegion)
                .build();

        Mockito.when(this.makerRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMakerService.modifyMaker(id, makerRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제조사 삭제 성공")
    public void deleteMakerTest() {
        // given
        Mockito.when(makerRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getOne());
        Mockito.when(makerRepository.save(any(Maker.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MakerDetailResponse makerDetailResponse = adminMakerService.deleteMaker(id);

        // then
        assertThat(makerDetailResponse.getId()).isEqualTo(id);
        assertThat(makerDetailResponse.getName()).isEqualTo(name);
        assertThat(makerDetailResponse.getAddress()).isEqualTo(address);
        assertThat(makerDetailResponse.getDetail()).isEqualTo(detail);
        assertThat(makerDetailResponse.getRegion()).isEqualTo(region);
        assertThat(makerDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(makerDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(makerDetailResponse.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("제조사 삭제 실패 - 찾을 수 없는 제조사")
    public void deleteMakerFailNotFoundTest() {
        // given
        Mockito.when(makerRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMakerService.deleteMaker(id);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제조사 삭제 실패 - 제품과 연결된 제조사")
    public void deleteMakerFailMakerInUseTest() {
        // given
        Mockito.when(makerRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getOne());
        Mockito.when(productRepository.existsByMakerAndDeletedAtIsNull(any(Maker.class))).thenReturn(true);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMakerService.deleteMaker(id);
        });

        // then
        assertEquals(HttpResponse.Fail.MAKER_IN_USE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.MAKER_IN_USE.getMessage(), exception.getMessage());
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
