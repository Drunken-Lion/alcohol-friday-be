package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
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
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class RestaurantOrderControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryClassRepository categoryClassRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private RestaurantStockRepository restaurantStockRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private RestaurantOrderDetailRepository restaurantOrderDetailRepository;

    @Autowired
    private RestaurantOrderRefundRepository restaurantOrderRefundRepository;

    @Autowired
    private RestaurantOrderRefundDetailRepository restaurantOrderRefundDetailRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    private static final String OWNER = "owner1@test.com";

    @BeforeEach
    void beforeEach() {
        Member owner = memberRepository.findByEmail(OWNER)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(OWNER)
                        .provider(ProviderType.KAKAO)
                        .name("owner1")
                        .nickname("owner1")
                        .role(MemberRole.OWNER)
                        .phone(1012345678L)
                        .certifyAt(null)
                        .agreedToServiceUse(true)
                        .agreedToServicePolicy(true)
                        .agreedToServicePolicyUse(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .deletedAt(null)
                        .build()));

        Restaurant restaurant = restaurantRepository.save(
                Restaurant.builder()
                        .member(owner)
                        .category("음식점")
                        .name("레스쁘아")
                        .address("서울특별시 종로구 종로8길 16")
                        .location(geometryFactory.createPoint(new Coordinate(37.569343, 126.983857)))
                        .contact(212345678L)
                        .menu(getMenuTest())
                        .time(getTimeTest())
                        .provision(getProvisionTest())
                        .businessName("레스쁘아")
                        .businessNumber("101-10-10001")
                        .addressDetail("101")
                        .postcode("00001")
                        .build());

        CategoryClass categoryClass = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("전통주")
                        .build());

        Category category = categoryRepository.save(
                Category.builder()
                        .categoryClass(categoryClass)
                        .lastName("탁주/막걸리")
                        .build());

        Maker maker = makerRepository.save(
                Maker.builder()
                        .name("(주)국순당")
                        .address("강원도 횡성군 둔내면 강변로 975")
                        .region("강원도 횡성군")
                        .detail("101")
                        .build());

        Product product1 = productRepository.save(
                Product.builder()
                        .name("1000억 막걸리 프리바이오")
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
                        .category(category)
                        .build());

        Product product2 = productRepository.save(
                Product.builder()
                        .name("1000억 유산균막걸리")
                        .price(BigDecimal.valueOf(3200))
                        .quantity(100L)
                        .alcohol(5D)
                        .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                        .sweet(3L)
                        .sour(5L)
                        .cool(5L)
                        .body(3L)
                        .balance(0L)
                        .incense(0L)
                        .throat(0L)
                        .maker(maker)
                        .distributionPrice(BigDecimal.valueOf(3520.0))
                        .category(category)
                        .build());

        restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(product1)
                        .quantity(100L)
                        .restaurant(restaurant)
                        .build());

        restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(product2)
                        .quantity(100L)
                        .restaurant(restaurant)
                        .build());

        RestaurantOrder restaurantOrder = restaurantOrderRepository.save(
                RestaurantOrder.builder()
                        .orderStatus(RestaurantOrderStatus.COMPLETED)
                        .totalPrice(BigDecimal.valueOf(50000))
                        .recipient(owner.getName())
                        .address(restaurant.getAddress())
                        .addressDetail(restaurant.getAddressDetail())
                        .postcode(restaurant.getPostcode())
                        .description("부재시 연락주세요.")
                        .phone(owner.getPhone())
                        .restaurant(restaurant)
                        .member(restaurant.getMember())
                        .build()
        );

        RestaurantOrderDetail restaurantOrderDetail1 =
                restaurantOrderDetailRepository.save(
                        RestaurantOrderDetail.builder()
                                .quantity(2L)
                                .price(product1.getDistributionPrice())
                                .totalPrice(product1.getDistributionPrice().multiply(BigDecimal.valueOf(2)))
                                .restaurantOrder(restaurantOrder)
                                .product(product1)
                                .build());
        restaurantOrderDetail1.addOrder(restaurantOrder);

        RestaurantOrderDetail restaurantOrderDetail2 =
                restaurantOrderDetailRepository.save(
                        RestaurantOrderDetail.builder()
                                .quantity(2L)
                                .price(product2.getDistributionPrice())
                                .totalPrice(product2.getDistributionPrice().multiply(BigDecimal.valueOf(2)))
                                .restaurantOrder(restaurantOrder)
                                .product(product2)
                                .build());
        restaurantOrderDetail2.addOrder(restaurantOrder);

        RestaurantOrderRefund restaurantOrderRefund =
                restaurantOrderRefundRepository.save(
                        RestaurantOrderRefund.builder()
                                .restaurantOrder(restaurantOrder)
                                .totalPrice(BigDecimal.valueOf(3850))
                                .ownerReason("불필요 수량")
                                .status(RestaurantOrderRefundStatus.COMPLETED_APPROVAL)
                                .restaurant(restaurantOrder.getRestaurant())
                                .build());

        RestaurantOrderRefundDetail restaurantOrderRefundDetail1 =
                restaurantOrderRefundDetailRepository.save(
                        RestaurantOrderRefundDetail.builder()
                                .restaurantOrderRefund(restaurantOrderRefund)
                                .product(product1)
                                .quantity(1L)
                                .price(product1.getDistributionPrice())
                                .totalPrice(product1.getDistributionPrice().multiply(BigDecimal.valueOf(1)))
                                .build());
        restaurantOrderRefundDetail1.addOrderRefund(restaurantOrderRefund);

        RestaurantOrderRefundDetail restaurantOrderRefundDetail2 =
                restaurantOrderRefundDetailRepository.save(
                        RestaurantOrderRefundDetail.builder()
                                .restaurantOrderRefund(restaurantOrderRefund)
                                .product(product2)
                                .quantity(1L)
                                .price(product2.getDistributionPrice())
                                .totalPrice(product2.getDistributionPrice().multiply(BigDecimal.valueOf(1)))
                                .build());
        restaurantOrderRefundDetail2.addOrderRefund(restaurantOrderRefund);
    }

    private Map<String, Object> getMenuTest() {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("비빔밥", 8000);
        frame.put("불고기", 12000);
        return frame;
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put("holiday", true);
        allDayTime.put("etc", "명절 당일만 휴업");

        TimeData timeData = TimeData.builder()
                .businessStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .breakBusinessStatus(true)
                .breakStartTime(LocalTime.of(15, 0))
                .breakEndTime(LocalTime.of(17, 0))
                .build();

        for (DayInfo value : DayInfo.values()) {
            allDayTime.put(value.toString(), timeData);
        }

        return allDayTime;
    }

    private Map<String, Object> getProvisionTest() {
        Map<String, Object> frame = new LinkedHashMap<>();

        for (Provision value : Provision.values()) {
            frame.put(value.toString(), true);
        }
        return frame;
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        productRepository.deleteAll();
        categoryClassRepository.deleteAll();
        categoryRepository.deleteAll();
        makerRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        restaurantOrderRepository.deleteAll();
        restaurantOrderDetailRepository.deleteAll();
        restaurantOrderRefundRepository.deleteAll();
        restaurantOrderRefundDetailRepository.deleteAll();
    }

    @Test
    @DisplayName("모든 발주 내역 조회 (Admin)")
    @WithAccount(email = "admin@test.com", role = MemberRole.ADMIN)
    void getRestaurantOrdersByAdminTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurant-orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderController.class))
                .andExpect(handler().methodName("getRestaurantOrdersByAdminOrStoreManager"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].orderStatus", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].businessName", notNullValue()))
                .andExpect(jsonPath("$.data[0].address", notNullValue()))
                .andExpect(jsonPath("$.data[0].addressDetail", notNullValue()))
                .andExpect(jsonPath("$.data[0].postcode", notNullValue()))
                .andExpect(jsonPath("$.data[0].description", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].details[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].makerName", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].orderQuantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].file", nullValue()))
                .andExpect(jsonPath("$.data[0].details[1].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].details[1].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].makerName", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].orderQuantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].file", nullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("모든 발주 내역 조회 (StoreManager)")
    @WithAccount(email = "storeManager@test.com", role = MemberRole.STORE_MANAGER)
    void getRestaurantOrdersByStoreManagerTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurant-orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderController.class))
                .andExpect(handler().methodName("getRestaurantOrdersByAdminOrStoreManager"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].orderStatus", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].businessName", notNullValue()))
                .andExpect(jsonPath("$.data[0].address", notNullValue()))
                .andExpect(jsonPath("$.data[0].addressDetail", notNullValue()))
                .andExpect(jsonPath("$.data[0].postcode", notNullValue()))
                .andExpect(jsonPath("$.data[0].description", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].details[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].makerName", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].orderQuantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[0].file", nullValue()))
                .andExpect(jsonPath("$.data[0].details[1].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].details[1].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].makerName", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].orderQuantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.data[0].details[1].file", nullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("사장의 발주 내역 조회")
    @WithAccount(email = OWNER, role = MemberRole.OWNER)
    void getRestaurantOrdersByOwnerTest() throws Exception {
        Restaurant restaurant = restaurantRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurant-orders/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("restaurantId", restaurant.getId().toString()))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderController.class))
                .andExpect(handler().methodName("getRestaurantOrdersByOwner"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].orderStatus", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].businessName", notNullValue()))
                .andExpect(jsonPath("$.data[0].address", notNullValue()))
                .andExpect(jsonPath("$.data[0].addressDetail", notNullValue()))
                .andExpect(jsonPath("$.data[0].postcode", notNullValue()))
                .andExpect(jsonPath("$.data[0].description", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].orderDetails[0].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[0].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[0].refundQuantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[0].file", nullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[1].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].orderDetails[1].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[1].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[1].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[1].quantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[1].refundQuantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails[1].file", nullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("발주를 위한 제품 목록 조회")
    @WithAccount(email = OWNER, role = MemberRole.OWNER)
    void getRestaurantOrderProductsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurant-orders/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderController.class))
                .andExpect(handler().methodName("getRestaurantOrderProducts"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data.[0].name", notNullValue()))
                .andExpect(jsonPath("$.data.[0].makerName", notNullValue()))
                .andExpect(jsonPath("$.data.[0].price", notNullValue()))
                .andExpect(jsonPath("$.data.[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.data.[0].file", nullValue()))
                .andExpect(jsonPath("$.data.[1].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data.[1].name", notNullValue()))
                .andExpect(jsonPath("$.data.[1].makerName", notNullValue()))
                .andExpect(jsonPath("$.data.[1].price", notNullValue()))
                .andExpect(jsonPath("$.data.[1].quantity", notNullValue()))
                .andExpect(jsonPath("$.data.[1].file", nullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }
}
