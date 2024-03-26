package com.drunkenlion.alcoholfriday.domain.product.api;

import com.drunkenlion.alcoholfriday.domain.product.application.ProductService;
import com.drunkenlion.alcoholfriday.domain.product.dto.ProductDetailPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
@Tag(name = "v1-product-controller", description = "제품 관련 API")
public class ProductController {
    private final ProductService productService;
    @GetMapping("{id}")
    @Operation(summary = "제품 상세 조회")
    public ResponseEntity<ProductDetailPageResponse> getProduct(@PathVariable("id") Long productId) {
        ProductDetailPageResponse response = productService.findProduct(productId);
        return ResponseEntity.ok(response);
    }
}
