package com.drunkenlion.alcoholfriday.domain.payment.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderUtil;
import com.drunkenlion.alcoholfriday.domain.payment.dao.PaymentRepository;
import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.*;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class PaymentServiceTest {
    @Mock
    private CartService cartService;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CartRepository cartRepository;

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

    // OrderDetail
    private final Long orderDetailId = 1L;
    private final Long orderDetailId2 = 2L;

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
        when(orderRepository.findByOrderNo(orderNo)).thenReturn(getOneOrder());
        BigDecimal amount = new BigDecimal(totalAmount);

        // when
        paymentService.checkAmountValidity(orderNo, amount);

        // then
        verify(orderRepository, times(1)).findByOrderNo(orderNo);
    }

    @Test
    @DisplayName("결제 전 클라이언트에서 오는 가격이랑 서버에 저장된 가격이 다를 경우")
    void checkAmountValidity_Exception() {
        // given
        when(orderRepository.findByOrderNo(orderNo)).thenReturn(getOneOrder());
        BigDecimal amount = new BigDecimal("5000");

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.checkAmountValidity(orderNo, amount);
        });
        verify(orderRepository, times(1)).findByOrderNo(orderNo);
    }

    @Test
    @DisplayName("결제 전 OrderStatus에 ORDER_RECEIVED가 아닌 경우")
    void checkAmountValidity_NOT_ORDER_RECEIVED() {
        // given
        when(orderRepository.findByOrderNo(orderNo2)).thenReturn(getOneOrder2());
        BigDecimal amount = new BigDecimal(totalAmount);

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.checkAmountValidity(orderNo2, amount);
        });
        verify(orderRepository, times(1)).findByOrderNo(orderNo2);
    }

    @Test
    @DisplayName("결제 전 존재하지 않는 orderNo일 경우")
    void checkAmountValidity_notExistOrderNo() {
        // given
        BigDecimal amount = new BigDecimal(totalAmount);

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.checkAmountValidity(notExistOrderNo, amount);
        });
    }

    @Test
    @DisplayName("결제 성공 후 토스페이먼츠에서 응답 받은 값으로 Payment 저장")
    void saveSuccessPayment() {
        // given
        when(orderRepository.findByOrderNo(orderNo)).thenReturn(getOneOrder());
        when(memberRepository.findByEmail(email)).thenReturn(getOneMember());

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

        when(paymentRepository.save(getDataPayment())).thenReturn(getDataPayment());

        // when
        paymentService.saveSuccessPayment(tossPaymentsReq);

        // then
        verify(orderRepository, times(1)).findByOrderNo(orderNo);
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("결제 성공 후 PaymentProvider 등 enum 필드에 null 값이 들어오는 경우")
    void saveSuccessPayment_nullTest() {
        // given
        when(orderRepository.findByOrderNo(orderNo)).thenReturn(getOneOrder());
        when(memberRepository.findByEmail(email)).thenReturn(getOneMember());

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

        when(paymentRepository.save(getDataPayment())).thenReturn(getDataPayment());

        // when
        paymentService.saveSuccessPayment(tossPaymentsReq);

        // then
        verify(orderRepository, times(1)).findByOrderNo(orderNo);
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("결제 성공 후 없는 주문 번호 일 경우")
    void saveSuccessPayment_notFound_orderNo() {
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

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.saveSuccessPayment(tossPaymentsReq);
        } );
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
    @DisplayName("결제 성공 후 주문번호에 해당하는 주문이 없는 경우")
    void paymentSuccess_deleteCartItems_fail() {
        // given
        when(orderRepository.findByOrderNo(orderNo)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            paymentService.deletedCartItems(orderNo);
        } );
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
                .item(getDataItem())
                .order(getDataOrder())
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
                .build();
        orderDetail.addItem(this.getDataItem2());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private Optional<Order> getOneOrder() {
        return Optional.of(this.getDataOrder());
    }

    // 주문 접수
    private Order getDataOrder() {
        Order order = Order.builder()
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

        return order;
    }

    private Optional<Order> getOneOrder2() {
        return Optional.of(this.getDataOrder2());
    }

    // 결제 완료
    private Order getDataOrder2() {
        Order order = Order.builder()
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

        return order;
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