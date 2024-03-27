package com.drunkenlion.alcoholfriday.domain.order.application;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.*;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderResponseList receive(OrderRequestList orderRequestList, Member member);

    OrderDetail orderDetailSave(OrderItemRequest orderItemRequest, Order order);

    BigDecimal getTotalItemPrice(OrderItemRequest orderItemRequest, Item item);

    void updateProductQuantity(Long orderItemRequestQuantity, ItemProduct itemProduct);

    void updateOrderAddress(OrderAddressRequest orderAddressRequest, Long orderId, Member member);

    Order getOrder(String orderNo);

    OrderResponse cancelOrder(Long orderId, OrderCancelRequest orderCancelRequest, Member member);

    List<OrderDetail> getOrderDetails(Order order);

    void checkOrderDetails(Order order);
}
