package com.drunkenlion.alcoholfriday.domain.admin.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderModifyRequest;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import org.springframework.data.domain.Page;

public interface AdminOrderService {
    Page<OrderListResponse> getOrdersByOrderStatus(int page, int size, OrderStatus status);
    OrderDetailResponse getOrder(Long id);
    OrderDetailResponse modifyOrder(Long id, OrderModifyRequest orderModifyRequest);
}
