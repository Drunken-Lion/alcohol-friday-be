package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.api;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.application.RestaurantOrderCartServiceImpl;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartDeleteRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartUpdateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.api.MemberController;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.AfterEach;
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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class RestaurantOrderCartControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private RestaurantOrderCartDetailRepository restaurantOrderCartDetailRepository;

    @Autowired
    private RestaurantOrderCartRepository restaurantOrderCartRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        productRepository.deleteAll();
        restaurantOrderCartDetailRepository.deleteAll();
        restaurantOrderCartRepository.deleteAll();
        restaurantRepository.deleteAll();
        makerRepository.deleteAll();
    }

    @Test
    @DisplayName("Owner는 장바구니 저장이 가능하다.")
    @WithAccount(role = MemberRole.OWNER)
    public void t1() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartSaveRequest.builder()
                .restaurantId(restaurant.getId())
                .productId(product.getId())
                .quantity(1L)
                .build());

        ResultActions actions = mvc.perform(post("/v1/admin/restaurant-orders-carts/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("addOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.ableQuantity", notNullValue()))
                .andExpect(jsonPath("$.quantity", notNullValue()))
        ;
    }

    @Test
    @DisplayName("Owner가 아니면 장바구니 저장이 불가능하다.")
    @WithAccount(role = MemberRole.MEMBER)
    public void t2() throws Exception {
        String request = JsonConvertor.build(RestaurantOrderCartSaveRequest.builder()
                .restaurantId(1L)
                .productId(1L)
                .quantity(1L)
                .build());

        ResultActions actions = mvc.perform(post("/v1/admin/restaurant-orders-carts/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("addOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("제품의 재고가 장바구니에 담을 제품의 수량보다 적으면 안된다.")
    @WithAccount(role = MemberRole.OWNER)
    public void t3() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartSaveRequest.builder()
                .restaurantId(restaurant.getId())
                .productId(product.getId())
                .quantity(1000L)
                .build());

        ResultActions actions = mvc.perform(post("/v1/admin/restaurant-orders-carts/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("addOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("조회되지 않는 제품을 구매할 수 없다.")
    @WithAccount(role = MemberRole.OWNER)
    public void t4() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartSaveRequest.builder()
                .restaurantId(restaurant.getId())
                .productId(1000L)
                .quantity(1L)
                .build());

        ResultActions actions = mvc.perform(post("/v1/admin/restaurant-orders-carts/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("addOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("Owner는 장바구니 수정이 가능하다.")
    @WithAccount(role = MemberRole.OWNER)
    public void t5() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(RestaurantOrderCartDetail.builder()
                .product(product)
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartUpdateRequest.builder()
                .productId(product.getId())
                .quantity(1L)
                .build());

        ResultActions actions = mvc.perform(put("/v1/admin/restaurant-orders-carts/"+ restaurantOrderCartDetail.getId()+"/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("updateOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.ableQuantity", notNullValue()))
                .andExpect(jsonPath("$.quantity", notNullValue()))
        ;
    }

    @Test
    @DisplayName("Owner가 아니면 수량 수정이 불가하다.")
    @WithAccount(role = MemberRole.MEMBER)
    public void t6() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(RestaurantOrderCartDetail.builder()
                .product(product)
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartUpdateRequest.builder()
                .productId(product.getId())
                .quantity(1L)
                .build());

        ResultActions actions = mvc.perform(put("/v1/admin/restaurant-orders-carts/"+ restaurantOrderCartDetail.getId()+"/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("updateOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("입력한 수량이 0 이하면 수정이 불가하다.")
    @WithAccount(role = MemberRole.OWNER)
    public void t7() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(RestaurantOrderCartDetail.builder()
                .product(product)
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartUpdateRequest.builder()
                .productId(product.getId())
                .quantity(-1L)
                .build());

        ResultActions actions = mvc.perform(put("/v1/admin/restaurant-orders-carts/"+ restaurantOrderCartDetail.getId()+"/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("updateOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("입력한 수량이 재고 수량 이상이면 수정이 불가하다.")
    @WithAccount(role = MemberRole.OWNER)
    public void t8() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(RestaurantOrderCartDetail.builder()
                .product(product)
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartUpdateRequest.builder()
                .productId(product.getId())
                .quantity(1000L)
                .build());

        ResultActions actions = mvc.perform(put("/v1/admin/restaurant-orders-carts/"+ restaurantOrderCartDetail.getId()+"/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("updateOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("Owner는 장바구니 제품 삭제가 가능하다.")
    @WithAccount(role = MemberRole.OWNER)
    public void t9() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(RestaurantOrderCartDetail.builder()
                .product(product)
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartDeleteRequest.builder()
                .productId(product.getId())
                .build());

        ResultActions actions = mvc.perform(delete("/v1/admin/restaurant-orders-carts/"+ restaurantOrderCartDetail.getId()+"/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("deleteOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.ableQuantity", notNullValue()))
                .andExpect(jsonPath("$.quantity", notNullValue()))
        ;
    }

    @Test
    @DisplayName("Owner가 아니면 장바구니 제품 삭제가 불가능하다.")
    @WithAccount(role = MemberRole.MEMBER)
    public void t10() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("테스트")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(RestaurantOrderCartDetail.builder()
                .product(product)
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build());

        String request = JsonConvertor.build(RestaurantOrderCartDeleteRequest.builder()
                .productId(product.getId())
                .build());

        ResultActions actions = mvc.perform(delete("/v1/admin/restaurant-orders-carts/"+ restaurantOrderCartDetail.getId()+"/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(request)
        ).andDo(print());

        actions
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(RestaurantOrderCartController.class))
                .andExpect(handler().methodName("deleteOwnerCart"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }
}