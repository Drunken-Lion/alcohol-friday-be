package com.drunkenlion.alcoholfriday.domain.order.application;

import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderItemRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderRequestList;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
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
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public OrderResponseList receive(OrderRequestList orderRequestList, Member member) {
        Order order = Order.builder()
                .orderStatus(OrderStatus.ORDER_RECEIVED)
                .price(new BigDecimal("0"))
                .recipient(orderRequestList.getRecipient())
                .phone(orderRequestList.getPhone())
                .address(orderRequestList.getAddress())
                .addressDetail(orderRequestList.getAddressDetail())
                .description(orderRequestList.getDescription())
                .postcode(orderRequestList.getPostcode())
                .member(member)
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> orderDetailList = orderRequestList.getOrderItemList().stream()
                .map(orderItemRequest -> orderDetailSave(orderItemRequest, savedOrder))
                .toList();

        // 주문 고유번호 만들기
        savedOrder.genOrderNo(savedOrder.getId());
        // 주문 총 금액
        savedOrder.addPrice(orderDetailList);

        return OrderResponseList.of(savedOrder, orderDetailList);
    }

    @Override
    @Transactional
    public OrderDetail orderDetailSave(OrderItemRequest orderItemRequest, Order order) {
        Item item = itemRepository.findById(orderItemRequest.getItemId()).orElseThrow(() -> BusinessException.builder()
                .response(HttpResponse.Fail.NOT_FOUND_ITEM).build());

        // Long 타입의 quantity를 BigDecimal로 변환
        BigDecimal quantityBigDecimal = BigDecimal.valueOf(orderItemRequest.getQuantity());
        // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
        BigDecimal totalItemPrice = quantityBigDecimal.multiply(item.getPrice());

        OrderDetail orderDetail = OrderDetail.builder()
                .itemPrice(item.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .totalPrice(totalItemPrice)
                .item(item)
                .order(order)
                .build();
        

        OrderDetail save = orderDetailRepository.save(orderDetail);
        save.addItemTotalPrice(totalItemPrice);

        return save;
    }
}
