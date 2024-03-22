package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOwnerOrderRefundCancelResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOrderRefundResponse;
import org.springframework.data.domain.Page;

public interface RestaurantOrderRefundService {
    Page<RestaurantOrderRefundResponse> getRestaurantOrderRefunds(Long restaurantId, int page, int size);
    RestaurantOrderRefundResponse createRestaurantOrderRefund(RestaurantOrderRefundCreateRequest request);
    RestaurantOwnerOrderRefundCancelResponse cancelRestaurantOrderRefund(Long id);
}
