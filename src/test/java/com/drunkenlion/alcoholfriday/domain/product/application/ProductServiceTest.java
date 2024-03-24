package com.drunkenlion.alcoholfriday.domain.product.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.dto.ProductDetailPageResponse;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.file.application.FileServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("[ProductServiceTest] 제품 Service Test")
class ProductServiceTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private FileServiceImpl fileService;

    @AfterEach
    @Transactional
    public void after() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("제품 상세 조회")
    public void t1() {
        Product product = Product.builder()
                .id(1L)
                .name("1000억 막걸리 프리바이오")
                .price(BigDecimal.valueOf(3500))
                .quantity(100L)
                .alcohol(5D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .category(Category.builder().id(1L).build())
                .maker(Maker.builder().id(1L).build())
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .distributionPrice(BigDecimal.valueOf(3850.0))
                .build();

        Mockito.when(productRepository.findByIdAndDeletedAtIsNull(product.getId())).thenReturn(
                Optional.of(product)
        );

        ProductDetailPageResponse response = productService.findProduct(product.getId());

        assertThat(response.getName()).isEqualTo(product.getName());
        assertThat(response.getAlcohol()).isEqualTo(product.getAlcohol());
        assertThat(response.getBody()).isEqualTo(product.getBody());
        assertThat(response.getIngredient()).isEqualTo(product.getIngredient());
        assertThat(response.getSweet()).isEqualTo(product.getSweet());
        assertThat(response.getSour()).isEqualTo(product.getSour());
        assertThat(response.getCool()).isEqualTo(product.getCool());
        assertThat(response.getBalance()).isEqualTo(product.getBalance());
        assertThat(response.getIncense()).isEqualTo(product.getIncense());
        assertThat(response.getThroat()).isEqualTo(product.getThroat());
    }
}