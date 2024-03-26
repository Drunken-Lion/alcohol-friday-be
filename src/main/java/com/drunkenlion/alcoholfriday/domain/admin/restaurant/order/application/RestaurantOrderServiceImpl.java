package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.util.RestaurantValidator;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantOrderServiceImpl implements RestaurantOrderService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantOrderRefundRepository restaurantOrderRefundRepository;
    private final ProductRepository productRepository;
    private final FileService fileService;

    @Override
    public Page<RestaurantOrderListResponse> getRestaurantOrdersByAdminOrStoreManager(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RestaurantOrder> orders = restaurantOrderRepository.findAllRestaurantOrders(pageable);

        return orders.map(order -> {
            List<RestaurantOrderDetailResponse> detailResponses =
                    order.getDetails().stream().map(detail ->
                            RestaurantOrderDetailResponse.of(detail, fileService.findOne(detail.getProduct()))).toList();

            return RestaurantOrderListResponse.of(order, detailResponses);
        });
    }

    @Override
    public Page<OwnerRestaurantOrderListResponse> getRestaurantOrdersByOwner(Member member, Long restaurantId, int page, int size) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(restaurantId)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_RESTAURANT));

        RestaurantValidator.validateOwnership(member, restaurant);

        Pageable pageable = PageRequest.of(page, size);

        Page<RestaurantOrder> restaurantOrders =
                restaurantOrderRepository.findRestaurantOrdersByOwner(member, restaurant, pageable);

        return restaurantOrders.map(restaurantOrder -> {
            List<OwnerRestaurantOrderDetailResponse> detailResponses = new ArrayList<>();

            List<RestaurantOrderRefund> refunds =
                    restaurantOrderRefundRepository.findRefundByRestaurantOrderId(restaurantOrder);

            // 제품별 환불된 수량 계산
            Map<Long, Long> productRefundQuantities = new HashMap<>();
            refunds.forEach(refund -> refund.getRestaurantOrderRefundDetails().forEach(refundDetail ->
                    productRefundQuantities.merge(
                            refundDetail.getProduct().getId(),
                            refundDetail.getQuantity(),
                            Long::sum)));

            for (RestaurantOrderDetail orderDetail : restaurantOrder.getDetails()) {
                Long refundableQuantity = orderDetail.getQuantity();

                refundableQuantity -= productRefundQuantities.getOrDefault(
                        orderDetail.getProduct().getId(), 0L);

                NcpFileResponse ncpFileResponse = fileService.findOne(orderDetail.getProduct());

                OwnerRestaurantOrderDetailResponse response =
                        OwnerRestaurantOrderDetailResponse.of(orderDetail, refundableQuantity, ncpFileResponse);

                detailResponses.add(response);
            }

            return OwnerRestaurantOrderListResponse.of(restaurantOrder, detailResponses);
        });
    }

    /**
     * 발주를 위한 제품 목록
     */
    @Override
    public Page<RestaurantOrderProductListResponse> getRestaurantOrderProducts(int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAllByDeletedAtIsNull(pageable);

        return products.map(product -> RestaurantOrderProductListResponse.of(product, fileService.findOne(product)));
    }
}
