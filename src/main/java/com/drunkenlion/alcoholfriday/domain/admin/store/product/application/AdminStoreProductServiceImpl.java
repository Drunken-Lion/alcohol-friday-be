package com.drunkenlion.alcoholfriday.domain.admin.store.product.application;

import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.product.dto.ProductRequest;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
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

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminStoreProductServiceImpl implements AdminStoreProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MakerRepository makerRepository;
    private final ItemProductRepository itemProductRepository;

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

    public ProductDetailResponse createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryLastId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        Maker maker = makerRepository.findById(productRequest.getMakerId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MAKER)
                        .build());

        Product product = ProductRequest.toEntity(productRequest, category, maker);
        productRepository.save(product);

        return ProductDetailResponse.of(product);
    }

    @Transactional
    public ProductDetailResponse modifyProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

        Category category = categoryRepository.findById(productRequest.getCategoryLastId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        Maker maker = makerRepository.findById(productRequest.getMakerId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MAKER)
                        .build());

        product = product.toBuilder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .alcohol(productRequest.getAlcohol())
                .ingredient(productRequest.getIngredient())
                .sweet(productRequest.getSweet())
                .sour(productRequest.getSour())
                .cool(productRequest.getCool())
                .body(productRequest.getBody())
                .balence(productRequest.getBalence())
                .insense(productRequest.getInsense())
                .throat(productRequest.getThroat())
                .category(category)
                .maker(maker)
                .build();

        productRepository.save(product);

        return ProductDetailResponse.of(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

        if (product.getDeletedAt() != null) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                    .build();
        }

        // product 와 관계가 있는 itemProduct 중 삭제 상태가 아닌 것이 있는지 확인
        if (itemProductRepository.existsByProductAndDeletedAtIsNull(product)) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.PRODUCT_IN_USE)
                    .build();
        }

        product = product.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        productRepository.save(product);
    }
}
