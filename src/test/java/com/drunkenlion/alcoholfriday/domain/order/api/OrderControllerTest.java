package com.drunkenlion.alcoholfriday.domain.order.api;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
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
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderCancelRequest;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private Long orderId; // 주문의 ID를 저장할 변수
    public static final String EMAIL = "test@example.com";
    public static final String EMAIL2 = "test2@example.com";

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
    private MemberRepository memberRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

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
                .quantity(3L)
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
                .quantity(3L)
                .build();
        itemProduct2.addItem(item2);
        itemProduct2.addProduct(product2);

        productRepository.save(product2);
        Item savedItem2 = itemRepository.save(item2);
        itemId2 = savedItem2.getId();
        itemProductRepository.save(itemProduct2);

        // Member 등록
        Member member = memberRepository.findByEmail(EMAIL)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(EMAIL)
                        .provider(ProviderType.KAKAO)
                        .name("홍길동")
                        .nickname("hong")
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

        // 주소 등록
        Address address = Address.builder()
                .member(member)
                .isPrimary(true)
                .address("서울특별시 중구 세종대로 110(태평로1가)")
                .addressDetail("서울특별시청 103호")
                .postcode("04524")
                .recipient("홍길동")
                .phone(1012345678L)
                .request("부재시 문앞에 놓아주세요.")
                .build();
        addressRepository.save(address);

        // 주문 등록
        Order order =
                orderRepository.save(Order.builder()
                        .orderNo("240314-221628-987501-1")
                        .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                        .price(BigDecimal.valueOf(220000L))
                        .deliveryPrice(BigDecimal.valueOf(2500L))
                        .totalPrice(BigDecimal.valueOf(225000L))
                        .recipient("테스트1")
                        .phone(1012345678L)
                        .address("서울특별시 마포구 연남동")
                        .addressDetail("123-12")
                        .description("부재시 연락주세요.")
                        .postcode("123123")
                        .member(member)
                        .build());
        orderId = order.getId();

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
    }

    @AfterEach
    @Transactional
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryClassRepository.deleteAll();
        memberRepository.deleteAll();
        addressRepository.deleteAll();
        orderRepository.deleteAll();
        orderDetailRepository.deleteAll();
    }


    @Test
    @DisplayName("[즉시 주문] 한 개 상품 주문 접수")
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
                                  ]
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
    @DisplayName("[장바구니 주문] 한 개 이상 상품 주문 접수")
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
                                  ]
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
                                  ]
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
    @DisplayName("주문 접수 시 상품에 재고가 없을 때")
    @WithAccount
    void orderReceive_outOfItemStock() throws Exception {
        // given
        Item item = itemRepository.findByIdAndDeletedAtIsNull(itemId).get();
        Product product = productRepository.findById(item.getItemProducts().get(0).getProduct().getId()).get();
        product.updateQuantity(1L);

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
                                  ]
                                }
                                """.formatted(itemId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("receive"))
                .andExpect(jsonPath("$.message").value("현재 상품에 재고가 없습니다."));
    }

    @Test
    @DisplayName("구매하기(주문 접수)할 때 등록된 주소가 없는 경우")
    @WithAccount
    void orderReceive_noAddress() throws Exception {
        // given
        addressRepository.deleteAll();

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
                                  ]
                                }
                                """.formatted(itemId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("receive"))
                .andExpect(jsonPath("$.message").value("등록된 주소가 없습니다."));
    }

    @Test
    @DisplayName("주문 생성 후 배송지 업데이트")
    @WithAccount
    void updateOrderAddress() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderNo": "240314-221628-987501-1",
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
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("updateOrderAddress"));
    }

    @Test
    @DisplayName("주문 생성 후 배송지 업데이트 - 주문 번호가 맞지 않는 경우")
    @WithAccount
    void updateOrderAddress_invalidOrderNo() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderNo": "240314-1",
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
                .andExpect(handler().methodName("updateOrderAddress"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 주문입니다."));
    }

    @Test
    @DisplayName("주문 생성 후 배송지 업데이트 - 주문한 회원이 아닐 경우")
    @WithAccount(email = EMAIL2)
    void updateOrderAddress_invalidMember() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "orderNo": "240314-221628-987501-1",
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
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("updateOrderAddress"))
                .andExpect(jsonPath("$.message").value("권한이 없는 접근입니다."));
    }
    @Test
    @DisplayName("[주문 취소] 주문자가 주문 취소 성공")
    @WithAccount
    void orderCancelTest() throws Exception {
        // given
        Order order = orderRepository.findAll().get(0);
        OrderCancelRequest request = OrderCancelRequest.builder()
                .cancelReason("단순 변심")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/orders/" + order.getId() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonConvertor.build(request))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("cancelOrder"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.orderNo", instanceOf(String.class)))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.CANCELLED.name()))
                .andExpect(jsonPath("$.price").value(220000L))
                .andExpect(jsonPath("$.deliveryPrice").value(2500L))
                .andExpect(jsonPath("$.totalPrice").value(225000L))
                .andExpect(jsonPath("$.recipient").value("테스트1"))
                .andExpect(jsonPath("$.phone").value(1012345678L))
                .andExpect(jsonPath("$.postcode").value("123123"))
                .andExpect(jsonPath("$.address").value("서울특별시 마포구 연남동"))
                .andExpect(jsonPath("$.addressDetail").value("123-12"))
                .andExpect(jsonPath("$.description").value("부재시 연락주세요."))
                .andExpect(jsonPath("$.cancelReason").value("단순 변심"))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)));
    }
}