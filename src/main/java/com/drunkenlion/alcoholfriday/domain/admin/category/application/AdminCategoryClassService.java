package com.drunkenlion.alcoholfriday.domain.admin.category.application;

import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryClassDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryClassListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryClassRequest;
import org.springframework.data.domain.Page;

public interface AdminCategoryClassService {
    Page<CategoryClassListResponse> getCategoryClasses(int page, int size);
    CategoryClassDetailResponse getCategoryClass(Long id);
    CategoryClassDetailResponse createCategoryClass(CategoryClassRequest categoryClassRequest);
    CategoryClassDetailResponse modifyCategoryClass(Long id, CategoryClassRequest categoryClassRequest);
    void deleteCategoryClass(Long id);
}
