package com.drunkenlion.alcoholfriday.domain.order.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderAddressRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderCancelRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderItemRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderRequestList;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderUtil;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderValidator;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AddressRepository addressRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public OrderResponseList receive(OrderRequestList orderRequestList, Member member) {
        Order order = Order.builder()
                .orderStatus(OrderStatus.ORDER_RECEIVED)
                .price(BigDecimal.valueOf(0))
                .deliveryPrice(OrderUtil.price.getDeliveryPrice())
                .totalPrice(BigDecimal.valueOf(0))
                .member(member)
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> orderDetailList = orderRequestList.getOrderItemList().stream()
                .map(orderItemRequest -> orderDetailSave(orderItemRequest, savedOrder))
                .toList();

        // 주문 고유번호 만들기
        savedOrder.genOrderNo();
        // 주문 총 금액
        savedOrder.addPrice(orderDetailList);
        // 주문 총 금액 + 배송비
        savedOrder.addTotalPrice();

        // 기본 배송지
        Address address = addressRepository.findByMemberAndIsPrimaryIsTrue(member)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ADDRESSES)
                        .build());

        return OrderResponseList.of(
                savedOrder,
                orderDetailList,
                AddressResponse.of(address),
                MemberResponse.of(member)
        );
    }

    @Override
    @Transactional
    public OrderDetail orderDetailSave(OrderItemRequest orderItemRequest, Order order) {
        Item item = itemRepository.findById(orderItemRequest.getItemId()).orElseThrow(() -> BusinessException.builder()
                .response(HttpResponse.Fail.NOT_FOUND_ITEM).build());

        BigDecimal totalItemPrice = getTotalItemPrice(orderItemRequest, item);

        OrderDetail orderDetail = OrderDetail.builder()
                .itemPrice(item.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .totalPrice(totalItemPrice)
                .build();
        orderDetail.addItem(item);
        orderDetail.addOrder(order);

        // 재고 줄이기
        List<ItemProduct> itemProducts = item.getItemProducts();
        itemProducts.forEach(itemProduct -> updateProductQuantity(orderItemRequest.getQuantity(), itemProduct));

        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public BigDecimal getTotalItemPrice(OrderItemRequest orderItemRequest, Item item) {
        // Long 타입의 quantity를 BigDecimal로 변환
        BigDecimal quantityBigDecimal = BigDecimal.valueOf(orderItemRequest.getQuantity());
        // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
        return quantityBigDecimal.multiply(item.getPrice());
    }

    @Override
    @Transactional
    public void updateProductQuantity(Long orderItemRequestQuantity, ItemProduct itemProduct) {
        Product product = itemProduct.getProduct();
        Long productQuantity = product.getQuantity();

        Long itemQuantity = itemProduct.getQuantity(); // item에 따른 개수
        Long minusProductQuantity = orderItemRequestQuantity * itemQuantity;

        // 제품의 수량이 원하는 수량 보다 적을 때
        if (productQuantity < minusProductQuantity) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.OUT_OF_ITEM_STOCK)
                    .build();
        }

        product.updateQuantity(productQuantity - minusProductQuantity);
    }

    @Override
    @Transactional
    public void updateOrderAddress(OrderAddressRequest orderAddressRequest, Long orderId, Member member) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                        .build());

        OrderValidator.compareEntityIdToMemberId(order, member);
        OrderValidator.checkOrderNo(order, orderAddressRequest.getOrderNo());

        order.updateOrderAddress(orderAddressRequest);
    }

    /**
     * orderNo(주문 고유번호)로 Order 조회
     */
    @Override
    public Order getOrder(String orderNo) {
        return orderRepository.findByOrderNoAndDeletedAtIsNull(orderNo)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                        .build());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, OrderCancelRequest orderCancelRequest, Member member) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                        .build());

        OrderValidator.compareEntityIdToMemberId(order, member);
        OrderValidator.checkOrderStatusAbleCancel(order);
        order.updateCancel(orderCancelRequest.getCancelReason());

        return OrderResponse.of(order);
    }

    @Override
    public List<OrderDetail> getOrderDetails(Order order) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderAndDeletedAtIsNull(order);
        if (orderDetails.isEmpty()) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.NOT_FOUND_ORDER_DETAIL)
                    .build();
        }

        return orderDetails;
    }

    @Override
    public void checkOrderDetails(Order order) {
        boolean exist = orderDetailRepository.existsByOrderAndDeletedAtIsNotNull(order);

        if (exist) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.EXIST_DELETED_DATA)
                    .build();
        }
    }
}
