package com.drunkenlion.alcoholfriday.domain.admin.store.product.application;

import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductListResponse;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminStoreProductServiceImpl implements AdminStoreProductService {
    private final ProductRepository productRepository;
    public Page<ProductListResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(ProductListResponse::of);
    }

    public ProductDetailResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

        return ProductDetailResponse.of(product);
    }
}
