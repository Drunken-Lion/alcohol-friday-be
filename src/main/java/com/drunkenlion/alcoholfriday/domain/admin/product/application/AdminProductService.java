package com.drunkenlion.alcoholfriday.domain.admin.product.application;

import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductModifyRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminProductService {
    Page<ProductListResponse> getProducts(int page, int size);
    ProductDetailResponse getProduct(Long id);
    ProductDetailResponse createProduct(ProductCreateRequest productCreateRequest, List<MultipartFile> files);
    ProductDetailResponse modifyProduct(Long id, ProductModifyRequest productModifyRequest, List<Integer> remove, List<MultipartFile> files);
    void deleteProduct(Long id);
}
