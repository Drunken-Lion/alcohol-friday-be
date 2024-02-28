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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCategoryClassServiceImpl implements AdminCategoryClassService {
    private final CategoryRepository categoryRepository;
    private final CategoryClassRepository categoryClassRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;

    public Page<CategoryClassListResponse> getCategoryClasses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryClass> categoryClasses = categoryClassRepository.findAll(pageable);

        return categoryClasses.map(CategoryClassListResponse::of);
    }

    public CategoryClassDetailResponse getCategoryClass(Long id) {
        CategoryClass categoryClass = categoryClassRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS)
                        .build());

        return CategoryClassDetailResponse.of(categoryClass);
    }

    @Transactional
    public CategoryClassDetailResponse createCategoryClass(CategoryClassRequest categoryClassRequest) {
        CategoryClass categoryClass = CategoryClassRequest.toEntity(categoryClassRequest);
        categoryClassRepository.save(categoryClass);

        return CategoryClassDetailResponse.of(categoryClass);
    }

    @Transactional
    public CategoryClassDetailResponse modifyCategoryClass(Long id, CategoryClassRequest categoryClassRequest) {
        CategoryClass categoryClass = categoryClassRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS)
                        .build());

        categoryClass = categoryClass.toBuilder()
                .firstName(categoryClassRequest.getCategoryFirstName())
                .build();

        categoryClassRepository.save(categoryClass);

        return CategoryClassDetailResponse.of(categoryClass);
    }

    @Transactional
    public void deleteCategoryClass(Long id) {
        CategoryClass categoryClass = categoryClassRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY_CLASS)
                        .build());

        List<Category> categories = categoryRepository.findByCategoryClassAndDeletedAtIsNull(categoryClass);

        if (!categories.isEmpty()) {
            // category와 관계가 있는 item, product 중 삭제 상태가 아닌 것이 있는지 확인
            if (itemRepository.existsByCategoryInAndDeletedAtIsNull(categories) ||
                    productRepository.existsByCategoryInAndDeletedAtIsNull(categories)) {
                throw BusinessException.builder()
                        .response(HttpResponse.Fail.CATEGORY_IN_USE)
                        .build();
            }
        }

        categories = categories.stream()
                .map(category -> category.toBuilder().deletedAt(LocalDateTime.now()).build())
                .collect(Collectors.toList());

        categoryClass = categoryClass.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        categoryRepository.saveAll(categories);
        categoryClassRepository.save(categoryClass);
    }
}
