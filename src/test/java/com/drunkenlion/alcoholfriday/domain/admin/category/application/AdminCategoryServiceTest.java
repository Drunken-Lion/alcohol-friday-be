package com.drunkenlion.alcoholfriday.domain.admin.category.application;

import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryRequest;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminCategoryServiceTest {
    @InjectMocks
    private AdminCategoryServiceImpl adminCategoryService;
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
    private final Long modifyCategoryFirstId = 2L;
    private final String modifyFirstName = "테스트 카테고리 대분류 1 수정";

    private final Long categoryLastId = 1L;
    private final String lastName = "테스트 카테고리 소분류1";
    private final String modifyLastName = "테스트 카테고리 소분류1 수정";

    private final int page = 0;
    private final int size = 20;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();

    @Test
    @DisplayName("카테고리 소분류 목록 조회 성공")
    public void getCategoriesTest() {
        // given
        when(this.categoryRepository.findAll(any(Pageable.class))).thenReturn(this.getCategories());

        // when
        Page<CategoryListResponse> categories = this.adminCategoryService.getCategories(page, size);

        // then
        List<CategoryListResponse> content = categories.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(categoryLastId);
        assertThat(content.get(0).getCategoryFirstName()).isEqualTo(firstName);
        assertThat(content.get(0).getCategoryLastName()).isEqualTo(lastName);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("카테고리 소분류 상세 조회 성공")
    public void getCategoryTest() {
        // given
        when(this.categoryRepository.findById(any())).thenReturn(this.getCategoryOne());

        // when
        CategoryDetailResponse categoryDetailResponse = this.adminCategoryService.getCategory(categoryLastId);

        // then
        assertThat(categoryDetailResponse.getId()).isEqualTo(categoryLastId);
        assertThat(categoryDetailResponse.getCategoryFirstId()).isEqualTo(categoryFirstId);
        assertThat(categoryDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
        assertThat(categoryDetailResponse.getCategoryLastName()).isEqualTo(lastName);
        assertThat(categoryDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(categoryDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("카테고리 소분류 상세 조회 실패 - 찾을 수 없는 카테고리 소분류")
    public void getCategoryFailNotFoundTest() {
        // given
        when(this.categoryRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryService.getCategory(categoryLastId);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 소분류 등록 성공")
    public void createCategoryTest() {
        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .categoryFirstId(categoryFirstId)
                .categoryLastName(lastName)
                .build();

        when(categoryClassRepository.findByIdAndDeletedAtIsNull(categoryFirstId)).thenReturn(this.getCategoryClassOne());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryDetailResponse categoryDetailResponse = this.adminCategoryService.createCategory(categoryRequest);

        // then
        assertThat(categoryDetailResponse.getCategoryFirstId()).isEqualTo(categoryFirstId);
        assertThat(categoryDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
        assertThat(categoryDetailResponse.getCategoryLastName()).isEqualTo(lastName);
    }

    @Test
    @DisplayName("카테고리 소분류 등록 실패 - 찾을 수 없는 카테고리 대분류")
    public void createCategoryFailCategoryClassNotFoundTest() {
        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .categoryFirstId(categoryFirstId)
                .categoryLastName(lastName)
                .build();

        when(categoryClassRepository.findByIdAndDeletedAtIsNull(categoryFirstId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryService.createCategory(categoryRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 소분류 수정 성공")
    public void modifyCategoryTest() {
        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .categoryFirstId(modifyCategoryFirstId)
                .categoryLastName(modifyLastName)
                .build();

        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(this.getCategoryOne());
        when(categoryClassRepository.findByIdAndDeletedAtIsNull(modifyCategoryFirstId)).thenReturn(this.getModifyCategoryClassOne());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryDetailResponse categoryDetailResponse = adminCategoryService.modifyCategory(categoryLastId, categoryRequest);

        // then
        assertThat(categoryDetailResponse.getCategoryFirstId()).isEqualTo(modifyCategoryFirstId);
        assertThat(categoryDetailResponse.getCategoryFirstName()).isEqualTo(modifyFirstName);
        assertThat(categoryDetailResponse.getCategoryLastName()).isEqualTo(modifyLastName);
    }

    @Test
    @DisplayName("카테고리 소분류 수정 실패 - 찾을 수 없는 카테고리 소분류")
    public void modifyCategoryFailNotFoundTest() {
        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .categoryFirstId(modifyCategoryFirstId)
                .categoryLastName(modifyLastName)
                .build();

        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryService.modifyCategory(categoryLastId, categoryRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 소분류 수정 실패 - 찾을 수 없는 카테고리 대분류")
    public void modifyCategoryFailCategoryClassNotFoundTest() {
        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .categoryFirstId(modifyCategoryFirstId)
                .categoryLastName(modifyLastName)
                .build();

        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(this.getCategoryOne());
        when(categoryClassRepository.findByIdAndDeletedAtIsNull(modifyCategoryFirstId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryService.modifyCategory(categoryLastId, categoryRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 소분류 삭제 성공")
    public void deleteCategoryTest() {
        // given
        when(this.categoryRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getCategoryOne());
        when(this.itemRepository.existsByCategoryAndDeletedAtIsNull(any())).thenReturn(false);
        when(this.productRepository.existsByCategoryAndDeletedAtIsNull(any())).thenReturn(false);
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        // When
        adminCategoryService.deleteCategory(categoryLastId);

        // then
        verify(categoryRepository).save(categoryCaptor.capture());
        Category savedCategory = categoryCaptor.getValue();
        assertThat(savedCategory.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("카테고리 소분류 삭제 실패 - 찾을 수 없는 카테고리 소분류")
    public void deleteCategoryFailNotFoundTest() {
        // given
        when(this.categoryRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryService.deleteCategory(categoryLastId);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 소분류 삭제 실패 - 사용중인 카테고리")
    public void deleteCategoryClassFailCategoryInUseTest() {
        // given
        when(this.categoryRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getCategoryOne());
        when(this.itemRepository.existsByCategoryAndDeletedAtIsNull(any())).thenReturn(false);
        when(this.productRepository.existsByCategoryAndDeletedAtIsNull(any())).thenReturn(true);

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminCategoryService.deleteCategory(categoryLastId);
        });

        // then
        assertEquals(HttpResponse.Fail.CATEGORY_IN_USE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.CATEGORY_IN_USE.getMessage(), exception.getMessage());
    }

    private Page<Category> getCategories() {
        List<Category> list = List.of(this.getCategoryData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Category>(list, pageable, list.size());
    }

    private Optional<CategoryClass> getCategoryClassOne() {
        return Optional.of(this.getCategoryClassData());
    }

    private Optional<CategoryClass> getModifyCategoryClassOne() {
        return Optional.of(this.getModifyCategoryClassData());
    }

    private Optional<Category> getCategoryOne() {
        return Optional.of(this.getCategoryData());
    }

    private CategoryClass getCategoryClassData() {
        return CategoryClass.builder()
                .id(categoryFirstId)
                .firstName(firstName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private CategoryClass getModifyCategoryClassData() {
        return CategoryClass.builder()
                .id(modifyCategoryFirstId)
                .firstName(modifyFirstName)
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
