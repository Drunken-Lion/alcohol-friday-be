package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderCartListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderCartSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartUpdateRequest;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface RestaurantOrderCartService {
    Page<RestaurantOrderCartListResponse> findRestaurantCart(Long restaurantId, Member member, int page, int size);
    RestaurantOrderCartSaveResponse saveRestaurantOrderCart(RestaurantOrderCartSaveRequest request, Member member);
    RestaurantOrderCartSaveResponse updateRestaurantOrderCart(Long restaurantOrderCartDetailId, RestaurantOrderCartUpdateRequest request, Member member);
    void deleteRestaurantOrderCart(Long id, Member member);
}