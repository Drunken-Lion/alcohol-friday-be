package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.RestaurantOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.RestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.refund.dao.RestaurantOrderRefundRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantOrderServiceImpl implements RestaurantOrderService {
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantOrderRefundRepository restaurantOrderRefundRepository;
    private final FileService fileService;

    @Override
    public Page<RestaurantOrderListResponse> getRestaurantOrdersByOwner(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RestaurantOrder> restaurantOrders =
                restaurantOrderRepository.findRestaurantOrdersByOwner(member, pageable);

        return restaurantOrders.map(restaurantOrder -> {
            List<RestaurantOrderDetailResponse> detailResponses = new ArrayList<>();

            List<RestaurantOrderRefund> refunds =
                    restaurantOrderRefundRepository.findRefundByRestaurantOrderId(restaurantOrder);

            // 제품별 환불된 수량 계산
            Map<Long, Long> productRefundQuantities = new HashMap<>();
            refunds.forEach(refund -> refund.getRestaurantOrderRefundDetails().forEach(refundDetail ->
                    productRefundQuantities.merge(
                            refundDetail.getProduct().getId(),
                            refundDetail.getQuantity(),
                            Long::sum)));

            for (RestaurantOrderDetail orderDetail : restaurantOrder.getRestaurantOrderDetails()) {
                Long refundableQuantity = orderDetail.getQuantity();
                
                refundableQuantity -= productRefundQuantities.getOrDefault(
                        orderDetail.getProduct().getId(), 0L);

                NcpFileResponse ncpFileResponse = fileService.findOne(orderDetail.getProduct());

                RestaurantOrderDetailResponse response =
                        RestaurantOrderDetailResponse.of(orderDetail, refundableQuantity, ncpFileResponse);

                detailResponses.add(response);
            }

            return RestaurantOrderListResponse.of(restaurantOrder, detailResponses);
        });
    }
}
