package com.drunkenlion.alcoholfriday.domain.product.application;

import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.dto.ProductDetailPageResponse;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final FileService fileService;
    @Override
    public ProductDetailPageResponse findProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_PRODUCT));
        NcpFileResponse productImages = fileService.findAll(product);
        return ProductDetailPageResponse.of(product, productImages);
    }
}
