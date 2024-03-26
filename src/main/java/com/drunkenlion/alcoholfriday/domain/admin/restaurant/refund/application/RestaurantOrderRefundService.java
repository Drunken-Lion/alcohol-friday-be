package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundRejectRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOrderRefundResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOrderRefundResultResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface RestaurantOrderRefundService {
    Page<RestaurantOrderRefundResponse> getRestaurantOrderRefunds(Member member, Long restaurantId, int page, int size);
    RestaurantOrderRefundResponse createRestaurantOrderRefund(Member member, RestaurantOrderRefundCreateRequest request);
    RestaurantOrderRefundResultResponse cancelRestaurantOrderRefund(Member member, Long id);

    Page<RestaurantOrderRefundResponse> getAllRestaurantOrderRefunds(int page, int size);
    RestaurantOrderRefundResultResponse approvalRestaurantOrderRefund(Long id);
    RestaurantOrderRefundResultResponse rejectRestaurantOrderRefund(Long id, RestaurantOrderRefundRejectRequest request);
}
