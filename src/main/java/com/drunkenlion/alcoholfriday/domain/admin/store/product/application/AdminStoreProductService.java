package com.drunkenlion.alcoholfriday.domain.admin.store.product.application;

import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductListResponse;
import org.springframework.data.domain.Page;

public interface AdminStoreProductService {
    Page<ProductListResponse> getProducts(int page, int size);
}
