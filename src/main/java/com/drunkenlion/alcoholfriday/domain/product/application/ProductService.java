package com.drunkenlion.alcoholfriday.domain.product.application;

import com.drunkenlion.alcoholfriday.domain.product.dto.ProductDetailPageResponse;

public interface ProductService {
    ProductDetailPageResponse findProduct(Long id);
}
