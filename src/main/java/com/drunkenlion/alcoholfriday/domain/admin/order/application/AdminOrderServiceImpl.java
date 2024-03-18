package com.drunkenlion.alcoholfriday.domain.admin.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderItemResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderModifyRequest;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.payment.dao.PaymentRepository;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
    private final FileService fileService;

    @Override
    public Page<OrderListResponse> getOrdersByOrderStatus(int page, int size, OrderStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderListResponse> orders = orderRepository.findOrderList(pageable, status);

        return orders;
    }

    public OrderDetailResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                        .build());

        Payment payment = paymentRepository.findTopByOrderOrderByCreatedAtDesc(order)
                .orElse(Payment.builder().build());

        return OrderDetailResponse.of(order, payment, getOrderItemResponseList(order));
    }

    @Transactional
    public OrderDetailResponse modifyOrder(Long id, OrderModifyRequest request) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                        .build());

        order = order.toBuilder()
                .recipient(request.getRecipient())
                .phone(request.getPhone())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .postcode(request.getPostcode())
                .description(request.getDescription())
                .build();

        orderRepository.save(order);

        Payment payment = paymentRepository.findTopByOrderOrderByCreatedAtDesc(order)
                .orElse(Payment.builder().build());

        return OrderDetailResponse.of(order, payment, getOrderItemResponseList(order));
    }

    private List<OrderItemResponse> getOrderItemResponseList(Order order) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderAndDeletedAtIsNull(order);
        List<OrderItemResponse> orderItems = new ArrayList<>();

        if (!orderDetails.isEmpty()) {
            List<Long> entityIds = orderDetails.stream()
                    .map(rs -> rs.getItem().getId())
                    .collect(Collectors.toList());

            List<NcpFileResponse> ncpFiles = fileService.findAllByEntityIds(entityIds, EntityType.ITEM.getEntityName());

            for (OrderDetail orderDetail : orderDetails) {
                Optional<NcpFileResponse> targetFile = ncpFiles.stream()
                        .filter(file -> file.getEntityId().equals(orderDetail.getItem().getId()))
                        .findFirst();

                orderItems.add(OrderItemResponse.of(orderDetail, targetFile.orElse(null)));
            }
        }

        return orderItems;
    }
}
