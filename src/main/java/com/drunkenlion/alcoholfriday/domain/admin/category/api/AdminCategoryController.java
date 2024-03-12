package com.drunkenlion.alcoholfriday.domain.admin.category.api;

import com.drunkenlion.alcoholfriday.domain.admin.category.application.AdminCategoryClassService;
import com.drunkenlion.alcoholfriday.domain.admin.category.application.AdminCategoryService;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.*;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-admin-category", description = "관리자 카테고리 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;
    private final AdminCategoryClassService adminCategoryClassService;

    @Operation(summary = "전체 카테고리 대분류 조회", description = "관리자 권한에 대한 전체 카테고리 대분류 조회")
    @GetMapping(value = "category-classes")
    public ResponseEntity<PageResponse<CategoryClassListResponse>> getCategoryClasses(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<CategoryClassListResponse> pageResponse = PageResponse.of(this.adminCategoryClassService.getCategoryClasses(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "카테고리 대분류 상세 조회", description = "관리자 권한에 대한 카테고리 대분류 상세 조회")
    @GetMapping(value = "category-classes/{id}")
    public ResponseEntity<CategoryClassDetailResponse> getCategoryClass(
            @PathVariable("id") Long id
    ) {
        CategoryClassDetailResponse categoryClassDetailResponse = adminCategoryClassService.getCategoryClass(id);
        return ResponseEntity.ok().body(categoryClassDetailResponse);
    }

    @Operation(summary = "카테고리 대분류 등록", description = "관리자 권한에 대한 카테고리 대분류 등록")
    @PostMapping(value = "category-classes")
    public ResponseEntity<CategoryClassDetailResponse> createCategoryClass(
            @Valid @RequestBody CategoryClassRequest categoryClassRequest
    ) {
        CategoryClassDetailResponse categoryClassDetailResponse = adminCategoryClassService.createCategoryClass(categoryClassRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryClassDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(categoryClassDetailResponse);
    }

    @Operation(summary = "카테고리 대분류 수정", description = "관리자 권한에 대한 카테고리 대분류 수정")
    @PutMapping(value = "category-classes/{id}")
    public ResponseEntity<CategoryClassDetailResponse> modifyCategoryClass(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryClassRequest categoryClassRequest
    ) {
        CategoryClassDetailResponse categoryClassDetailResponse = adminCategoryClassService.modifyCategoryClass(id, categoryClassRequest);
        return ResponseEntity.ok().body(categoryClassDetailResponse);
    }

    @Operation(summary = "카테고리 대분류 삭제", description = "관리자 권한에 대한 카테고리 대분류 삭제")
    @DeleteMapping(value = "category-classes/{id}")
    public ResponseEntity<Void> deleteCategoryClass(
            @PathVariable("id") Long id
    ) {
        adminCategoryClassService.deleteCategoryClass(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 카테고리 소분류 조회", description = "관리자 권한에 대한 전체 카테고리 소분류 조회")
    @GetMapping(value = "categories")
    public ResponseEntity<PageResponse<CategoryListResponse>> getCategories(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<CategoryListResponse> pageResponse = PageResponse.of(this.adminCategoryService.getCategories(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }


    @Operation(summary = "카테고리 소분류 상세 조회", description = "관리자 권한에 대한 카테고리 소분류 상세 조회")
    @GetMapping(value = "categories/{id}")
    public ResponseEntity<CategoryDetailResponse> getCategory(
            @PathVariable("id") Long id
    ) {
        CategoryDetailResponse categoryDetailResponse = adminCategoryService.getCategory(id);
        return ResponseEntity.ok().body(categoryDetailResponse);
    }

    @Operation(summary = "카테고리 소분류 등록", description = "관리자 권한에 대한 카테고리 소분류 등록")
    @PostMapping(value = "categories")
    public ResponseEntity<CategoryDetailResponse> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryDetailResponse categoryDetailResponse = adminCategoryService.createCategory(categoryRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(categoryDetailResponse);
    }

    @Operation(summary = "카테고리 소분류 수정", description = "관리자 권한에 대한 카테고리 소분류 수정")
    @PutMapping(value = "categories/{id}")
    public ResponseEntity<CategoryDetailResponse> modifyCategory(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryDetailResponse categoryDetailResponse = adminCategoryService.modifyCategory(id, categoryRequest);
        return ResponseEntity.ok().body(categoryDetailResponse);
    }

    @Operation(summary = "카테고리 소분류 삭제", description = "관리자 권한에 대한 카테고리 소분류 삭제")
    @DeleteMapping(value = "categories/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("id") Long id
    ) {
        adminCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
