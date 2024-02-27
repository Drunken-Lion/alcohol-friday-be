package com.drunkenlion.alcoholfriday.domain.admin.category.application;

import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryRequest;
import org.springframework.data.domain.Page;

public interface AdminCategoryService {
    Page<CategoryListResponse> getCategories(int page, int size);
    CategoryDetailResponse getCategory(Long id);
    CategoryDetailResponse createCategory(CategoryRequest categoryRequest);
    CategoryDetailResponse modifyCategory(Long id, CategoryRequest categoryRequest);
    void deleteCategory(Long id);
}
