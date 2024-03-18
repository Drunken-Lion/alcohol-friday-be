package com.drunkenlion.alcoholfriday.domain.admin.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderModifyRequest;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.payment.dao.PaymentRepository;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.*;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminOrderServiceTest {
    @InjectMocks
    private AdminOrderServiceImpl adminOrderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private FileService fileService;

    private final Long orderId = 1L;
    private final String orderNo = "주문_1";
    private final OrderStatus orderStatus = OrderStatus.PAYMENT_COMPLETED;
    private final BigDecimal price = BigDecimal.valueOf(20000);
    private final BigDecimal deliveryPrice = BigDecimal.valueOf(2500);
    private final BigDecimal totalPrice = BigDecimal.valueOf(22500);
    private final String recipient = "테스트회원5";
    private final Long phone = 1012345678L;
    private final String address = "서울시 마포구 연남동";
    private final String addressDetail = "123-12번지";
    private final String description = "부재 시 문앞에 놓아주세요.";
    private final String postcode = "123123";

    private final String modifyRecipient = "테스트회원5 수정";
    private final Long modifyPhone = 1011112222L;
    private final String modifyAddress = "서울시 마포구 연남동 수정";
    private final String modifyAddressDetail = "123-12번지 수정";
    private final String modifyDescription = "부재 시 문앞에 놓아주세요. 수정";
    private final String modifyPostcode = "111111";

    private final Long orderDetailQuantity = 2L;

    private final String memberName = "멤버";
    private final String memberNickname = "Member";


    private final Long itemId = 1L;
    private final ItemType itemType = ItemType.REGULAR;
    private final String itemName = "프리바이오 막걸리 10개";
    private final BigDecimal itemPrice = BigDecimal.valueOf(20000);
    private final String itemInfo = "국순당 프리바이오 막걸리 10개입";

    private final Long itemProductId = 1L;
    private final Long itemProductQuantity = 100L;

    private final PaymentStatus paymentStatus = PaymentStatus.DONE;
    private final PaymentCardCode issuerCode = PaymentCardCode.SHINHAN;

    private final int page = 0;
    private final int size = 20;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();

    @Test
    @DisplayName("주문 목록 조회 성공 - All")
    public void getAllOrdersTest() {
        // given
        when(this.orderRepository.findOrderList(any(Pageable.class), any())).thenReturn(this.getOrderList());

        // when
        Page<OrderListResponse> orders = this.adminOrderService.getOrdersByOrderStatus(page, size, null);

        // then
        List<OrderListResponse> content = orders.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(orderId);
        assertThat(content.get(0).getOrderNo()).isEqualTo(orderNo);
        assertThat(content.get(0).getCustomerName()).isEqualTo(memberName);
        assertThat(content.get(0).getCustomerNickname()).isEqualTo(memberNickname);
        assertThat(content.get(0).getOrderStatus()).isEqualTo(orderStatus);
        assertThat(content.get(0).getPrice()).isEqualTo(price);
        assertThat(content.get(0).getIssuerName()).isEqualTo(PaymentCardCode.ofCardName(issuerCode));
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("주문 목록 조회 성공 - OrderStatus")
    public void getStatusOrdersTest() {
        // given
        when(this.orderRepository.findOrderList(any(Pageable.class), any())).thenReturn(this.getStatusOrderList());

        // when
        Page<OrderListResponse> orders = this.adminOrderService.getOrdersByOrderStatus(page, size, OrderStatus.CANCELLED);

        // then
        List<OrderListResponse> content = orders.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(orderId);
        assertThat(content.get(0).getOrderNo()).isEqualTo(orderNo);
        assertThat(content.get(0).getCustomerName()).isEqualTo(memberName);
        assertThat(content.get(0).getCustomerNickname()).isEqualTo(memberNickname);
        assertThat(content.get(0).getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(content.get(0).getPrice()).isEqualTo(price);
        assertThat(content.get(0).getIssuerName()).isEqualTo(PaymentCardCode.ofCardName(issuerCode));
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    public void getOrderTest() {
        // given
        when(this.orderRepository.findById(any())).thenReturn(this.getOrderOne());
        when(this.paymentRepository.findTopByOrderOrderByCreatedAtDesc(any())).thenReturn(this.getPaymentOne());
        when(this.orderDetailRepository.findByOrderAndDeletedAtIsNull(any())).thenReturn(this.getOrderDetails());

        // when
        OrderDetailResponse orderDetailResponse = this.adminOrderService.getOrder(orderId);

        // then
        assertThat(orderDetailResponse.getId()).isEqualTo(orderId);
        assertThat(orderDetailResponse.getOrderNo()).isEqualTo(orderNo);
        assertThat(orderDetailResponse.getCustomerName()).isEqualTo(memberName);
        assertThat(orderDetailResponse.getOrderStatus()).isEqualTo(orderStatus);
        assertThat(orderDetailResponse.getOrderItems().get(0).getName()).isEqualTo(itemName);
        assertThat(orderDetailResponse.getOrderItems().get(0).getQuantity()).isEqualTo(orderDetailQuantity);
        assertThat(orderDetailResponse.getOrderItems().get(0).getPrice()).isEqualTo(itemPrice);
        assertThat(orderDetailResponse.getOrderItems().get(0).getTotalPrice()).isEqualTo(itemPrice.multiply(BigDecimal.valueOf(orderDetailQuantity)));
        assertThat(orderDetailResponse.getRecipient()).isEqualTo(recipient);
        assertThat(orderDetailResponse.getPhone()).isEqualTo(phone);
        assertThat(orderDetailResponse.getAddress()).isEqualTo(address);
        assertThat(orderDetailResponse.getAddressDetail()).isEqualTo(addressDetail);
        assertThat(orderDetailResponse.getPostcode()).isEqualTo(postcode);
        assertThat(orderDetailResponse.getDescription()).isEqualTo(description);
        assertThat(orderDetailResponse.getIssuerCode()).isEqualTo(issuerCode);
        assertThat(orderDetailResponse.getTotalPrice()).isEqualTo(totalPrice);
        assertThat(orderDetailResponse.getPaymentStatus()).isEqualTo(paymentStatus);
        assertThat(orderDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(orderDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("주문 상세 조회 성공 - Payment 값 없을 시")
    public void getOrderNonPaymentTest() {
        // given
        when(this.orderRepository.findById(any())).thenReturn(this.getOrderOne());
        when(this.paymentRepository.findTopByOrderOrderByCreatedAtDesc(any())).thenReturn(Optional.empty());
        when(this.orderDetailRepository.findByOrderAndDeletedAtIsNull(any())).thenReturn(this.getOrderDetails());

        // when
        OrderDetailResponse orderDetailResponse = this.adminOrderService.getOrder(orderId);

        // then
        assertThat(orderDetailResponse.getId()).isEqualTo(orderId);
        assertThat(orderDetailResponse.getOrderNo()).isEqualTo(orderNo);
        assertThat(orderDetailResponse.getCustomerName()).isEqualTo(memberName);
        assertThat(orderDetailResponse.getOrderStatus()).isEqualTo(orderStatus);
        assertThat(orderDetailResponse.getOrderItems().get(0).getName()).isEqualTo(itemName);
        assertThat(orderDetailResponse.getOrderItems().get(0).getQuantity()).isEqualTo(orderDetailQuantity);
        assertThat(orderDetailResponse.getOrderItems().get(0).getPrice()).isEqualTo(itemPrice);
        assertThat(orderDetailResponse.getOrderItems().get(0).getTotalPrice()).isEqualTo(itemPrice.multiply(BigDecimal.valueOf(orderDetailQuantity)));
        assertThat(orderDetailResponse.getRecipient()).isEqualTo(recipient);
        assertThat(orderDetailResponse.getPhone()).isEqualTo(phone);
        assertThat(orderDetailResponse.getAddress()).isEqualTo(address);
        assertThat(orderDetailResponse.getAddressDetail()).isEqualTo(addressDetail);
        assertThat(orderDetailResponse.getPostcode()).isEqualTo(postcode);
        assertThat(orderDetailResponse.getDescription()).isEqualTo(description);
        assertThat(orderDetailResponse.getIssuerCode()).isNull();
        assertThat(orderDetailResponse.getTotalPrice()).isNull();
        assertThat(orderDetailResponse.getPaymentStatus()).isNull();
        assertThat(orderDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(orderDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("주문 상세 조회 실패 - 찾을 수 없는 주문")
    public void getOrderFailNotFoundTest() {
        // given
        when(this.orderRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminOrderService.getOrder(orderId);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("주문 수정 성공")
    public void modifyOrderTest() {
        // given
        OrderModifyRequest orderModifyRequest = OrderModifyRequest.builder()
                .recipient(modifyRecipient)
                .phone(modifyPhone)
                .address(modifyAddress)
                .addressDetail(modifyAddressDetail)
                .postcode(modifyPostcode)
                .description(modifyDescription)
                .build();

        Mockito.when(this.orderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getOrderOne());
        Mockito.when(this.orderDetailRepository.findByOrderAndDeletedAtIsNull(any())).thenReturn(this.getOrderDetails());
        Mockito.when(this.orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderDetailResponse orderDetailResponse = this.adminOrderService.modifyOrder(orderId, orderModifyRequest);

        // then
        assertThat(orderDetailResponse.getId()).isEqualTo(orderId);
        assertThat(orderDetailResponse.getOrderNo()).isEqualTo(orderNo);
        assertThat(orderDetailResponse.getCustomerName()).isEqualTo(memberName);
        assertThat(orderDetailResponse.getOrderStatus()).isEqualTo(orderStatus);
        assertThat(orderDetailResponse.getOrderItems().get(0).getName()).isEqualTo(itemName);
        assertThat(orderDetailResponse.getOrderItems().get(0).getQuantity()).isEqualTo(orderDetailQuantity);
        assertThat(orderDetailResponse.getOrderItems().get(0).getPrice()).isEqualTo(itemPrice);
        assertThat(orderDetailResponse.getOrderItems().get(0).getTotalPrice()).isEqualTo(itemPrice.multiply(BigDecimal.valueOf(orderDetailQuantity)));
        assertThat(orderDetailResponse.getRecipient()).isEqualTo(modifyRecipient);
        assertThat(orderDetailResponse.getPhone()).isEqualTo(modifyPhone);
        assertThat(orderDetailResponse.getAddress()).isEqualTo(modifyAddress);
        assertThat(orderDetailResponse.getAddressDetail()).isEqualTo(modifyAddressDetail);
        assertThat(orderDetailResponse.getPostcode()).isEqualTo(modifyPostcode);
        assertThat(orderDetailResponse.getDescription()).isEqualTo(modifyDescription);
        assertThat(orderDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(orderDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("주문 수정 실패 - 찾을 수 없는 상품")
    public void modifyItemFailItemNotFoundTest() {
        // given
        OrderModifyRequest orderModifyRequest = OrderModifyRequest.builder()
                .recipient(modifyRecipient)
                .phone(modifyPhone)
                .address(modifyAddress)
                .addressDetail(modifyAddressDetail)
                .postcode(modifyPostcode)
                .description(modifyDescription)
                .build();

        Mockito.when(this.orderRepository.findByIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminOrderService.modifyOrder(orderId, orderModifyRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getMessage(), exception.getMessage());
    }

    private Page<OrderListResponse> getOrderList() {
        List<OrderListResponse> list = List.of(this.getOrderListData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<OrderListResponse>(list, pageable, list.size());
    }

    private Page<OrderListResponse> getStatusOrderList() {
        List<OrderListResponse> list = List.of(this.getCancelledOrderListData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<OrderListResponse>(list, pageable, list.size());
    }

    private List<OrderDetail> getOrderDetails() {
        return List.of(this.getOrderDetailData());
    }

    private Optional<Order> getOrderOne() {
        return Optional.of(this.getOrderData());
    }

    private Optional<Payment> getPaymentOne() {
        return Optional.of(this.getPaymentData());
    }

    private Maker getMakerData() {
        return Maker.builder()
                .name("(주)국순당")
                .address("강원도 횡성군 둔내면 강변로 975")
                .detail("101")
                .region("강원도")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private CategoryClass getCategoryClassData() {
        return CategoryClass.builder()
                .firstName("테스트 카테고리 대분류 1")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Category getCategoryData() {
        CategoryClass categoryClass = getCategoryClassData();

        return Category.builder()
                .lastName("테스트 카테고리 소분류1")
                .categoryClass(categoryClass)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Product getProductData() {
        Maker maker = getMakerData();
        Category category = getCategoryData();

        return Product.builder()
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
                .maker(maker)
                .category(category)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Item getItemData() {
        Category category = getCategoryData();
        Product product = getProductData();

        return Item.builder()
                .id(itemId)
                .type(itemType)
                .name(itemName)
                .price(itemPrice)
                .info(itemInfo)
                .category(category)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .itemProducts(List.of(
                        ItemProduct.builder()
                                .product(product)
                                .quantity(itemProductQuantity)
                                .build()
                ))
                .build();
    }

    private Member getMemberData() {
        return Member.builder()
                .email("member5@example.com")
                .provider(ProviderType.KAKAO)
                .name(memberName)
                .nickname(memberNickname)
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Order getOrderData() {
        Member member = getMemberData();
        Item item = getItemData();

        return com.drunkenlion.alcoholfriday.domain.order.entity.Order.builder()
                .id(orderId)
                .orderNo(orderNo)
                .orderStatus(orderStatus)
                .price(price)
                .deliveryPrice(deliveryPrice)
                .totalPrice(totalPrice)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .member(member)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .orderDetails(List.of(
                        OrderDetail.builder()
                                .itemPrice(item.getPrice())
                                .quantity(orderDetailQuantity)
                                .totalPrice(item.getPrice().multiply(BigDecimal.valueOf(orderDetailQuantity)))
                                .item(item)
                                .review(null)
                                .build()
                ))
                .build();
    }

    private OrderDetail getOrderDetailData() {
        Order order = getOrderData();
        Item item = getItemData();

        return OrderDetail.builder()
                .itemPrice(item.getPrice())
                .quantity(orderDetailQuantity)
                .totalPrice(item.getPrice().multiply(BigDecimal.valueOf(orderDetailQuantity)))
                .item(item)
                .order(order)
                .review(null)
                .build();
    }

    private Payment getPaymentData() {
        Member member = getMemberData();
        Order order = getOrderData();

        return Payment.builder()
                .paymentNo("jPR7DvYpNk6bJXmgo01emDojZdPByA8LAnGKWx4qMl00aEwB")
                .paymentStatus(paymentStatus)
                .paymentMethod(PaymentMethod.CARD)
                .paymentProvider(PaymentProvider.TOSS_PAY)
                .paymentCardType(PaymentCardType.CHECK)
                .paymentOwnerType(PaymentOwnerType.PERSONAL)
                .issuerCode(issuerCode)
                .acquirerCode(PaymentCardCode.SHINHAN)
                .totalPrice(order.getTotalPrice())
                .requestedAt(LocalDateTime.now())
                .approvedAt(LocalDateTime.now())
                .currency("KRW")
                .order(order)
                .member(member)
                .build();
    }

    private OrderListResponse getOrderListData() {
        return OrderListResponse.builder()
                .id(orderId)
                .orderNo(orderNo)
                .customerName(memberName)
                .customerNickname(memberNickname)
                .orderStatus(orderStatus)
                .price(price)
                .issuerName(PaymentCardCode.ofCardName(issuerCode))
                .createdAt(createdAt)
                .deleted(false)
                .build();
    }

    private OrderListResponse getCancelledOrderListData() {
        return OrderListResponse.builder()
                .id(orderId)
                .orderNo(orderNo)
                .customerName(memberName)
                .customerNickname(memberNickname)
                .orderStatus(OrderStatus.CANCELLED)
                .price(price)
                .issuerName(PaymentCardCode.ofCardName(issuerCode))
                .createdAt(createdAt)
                .deleted(false)
                .build();
    }
}
