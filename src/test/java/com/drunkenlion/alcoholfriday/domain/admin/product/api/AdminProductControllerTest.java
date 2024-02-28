package com.drunkenlion.alcoholfriday.domain.admin.product.api;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminProductControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryClassRepository categoryClassRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Maker 제조사_국순당 = makerRepository.save( // 1
                Maker.builder()
                        .name("(주)국순당")
                        .address("강원도 횡성군 둔내면 강변로 975")
                        .detail("101")
                        .region("강원도")
                        .build());

        CategoryClass 카테고리_대분류1 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 1")
                        .build());

        Category 카테고리_소분류1 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류1")
                        .categoryClass(카테고리_대분류1)
                        .build()
        );

        Product 제품_국순당_프리바이오 = productRepository.save(Product.builder()
                .name("1000억 막걸리 프리바이오")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balence(0L)
                .insense(0L)
                .throat(0L)
                .maker(제조사_국순당)
                .category(카테고리_소분류1)
                .build());
    }

    @AfterEach
    @Transactional
    void afterEach() {
        makerRepository.deleteAll();
        categoryClassRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("제품 목록 조회 성공")
    void getProductsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("getProducts"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].makerName", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("제품 상세 조회 성공")
    void getProductTest() throws Exception {
        // given
        Product product = this.productRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("getProduct"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.alcohol", instanceOf(Number.class)))
                .andExpect(jsonPath("$.ingredient", notNullValue()))
                .andExpect(jsonPath("$.sweet", instanceOf(Number.class)))
                .andExpect(jsonPath("$.sour", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cool", instanceOf(Number.class)))
                .andExpect(jsonPath("$.body", instanceOf(Number.class)))
                .andExpect(jsonPath("$.balence", instanceOf(Number.class)))
                .andExpect(jsonPath("$.insense", instanceOf(Number.class)))
                .andExpect(jsonPath("$.throat", instanceOf(Number.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("제품 등록 성공")
    void createProductTest() throws Exception {
        // given
        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();
        Long makerId = this.makerRepository.findAll().get(0).getId();

        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "categoryLastId": %d,
                                  "name": "test 제품명",
                                  "makerId": %d,
                                  "price": 100000,
                                  "quantity": 1000,
                                  "alcohol": 0,
                                  "ingredient": "test 쌀(국내산), 밀(국내산), 누룩, 정제수",
                                  "sweet": 0,
                                  "sour": 0,
                                  "cool": 0,
                                  "body": 0,
                                  "balence": 0,
                                  "insense": 0,
                                  "throat": 0
                                }
                                """, categoryLastId, makerId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("createProduct"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.alcohol", instanceOf(Number.class)))
                .andExpect(jsonPath("$.ingredient", notNullValue()))
                .andExpect(jsonPath("$.sweet", instanceOf(Number.class)))
                .andExpect(jsonPath("$.sour", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cool", instanceOf(Number.class)))
                .andExpect(jsonPath("$.body", instanceOf(Number.class)))
                .andExpect(jsonPath("$.balence", instanceOf(Number.class)))
                .andExpect(jsonPath("$.insense", instanceOf(Number.class)))
                .andExpect(jsonPath("$.throat", instanceOf(Number.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("제품 수정 성공")
    void modifyProductTest() throws Exception {
        // given
        Product product = this.productRepository.findAll().get(0);
        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();
        Long makerId = this.makerRepository.findAll().get(0).getId();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "categoryLastId": %d,
                                  "name": "test 제품명",
                                  "makerId": %d,
                                  "price": 100000,
                                  "quantity": 1000,
                                  "alcohol": 0,
                                  "ingredient": "test 쌀(국내산), 밀(국내산), 누룩, 정제수",
                                  "sweet": 0,
                                  "sour": 0,
                                  "cool": 0,
                                  "body": 0,
                                  "balence": 0,
                                  "insense": 0,
                                  "throat": 0,
                                  "remove": [
                                        1
                                      ]
                                }
                                """, categoryLastId, makerId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("modifyProduct"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.alcohol", instanceOf(Number.class)))
                .andExpect(jsonPath("$.ingredient", notNullValue()))
                .andExpect(jsonPath("$.sweet", instanceOf(Number.class)))
                .andExpect(jsonPath("$.sour", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cool", instanceOf(Number.class)))
                .andExpect(jsonPath("$.body", instanceOf(Number.class)))
                .andExpect(jsonPath("$.balence", instanceOf(Number.class)))
                .andExpect(jsonPath("$.insense", instanceOf(Number.class)))
                .andExpect(jsonPath("$.throat", instanceOf(Number.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("제품 삭제 성공")
    void deleteProductTest() throws Exception {
        // given
        Product product = this.productRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("deleteProduct"));
    }
}
