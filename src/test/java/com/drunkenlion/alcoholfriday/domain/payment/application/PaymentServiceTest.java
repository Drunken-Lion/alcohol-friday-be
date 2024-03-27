package com.drunkenlion.alcoholfriday.domain.payment.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.application.OrderService;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderCancelCompleteRequest;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderUtil;
import com.drunkenlion.alcoholfriday.domain.payment.dao.PaymentRepository;
import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.*;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class PaymentServiceTest {
    @Mock
    private CartService cartService;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ProductRepository productRepository;

    // test를 위한 임의 변수
    private BigDecimal deliveryPrice = BigDecimal.valueOf(2500);
    // Item
    private final Long itemId1 = 1L;
    private final String firstName = "식품";
    private final String lastName = "탁주";
    private final String productName = "test data";
    private final String itemName = "test ddaattaa";
    private final BigDecimal price = new BigDecimal(50000);
    private final BigDecimal totalPrice = price.add(deliveryPrice);
    private final String info = "이 상품은 테스트 상품입니다.";
    private final Long quantity = 10L;
    private final Double alcohol = 17D;
    private final String ingredient = "알콜, 누룩 등등...";
    private final Long sweet = 10L;
    private final Long sour = 10L;
    private final Long cool = 10L;
    private final Long body = 10L;
    private final Long balance = 10L;
    private final Long incense = 10L;
    private final Long throat = 10L;
    private final Long itemProductQuantity = 5L;

    // Item2
    private final Long itemId2 = 2L;
    private final String firstName2 = "식품";
    private final String lastName2 = "청주";
    private final String productName2 = "test data2";
    private final String itemName2 = "test ddaattaa";
    private final BigDecimal price2 = new BigDecimal(100_000);
    private final BigDecimal totalPrice2 = price2.add(deliveryPrice);
    private final String info2 = "이 상품은 테스트 상품2 입니다.";
    private final Long quantity2 = 10L;
    private final Double alcohol2 = 17D;
    private final String ingredient2 = "알콜, 누룩 등등...";
    private final Long sweet2 = 10L;
    private final Long sour2 = 10L;
    private final Long cool2 = 10L;
    private final Long body2 = 10L;
    private final Long balance2 = 10L;
    private final Long incense2 = 10L;
    private final Long throat2 = 10L;
    private final Long itemProductQuantity2 = 10L;

    // Member
    private final Long id = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = MemberRole.MEMBER.getRole();
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = null;
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = null;
    private final LocalDateTime deletedAt = null;
    private final int page = 0;
    private final int size = 20;

    // Order (주문 접수)
    private final Long orderId = 1L;
    private String orderNo = OrderUtil.date.getDate(getDataOrder().getCreatedAt()) + "-"
            + OrderUtil.date.getTime() + "-"
            + OrderUtil.date.getTimeMillis(getDataOrder().getCreatedAt()) + "-"
            + 1;
    private OrderStatus orderStatus = OrderStatus.ORDER_RECEIVED;
    private String recipient = "홍길동";
    private String address = "서울특별시 중구 세종대로 110(태평로1가)";
    private String addressDetail = "서울특별시청 103호";
    private String description = "부재시 문앞에 놓아주세요.";
    private String postcode = "04524";

    // Order2 (결제 완료)
    private final Long orderId2 = 2L;
    private String orderNo2 = OrderUtil.date.getDate(getDataOrder().getCreatedAt()) + "-"
            + OrderUtil.date.getTime() + "-"
            + OrderUtil.date.getTimeMillis(getDataOrder().getCreatedAt()) + "-"
            + 2;
    private OrderStatus orderStatus2 = OrderStatus.PAYMENT_COMPLETED;

    private String notExistOrderNo = "2024-02-28_100";

    // OrderDetail
    private Long quantityItem = 2L;
    private Long quantityItem2 = 1L;

    // Payment, TossPaymentReq
    private String paymentKey = "k0A2Ga1QqXjExPeJWYVQeQZ9JgjRXoV49R5gvNLdzZwO6oKl";
    private String status = "DONE";
    private String method = "간편결제";
    private String cardType = "체크";
    private String ownerType = "개인";
    private String paymentProvider = "토스페이";
    private String issuerCode = "41";
    private String acquirerCode = "41";
    private String totalAmount = "52500";
    private String requestedAt = "2024-02-26T11:14:52+09:00";
    private String approvedAt = "2024-02-26T11:15:14+09:00";
    private String currency = "KRW";

    // Payment 결제 취소 성공, TossPaymentReq
    private String statusCancel = "CANCELED";
    private String requestedAtCancel = "2024-02-27T11:14:52+09:00";
    private String approvedAtCancel = "2024-02-27T11:15:14+09:00";

    // OrderDetail
    private final Long orderDetailId = 1L;
    private final Long orderDetailId2 = 2L;
    private final LocalDateTime deletedAtCancel = LocalDateTime.now().plusMinutes(10);

    // Cart
    private final Long cartId = 1L;
    private final Member member = getDataMember();

    // CartDetail
    private final Cart cart = getDataCart();
    private final Item item = getDataItem();
    private final Item item2 = getDataItem2();
    private final Long quantityCart = 2L;
    private final Long quantityCart2 = 1L;


    @Test
    @DisplayName("결제 전 클라이언트에서 오는 가격이랑 서버에 저장된 가격이 동일한 경우")
    void checkAmountValidity() {
        // given
        when(orderService.getOrder(orderNo)).thenReturn(getDataOrder());
        BigDecimal amount = new BigDecimal(totalAmount);

        // when
        paymentService.validatePaymentAmount(orderNo, amount);

        // then
        verify(orderService, times(1)).getOrder(orderNo);
    }

    @Test
    @DisplayName("결제 전 클라이언트에서 오는 가격이랑 서버에 저장된 가격이 다를 경우")
    void checkAmountValidity_Exception() {
        // given
        when(orderService.getOrder(orderNo)).thenReturn(getDataOrder());
        BigDecimal amount = new BigDecimal("5000");

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.validatePaymentAmount(orderNo, amount);
        });
        verify(orderService, times(1)).getOrder(orderNo);
    }

    @Test
    @DisplayName("결제 전 OrderStatus에 ORDER_RECEIVED가 아닌 경우")
    void checkAmountValidity_NOT_ORDER_RECEIVED() {
        // given
        when(orderService.getOrder(orderNo2)).thenReturn(getDataOrder2());
        BigDecimal amount = new BigDecimal(totalAmount);

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.validatePaymentAmount(orderNo2, amount);
        });
        verify(orderService, times(1)).getOrder(orderNo2);
    }

    @Test
    @DisplayName("결제 성공 후 토스페이먼츠에서 응답 받은 값으로 Payment 저장")
    void saveSuccessPayment() {
        // given
        TossPaymentsReq tossPaymentsReq = TossPaymentsReq.builder()
                .orderNo(orderNo)
                .paymentNo(paymentKey)
                .status(status)
                .method(method)
                .cardType(cardType)
                .ownerType(ownerType)
                .provider(paymentProvider)
                .issuerCode(issuerCode)
                .acquirerCode(acquirerCode)
                .totalAmount(totalAmount)
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .currency(currency)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        // when
        paymentService.saveSuccessPayment(tossPaymentsReq, getDataOrder(), getDataMember());

        // then
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));

        verify(orderRepository).save(orderCaptor.capture());
        verify(paymentRepository).save(paymentCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
        assertThat(savedPayment.getPaymentNo()).isEqualTo(paymentKey);
        assertThat(savedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(savedPayment.getPaymentMethod()).isEqualTo(PaymentMethod.ofMethod(method));
        assertThat(savedPayment.getPaymentProvider()).isEqualTo(PaymentProvider.ofPaymentProvider(paymentProvider));
        assertThat(savedPayment.getPaymentCardType()).isEqualTo(PaymentCardType.ofCardType(cardType));
        assertThat(savedPayment.getPaymentOwnerType()).isEqualTo(PaymentOwnerType.ofOwnerType(ownerType));
        assertThat(savedPayment.getIssuerCode()).isEqualTo(PaymentCardCode.ofCardCode(issuerCode));
        assertThat(savedPayment.getAcquirerCode()).isEqualTo(PaymentCardCode.ofCardCode(acquirerCode));
        assertThat(savedPayment.getTotalPrice()).isEqualTo(totalAmount);
        assertThat(savedPayment.getRequestedAt()).isEqualTo(LocalDateTime.parse(requestedAt.substring(0, 19)));
        assertThat(savedPayment.getApprovedAt()).isEqualTo(LocalDateTime.parse(approvedAt.substring(0, 19)));
        assertThat(savedPayment.getCurrency()).isEqualTo(currency);
        assertThat(savedPayment.getOrder()).isEqualTo(getDataOrder());
        assertThat(savedPayment.getMember()).isEqualTo(getDataMember());
    }

    @Test
    @DisplayName("결제 성공 후 PaymentProvider 등 enum 필드에 null 값이 들어오는 경우")
    void saveSuccessPayment_nullTest() {
        // given
        TossPaymentsReq tossPaymentsReq = TossPaymentsReq.builder()
                .orderNo(orderNo)
                .paymentNo(paymentKey)
                .status(status)
                .method(method)
                .cardType(null)
                .ownerType(ownerType)
                .provider(null)
                .issuerCode(issuerCode)
                .acquirerCode(acquirerCode)
                .totalAmount(totalAmount)
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .currency(currency)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        // when
        paymentService.saveSuccessPayment(tossPaymentsReq, getDataOrder(), getDataMember());

        // then
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));

        verify(orderRepository).save(orderCaptor.capture());
        verify(paymentRepository).save(paymentCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
        assertThat(savedPayment.getPaymentNo()).isEqualTo(paymentKey);
        assertThat(savedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(savedPayment.getPaymentMethod()).isEqualTo(PaymentMethod.ofMethod(method));
        assertThat(savedPayment.getPaymentProvider()).isEqualTo(null);
        assertThat(savedPayment.getPaymentCardType()).isEqualTo(null);
        assertThat(savedPayment.getPaymentOwnerType()).isEqualTo(PaymentOwnerType.ofOwnerType(ownerType));
        assertThat(savedPayment.getIssuerCode()).isEqualTo(PaymentCardCode.ofCardCode(issuerCode));
        assertThat(savedPayment.getAcquirerCode()).isEqualTo(PaymentCardCode.ofCardCode(acquirerCode));
        assertThat(savedPayment.getTotalPrice()).isEqualTo(totalAmount);
        assertThat(savedPayment.getRequestedAt()).isEqualTo(LocalDateTime.parse(requestedAt.substring(0, 19)));
        assertThat(savedPayment.getApprovedAt()).isEqualTo(LocalDateTime.parse(approvedAt.substring(0, 19)));
        assertThat(savedPayment.getCurrency()).isEqualTo(currency);
        assertThat(savedPayment.getOrder()).isEqualTo(getDataOrder());
        assertThat(savedPayment.getMember()).isEqualTo(getDataMember());
    }

    @Test
    @DisplayName("결제 성공 후 장바구니에서 주문 아이템 삭제")
    void paymentSuccess_deleteCartItems() {
        // given
        List<DeleteCartRequest> deleteCartRequests = new ArrayList<>();
        deleteCartRequests.add(DeleteCartRequest.of(1L));
        deleteCartRequests.add(DeleteCartRequest.of(2L));

        doNothing().when(cartService).deleteCartList(deleteCartRequests, getDataOrder().getMember());

        // when
        cartService.deleteCartList(deleteCartRequests, member);

        // then
        verify(cartService, times(1)).deleteCartList(deleteCartRequests, getDataOrder().getMember());
    }

    @Test
    @DisplayName("결제 취소 성공 후 토스페이먼츠에서 응답 받은 값으로 Payment 저장 및 제품 수량 원복")
    void saveCancelSuccessPayment() {
        // given
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Order order = getDataCanceledOrder();

        TossPaymentsReq tossPaymentsReq = TossPaymentsReq.builder()
                .orderNo(orderNo)
                .paymentNo(paymentKey)
                .status(statusCancel)
                .method(method)
                .cardType(cardType)
                .ownerType(ownerType)
                .provider(paymentProvider)
                .issuerCode(issuerCode)
                .acquirerCode(acquirerCode)
                .totalAmount(totalAmount)
                .requestedAt(requestedAtCancel)
                .approvedAt(approvedAtCancel)
                .currency(currency)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        List<Product> savedProducts1 = new ArrayList<>();
        when(productRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    savedProducts1.addAll(invocation.getArgument(0));
                    return invocation.getArgument(0);
                });
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        ArgumentCaptor<List<Product>> productsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        // when
        paymentService.saveCancelSuccessPayment(tossPaymentsReq, order, order.getOrderDetails(), adminMember);

        // then
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(2, savedProducts1.size());
        verify(paymentRepository, times(1)).save(any(Payment.class));

        verify(orderRepository).save(orderCaptor.capture());
        verify(productRepository).saveAll(productsCaptor.capture());
        verify(paymentRepository).save(paymentCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        List<Product> savedProducts = productsCaptor.getValue();
        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_COMPLETED);
        assertThat(savedPayment.getPaymentNo()).isEqualTo(paymentKey);
        assertThat(savedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(savedPayment.getPaymentMethod()).isEqualTo(PaymentMethod.ofMethod(method));
        assertThat(savedPayment.getPaymentProvider()).isEqualTo(PaymentProvider.ofPaymentProvider(paymentProvider));
        assertThat(savedPayment.getPaymentCardType()).isEqualTo(PaymentCardType.ofCardType(cardType));
        assertThat(savedPayment.getPaymentOwnerType()).isEqualTo(PaymentOwnerType.ofOwnerType(ownerType));
        assertThat(savedPayment.getIssuerCode()).isEqualTo(PaymentCardCode.ofCardCode(issuerCode));
        assertThat(savedPayment.getAcquirerCode()).isEqualTo(PaymentCardCode.ofCardCode(acquirerCode));
        assertThat(savedPayment.getTotalPrice()).isEqualTo(totalAmount);
        assertThat(savedPayment.getRequestedAt()).isEqualTo(LocalDateTime.parse(requestedAtCancel.substring(0, 19)));
        assertThat(savedPayment.getApprovedAt()).isEqualTo(LocalDateTime.parse(approvedAtCancel.substring(0, 19)));
        assertThat(savedPayment.getCurrency()).isEqualTo(currency);
        assertThat(savedPayment.getOrder()).isEqualTo(getDataOrder());
        assertThat(savedPayment.getMember()).isEqualTo(getDataMember());

        assertThat(savedProducts.get(0).getQuantity()).isEqualTo(quantity + (itemProductQuantity * quantityItem));
        assertThat(savedProducts.get(1).getQuantity()).isEqualTo(quantity2 + (itemProductQuantity2 * quantityItem2));
    }

    @Test
    @DisplayName("결제 취소 실패 - 관리자가 아닐 경우")
    void checkCancelPayment_validateAdminOrStoreManager() {
        // given
        OrderCancelCompleteRequest orderCancelCompleteRequest = OrderCancelCompleteRequest.builder()
                .orderNo(orderNo)
                .paymentKey(paymentKey)
                .cancelReason("변심")
                .build();

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.checkCancelPayment(getDataOrder(), List.of(getDataOrderDetail()), getDataMember());
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.FORBIDDEN.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("결제 취소 실패 - 삭제된 Item이 있을 경우 취소 불가")
    void checkCancelPayment_existDeletedItem() {
        // given
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        List<OrderDetail> orderDetails = List.of(getDataOrderDetail(), getDeletedOrderDetail());

        OrderCancelCompleteRequest orderCancelCompleteRequest = OrderCancelCompleteRequest.builder()
                .orderNo(orderNo)
                .paymentKey(paymentKey)
                .cancelReason("변심")
                .build();

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.checkCancelPayment(getDataCanceledOrder(), orderDetails, adminMember);
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getMessage());
    }

    @Test
    @DisplayName("결제 취소 실패 - 삭제된 ItemProduct가 있을 경우 취소 불가")
    void checkCancelPayment_existDeletedItemProduct() {
        // given
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        List<OrderDetail> orderDetails = List.of(getDataOrderDetail(), getOrderDetailItemProductDeletedAtNotNull());

        OrderCancelCompleteRequest orderCancelCompleteRequest = OrderCancelCompleteRequest.builder()
                .orderNo(orderNo)
                .paymentKey(paymentKey)
                .cancelReason("변심")
                .build();

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.checkCancelPayment(getDataCanceledOrder(), orderDetails, adminMember);
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getMessage());
    }

    @Test
    @DisplayName("결제 취소 실패 - 삭제된 Product가 있을 경우 취소 불가")
    void checkCancelPayment_existDeletedProduct() {
        // given
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        List<OrderDetail> orderDetails = List.of(getDataOrderDetail(), getOrderDetailProductDeletedAtNotNull());

        OrderCancelCompleteRequest orderCancelCompleteRequest = OrderCancelCompleteRequest.builder()
                .orderNo(orderNo)
                .paymentKey(paymentKey)
                .cancelReason("변심")
                .build();

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.checkCancelPayment(getDataCanceledOrder(), orderDetails, adminMember);
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getMessage());
    }


    private Optional<Payment> getOnePayment() {
        return Optional.of(this.getDataPayment());
    }

    private Payment getDataPayment() {
        String requestedAtStr = requestedAt.substring(0, 19);
        String approvedAtStr = approvedAt.substring(0, 19);
        LocalDateTime requestedAt_ = LocalDateTime.parse(requestedAtStr);
        LocalDateTime approvedAt_ = LocalDateTime.parse(approvedAtStr);

        return Payment.builder()
                .paymentNo(paymentKey)
                .paymentStatus(PaymentStatus.DONE)
                .paymentMethod(PaymentMethod.ofMethod(method))
                .paymentProvider(PaymentProvider.ofPaymentProvider(paymentProvider))
                .paymentCardType(PaymentCardType.ofCardType(cardType))
                .paymentOwnerType(PaymentOwnerType.ofOwnerType(ownerType))
                .issuerCode(PaymentCardCode.ofCardCode(issuerCode))
                .acquirerCode(PaymentCardCode.ofCardCode(acquirerCode))
                .totalPrice(new BigDecimal(totalAmount))
                .requestedAt(requestedAt_)
                .approvedAt(approvedAt_)
                .currency(currency)
                .order(getDataOrder())
                .member(getDataMember())
                .build();
    }

    private Optional<Payment> getOneCanceledPayment() {
        return Optional.of(this.getDataCanceledPayment());
    }

    private Payment getDataCanceledPayment() {
        String requestedAtStr = requestedAtCancel.substring(0, 19);
        String approvedAtStr = approvedAtCancel.substring(0, 19);
        LocalDateTime requestedAt_ = LocalDateTime.parse(requestedAtStr);
        LocalDateTime approvedAt_ = LocalDateTime.parse(approvedAtStr);

        return Payment.builder()
                .paymentNo(paymentKey)
                .paymentStatus(PaymentStatus.CANCELED)
                .paymentMethod(PaymentMethod.ofMethod(method))
                .paymentProvider(PaymentProvider.ofPaymentProvider(paymentProvider))
                .paymentCardType(PaymentCardType.ofCardType(cardType))
                .paymentOwnerType(PaymentOwnerType.ofOwnerType(ownerType))
                .issuerCode(PaymentCardCode.ofCardCode(issuerCode))
                .acquirerCode(PaymentCardCode.ofCardCode(acquirerCode))
                .totalPrice(new BigDecimal(totalAmount))
                .requestedAt(requestedAt_)
                .approvedAt(approvedAt_)
                .currency(currency)
                .order(getDataOrder())
                .member(getDataMember())
                .build();
    }

    private Optional<OrderDetail> getOneOrderDetail() {
        return Optional.of(this.getDataOrderDetail());
    }

    // Long 타입의 quantity를 BigDecimal로 변환
    BigDecimal quantityBigDecimal = BigDecimal.valueOf(quantityItem);
    // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
    BigDecimal totalItemPrice = quantityBigDecimal.multiply(price);

    private OrderDetail getDataOrderDetail() {
        OrderDetail orderDetail = OrderDetail.builder()
                .id(orderDetailId)
                .itemPrice(price)
                .quantity(quantityItem)
                .totalPrice(totalItemPrice)
                .deletedAt(deletedAt)
                .build();
        orderDetail.addItem(getDataItem());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private Optional<OrderDetail> getOneOrderDetail2() {
        return Optional.of(this.getDataOrderDetail2());
    }

    // Long 타입의 quantity를 BigDecimal로 변환
    BigDecimal quantityBigDecimal2 = BigDecimal.valueOf(quantityItem2);
    // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
    BigDecimal totalItemPrice2 = quantityBigDecimal2.multiply(price2);

    private OrderDetail getDataOrderDetail2() {
        OrderDetail orderDetail = OrderDetail.builder()
                .id(orderDetailId2)
                .itemPrice(price2)
                .quantity(quantityItem2)
                .totalPrice(totalItemPrice2)
                .deletedAt(deletedAt)
                .build();
        orderDetail.addItem(this.getDataItem2());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private OrderDetail getDeletedOrderDetail() {
        OrderDetail orderDetail = OrderDetail.builder()
                .id(orderDetailId2)
                .itemPrice(price2)
                .quantity(quantityItem2)
                .totalPrice(totalItemPrice2)
                .deletedAt(deletedAtCancel)
                .build();
        orderDetail.addItem(this.getDeletedItem());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private OrderDetail getOrderDetailItemProductDeletedAtNotNull() {
        OrderDetail orderDetail = OrderDetail.builder()
                .id(orderDetailId2)
                .itemPrice(price2)
                .quantity(quantityItem2)
                .totalPrice(totalItemPrice2)
                .deletedAt(deletedAt)
                .build();
        orderDetail.addItem(this.getDeletedItem());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private OrderDetail getOrderDetailProductDeletedAtNotNull() {
        OrderDetail orderDetail = OrderDetail.builder()
                .id(orderDetailId2)
                .itemPrice(price2)
                .quantity(quantityItem2)
                .totalPrice(totalItemPrice2)
                .deletedAt(deletedAt)
                .build();
        orderDetail.addItem(this.getDeletedProduct());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private Optional<Order> getOneOrder() {
        return Optional.of(this.getDataOrder());
    }

    // 주문 접수
    private Order getDataOrder() {
        return Order.builder()
                .id(orderId)
                .orderNo(orderNo)
                .orderStatus(orderStatus) // 주문 접수
                .price(price)
                .deliveryPrice(deliveryPrice)
                .totalPrice(totalPrice)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .createdAt(LocalDateTime.now())
                .member(this.getDataMember())
                .build();
    }

    private Optional<Order> getOneOrder2() {
        return Optional.of(this.getDataOrder2());
    }

    // 결제 완료
    private Order getDataOrder2() {
        return Order.builder()
                .id(orderId2)
                .orderNo(orderNo2)
                .orderStatus(orderStatus2) // 결제 완료
                .price(price2)
                .deliveryPrice(deliveryPrice)
                .totalPrice(totalPrice2)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .createdAt(LocalDateTime.now())
                .member(this.getDataMember())
                .build();
    }

    private Optional<Order> getOneCanceledOrder() {
        return Optional.of(this.getDataCanceledOrder());
    }

    // 주문 취소된 접수
    private Order getDataCanceledOrder() {
        return Order.builder()
                .id(orderId)
                .orderNo(orderNo)
                .orderStatus(OrderStatus.CANCELLED) // 주문 접수
                .price(price)
                .deliveryPrice(deliveryPrice)
                .totalPrice(totalPrice)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .createdAt(LocalDateTime.now())
                .member(this.getDataMember())
                .orderDetails(List.of(getDataOrderDetail(), getDataOrderDetail2()))
                .build();
    }

    private Optional<Member> getOneMember() {
        return Optional.of(this.getDataMember());
    }

    private Member getDataMember() {
        return Member.builder()
                .id(id)
                .email(email)
                .provider(ProviderType.byProviderName(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.byRole(role))
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    private Optional<Item> getOneItem() {
        return Optional.of(this.getDataItem());
    }

    private Item getDataItem() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName)
                .build();

        Category category = Category.builder()
                .lastName(lastName)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName)
                .quantity(quantity)
                .alcohol(alcohol)
                .ingredient(ingredient)
                .sweet(sweet)
                .sour(sour)
                .cool(cool)
                .body(body)
                .balance(balance)
                .incense(incense)
                .throat(throat)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName)
                .price(price)
                .info(info)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(itemProductQuantity)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }

    private Optional<Item> getOneItem2() {
        return Optional.of(this.getDataItem2());
    }

    private Item getDataItem2() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName2)
                .build();

        Category category = Category.builder()
                .lastName(lastName2)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName2)
                .quantity(quantity2)
                .alcohol(alcohol2)
                .ingredient(ingredient2)
                .sweet(sweet2)
                .sour(sour2)
                .cool(cool2)
                .body(body2)
                .balance(balance2)
                .incense(incense2)
                .throat(throat2)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName2)
                .price(price2)
                .info(info2)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(itemProductQuantity2)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }

    private Item getDeletedProduct() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName2)
                .build();

        Category category = Category.builder()
                .lastName(lastName2)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName2)
                .quantity(quantity2)
                .alcohol(alcohol2)
                .ingredient(ingredient2)
                .sweet(sweet2)
                .sour(sour2)
                .cool(cool2)
                .body(body2)
                .balance(balance2)
                .incense(incense2)
                .throat(throat2)
                .deletedAt(deletedAtCancel)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName2)
                .price(price2)
                .info(info2)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(itemProductQuantity2)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }

    private Item getDeletedItem() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName2)
                .build();

        Category category = Category.builder()
                .lastName(lastName2)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName2)
                .quantity(quantity2)
                .alcohol(alcohol2)
                .ingredient(ingredient2)
                .sweet(sweet2)
                .sour(sour2)
                .cool(cool2)
                .body(body2)
                .balance(balance2)
                .incense(incense2)
                .throat(throat2)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName2)
                .price(price2)
                .info(info2)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(itemProductQuantity2)
                .deletedAt(deletedAtCancel)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }

    private Optional<Cart> getOneCart() {
        return Optional.of(this.getDataCart());
    }

    private Cart getDataCart() {
        return Cart.builder()
                .id(cartId)
                .member(member)
                .build();
    }

    private Optional<CartDetail> getOneCartDetail() {
        return Optional.of(this.getDataCartDetail());
    }

    private CartDetail getDataCartDetail() {
        return CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(quantityCart)
                .build();
    }

    private Optional<CartDetail> getOneCartDetail2() {
        return Optional.of(this.getDataCartDetail2());
    }

    private CartDetail getDataCartDetail2() {
        return CartDetail.builder()
                .cart(cart)
                .item(item2)
                .quantity(quantityCart2)
                .build();
    }
}