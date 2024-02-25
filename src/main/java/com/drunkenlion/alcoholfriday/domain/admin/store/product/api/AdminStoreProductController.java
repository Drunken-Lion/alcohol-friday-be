package com.drunkenlion.alcoholfriday.domain.admin.store.product.api;

import com.drunkenlion.alcoholfriday.domain.admin.store.product.application.AdminStoreProductService;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductRequest;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

    @Operation(summary = "제품 등록", description = "관리자 권한에 대한 제품 등록")
    @PostMapping(value = "products")
    public ResponseEntity<ProductDetailResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest
    ) {
        ProductDetailResponse productDetailResponse = adminStoreProductService.createProduct(productRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(productDetailResponse);
    }

    @Operation(summary = "제품 수정", description = "관리자 권한에 대한 제품 수정")
    @PutMapping(value = "products/{id}")
    public ResponseEntity<ProductDetailResponse> modifyProduct(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        ProductDetailResponse productDetailResponse = adminStoreProductService.modifyProduct(id, productRequest);
        return ResponseEntity.ok().body(productDetailResponse);
    }

    @Operation(summary = "제품 삭제", description = "관리자 권한에 대한 제품 삭제")
    @DeleteMapping(value = "products/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("id") Long id
    ) {
        adminStoreProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}