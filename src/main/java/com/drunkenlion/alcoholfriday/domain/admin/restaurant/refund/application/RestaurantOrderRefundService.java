package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantInfoRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantOrderRefundCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantOrderRefundResponse;
import org.springframework.data.domain.Page;

public interface RestaurantOrderRefundService {
    Page<RestaurantOrderRefundResponse> getRestaurantOrderRefunds(RestaurantInfoRequest request, int page, int size);
    RestaurantOrderRefundResponse createRestaurantOrderRefund(RestaurantOrderRefundCreateRequest request);
    void cancelRestaurantOrderRefund(Long id);
}
