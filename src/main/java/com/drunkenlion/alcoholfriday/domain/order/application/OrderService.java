package com.drunkenlion.alcoholfriday.domain.order.application;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderAddressRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderItemRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderRequestList;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;

import java.math.BigDecimal;

public interface OrderService {
    OrderResponseList receive(OrderRequestList orderRequestList, Member member);

    OrderDetail orderDetailSave(OrderItemRequest orderItemRequest, Order order);

    BigDecimal getTotalItemPrice(OrderItemRequest orderItemRequest, Item item);

    void updateProductQuantity(Long orderItemRequestQuantity, ItemProduct itemProduct);

    void updateOrderAddress(OrderAddressRequest orderAddressRequest, Long orderId, Member member);

    Order getOrder(String orderNo);
}
