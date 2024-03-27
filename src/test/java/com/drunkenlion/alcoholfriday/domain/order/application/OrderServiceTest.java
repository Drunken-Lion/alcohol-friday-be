package com.drunkenlion.alcoholfriday.domain.order.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderAddressRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderCancelRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderItemRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderRequestList;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderUtil;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderValidator;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import io.jsonwebtoken.lang.Collections;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private AddressRepository addressRepository;

    // test를 위한 임의 변수
    // Item
    private final Long itemId1 = 1L;
    private final String firstName = "식품";
    private final String lastName = "탁주";
    private final String productName = "test data";
    private final String itemName = "test ddaattaa";
    private final BigDecimal price = new BigDecimal(50000);
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

    // Member2
    private final Long id2 = 2L;
    private final String email2 = "test2@example.com";
    private final String name2 = "테스트2";
    private final String nickname2 = "test2";
    private final Long phone2 = 1012345679L;

    // Order
    private final Long orderId = 1L;
    private String orderNo = OrderUtil.date.getDate(getDataOrder().getCreatedAt()) + "-"
            + OrderUtil.date.getTime() + "-"
            + OrderUtil.date.getTimeMillis(getDataOrder().getCreatedAt()) + "-"
            + 1;
    private OrderStatus orderStatus = OrderStatus.ORDER_RECEIVED;
    private BigDecimal deliveryPrice = BigDecimal.valueOf(2500);
    private BigDecimal totalPrice = BigDecimal.valueOf(100000);
    private String recipient = "홍길동";
    private String address = "서울특별시 중구 세종대로 110(태평로1가)";
    private String addressDetail = "서울특별시청 103호";
    private String description = "부재시 문앞에 놓아주세요.";
    private String postcode = "04524";

    // OrderDetail
    private final Long orderDetailId = 1L;
    private Long quantityItem = 2L;
    private final Long orderDetailId2 = 2L;
    private Long quantityItem2 = 1L;

    // Address
    private final Long addressId = 1L;
    private final String request = "부재시 연락주세요.";
    private final Boolean isPrimaryTrue = true;


    // 바로 주문
    @Test
    @DisplayName("[즉시 주문] 상품 한 개 주문할 경우")
    void orderReceive_oneItem() {
        // given
        // orderRepository.save(order)
        when(orderRepository.save(any(Order.class))).thenReturn(this.getDataOrder());

        // itemRepository.findById(orderItemRequest.getItemId())
        when(itemRepository.findById(itemId1)).thenReturn(this.getOneItem());

        // orderDetailRepository.save(orderDetail)
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(this.getDataOrderDetail());

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder()
                .itemId(itemId1)
                .quantity(quantityItem)
                .build();
        orderItemRequestList.add(orderItemRequest);


        OrderRequestList orderRequestList = OrderRequestList.builder()
                .orderItemList(orderItemRequestList)
                .build();

        // addressRepository.findByMemberAndIsPrimaryIsTrue(member)
        when(addressRepository.findByMemberAndIsPrimaryIsTrue(getDataMember())).thenReturn(getOneAddress());


        // when
        OrderResponseList receive = this.orderService.receive(orderRequestList, getDataMember());

        // then
        List<OrderDetailResponse> orderDetails = receive.getOrderDetails();

        assertThat(receive.getOrderStatus()).isEqualTo(orderStatus);
        assertThat(receive.getOrderNo().substring(0, 6)).isEqualTo(orderNo.substring(0, 6));
        assertThat(receive.getPrice()).isEqualTo(new BigDecimal("100000"));
        assertThat(receive.getDeliveryPrice()).isEqualTo(new BigDecimal("2500"));
        assertThat(receive.getTotalPrice()).isEqualTo(new BigDecimal("102500"));
        assertThat(receive.getTotalQuantity()).isEqualTo(2L);
        assertThat(receive.getAddressInfo().getRecipient()).isEqualTo(recipient);
        assertThat(receive.getMemberInfo().getName()).isEqualTo(getDataMember().getName());

        assertThat(orderDetails).isInstanceOf(List.class);
        assertThat(orderDetails.size()).isEqualTo(1);
        assertThat(orderDetails.get(0).getItem().getId()).isEqualTo(itemId1);
        assertThat(orderDetails.get(0).getItem().getPrice()).isEqualTo("50000");
    }

    @Test
    @DisplayName("[장바구니 주문] 상품 한 개 이상 주문할 경우")
    void orderReceive_itemList() {
        // given
        // orderRepository.save(order)
        when(orderRepository.save(any(Order.class))).thenReturn(this.getDataOrder());

        // itemRepository.findById(orderItemRequest.getItemId())
        when(itemRepository.findById(itemId1)).thenReturn(this.getOneItem());
        when(itemRepository.findById(itemId2)).thenReturn(this.getOneItem2());

        // orderDetailRepository.save(orderDetail)
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(this.getDataOrderDetail()).thenReturn(this.getDataOrderDetail2());

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder()
                .itemId(itemId1)
                .quantity(quantityItem)
                .build();
        orderItemRequestList.add(orderItemRequest);
        OrderItemRequest orderItemRequest2 = OrderItemRequest.builder()
                .itemId(itemId2)
                .quantity(quantityItem2)
                .build();
        orderItemRequestList.add(orderItemRequest2);

        OrderRequestList orderRequestList = OrderRequestList.builder()
                .orderItemList(orderItemRequestList)
                .build();

        // addressRepository.findByMemberAndIsPrimaryIsTrue(member)
        when(addressRepository.findByMemberAndIsPrimaryIsTrue(getDataMember())).thenReturn(getOneAddress());


        // when
        OrderResponseList receive = this.orderService.receive(orderRequestList, getDataMember());

        // then
        List<OrderDetailResponse> orderDetails = receive.getOrderDetails();

        assertThat(receive.getOrderStatus()).isEqualTo(orderStatus);
        assertThat(receive.getOrderNo().substring(0, 6)).isEqualTo(orderNo.substring(0, 6));
        assertThat(receive.getPrice()).isEqualTo(new BigDecimal("200000"));
        assertThat(receive.getDeliveryPrice()).isEqualTo(new BigDecimal("2500"));
        assertThat(receive.getTotalPrice()).isEqualTo(new BigDecimal("202500"));
        assertThat(receive.getTotalQuantity()).isEqualTo(3L);
        assertThat(receive.getAddressInfo().getRecipient()).isEqualTo(recipient);
        assertThat(receive.getMemberInfo().getName()).isEqualTo(getDataMember().getName());

        assertThat(orderDetails).isInstanceOf(List.class);
        assertThat(orderDetails.size()).isEqualTo(2);
        assertThat(orderDetails.get(0).getItem().getId()).isEqualTo(itemId1);
        assertThat(orderDetails.get(0).getItem().getPrice()).isEqualTo("50000");
        assertThat(orderDetails.get(1).getItem().getId()).isEqualTo(itemId2);
        assertThat(orderDetails.get(1).getItem().getPrice()).isEqualTo("100000");
    }

    @Test
    @DisplayName("없는 상품 주문할 경우")
    void orderReceive_noItem() {
        // given
        // orderRepository.save(order)
        when(orderRepository.save(any(Order.class))).thenReturn(this.getDataOrder());

        // itemRepository.findById(orderItemRequest.getItemId())
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder()
                .itemId(100L)
                .quantity(quantityItem)
                .build();
        orderItemRequestList.add(orderItemRequest);
        OrderItemRequest orderItemRequest2 = OrderItemRequest.builder()
                .itemId(itemId2)
                .quantity(quantityItem2)
                .build();
        orderItemRequestList.add(orderItemRequest2);

        OrderRequestList orderRequestList = OrderRequestList.builder()
                .orderItemList(orderItemRequestList)
                .build();

        // when & then
        assertThrows(BusinessException.class, () -> {
            orderService.receive(orderRequestList, getDataMember());
        });
    }

    @Test
    @DisplayName("주문 접수 시 상품에 재고가 없을 때")
    void orderReceive_outOfItemStock() {
        // given
        // orderRepository.save(order)
        when(orderRepository.save(any(Order.class))).thenReturn(this.getDataOrder());

        // itemRepository.findById(orderItemRequest.getItemId())
        when(itemRepository.findById(itemId1)).thenReturn(this.getOneItem3());

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder()
                .itemId(itemId1)
                .quantity(quantityItem)
                .build();
        orderItemRequestList.add(orderItemRequest);

        OrderRequestList orderRequestList = OrderRequestList.builder()
                .orderItemList(orderItemRequestList)
                .build();

        // when & then
        assertThrows(BusinessException.class, () -> {
            orderService.receive(orderRequestList, getDataMember());
        });
    }

    @Test
    @DisplayName("주문 생성 후 배송지 업데이트")
    void updateOrderAddress() {
        // given
        // orderRepository.findByIdAndDeletedAtIsNull(orderId)
        when(orderRepository.findByIdAndDeletedAtIsNull(orderId)).thenReturn(getOneOrder());

        OrderAddressRequest orderAddressRequest = OrderAddressRequest.builder()
                .orderNo(orderNo)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .build();

        // when
        this.orderService.updateOrderAddress(orderAddressRequest, orderId, getDataMember());

        //then
        verify(orderRepository, times(1)).findByIdAndDeletedAtIsNull(orderId);
    }

    @Test
    @DisplayName("주문 생성 후 배송지 업데이트 - 주문 번호가 맞지 않는 경우")
    void updateOrderAddress_invalidOrderNo() {
        // given
        // orderRepository.findByIdAndDeletedAtIsNull(orderId)
        when(orderRepository.findByIdAndDeletedAtIsNull(orderId)).thenReturn(getOneOrder());

        OrderAddressRequest orderAddressRequest = OrderAddressRequest.builder()
                .orderNo("order_1")
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .build();

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.orderService.updateOrderAddress(orderAddressRequest, orderId, getDataMember());
        });

        //then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성 후 배송지 업데이트 - 주문한 회원이 아닐 경우")
    void updateOrderAddress_invalidMember() {
        // given
        // orderRepository.findByIdAndDeletedAtIsNull(orderId)
        when(orderRepository.findByIdAndDeletedAtIsNull(orderId)).thenReturn(getOneOrder());

        OrderAddressRequest orderAddressRequest = OrderAddressRequest.builder()
                .orderNo(orderNo)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .build();

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.orderService.updateOrderAddress(orderAddressRequest, orderId, getDataMember2());
        });

        //then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("orderNo로 Order 객체 조회")
    void getOrder() {
        // given
        // orderRepository.findByOrderNo(orderNo)
        when(orderRepository.findByOrderNoAndDeletedAtIsNull(orderNo)).thenReturn(getOneOrder());

        // when
        Order order = orderService.getOrder(orderNo);

        // then
        assertThat(order.getOrderNo()).isEqualTo(orderNo);
        assertThat(order.getAddress()).isEqualTo(address);
        assertThat(order.getAddressDetail()).isEqualTo(addressDetail);
        assertThat(order.getTotalPrice()).isEqualTo(totalItemPrice);
    }

    @Test
    @DisplayName("orderNo로 Order 객체 조회 - 주문번호에 해당하는 주문이 없는 경우")
    void getOrder_notOrderNo() {
        // given
        // orderRepository.findByOrderNo(orderNo)
        when(orderRepository.findByOrderNoAndDeletedAtIsNull(orderNo)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            orderService.getOrder(orderNo);
        } );
    }

    @DisplayName("[주문 취소] 주문자가 주문 취소 성공")
    void orderCancelTest() {
        // given
        Order order = getDataOrder();
        order = order.toBuilder()
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .price(price)
                .totalPrice(price.add(deliveryPrice))
                .build();

        OrderCancelRequest request = OrderCancelRequest.builder()
                .cancelReason("단순 변심")
                .build();

        when(orderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(order));

        // when
        OrderResponse response = this.orderService.cancelOrder(orderId, request, this.getDataMember());

        // then
        assertThat(response.getId()).isEqualTo(orderId);
        assertThat(response.getOrderNo()).isEqualTo(orderNo);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(response.getPrice()).isEqualTo(price);
        assertThat(response.getDeliveryPrice()).isEqualTo(deliveryPrice);
        assertThat(response.getTotalPrice()).isEqualTo(price.add(deliveryPrice));
        assertThat(response.getRecipient()).isEqualTo(recipient);
        assertThat(response.getPhone()).isEqualTo(phone);
        assertThat(response.getPostcode()).isEqualTo(postcode);
        assertThat(response.getAddress()).isEqualTo(address);
        assertThat(response.getAddressDetail()).isEqualTo(addressDetail);
        assertThat(response.getDescription()).isEqualTo(description);
        assertThat(response.getCancelReason()).isEqualTo("단순 변심");
    }

    @Test
    @DisplayName("[주문 취소] 주문자가 주문 취소 실패 - 존재하지 않는 주문")
    void orderCancelTest_1() {
        // given
        Order order = getDataOrder();
        order = order.toBuilder()
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .price(price)
                .totalPrice(price.add(deliveryPrice))
                .build();

        OrderCancelRequest request = OrderCancelRequest.builder()
                .cancelReason("단순 변심")
                .build();

        when(orderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.orderService.cancelOrder(orderId, request, this.getDataMember());
        });

        //then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("[주문 취소] 주문자가 주문 취소 실패 - 본인이 주문한 내역이 아니면 권한 없음")
    void orderCancelTest_2() {
        // given
        Order order = getDataOrder();
        order = order.toBuilder()
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .price(price)
                .totalPrice(price.add(deliveryPrice))
                .build();

        OrderCancelRequest request = OrderCancelRequest.builder()
                .cancelReason("단순 변심")
                .build();

        when(orderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(order));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.orderService.cancelOrder(orderId, request, this.getDataMember2());
        });

        //then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("[주문 취소] 주문자가 주문 취소 실패 - 결제완료, 배송 준비 상태가 아닌 상태이면 취소 불가")
    void orderCancelTest_3() {
        // given
        Order order = getDataOrder();
        order = order.toBuilder()
                .orderStatus(OrderStatus.SHIPPED)
                .price(price)
                .totalPrice(price.add(deliveryPrice))
                .build();

        OrderCancelRequest request = OrderCancelRequest.builder()
                .cancelReason("단순 변심")
                .build();

        when(orderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(order));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.orderService.cancelOrder(orderId, request, this.getDataMember());
        });

        //then
        assertEquals(HttpResponse.Fail.ORDER_CANCEL_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.ORDER_CANCEL_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("주문 상태가 OrderStatus.CANCELLED 가 아닐 경우")
    void checkOrderStatusAbleCancelComplete() {
        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            OrderValidator.checkOrderStatusAbleCancelComplete(getDataOrder());
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.ORDER_CANCEL_COMPLETE_FAIL.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.ORDER_CANCEL_COMPLETE_FAIL.getMessage());
    }

    @Test
    @DisplayName("Order로 OrderDetials를 찾을 때 값이 있는 경우(논리적 삭제된 OrderDetials가 없다.)")
    void getOrderDetails() {
        // given
        List<OrderDetail> orderDetails = List.of(getDataOrderDetail(), getDataOrderDetail2());

        when(orderDetailRepository.findByOrderAndDeletedAtIsNull(getDataOrder())).thenReturn(orderDetails);

        // when
        List<OrderDetail> orderDetailsResponse = orderService.getOrderDetails(getDataOrder());

        // then
        assertThat(orderDetailsResponse.get(0).getId()).isEqualTo(orderDetailId);
        assertThat(orderDetailsResponse.get(0).getItemPrice()).isEqualTo(price);
        assertThat(orderDetailsResponse.get(0).getQuantity()).isEqualTo(quantityItem);
        assertThat(orderDetailsResponse.get(0).getTotalPrice()).isEqualTo(totalItemPrice);
        assertThat(orderDetailsResponse.get(1).getId()).isEqualTo(orderDetailId2);
        assertThat(orderDetailsResponse.get(1).getItemPrice()).isEqualTo(price2);
        assertThat(orderDetailsResponse.get(1).getQuantity()).isEqualTo(quantityItem2);
        assertThat(orderDetailsResponse.get(1).getTotalPrice()).isEqualTo(totalItemPrice2);
    }

    @Test
    @DisplayName("Order로 OrderDetials를 찾을 때 값이 없는 경우(논리적 삭제된 OrderDetials가 있다.)")
    void getOrderDetails_orderDetailsIsEmpty() {
        // given
        when(orderDetailRepository.findByOrderAndDeletedAtIsNull(getDataOrder())).thenReturn(Collections.emptyList());

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            orderService.getOrderDetails(getDataOrder());
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.NOT_FOUND_ORDER_DETAIL.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.NOT_FOUND_ORDER_DETAIL.getMessage());
    }

    @Test
    @DisplayName("Order로 OrderDetials를 찾을 때 존재 유무 확인(논리적 삭제된 OrderDetials가 없다.)")
    void checkOrderDetails_false() {
        // given
        when(orderDetailRepository.existsByOrderAndDeletedAtIsNotNull(getDataOrder())).thenReturn(false);

        // when
        orderService.checkOrderDetails(getDataOrder());

        // then
        verify(orderDetailRepository, times(1)).existsByOrderAndDeletedAtIsNotNull(getDataOrder());
    }

    @Test
    @DisplayName("Order로 OrderDetials를 찾을 때 존재 유무 확인(논리적 삭제된 OrderDetials가 있다.)")
    void checkOrderDetails_true() {
        // given
        when(orderDetailRepository.existsByOrderAndDeletedAtIsNotNull(getDataOrder())).thenReturn(true);

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            orderService.checkOrderDetails(getDataOrder());
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.EXIST_DELETED_DATA.getMessage());
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

    private Optional<Member> getOneMember2() {
        return Optional.of(this.getDataMember2());
    }

    private Member getDataMember2() {
        return Member.builder()
                .id(id2)
                .email(email2)
                .provider(ProviderType.byProviderName(provider))
                .name(name2)
                .nickname(nickname2)
                .role(MemberRole.byRole(role))
                .phone(phone2)
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
                .id(itemId1)
                .name(itemName)
                .price(price)
                .info(info)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(3L)
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
                .id(itemId2)
                .name(itemName2)
                .price(price2)
                .info(info2)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(3L)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }

    private Optional<Item> getOneItem3() {
        return Optional.of(this.getDataItem3());
    }

    private Item getDataItem3() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName2)
                .build();

        Category category = Category.builder()
                .lastName(lastName2)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName2)
                .quantity(1L)
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
                .id(itemId2)
                .name(itemName2)
                .price(price2)
                .info(info2)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(3L)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }

    private Optional<Address> getOneAddress() {
        return Optional.of(this.getAddressData());
    }

    private Address getAddressData() {
        return Address.builder()
                .id(addressId)
                .member(getDataMember())
                .isPrimary(isPrimaryTrue)
                .address(address)
                .addressDetail(addressDetail)
                .postcode(postcode)
                .recipient(recipient)
                .phone(phone)
                .request(request)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}