package com.drunkenlion.alcoholfriday.domain.cart.api;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
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
import java.util.Optional;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class CartControllerTest {
    @Autowired
    private MockMvc mvc;

    private Long itemId; // 아이템의 ID를 저장할 변수
    private Long itemId2;
    private Long cartId;

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
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private MemberRepository memberRepository;

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
                .alcohol(17.0D)
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
                .alcohol(17.0D)
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

        Optional<Member> member = memberRepository.findByEmail("test@example.com");

        Cart cart = Cart.builder()
                .member(member.get())
                .build();
        Cart savedCart = cartRepository.save(cart);
        cartId = savedCart.getId();

        CartDetail cartDetail = CartDetail.builder()
                .cart(savedCart)
                .item(savedItem)
                .quantity(2L)
                .build();
        CartDetail cartDetail2 = CartDetail.builder()
                .cart(savedCart)
                .item(savedItem2)
                .quantity(3L)
                .build();
        cartDetailRepository.save(cartDetail);
        cartDetailRepository.save(cartDetail2);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryClassRepository.deleteAll();
        cartRepository.deleteAll();
        cartDetailRepository.deleteAll();
    }


    @Test
    @DisplayName("장바구니에 한 개 상품 등록")
    @WithAccount
    void addCartOneItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "cartRequestList": [
                                    {
                                      "itemId": "%d",
                                      "quantity": "2"
                                    }
                                  ]
                                }
                                """.formatted(itemId))
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

    @Test
    @DisplayName("장바구니에 한 개 이상 상품 등록")
    @WithAccount
    void addCartListItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "cartRequestList": [
                                    {
                                      "itemId": "%d",
                                      "quantity": "2"
                                    },
                                    {
                                      "itemId": "%d",
                                      "quantity": "4"
                                    }
                                  ]
                                }
                                """.formatted(itemId, itemId2))
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
                .andExpect(jsonPath("$.cartDetailResponseList[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.cartDetailResponseList[1].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cartDetailResponseList[1].quantity", notNullValue()));
    }

    @Test
    @DisplayName("장바구니 상품 수량 변경")
    @WithAccount
    void modifyCart() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                    "itemId": "%d",
                                    "quantity": "5"
                                }
                                """.formatted(itemId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("modifyCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", notNullValue()));
    }

    @Test
    @DisplayName("추가 시_존재하지 않는 상품일 경우")
    @WithAccount
    void addCartList_notFoundItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "cartRequestList": [
                                    {
                                      "itemId": "100",
                                      "quantity": "2"
                                    }
                                  ]
                                }
                                """)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("addCartList"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."));
    }

    @Test
    @DisplayName("장바구니 조회")
    @WithAccount
    void getCartList() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/carts")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("getCartList"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.cartId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cartDetailResponseList[0].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cartDetailResponseList[1].item.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.totalCartPrice", notNullValue()))
                .andExpect(jsonPath("$.totalCartQuantity", notNullValue()));
    }

    @Test
    @DisplayName("장바구니가 없는 경우_EmptyCart")
    @WithAccount
    void getCartList_EmptyCart() throws Exception {
        // given
        cartDetailRepository.deleteAll();
        cartRepository.deleteAll();

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/carts")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("getCartList"))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.cartId").value(-1))
                .andExpect(jsonPath("$.cartDetailResponseList").isEmpty())
                .andExpect(jsonPath("$.totalCartPrice").value(new BigDecimal("0")))
                .andExpect(jsonPath("$.totalCartQuantity").value(0));
    }

    @Test
    @DisplayName("장바구니에 상품이 없는 경우_EmptyCartDetail")
    @WithAccount
    void getCartList_EmptyCartDetail() throws Exception {
        // given
        cartDetailRepository.deleteAll();

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/carts")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("getCartList"))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.cartId").value(cartId))
                .andExpect(jsonPath("$.cartDetailResponseList").isEmpty())
                .andExpect(jsonPath("$.totalCartPrice").value(new BigDecimal("0")))
                .andExpect(jsonPath("$.totalCartQuantity").value(0));
    }

    @Test
    @DisplayName("장바구니에 한 개 상품 삭제")
    @WithAccount
    void deleteCartOneItem() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "cartDeleteReqList": [
                                    {
                                      "itemId": "%d"
                                    }
                                  ]
                                }
                                """.formatted(itemId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("deleteCartList"));
    }

    @Test
    @DisplayName("장바구니에 한 개 이상 상품 삭제")
    @WithAccount
    void deleteCartList() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "cartDeleteReqList": [
                                    {
                                      "itemId": "%d"
                                    },
                                    {
                                      "itemId": "%d"
                                    }
                                  ]
                                }
                                """.formatted(itemId, itemId2))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("deleteCartList"));
    }

    @Test
    @DisplayName("삭제 시_장바구니에 상품이 없는 경우_EmptyCart")
    @WithAccount
    void deleteCartList_EmptyCart() throws Exception {
        // given
        cartDetailRepository.deleteAll();
        cartRepository.deleteAll();

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "cartDeleteReqList": [
                                    {
                                      "itemId": "%d"
                                    }
                                  ]
                                }
                                """.formatted(itemId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("deleteCartList"))
                .andExpect(jsonPath("$.message").value("장바구니에 상품 내역이 없습니다."));
    }
}