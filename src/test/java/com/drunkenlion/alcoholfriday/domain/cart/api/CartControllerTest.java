package com.drunkenlion.alcoholfriday.domain.cart.api;

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
import com.drunkenlion.alcoholfriday.global.security.auth.UserDetailsServiceImpl;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

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
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryClassRepository.deleteAll();
    }


    @Test
    @DisplayName("장바구니에 한 개 상품 등록")
    @WithAccount
    void addOneItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cartRequestList": [
                                    {
                                      "itemId": "1",
                                      "quantity": "2"
                                    }
                                  ]
                                }
                                """)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("addCartList"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.cartId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cartDetailResponseList[0].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cartDetailResponseList[0].quantity", notNullValue()));
    }
}