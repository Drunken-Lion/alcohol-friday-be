package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.application;


import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartDeleteRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartUpdateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.response.RestaurantOrderCartSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface RestaurantOrderCartService {
    Page<RestaurantOrderProductListResponse> getRestaurantOrderProducts(int page, int size, Member member);
    RestaurantOrderCartSaveResponse saveRestaurantOrderCart(RestaurantOrderCartSaveRequest request, Member member);
}