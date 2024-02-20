package com.drunkenlion.alcoholfriday.domain.admin.store.product.api;

import com.drunkenlion.alcoholfriday.domain.admin.store.product.application.AdminStoreProductService;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/store")
@Tag(name = "v1-admin-store-product", description = "관리자 스토어 제품 관리에 대한 API")
public class AdminStoreProductController {
    private final AdminStoreProductService adminStoreProductService;

    @Operation(summary = "전체 제품 조회", description = "관리자 권한에 대한 전체 제품 조회")
    @GetMapping(value = "products")
    public ResponseEntity<PageResponse<ProductListResponse>> getProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<ProductListResponse> pageResponse = PageResponse.of(this.adminStoreProductService.getProducts(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "제품 상세 조회", description = "관리자 권한에 대한 제품 상세 조회")
    @GetMapping(value = "products/{id}")
    public ResponseEntity<ProductDetailResponse> getProduct(
            @PathVariable("id") Long id
    ) {
        ProductDetailResponse productDetailResponse = adminStoreProductService.getProduct(id);
        return ResponseEntity.ok().body(productDetailResponse);
    }

}
