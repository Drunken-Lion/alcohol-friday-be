package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.application;


import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartDeleteRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartUpdateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderCartSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface RestaurantOrderCartService {
    Page<RestaurantOrderProductListResponse> getRestaurantOrderProducts(int page, int size, Member member);
    RestaurantOrderCartSaveResponse saveRestaurantOrderCart(RestaurantOrderCartSaveRequest request, Member member);
}