package com.drunkenlion.alcoholfriday.domain.admin.product.application;

import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductRequest;
import org.springframework.data.domain.Page;

public interface AdminProductService {
    Page<ProductListResponse> getProducts(int page, int size);
    ProductDetailResponse getProduct(Long id);
    ProductDetailResponse createProduct(ProductRequest productRequest);
    ProductDetailResponse modifyProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
}
