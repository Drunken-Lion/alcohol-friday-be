package com.drunkenlion.alcoholfriday.domain.product.api;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.api.MemberController;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    @Transactional
    void afterEach() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("제품 상세 조회")
    public void t1() throws Exception{
        Product product = Product.builder()
                .name("1000억 막걸리 프리바이오")
                .price(BigDecimal.valueOf(3500))
                .quantity(100L)
                .alcohol(5D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .category(Category.builder().id(1L).lastName("테스트").build())
                .maker(Maker.builder().id(1L).name("테스트").build())
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .distributionPrice(BigDecimal.valueOf(3850.0))
                .build();

        productRepository.save(product);

        ResultActions actions = mvc
                .perform(get("/v1/products/" + product.getId()))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProduct"))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.ingredient", notNullValue()))
                .andExpect(jsonPath("$.alcohol", notNullValue()))
                .andExpect(jsonPath("$.sweet", notNullValue()))
                .andExpect(jsonPath("$.sour", notNullValue()))
                .andExpect(jsonPath("$.cool", notNullValue()))
                .andExpect(jsonPath("$.body", notNullValue()))
                .andExpect(jsonPath("$.balance", notNullValue()))
                .andExpect(jsonPath("$.incense", notNullValue()))
                .andExpect(jsonPath("$.throat", notNullValue()))
                .andExpect(jsonPath("$.categoryName", notNullValue()))
                .andExpect(jsonPath("$.makerName", notNullValue()))
        ;

    }
}