package com.drunkenlion.alcoholfriday.domain.admin.category.application;

import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryClassDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryClassListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryClassRequest;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminCategoryClassServiceTest {
    @InjectMocks
    private AdminCategoryClassServiceImpl adminCategoryClassService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryClassRepository categoryClassRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ProductRepository productRepository;

    private final Long categoryFirstId = 1L;
    private final String firstName = "테스트 카테고리 대분류 1";
    private final String modifyFirstName = "테스트 카테고리 대분류 1 수정";

    private final Long categoryLastId = 1L;
    private final String lastName = "테스트 카테고리 소분류1";

    private final int page = 0;
    private final int size = 20;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();

    @Test
    @DisplayName("카테고리 대분류 목록 조회 성공")
    public void getCategoryClassesTest() {
        // given
        when(this.categoryClassRepository.findAll(any(Pageable.class))).thenReturn(this.getCategoryClasses());

        // when
        Page<CategoryClassListResponse> categoryClasses = this.adminCategoryClassService.getCategoryClasses(page, size);

        // then
        List<CategoryClassListResponse> content = categoryClasses.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(categoryFirstId);
        assertThat(content.get(0).getCategoryFirstName()).isEqualTo(firstName);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("카테고리 대분류 상세 조회 성공")
    public void getCategoryClassTest() {
        // given
        when(this.categoryClassRepository.findById(any())).thenReturn(this.getCategoryClassOne());

        // when
        CategoryClassDetailResponse categoryClassDetailResponse = this.adminCategoryClassService.getCategoryClass(categoryFirstId);

        // then
        assertThat(categoryClassDetailResponse.getId()).isEqualTo(categoryFirstId);
        assertThat(categoryClassDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
        assertThat(categoryClassDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(categoryClassDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("카테고리 대분류 상세 조회 실패 - 찾을 수 없는 카테고리 대분류")
    public void getCategoryClassFailNotFoundTest() {
        // given
        when(this.categoryClassRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryClassService.getCategoryClass(categoryFirstId);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 대분류 등록 성공")
    public void createCategoryClassTest() {
        // given
        CategoryClassRequest categoryClassRequest = CategoryClassRequest.builder()
                .categoryFirstName(firstName)
                .build();

        when(categoryClassRepository.save(any(CategoryClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryClassDetailResponse categoryClassDetailResponse = this.adminCategoryClassService.createCategoryClass(categoryClassRequest);

        // then
        assertThat(categoryClassDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
    }

    @Test
    @DisplayName("카테고리 대분류 수정 성공")
    public void modifyCategoryClassTest() {
        // given
        CategoryClassRequest categoryClassRequest = CategoryClassRequest.builder()
                .categoryFirstName(modifyFirstName)
                .build();

        when(categoryClassRepository.findByIdAndDeletedAtIsNull(categoryFirstId)).thenReturn(this.getCategoryClassOne());
        when(categoryClassRepository.save(any(CategoryClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryClassDetailResponse categoryClassDetailResponse = adminCategoryClassService.modifyCategoryClass(categoryFirstId, categoryClassRequest);

        // then
        assertThat(categoryClassDetailResponse.getCategoryFirstName()).isEqualTo(modifyFirstName);
    }

    @Test
    @DisplayName("카테고리 대분류 수정 실패 - 찾을 수 없는 카테고리 대분류")
    public void modifyCategoryClassFailNotFoundTest() {
        // given
        CategoryClassRequest categoryClassRequest = CategoryClassRequest.builder()
                .categoryFirstName(modifyFirstName)
                .build();

        when(categoryClassRepository.findByIdAndDeletedAtIsNull(categoryFirstId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryClassService.modifyCategoryClass(categoryFirstId, categoryClassRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 대분류 삭제 성공")
    public void deleteCategoryClassTest() {
        // given
        when(this.categoryClassRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getCategoryClassOne());
        when(this.categoryRepository.findByCategoryClassAndDeletedAtIsNull(any())).thenReturn(this.getCategories());
        when(this.itemRepository.existsByCategoryInAndDeletedAtIsNull(anyList())).thenReturn(false);
        when(this.productRepository.existsByCategoryInAndDeletedAtIsNull(anyList())).thenReturn(false);
        ArgumentCaptor<CategoryClass> categoryClassCaptor = ArgumentCaptor.forClass(CategoryClass.class);
        ArgumentCaptor<List<Category>> categoryCaptor = ArgumentCaptor.forClass(List.class);

        // When
        adminCategoryClassService.deleteCategoryClass(categoryFirstId);

        // then
        verify(categoryClassRepository).save(categoryClassCaptor.capture());
        CategoryClass savedCategoryClass = categoryClassCaptor.getValue();
        assertThat(savedCategoryClass.getDeletedAt()).isNotNull();

        verify(categoryRepository).saveAll(categoryCaptor.capture());
        List<Category> savedCategories = categoryCaptor.getValue();
        assertThat(savedCategories).allSatisfy(category -> assertThat(category.getDeletedAt()).isNotNull());
    }

    @Test
    @DisplayName("카테고리 대분류 삭제 실패 - 찾을 수 없는 카테고리 대분류")
    public void deleteCategoryClassFailNotFoundTest() {
        // given
        when(this.categoryClassRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryClassService.deleteCategoryClass(categoryFirstId);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 대분류 삭제 실패 - 사용중인 카테고리")
    public void deleteCategoryClassFailCategoryInUseTest() {
        // given
        when(this.categoryClassRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getCategoryClassOne());
        when(this.categoryRepository.findByCategoryClassAndDeletedAtIsNull(any())).thenReturn(this.getCategories());
        when(this.itemRepository.existsByCategoryInAndDeletedAtIsNull(anyList())).thenReturn(false);
        when(this.productRepository.existsByCategoryInAndDeletedAtIsNull(anyList())).thenReturn(true);

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryClassService.deleteCategoryClass(categoryFirstId);
        });

        // then
        assertEquals(HttpResponse.Fail.CATEGORY_IN_USE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.CATEGORY_IN_USE.getMessage(), exception.getMessage());
    }

    private Page<CategoryClass> getCategoryClasses() {
        List<CategoryClass> list = List.of(this.getCategoryClassData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<CategoryClass>(list, pageable, list.size());
    }

    private List<Category> getCategories() {
        return List.of(this.getCategoryData());
    }

    private Optional<CategoryClass> getCategoryClassOne() {
        return Optional.of(this.getCategoryClassData());
    }

    private CategoryClass getCategoryClassData() {
        return CategoryClass.builder()
                .id(categoryFirstId)
                .firstName(firstName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Category getCategoryData() {
        CategoryClass categoryClass = getCategoryClassData();

        return Category.builder()
                .id(categoryLastId)
                .lastName(lastName)
                .categoryClass(categoryClass)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
