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
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryClassRepository categoryClassRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;

    public Page<CategoryListResponse> getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories.map(CategoryListResponse::of);
    }

    public CategoryDetailResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        return CategoryDetailResponse.of(category);
    }

    @Transactional
    public CategoryDetailResponse createCategory(CategoryRequest categoryRequest) {
        CategoryClass categoryClass = categoryClassRepository.findByIdAndDeletedAtIsNull(categoryRequest.getCategoryFirstId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS)
                        .build());

        Category category = CategoryRequest.toEntity(categoryRequest, categoryClass);
        categoryRepository.save(category);

        return CategoryDetailResponse.of(category);
    }

    @Transactional
    public CategoryDetailResponse modifyCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        CategoryClass categoryClass = categoryClassRepository.findByIdAndDeletedAtIsNull(categoryRequest.getCategoryFirstId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS)
                        .build());

        category = category.toBuilder()
                .categoryClass(categoryClass)
                .lastName(categoryRequest.getCategoryLastName())
                .build();

        categoryRepository.save(category);

        return CategoryDetailResponse.of(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        // category와 관계가 있는 item, product 중 삭제 상태가 아닌 것이 있는지 확인
        if (itemRepository.existsByCategoryAndDeletedAtIsNull(category) ||
                productRepository.existsByCategoryAndDeletedAtIsNull(category)) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.CATEGORY_IN_USE)
                    .build();
        }

        category = category.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        categoryRepository.save(category);
    }
}
