package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @Autowired
    private CategoryClassRepository categoryClassRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName("식품")
                .build();

        Category category = Category.builder()
                .lastName("탁주")
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
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
        product.addCategory(category);

        Item item = Item.builder()
                .name("test ddaattaa")
                .price(new BigDecimal(50000))
                .info("이 상품은 테스트 상품입니다.")
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        categoryClassRepository.save(categoryClass);
        categoryRepository.save(category);
        productRepository.save(product);
        itemRepository.save(item);
        itemProductRepository.save(itemProduct);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryClassRepository.deleteAll();
    }

    @Test
    void searchTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "10")
                        .param("keywordType", "type,name")
                        .param("keyword", "탁주")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].info", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    void getTest() throws Exception {
        // given
        Item saved = this.itemRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items/" + saved.getId()))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("get"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.category.firstName", notNullValue()))
                .andExpect(jsonPath("$.category.lastName", notNullValue()))
                .andExpect(jsonPath("$.products[0].name", notNullValue()))
                .andExpect(jsonPath("$.products[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.products[0].alcohol", notNullValue()))
                .andExpect(jsonPath("$.products[0].ingredient", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sour", notNullValue()))
                .andExpect(jsonPath("$.products[0].cool", notNullValue()))
                .andExpect(jsonPath("$.products[0].body", notNullValue()))
                .andExpect(jsonPath("$.products[0].balence", notNullValue()))
                .andExpect(jsonPath("$.products[0].insense", notNullValue()))
                .andExpect(jsonPath("$.products[0].throat", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()));
    }
}