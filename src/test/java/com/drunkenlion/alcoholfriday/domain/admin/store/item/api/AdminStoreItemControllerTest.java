package com.drunkenlion.alcoholfriday.domain.admin.store.item.api;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
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
public class AdminStoreItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryClassRepository categoryClassRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemProductRepository itemProductRepository;

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
                        .build());


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

        Item 상품_1 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("프리바이오 막걸리 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("국순당 프리바이오 막걸리 10개입")
                        .category(카테고리_소분류1)
                        .build());

        ItemProduct 상품상세1 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_1)
                        .product(제품_국순당_프리바이오)
                        .quantity(100L)
                        .build());
    }

    @AfterEach
    @Transactional
    void afterEach() {
        makerRepository.deleteAll();
        categoryClassRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        itemRepository.deleteAll();
        itemProductRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void getItemsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/store/items")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminStoreItemController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getItemTest() throws Exception {
        // given
        Item item = this.itemRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/store/items/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminStoreItemController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemProductInfos", notNullValue()))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("상품 등록 성공")
    void createItemTest() throws Exception {
        // given
        Long productId = this.productRepository.findAll().get(0).getId();
        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();

        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/store/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "itemProductInfos": [
                                   {
                                     "productId": %d,
                                     "quantity": 0
                                   }
                                  ],
                                  "categoryLastId": %d,
                                  "name": "test 제품명",
                                  "price": 100000,
                                  "info": "test 정보",
                                  "type": "PROMOTION"
                                }
                                """, productId, categoryLastId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminStoreItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemProductInfos", notNullValue()))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("상품 수정 성공")
    void modifyItemTest() throws Exception {
        // given
        Item item = this.itemRepository.findAll().get(0);
        Long productId = this.productRepository.findAll().get(0).getId();
        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/store/items/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "itemProductInfos": [
                                   {
                                     "productId": %d,
                                     "quantity": 0
                                   }
                                  ],
                                  "categoryLastId": %d,
                                  "name": "test 제품명 수정",
                                  "price": 2000,
                                  "info": "test 정보 수정",
                                  "type": "PROMOTION"
                                }
                                """, productId, categoryLastId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminStoreItemController.class))
                .andExpect(handler().methodName("modifyItem"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemProductInfos", notNullValue()))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteItemTest() throws Exception {
        // given
        Item item = this.itemRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/store/items/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminStoreItemController.class))
                .andExpect(handler().methodName("deleteItem"));
    }
}
