package com.drunkenlion.alcoholfriday.domain.member.api;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class MemberControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
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
    private AddressRepository addressRepository;
    @Autowired
    private ReviewRepository reviewRepository;


    public static final String EMAIL = "test@example.com";

    @BeforeEach
    @Transactional
    void beforeEach() {
        Member member = memberRepository.findByEmail(EMAIL)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(EMAIL)
                        .provider(ProviderType.KAKAO)
                        .name("테스트")
                        .nickname("test")
                        .role(MemberRole.MEMBER)
                        .phone(1012345678L)
                        .certifyAt(null)
                        .agreedToServiceUse(true)
                        .agreedToServicePolicy(true)
                        .agreedToServicePolicyUse(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .deletedAt(null)
                        .build()));

        Question question = Question.builder()
                .member(member)
                .title("문의 제목1")
                .content("문의 내용1")
                .status(QuestionStatus.COMPLETE)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        questionRepository.save(question);

        CategoryClass categoryClass =
                categoryClassRepository.save(
                        CategoryClass.builder()
                                .firstName("식품")
                                .build());


        Category category =
                categoryRepository.save(
                        Category.builder()
                                .lastName("탁주")
                                .build());
        category.addCategoryClass(categoryClass);

        Product product =
                productRepository.save(
                        Product.builder()
                                .name("테스트 상품")
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
                                .build());
        product.addCategory(category);

        Item item =
                itemRepository.save(
                        Item.builder()
                                .name("테스트 술")
                                .price(new BigDecimal(50000))
                                .info("이 상품은 테스트 상품입니다.")
                                .build());
        item.addCategory(category);

        Item item2 =
                itemRepository.save(
                        Item.builder()
                                .name("테스트 술2")
                                .price(new BigDecimal(20000))
                                .info("이 상품은 테스트 상품입니다2")
                                .build());
        item2.addCategory(category);

        ItemProduct itemProduct = itemProductRepository.save(ItemProduct.builder()
                .item(item)
                .product(product)
                .build());
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        Order order =
                orderRepository.save(Order.builder()
                        .orderNo("order_no")
                        .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                        .price(BigDecimal.valueOf(20000L))
                        .recipient("테스트1")
                        .phone(1012345678L)
                        .address("서울특별시 마포구 연남동")
                        .detail("123-12")
                        .description("부재시 연락주세요.")
                        .postcode(123123L)
                        .build());
        order.addMember(member);

        OrderDetail orderDetail =
                orderDetailRepository.save(
                        OrderDetail.builder()
                                .itemPrice(item.getPrice())
                                .quantity(2L)
                                .totalPrice(BigDecimal.valueOf(100000))
                                .build());
        orderDetail.addItem(item);
        orderDetail.addOrder(order);

        OrderDetail orderDetail2 =
                orderDetailRepository.save(
                        OrderDetail.builder()
                                .itemPrice(item2.getPrice())
                                .quantity(1L)
                                .totalPrice(BigDecimal.valueOf(20000))
                                .build()
                );
        orderDetail2.addItem(item2);
        orderDetail2.addOrder(order);

        Address address = Address.builder()
                .member(member)
                .isPrimary(true)
                .address("서울시 마포구 연남동")
                .detail("123-12번지")
                .postcode(123123L)
                .build();
        addressRepository.save(address);

        Review review = reviewRepository.save(
                Review.builder()
                        .score(5L)
                        .content("맛있어요")
                        .item(item)
                        .member(member)
                        .build());
        review.addOrderDetail(orderDetail);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        questionRepository.deleteAll();
        orderRepository.deleteAll();
        orderDetailRepository.deleteAll();
        itemRepository.deleteAll();
        itemProductRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryClassRepository.deleteAll();
        addressRepository.deleteAll();
        reviewRepository.deleteAll();
    }

    @Test
    @DisplayName("인증된 회원 정보 조회")
    @WithAccount
    void getMemberTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/members/me"))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getAuthMember"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.nickname", notNullValue()))
                .andExpect(jsonPath("$.provider", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("인증된 회원 정보 수정")
    @WithAccount
    void modifyMemberTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/members/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "nickname": "수정테스트",
                                   "phone": 1011112222
                                }
                                """)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("modifyMember"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.nickname", notNullValue()))
                .andExpect(jsonPath("$.provider", notNullValue()))
                .andExpect(jsonPath("$.phone", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("나의 문의내역 조회")
    @WithAccount
    void getMyQuestionsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/members/me/questions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getMyQuestions"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].title", notNullValue()))
                .andExpect(jsonPath("$.data[0].questionStatus", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("나의 주문내역 조회")
    @WithAccount
    void getMyOrdersTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/members/me/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getMyOrders"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].orderNo", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderStatus", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderPrice", notNullValue()))
                .andExpect(jsonPath("$.data[0].recipient", notNullValue()))
                .andExpect(jsonPath("$.data[0].phone", notNullValue()))
                .andExpect(jsonPath("$.data[0].postcode", notNullValue()))
                .andExpect(jsonPath("$.data[0].address", notNullValue()))
                .andExpect(jsonPath("$.data[0].addressDetail", notNullValue()))
                .andExpect(jsonPath("$.data[0].description", notNullValue()))
                .andExpect(jsonPath("$.data[0].orderDetails", instanceOf(List.class)))
        ;
    }

    @Test
    @DisplayName("나의 배송지 목록 조회")
    @WithAccount
    void getMyAddressesTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/members/me/addresses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getMyAddresses"))
                .andExpect(jsonPath("$", instanceOf(List.class)))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$[0].member.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$[0].member.email", notNullValue()))
                .andExpect(jsonPath("$[0].member.nickname", notNullValue()))
                .andExpect(jsonPath("$[0].member.phone", notNullValue()))
                .andExpect(jsonPath("$[0].member.provider", notNullValue()))
                .andExpect(jsonPath("$[0].member.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$[0].member.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$[0].member.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$[0].address", notNullValue()))
                .andExpect(jsonPath("$[0].addressDetail", notNullValue()))
                .andExpect(jsonPath("$[0].postcode", notNullValue()));
    }

    @Test
    @DisplayName("나의 작성할 리뷰 목록 조회")
    @WithAccount
    void getMyPendingReviewsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/members/me/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getMyReviews"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].status", notNullValue()))
                .andExpect(jsonPath("$.data[0].response.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].response.name", notNullValue()))
                .andExpect(jsonPath("$.data[0].response.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].response.totalPrice", instanceOf(Number.class)));
    }

    @Test
    @DisplayName("나의 작성한 리뷰 목록 조회")
    @WithAccount
    void getMyCompleteReviewsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/members/me/reviews")
                        .param("status", "complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getMyReviews"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].status", notNullValue()))
                .andExpect(jsonPath("$.data[0].response.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].response.score", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].response.content", notNullValue()))
                .andExpect(jsonPath("$.data[0].response.productInfo.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].response.productInfo.name", notNullValue()))
                .andExpect(jsonPath("$.data[0].response.productInfo.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].response.productInfo.totalPrice", instanceOf(Number.class)));
    }
}
