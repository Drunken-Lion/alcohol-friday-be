package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundDetailCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundRejectRequest;
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
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class RestaurantOrderRefundControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private RestaurantStockRepository restaurantStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryClassRepository categoryClassRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private RestaurantOrderDetailRepository restaurantOrderDetailRepository;

    @Autowired
    private RestaurantOrderRefundRepository restaurantOrderRefundRepository;

    @Autowired
    private RestaurantOrderRefundDetailRepository restaurantOrderRefundDetailRepository;

    @Autowired
    private FileService fileService;

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

    private static final String OWNER = "owner1@af.com";

    @BeforeEach
    @Transactional
    void beforeEach() throws IOException {
        Member owner = memberRepository.findByEmail(OWNER)
                .orElseGet(() -> memberRepository.save(Member.builder().email(OWNER).provider(ProviderType.KAKAO).name("owner1").nickname("owner1").role(MemberRole.OWNER).phone(1012345687L).certifyAt(LocalDate.now()).agreedToServiceUse(true).agreedToServicePolicy(true).agreedToServicePolicyUse(true).build()));
        Restaurant restaurant = restaurantRepository.save(Restaurant.builder().member(owner).category("음식점").name("레스쁘아").address("서울특별시 종로구 종로8길 16").location(geometryFactory.createPoint(new Coordinate(37.569343, 126.983857))).contact(212345678L).menu(getMenuTest()).time(getTimeTest()).provision(getProvisionTest()).businessName("레스쁘아").businessNumber("101-10-10001").addressDetail("101").postcode("00001").build());

        CategoryClass categoryClass = categoryClassRepository.save(CategoryClass.builder().firstName("전통주").build());
        Category category = categoryRepository.save(Category.builder().categoryClass(categoryClass).lastName("탁주/막걸리").build());
        Maker maker = makerRepository.save(Maker.builder().name("(주)국순당").address("강원도 횡성군 둔내면 강변로 975").region("강원도 횡성군").detail("101").build());

        Product product1 = productRepository.save(Product.builder().name("1000억 막걸리 프리바이오").price(BigDecimal.valueOf(3500)).quantity(100L).alcohol(5D).ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수").sweet(3L).sour(4L).cool(3L).body(3L).balance(0L).incense(0L).throat(0L).maker(maker).distributionPrice(BigDecimal.valueOf(3850.0)).category(category).build());
        Product product2 = productRepository.save(Product.builder().name("1000억 유산균막걸리").price(BigDecimal.valueOf(3200)).quantity(100L).alcohol(5D).ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수").sweet(3L).sour(5L).cool(5L).body(3L).balance(0L).incense(0L).throat(0L).maker(maker).distributionPrice(BigDecimal.valueOf(3520.0)).category(category).build());

        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "test1.txt", "test1 file");
        MockMultipartFile multipartFile2 = JsonConvertor.getMockImg("files", "test2.txt", "test2 file");
        fileService.saveFiles(product1, List.of(multipartFile1));
        fileService.saveFiles(product2, List.of(multipartFile2));

        restaurantStockRepository.save(
                RestaurantStock.builder().product(product1).quantity(100L).restaurant(restaurant).build());
        restaurantStockRepository.save(RestaurantStock.builder().product(product2).quantity(100L).restaurant(restaurant).build());

        RestaurantOrder restaurantOrder = restaurantOrderRepository.save(RestaurantOrder.builder().orderStatus(RestaurantOrderStatus.COMPLETED).totalPrice(BigDecimal.valueOf(58300)).address(restaurant.getAddress()).addressDetail(restaurant.getAddressDetail()).description("조심히 배송 부탁드립니다.").postcode(restaurant.getPostcode()).recipient(memberRepository.findById(restaurant.getMember().getId()).get().getName()).phone(memberRepository.findById(restaurant.getMember().getId()).get().getPhone()).restaurant(restaurant).member(restaurant.getMember()).build());

        restaurantOrderDetailRepository.save(RestaurantOrderDetail.builder().quantity(2L).price(product1.getDistributionPrice()).totalPrice(product1.getDistributionPrice().multiply(BigDecimal.valueOf(2))).restaurantOrder(restaurantOrder).product(product1).build());
        restaurantOrderDetailRepository.save(RestaurantOrderDetail.builder().quantity(2L).price(product2.getDistributionPrice()).totalPrice(product2.getDistributionPrice().multiply(BigDecimal.valueOf(2))).restaurantOrder(restaurantOrder).product(product2).build());

        RestaurantOrderRefund restaurantOrderRefund = restaurantOrderRefundRepository.save(RestaurantOrderRefund.builder().restaurantOrder(restaurantOrder).totalPrice(BigDecimal.valueOf(3850)).ownerReason("불필요 수량").status(RestaurantOrderRefundStatus.COMPLETED_APPROVAL).restaurant(restaurantOrder.getRestaurant()).build());

        restaurantOrderRefundDetailRepository.save(RestaurantOrderRefundDetail.builder().restaurantOrderRefund(restaurantOrderRefund).product(product1).quantity(1L).price(product1.getDistributionPrice()).totalPrice(product1.getDistributionPrice().multiply(BigDecimal.valueOf(1))).build());
        restaurantOrderRefundDetailRepository.save(RestaurantOrderRefundDetail.builder().restaurantOrderRefund(restaurantOrderRefund).product(product2).quantity(1L).price(product2.getDistributionPrice()).totalPrice(product2.getDistributionPrice().multiply(BigDecimal.valueOf(1))).build());
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        categoryClassRepository.deleteAll();
        categoryRepository.deleteAll();
        makerRepository.deleteAll();
        productRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        restaurantOrderRepository.deleteAll();
        restaurantOrderDetailRepository.deleteAll();
        restaurantOrderRefundRepository.deleteAll();
        restaurantOrderRefundDetailRepository.deleteAll();
    }

    @Test
    @DisplayName("매장 발주 환불 목록 조회 (사장)")
    @WithAccount(email = OWNER, role = MemberRole.OWNER)
    void t1() throws Exception {
        // given
        Restaurant restaurant = this.restaurantRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurant-order-refunds/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("restaurantId", restaurant.getId().toString())
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderRefundController.class))
                .andExpect(handler().methodName("getRestaurantOrderRefunds"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].refundId", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderId", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderCreatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].businessName", notNullValue()))
                .andExpect(jsonPath("$.data[0].fullAddress", notNullValue()))
                .andExpect(jsonPath("$.data[0].ownerReason", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundCreatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].status", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].file", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].quantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].file", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장)")
    @WithAccount(email = OWNER, role = MemberRole.OWNER)
    void t2() throws Exception {
        // given
        Restaurant restaurant = this.restaurantRepository.findAll().get(0);
        RestaurantOrder restaurantOrder = this.restaurantOrderRepository.findAll().get(0);
        RestaurantOrderDetail restaurantOrderDetail1 = this.restaurantOrderDetailRepository.findAll().get(0);
        Product product1 = this.productRepository.findAll().get(0);

        RestaurantOrderRefundDetailCreateRequest detailRequest = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(restaurantOrderDetail1.getPrice().multiply(BigDecimal.valueOf(1.1)))
                .possibleQuantity(100L)
                .quantity(10L)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason("발주 수량 잘못 입력")
                .refundDetails(List.of(detailRequest))
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/restaurant-order-refunds/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(request))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(RestaurantOrderRefundController.class))
                .andExpect(handler().methodName("createRestaurantOrderRefund"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.refundId", notNullValue()))
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.orderCreatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.businessName", notNullValue()))
                .andExpect(jsonPath("$.fullAddress", notNullValue()))
                .andExpect(jsonPath("$.ownerReason", notNullValue()))
                .andExpect(jsonPath("$.refundCreatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.refundDetails[0].productName", notNullValue()))
                .andExpect(jsonPath("$.refundDetails[0].price", notNullValue()))
                .andExpect(jsonPath("$.refundDetails[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.refundDetails[0].file", notNullValue()));
    }

    @Test
    @DisplayName("매장 발주 환불 취소 (사장)")
    @WithAccount(email = OWNER, role = MemberRole.OWNER)
    void t3() throws Exception {
        // given
        RestaurantOrderRefund refund = this.restaurantOrderRefundRepository.findAll().get(0);
        refund = refund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();
        restaurantOrderRefundRepository.save(refund);

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/restaurant-order-refunds/" + refund.getId() + "/cancel/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderRefundController.class))
                .andExpect(handler().methodName("cancelRestaurantOrderRefund"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.ownerReason", notNullValue()))
                .andExpect(jsonPath("$.adminReason", nullValue()))
                .andExpect(jsonPath("$.status", notNullValue()));
    }

    @Test
    @DisplayName("매장 발주 환불 목록 조회 (관리자)")
    @WithAccount(role = MemberRole.STORE_MANAGER)
    void t4() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurant-order-refunds")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderRefundController.class))
                .andExpect(handler().methodName("getAdminRestaurantOrderRefunds"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].refundId", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderId", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderCreatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].businessName", notNullValue()))
                .andExpect(jsonPath("$.data[0].fullAddress", notNullValue()))
                .andExpect(jsonPath("$.data[0].ownerReason", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundCreatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].status", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[0].file", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].quantity", notNullValue()))
                .andExpect(jsonPath("$.data[0].refundDetails[1].file", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("매장 환불 승인 (관리자)")
    @WithAccount(role = MemberRole.STORE_MANAGER)
    void t5() throws Exception {
        // given
        RestaurantOrderRefund refund = this.restaurantOrderRefundRepository.findAll().get(0);
        refund = refund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();
        restaurantOrderRefundRepository.save(refund);

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/restaurant-order-refunds/" + refund.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderRefundController.class))
                .andExpect(handler().methodName("approvalRestaurantOrderRefund"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.ownerReason", notNullValue()))
                .andExpect(jsonPath("$.adminReason", nullValue()))
                .andExpect(jsonPath("$.status", notNullValue()));
    }

    @Test
    @DisplayName("매장 환불 반려 (관리자)")
    @WithAccount(role = MemberRole.STORE_MANAGER)
    void t6() throws Exception {
        // given
        RestaurantOrderRefund refund = this.restaurantOrderRefundRepository.findAll().get(0);
        refund = refund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();
        restaurantOrderRefundRepository.save(refund);

        RestaurantOrderRefundRejectRequest request = RestaurantOrderRefundRejectRequest.builder()
                .adminReason("관리자의 권한으로 반려")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/restaurant-order-refunds/" + refund.getId() + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(request))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantOrderRefundController.class))
                .andExpect(handler().methodName("rejectRestaurantOrderRefund"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.ownerReason", notNullValue()))
                .andExpect(jsonPath("$.adminReason", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()));
    }
}
