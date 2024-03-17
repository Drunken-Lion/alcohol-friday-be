package com.drunkenlion.alcoholfriday.domain.order.api;

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
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class OrderControllerTest {
    @Autowired
    private MockMvc mvc;

    private Long itemId; // 아이템의 ID를 저장할 변수
    private Long itemId2;

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

        // Item1
        Product product = Product.builder()
                .name("test data")
                .quantity(10L)
                .alcohol(17D)
                .ingredient("알콜, 누룩 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
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
        Item savedItem = itemRepository.save(item);
        itemId = savedItem.getId();
        itemProductRepository.save(itemProduct);

        // Item2
        Product product2 = Product.builder()
                .name("test data2")
                .quantity(10L)
                .alcohol(17D)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        product2.addCategory(category);

        Item item2 = Item.builder()
                .name("test dd")
                .price(new BigDecimal(100000))
                .info("이 상품은 테스트 상품2입니다.")
                .build();
        item2.addCategory(category);

        ItemProduct itemProduct2 = ItemProduct.builder()
                .item(item2)
                .product(product2)
                .build();
        itemProduct2.addItem(item2);
        itemProduct2.addProduct(product2);

        productRepository.save(product2);
        Item savedItem2 = itemRepository.save(item2);
        itemId2 = savedItem2.getId();
        itemProductRepository.save(itemProduct2);
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
    @DisplayName("한 개 상품 주문 접수")
    @WithAccount
    void orderReceive_oneItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderItemList": [
                                    {
                                      "itemId": "%d",
                                      "quantity": "2"
                                    }
                                  ],
                                  "recipient" : "홍길동",
                                  "phone" : "1012345678",
                                  "address" : "서울특별시 중구 세종대로 110(태평로1가)",
                                  "addressDetail" : "서울특별시청 103호",
                                  "description" : "부재시 문앞에 놓아주세요.",
                                  "postcode" : "04524"
                                }
                                """.formatted(itemId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("receive"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderNo", instanceOf(String.class)))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.ORDER_RECEIVED.name()))
                .andExpect(jsonPath("$.price").value(100000L))
                .andExpect(jsonPath("$.deliveryPrice").value(2500L))
                .andExpect(jsonPath("$.totalPrice").value(102500L))
                .andExpect(jsonPath("$.totalQuantity").value(2L))
                .andExpect(jsonPath("$.orderDetails[0].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderDetails[0].item.price").value(50000L));
    }

    @Test
    @DisplayName("한 개 이상 상품 주문 접수")
    @WithAccount
    void orderReceive_itemList() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderItemList": [
                                    {
                                      "itemId": "%d",
                                      "quantity": "2"
                                    },
                                    {
                                      "itemId": "%d",
                                      "quantity": "1"
                                    }
                                  ],
                                  "recipient" : "홍길동",
                                  "phone" : "1012345678",
                                  "address" : "서울특별시 중구 세종대로 110(태평로1가)",
                                  "addressDetail" : "서울특별시청 103호",
                                  "description" : "부재시 문앞에 놓아주세요.",
                                  "postcode" : "04524"
                                }
                                """.formatted(itemId, itemId2))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("receive"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderNo", instanceOf(String.class)))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.ORDER_RECEIVED.name()))
                .andExpect(jsonPath("$.price").value(200000L))
                .andExpect(jsonPath("$.deliveryPrice").value(2500L))
                .andExpect(jsonPath("$.totalPrice").value(202500L))
                .andExpect(jsonPath("$.totalQuantity").value(3L))
                .andExpect(jsonPath("$.orderDetails[0].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderDetails[0].item.price").value(50000L))
                .andExpect(jsonPath("$.orderDetails[1].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderDetails[1].item.price").value(100000L));
    }

    @Test
    @DisplayName("없는 상품 주문 접수")
    @WithAccount
    void orderReceive_noItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderItemList": [
                                    {
                                      "itemId": "100",
                                      "quantity": "2"
                                    }
                                  ],
                                  "recipient" : "홍길동",
                                  "phone" : "1012345678",
                                  "address" : "서울특별시 중구 세종대로 110(태평로1가)",
                                  "addressDetail" : "서울특별시청 103호",
                                  "description" : "부재시 문앞에 놓아주세요.",
                                  "postcode" : "04524"
                                }
                                """)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("receive"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."));
    }

    @Test
    @DisplayName("한 개 이상 상품 주문 접수")
    @WithAccount
    void orderReceive_itemList() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderItemList": [
                                    {
                                      "itemId": "%d",
                                      "quantity": "2"
                                    },
                                    {
                                      "itemId": "%d",
                                      "quantity": "1"
                                    }
                                  ],
                                  "recipient" : "홍길동",
                                  "phone" : "1012345678",
                                  "address" : "서울특별시 중구 세종대로 110(태평로1가)",
                                  "detail" : "서울특별시청 103호",
                                  "description" : "부재시 문앞에 놓아주세요.",
                                  "postcode" : "04524"
                                }
                                """.formatted(itemId, itemId2))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("receive"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderNo", instanceOf(String.class)))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.ORDER_RECEIVED.name()))
                .andExpect(jsonPath("$.totalPrice").value(200000L))
                .andExpect(jsonPath("$.totalQuantity").value(3L))
                .andExpect(jsonPath("$.itemList[0].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemList[0].item.price").value(50000L))
                .andExpect(jsonPath("$.itemList[1].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemList[1].item.price").value(100000L));
    }

    @Test
    @DisplayName("없는 상품 주문 접수")
    @WithAccount
    void orderReceive_noItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderItemList": [
                                    {
                                      "itemId": "100",
                                      "quantity": "2"
                                    }
                                  ],
                                  "recipient" : "홍길동",
                                  "phone" : "1012345678",
                                  "address" : "서울특별시 중구 세종대로 110(태평로1가)",
                                  "detail" : "서울특별시청 103호",
                                  "description" : "부재시 문앞에 놓아주세요.",
                                  "postcode" : "04524"
                                }
                                """.formatted(itemId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("receive"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."));
    }
}