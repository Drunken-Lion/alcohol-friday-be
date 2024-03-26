package com.drunkenlion.alcoholfriday.domain.admin.product.application;

import com.drunkenlion.alcoholfriday.domain.admin.product.dto.*;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MakerRepository makerRepository;
    private final ItemProductRepository itemProductRepository;
    private final FileService fileService;

    @Override
    public Page<ProductListResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(ProductListResponse::of);
    }

    @Override
    public ProductDetailResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

        NcpFileResponse ncpFileResponse = fileService.findAll(product);

        return ProductDetailResponse.of(product, ncpFileResponse);
    }

    @Override
    @Transactional
    public ProductDetailResponse createProduct(ProductCreateRequest productCreateRequest, List<MultipartFile> files) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(productCreateRequest.getCategoryLastId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        Maker maker = makerRepository.findByIdAndDeletedAtIsNull(productCreateRequest.getMakerId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MAKER)
                        .build());

        Product product = ProductCreateRequest.toEntity(productCreateRequest, category, maker);
        productRepository.save(product);

        NcpFileResponse file = fileService.saveFiles(product, files);

        return ProductDetailResponse.of(product, file);
    }

    @Override
    @Transactional
    public ProductDetailResponse modifyProduct(Long id, ProductModifyRequest productModifyRequest, List<Integer> remove, List<MultipartFile> files) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(productModifyRequest.getCategoryLastId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        Maker maker = makerRepository.findByIdAndDeletedAtIsNull(productModifyRequest.getMakerId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MAKER)
                        .build());

        product = product.toBuilder()
                .name(productModifyRequest.getName())
                .price(productModifyRequest.getPrice())
                .distributionPrice(productModifyRequest.getDistributionPrice())
                .alcohol(productModifyRequest.getAlcohol())
                .ingredient(productModifyRequest.getIngredient())
                .sweet(productModifyRequest.getSweet())
                .sour(productModifyRequest.getSour())
                .cool(productModifyRequest.getCool())
                .body(productModifyRequest.getBody())
                .balance(productModifyRequest.getBalance())
                .incense(productModifyRequest.getIncense())
                .throat(productModifyRequest.getThroat())
                .category(category)
                .maker(maker)
                .build();

        productRepository.save(product);

        NcpFileResponse file = fileService.updateFiles(product, remove, files);

        return ProductDetailResponse.of(product, file);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

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

    @Override
    public ProductQuantityResponse getQuantity(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

        return ProductQuantityResponse.of(product);
    }

    @Override
    @Transactional
    public ProductQuantityResponse modifyQuantity(Long id, ProductQuantityRequest productQuantityRequest) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                        .build());

        product.updateQuantity(productQuantityRequest.getQuantity());
        productRepository.save(product);

        return ProductQuantityResponse.of(product);
    }
}
