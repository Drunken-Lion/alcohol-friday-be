package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemProductRepository itemProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void beforeEach() {
        Category category = Category.builder()
                .firstName("식품")
                .middleName("전통주")
                .lastName("탁주")
                .build();
        categoryRepository.save(category);
        categoryRepository.flush();

        Product product = Product.builder()
                .category(category)
                .name("test data")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜, 누룩 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balence(1L)
                .insense(1L)
                .throat(1L)
                .build();
        productRepository.save(product);
        productRepository.flush();

        Item item = Item.builder()
                .category(category)
                .name("test ddaattaa")
                .price(new BigDecimal(50000))
                .info("이 상품은 테스트 상품입니다.")
                .build();
        itemRepository.save(item);
        itemRepository.flush();

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .build();
        itemProductRepository.save(itemProduct);
        itemProductRepository.flush();
    }

    @AfterEach
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void searchTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "size": 10,
                                    "keywordType": [ "type", "name" ],
                                    "keyword": "탁주"
                                }
                                """)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"));
//                .andExpect(jsonPath("$.resultCode", is("200")))
//                .andExpect(jsonPath("$.msg", is(Message.Success.LOGIN_SUCCESS.getMessage())))
//                .andExpect(jsonPath("$.data.item.id", instanceOf(Number.class)))
//                .andExpect(jsonPath("$.data.item.createDate", matchesPattern(DATE_PATTERN)))
//                .andExpect(jsonPath("$.data.item.modifyDate", matchesPattern(DATE_PATTERN)))
//                .andExpect(jsonPath("$.data.item.username", notNullValue()))
//                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
    }
}