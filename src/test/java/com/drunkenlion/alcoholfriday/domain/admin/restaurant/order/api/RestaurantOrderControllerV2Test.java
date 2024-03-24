package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveCodeRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
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

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class RestaurantOrderControllerV2Test {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;
    @Autowired
    private RestaurantOrderDetailRepository restaurantOrderDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RestaurantOrderCartRepository restaurantOrderCartRepository;
    @Autowired
    private RestaurantOrderCartDetailRepository restaurantOrderCartDetailRepository;
    @Autowired
    private RestaurantStockRepository restaurantStockRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MakerRepository makerRepository;

    @AfterEach
    @Transactional
    public void after() {
        restaurantOrderRepository.deleteAll();
        restaurantOrderDetailRepository.deleteAll();
        productRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantOrderCartRepository.deleteAll();
        restaurantOrderCartDetailRepository.deleteAll();
        restaurantStockRepository.deleteAll();
    }

    @Test
    @DisplayName("발주 임시 등록")
    @WithAccount(role = MemberRole.OWNER)
    public void t1() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("(주)국순당")
                .address("강원도 횡성군 둔내면 강변로 975")
                .region("강원도 횡성군")
                .detail("101")
                .build());

        Product product = productRepository.save(
                Product.builder().name("1000억 막걸리 프리바이오")
                        .price(BigDecimal.valueOf(3500))
                        .quantity(100L)
                        .alcohol(5D)
                        .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                        .sweet(3L)
                        .sour(4L)
                        .cool(3L)
                        .body(3L)
                        .balance(0L)
                        .incense(0L)
                        .throat(0L)
                        .maker(maker)
                        .distributionPrice(BigDecimal.valueOf(3850.0))
                        .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .category("음식점")
                .name("레스쁘아")
                .address("서울특별시 종로구 종로8길 16")
                .contact(212345678L)
                .menu(Map.of("메뉴1", 5000, "메뉴2", 6000, "메뉴3", 7000))
                .businessName("레스쁘아")
                .businessNumber("101-10-10001")
                .addressDetail("101")
                .postcode("00001")
                .build());

        RestaurantStock restaurantStock = restaurantStockRepository.save(RestaurantStock.builder()
                .product(product)
                .quantity(100L)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(
                RestaurantOrderCartDetail.builder()
                        .restaurantOrderCart(restaurantOrderCart)
                        .product(product)
                        .quantity(2L)
                        .build());

        restaurantOrderCartDetail.addCart(restaurantOrderCart);

        restaurantOrderCartDetailRepository.save(restaurantOrderCartDetail);
        restaurantOrderCartRepository.save(restaurantOrderCart);

        RestaurantOrderSaveCodeRequest restaurantOrderSaveCodeRequest = RestaurantOrderSaveCodeRequest.builder()
                .restaurantId(restaurant.getId())
                .build();

        String request = JsonConvertor.build(restaurantOrderSaveCodeRequest);

        ResultActions actions = mvc
                .perform(post("/v1/admin/restaurant-orders/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(request)
                ).andDo(print());

        actions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(RestaurantOrderControllerV2.class))
                .andExpect(handler().methodName("getSaveCode"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.businessName", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.member", notNullValue()))
                .andExpect(jsonPath("$.details", notNullValue()))
        ;
    }

    @Test
    @DisplayName("발주 등록")
    @WithAccount(role = MemberRole.OWNER)
    public void t2() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("(주)국순당")
                .address("강원도 횡성군 둔내면 강변로 975")
                .region("강원도 횡성군")
                .detail("101")
                .build());

        Product product = productRepository.save(
                Product.builder().name("1000억 막걸리 프리바이오")
                        .price(BigDecimal.valueOf(3500))
                        .quantity(100L)
                        .alcohol(5D)
                        .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                        .sweet(3L)
                        .sour(4L)
                        .cool(3L)
                        .body(3L)
                        .balance(0L)
                        .incense(0L)
                        .throat(0L)
                        .maker(maker)
                        .distributionPrice(BigDecimal.valueOf(3850.0))
                        .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .category("음식점")
                .name("레스쁘아")
                .address("서울특별시 종로구 종로8길 16")
                .contact(212345678L)
                .menu(Map.of("메뉴1", 5000, "메뉴2", 6000, "메뉴3", 7000))
                .businessName("레스쁘아")
                .businessNumber("101-10-10001")
                .addressDetail("101")
                .postcode("00001")
                .build());

        RestaurantStock restaurantStock = restaurantStockRepository.save(RestaurantStock.builder()
                .product(product)
                .quantity(100L)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(
                RestaurantOrderCartDetail.builder()
                        .restaurantOrderCart(restaurantOrderCart)
                        .product(product)
                        .quantity(2L)
                        .build());

        restaurantOrderCartDetail.addCart(restaurantOrderCart);

        restaurantOrderCartDetailRepository.save(restaurantOrderCartDetail);
        restaurantOrderCartRepository.save(restaurantOrderCart);

        RestaurantOrder restaurantOrder = restaurantOrderRepository.save(RestaurantOrder.builder()
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .totalPrice(BigDecimal.ZERO)
                .orderStatus(RestaurantOrderStatus.ADD_INFO)
                .address("임시 주소")
                .addressDetail("임시 주소 123")
                .postcode("12345")
                .restaurant(restaurant)
                .member(member)
                .build());

        RestaurantOrderDetail restaurantOrderDetail = restaurantOrderDetailRepository.save(
                RestaurantOrderDetail.builder()
                        .quantity(10L)
                        .price(BigDecimal.valueOf(10000))
                        .totalPrice(BigDecimal.valueOf(100000))
                        .product(product)
                        .build());

        restaurantOrderDetail.addOrder(restaurantOrder);

        restaurantOrderRepository.save(restaurantOrder);
        restaurantOrderDetailRepository.save(restaurantOrderDetail);

        RestaurantOrderSaveRequest restaurantOrderSaveRequest = RestaurantOrderSaveRequest.builder()
                .description("안전하게 배송해 주세요.")
                .phone(1041932693L)
                .recipient("김태섭")
                .build();

        String request = JsonConvertor.build(restaurantOrderSaveRequest);

        ResultActions actions = mvc
                .perform(put("/v1/admin/restaurant-orders/" + restaurantOrder.getId() + "/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(request)
                ).andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderControllerV2.class))
                .andExpect(handler().methodName("saveRestaurantOrder"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.businessName", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.member", notNullValue()))
                .andExpect(jsonPath("$.details", notNullValue()))
        ;
    }

    @Test
    @DisplayName("발주 승인")
    @WithAccount(role = MemberRole.ADMIN)
    public void t3() throws Exception {
        Member member = memberRepository.save(Member.builder()
                .role(MemberRole.OWNER)
                .email("smileby95@test.com")
                .name("test")
                .nickname("test")
                .provider(ProviderType.KAKAO)
                .phone(1012345678L)
                .build());
        Member adminMember = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("(주)국순당")
                .address("강원도 횡성군 둔내면 강변로 975")
                .region("강원도 횡성군")
                .detail("101")
                .build());

        Product product = productRepository.save(
                Product.builder().name("1000억 막걸리 프리바이오")
                        .price(BigDecimal.valueOf(3500))
                        .quantity(100L)
                        .alcohol(5D)
                        .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                        .sweet(3L)
                        .sour(4L)
                        .cool(3L)
                        .body(3L)
                        .balance(0L)
                        .incense(0L)
                        .throat(0L)
                        .maker(maker)
                        .distributionPrice(BigDecimal.valueOf(3850.0))
                        .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .category("음식점")
                .name("레스쁘아")
                .address("서울특별시 종로구 종로8길 16")
                .contact(212345678L)
                .menu(Map.of("메뉴1", 5000, "메뉴2", 6000, "메뉴3", 7000))
                .businessName("레스쁘아")
                .businessNumber("101-10-10001")
                .addressDetail("101")
                .postcode("00001")
                .build());

        RestaurantStock restaurantStock = restaurantStockRepository.save(RestaurantStock.builder()
                .product(product)
                .quantity(100L)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(
                RestaurantOrderCartDetail.builder()
                        .restaurantOrderCart(restaurantOrderCart)
                        .product(product)
                        .quantity(2L)
                        .build());

        restaurantOrderCartDetail.addCart(restaurantOrderCart);

        restaurantOrderCartDetailRepository.save(restaurantOrderCartDetail);
        restaurantOrderCartRepository.save(restaurantOrderCart);

        RestaurantOrder restaurantOrder = restaurantOrderRepository.save(RestaurantOrder.builder()
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .totalPrice(BigDecimal.ZERO)
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .address("임시 주소")
                .addressDetail("임시 주소 123")
                .postcode("12345")
                .restaurant(restaurant)
                .member(member)
                .build());

        RestaurantOrderDetail restaurantOrderDetail = restaurantOrderDetailRepository.save(
                RestaurantOrderDetail.builder()
                        .quantity(10L)
                        .price(BigDecimal.valueOf(10000))
                        .totalPrice(BigDecimal.valueOf(100000))
                        .product(product)
                        .build());

        restaurantOrderDetail.addOrder(restaurantOrder);

        restaurantOrderRepository.save(restaurantOrder);
        restaurantOrderDetailRepository.save(restaurantOrderDetail);

        ResultActions actions = mvc
                .perform(put("/v1/admin/restaurant-orders/" + restaurantOrder.getId())
                ).andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderControllerV2.class))
                .andExpect(handler().methodName("adminOrderApproval"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.businessName", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
        ;
    }

    @Test
    @DisplayName("발주 반려")
    @WithAccount(role = MemberRole.ADMIN)
    public void t4() throws Exception {
        Member member = memberRepository.save(Member.builder()
                .role(MemberRole.OWNER)
                .email("smileby95@test.com")
                .name("test")
                .nickname("test")
                .provider(ProviderType.KAKAO)
                .phone(1012345678L)
                .build());
        Member adminMember = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("(주)국순당")
                .address("강원도 횡성군 둔내면 강변로 975")
                .region("강원도 횡성군")
                .detail("101")
                .build());

        Product product = productRepository.save(
                Product.builder().name("1000억 막걸리 프리바이오")
                        .price(BigDecimal.valueOf(3500))
                        .quantity(100L)
                        .alcohol(5D)
                        .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                        .sweet(3L)
                        .sour(4L)
                        .cool(3L)
                        .body(3L)
                        .balance(0L)
                        .incense(0L)
                        .throat(0L)
                        .maker(maker)
                        .distributionPrice(BigDecimal.valueOf(3850.0))
                        .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(member)
                .category("음식점")
                .name("레스쁘아")
                .address("서울특별시 종로구 종로8길 16")
                .contact(212345678L)
                .menu(Map.of("메뉴1", 5000, "메뉴2", 6000, "메뉴3", 7000))
                .businessName("레스쁘아")
                .businessNumber("101-10-10001")
                .addressDetail("101")
                .postcode("00001")
                .build());

        RestaurantStock restaurantStock = restaurantStockRepository.save(RestaurantStock.builder()
                .product(product)
                .quantity(100L)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(member)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(
                RestaurantOrderCartDetail.builder()
                        .restaurantOrderCart(restaurantOrderCart)
                        .product(product)
                        .quantity(2L)
                        .build());

        restaurantOrderCartDetail.addCart(restaurantOrderCart);

        restaurantOrderCartDetailRepository.save(restaurantOrderCartDetail);
        restaurantOrderCartRepository.save(restaurantOrderCart);

        RestaurantOrder restaurantOrder = restaurantOrderRepository.save(RestaurantOrder.builder()
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .totalPrice(BigDecimal.ZERO)
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .address("임시 주소")
                .addressDetail("임시 주소 123")
                .postcode("12345")
                .restaurant(restaurant)
                .member(member)
                .build());

        RestaurantOrderDetail restaurantOrderDetail = restaurantOrderDetailRepository.save(
                RestaurantOrderDetail.builder()
                        .quantity(10L)
                        .price(BigDecimal.valueOf(10000))
                        .totalPrice(BigDecimal.valueOf(100000))
                        .product(product)
                        .build());

        restaurantOrderDetail.addOrder(restaurantOrder);

        restaurantOrderRepository.save(restaurantOrder);
        restaurantOrderDetailRepository.save(restaurantOrderDetail);

        ResultActions actions = mvc
                .perform(put("/v1/admin/restaurant-orders/" + restaurantOrder.getId() + "/reject")
                ).andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderControllerV2.class))
                .andExpect(handler().methodName("adminOrderRejectedApproval"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.businessName", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
        ;
    }

    @Test
    @DisplayName("발주 취소 (사장)")
    @WithAccount(role = MemberRole.OWNER)
    public void t5() throws Exception {
        Member ownerMember = memberRepository.findByEmail("test@example.com").get();

        Maker maker = makerRepository.save(Maker.builder()
                .name("(주)국순당")
                .address("강원도 횡성군 둔내면 강변로 975")
                .region("강원도 횡성군")
                .detail("101")
                .build());

        Product product = productRepository.save(
                Product.builder().name("1000억 막걸리 프리바이오")
                        .price(BigDecimal.valueOf(3500))
                        .quantity(100L)
                        .alcohol(5D)
                        .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                        .sweet(3L)
                        .sour(4L)
                        .cool(3L)
                        .body(3L)
                        .balance(0L)
                        .incense(0L)
                        .throat(0L)
                        .maker(maker)
                        .distributionPrice(BigDecimal.valueOf(3850.0))
                        .build());

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .member(ownerMember)
                .category("음식점")
                .name("레스쁘아")
                .address("서울특별시 종로구 종로8길 16")
                .contact(212345678L)
                .menu(Map.of("메뉴1", 5000, "메뉴2", 6000, "메뉴3", 7000))
                .businessName("레스쁘아")
                .businessNumber("101-10-10001")
                .addressDetail("101")
                .postcode("00001")
                .build());

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.save(RestaurantOrderCart.builder()
                .member(ownerMember)
                .restaurant(restaurant)
                .build());

        RestaurantOrderCartDetail restaurantOrderCartDetail = restaurantOrderCartDetailRepository.save(
                RestaurantOrderCartDetail.builder()
                        .restaurantOrderCart(restaurantOrderCart)
                        .product(product)
                        .quantity(2L)
                        .build());

        restaurantOrderCartDetail.addCart(restaurantOrderCart);

        restaurantOrderCartDetailRepository.save(restaurantOrderCartDetail);
        restaurantOrderCartRepository.save(restaurantOrderCart);

        RestaurantOrder restaurantOrder = restaurantOrderRepository.save(RestaurantOrder.builder()
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .totalPrice(BigDecimal.ZERO)
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .address("임시 주소")
                .addressDetail("임시 주소 123")
                .postcode("12345")
                .restaurant(restaurant)
                .member(ownerMember)
                .build());

        RestaurantOrderDetail restaurantOrderDetail = restaurantOrderDetailRepository.save(
                RestaurantOrderDetail.builder()
                        .quantity(10L)
                        .price(BigDecimal.valueOf(10000))
                        .totalPrice(BigDecimal.valueOf(100000))
                        .product(product)
                        .build());

        restaurantOrderDetail.addOrder(restaurantOrder);

        restaurantOrderRepository.save(restaurantOrder);
        restaurantOrderDetailRepository.save(restaurantOrderDetail);

        ResultActions actions = mvc
                .perform(put("/v1/admin/restaurant-orders/" + restaurantOrder.getId() + "/cancel/owner")
                ).andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderControllerV2.class))
                .andExpect(handler().methodName("ownerOrderCancel"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.businessName", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
        ;
    }
}
