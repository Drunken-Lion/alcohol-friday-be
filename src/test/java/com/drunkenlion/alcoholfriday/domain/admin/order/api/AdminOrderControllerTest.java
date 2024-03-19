package com.drunkenlion.alcoholfriday.domain.admin.order.api;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
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
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.payment.dao.PaymentRepository;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.*;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class AdminOrderControllerTest {
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

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FileService fileService;

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
                .alcohol(5D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
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

        MockMultipartFile multipartFile1 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("files", "test2.txt", "text/plain", "test2 file".getBytes(StandardCharsets.UTF_8));

        fileService.saveFiles(상품_1, List.of(multipartFile1, multipartFile2));

        ItemProduct 상품상세1 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_1)
                        .product(제품_국순당_프리바이오)
                        .quantity(100L)
                        .build());


        Member 회원_일반회원5 = memberRepository.save(Member.builder()
                .email("member5@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member5")
                .nickname("Member5")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        com.drunkenlion.alcoholfriday.domain.order.entity.Order 주문_1 =
                orderRepository.save(
                        com.drunkenlion.alcoholfriday.domain.order.entity.Order.builder()
                                .orderNo("주문_1")
                                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                                .price(BigDecimal.valueOf(20000))
                                .deliveryPrice(BigDecimal.valueOf(2500))
                                .totalPrice(BigDecimal.valueOf(22500))
                                .recipient("테스트회원5")
                                .phone(1012345678L)
                                .address("서울시 마포구 연남동")
                                .addressDetail("123-12번지")
                                .description("부재 시 문앞에 놓아주세요.")
                                .postcode("123123")
                                .member(회원_일반회원5)
                                .build());

        OrderDetail 주문상품_1 = orderDetailRepository.save(
                OrderDetail.builder()
                        .itemPrice(상품_1.getPrice())
                        .quantity(1L)
                        .totalPrice(상품_1.getPrice())
                        .item(상품_1)
                        .order(주문_1)
                        .review(null)
                        .build());

        Payment 결제_1 = paymentRepository.save(
                Payment.builder()
                        .paymentNo("jPR7DvYpNk6bJXmgo01emDojZdPByA8LAnGKWx4qMl00aEwB")
                        .paymentStatus(PaymentStatus.DONE)
                        .paymentMethod(PaymentMethod.CARD)
                        .paymentProvider(PaymentProvider.TOSS_PAY)
                        .paymentCardType(PaymentCardType.CHECK)
                        .paymentOwnerType(PaymentOwnerType.PERSONAL)
                        .issuerCode(PaymentCardCode.SHINHAN)
                        .acquirerCode(PaymentCardCode.SHINHAN)
                        .totalPrice(주문_1.getTotalPrice())
                        .requestedAt(LocalDateTime.now())
                        .approvedAt(LocalDateTime.now())
                        .currency("KRW")
                        .order(주문_1)
                        .member(회원_일반회원5)
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
        memberRepository.deleteAll();
        orderRepository.deleteAll();
        orderDetailRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("주문 목록 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getOrdersTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("getOrders"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].orderNo", notNullValue()))
                .andExpect(jsonPath("$.data[0].customerName", notNullValue()))
                .andExpect(jsonPath("$.data[0].customerNickname", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderStatus", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getOrderTest() throws Exception {
        // given
        Order order = this.orderRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/orders/" + order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("getOrder"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderNo", notNullValue()))
                .andExpect(jsonPath("$.customerName", notNullValue()))
                .andExpect(jsonPath("$.orderStatus", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].name", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].price", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].itemFile", notNullValue()))
                .andExpect(jsonPath("$.recipient", notNullValue()))
                .andExpect(jsonPath("$.phone", instanceOf(Number.class)))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.addressDetail", notNullValue()))
                .andExpect(jsonPath("$.postcode", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.issuerCode", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.paymentStatus", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("주문 수정 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void modifyOrderTest() throws Exception {
        // given
        Order order = this.orderRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/orders/" + order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipient": "받는사람 수정",
                                  "phone": 1011112222,
                                  "address": "주소 수정",
                                  "addressDetail": "상세 주소 수정",
                                  "postcode": 123456,
                                  "description": "메모 수정"
                                }
                                """)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminOrderController.class))
                .andExpect(handler().methodName("modifyOrder"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderNo", notNullValue()))
                .andExpect(jsonPath("$.customerName", notNullValue()))
                .andExpect(jsonPath("$.orderStatus", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].name", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].price", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].totalPrice", notNullValue()))
                .andExpect(jsonPath("$.orderItems[0].itemFile", notNullValue()))
                .andExpect(jsonPath("$.recipient", notNullValue()))
                .andExpect(jsonPath("$.phone", instanceOf(Number.class)))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.addressDetail", notNullValue()))
                .andExpect(jsonPath("$.postcode", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.issuerCode", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.paymentStatus", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }
}
