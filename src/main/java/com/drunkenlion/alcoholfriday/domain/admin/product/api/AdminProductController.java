package com.drunkenlion.alcoholfriday.domain.admin.product.api;

import com.drunkenlion.alcoholfriday.domain.admin.product.application.AdminProductService;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.*;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.common.util.RoleValidator;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/products")
@Tag(name = "v1-admin-product", description = "관리자 제품 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminProductController {
    private final AdminProductService adminProductService;

    @Operation(summary = "전체 제품 조회", description = "관리자 권한에 대한 전체 제품 조회")
    @GetMapping
    public ResponseEntity<PageResponse<ProductListResponse>> getProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<ProductListResponse> pageResponse = PageResponse.of(this.adminProductService.getProducts(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "제품 상세 조회", description = "관리자 권한에 대한 제품 상세 조회")
    @GetMapping("{id}")
    public ResponseEntity<ProductDetailResponse> getProduct(
            @PathVariable("id") Long id
    ) {
        ProductDetailResponse productDetailResponse = adminProductService.getProduct(id);
        return ResponseEntity.ok().body(productDetailResponse);
    }

    @Operation(summary = "제품 등록", description = "관리자 권한에 대한 제품 등록")
    @PostMapping
    public ResponseEntity<ProductDetailResponse> createProduct(
            @Valid @RequestPart("productRequest") ProductCreateRequest productCreateRequest,
            @RequestPart("files") List<MultipartFile> files
    ) {
        ProductDetailResponse productDetailResponse = adminProductService.createProduct(productCreateRequest, files);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(productDetailResponse);
    }

    @Operation(summary = "제품 수정", description = "관리자 권한에 대한 제품 수정")
    @PutMapping("{id}")
    public ResponseEntity<ProductDetailResponse> modifyProduct(
            @PathVariable("id") Long id,
            @Valid @RequestPart("productRequest") ProductModifyRequest productModifyRequest,
            @RequestPart("files") List<MultipartFile> files
    ) {
        ProductDetailResponse productDetailResponse = adminProductService.modifyProduct(id, productModifyRequest, productModifyRequest.getRemove(), files);
        return ResponseEntity.ok().body(productDetailResponse);
    }

    @Operation(summary = "제품 삭제", description = "관리자 권한에 대한 제품 삭제")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("id") Long id
    ) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "재고 수량 조회", description = "관리자 권한에 대한 제품의 재고 수량 조회")
    @GetMapping("{id}/stocks")
    public ResponseEntity<ProductQuantityResponse> getQuantity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id
    ) {
        RoleValidator.validateAdminOrStoreManager(userPrincipal.getMember());
        ProductQuantityResponse productQuantityResponse = adminProductService.getQuantity(id);
        return ResponseEntity.ok().body(productQuantityResponse);
    }

    @Operation(summary = "재고 수량 수정", description = "관리자 권한에 대한 제품의 재고 수량 수정")
    @PutMapping("{id}/stocks")
    public ResponseEntity<ProductQuantityResponse> modifyQuantity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductQuantityRequest productQuantityRequest
    ) {
        RoleValidator.validateAdminOrStoreManager(userPrincipal.getMember());
        ProductQuantityResponse productQuantityResponse = adminProductService.modifyQuantity(id, productQuantityRequest);
        return ResponseEntity.ok().body(productQuantityResponse);
    }
}
