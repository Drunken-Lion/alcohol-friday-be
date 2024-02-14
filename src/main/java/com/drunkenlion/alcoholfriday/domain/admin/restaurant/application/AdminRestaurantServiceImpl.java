package com.drunkenlion.alcoholfriday.domain.admin.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.util.RestaurantDataValidator;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminRestaurantServiceImpl implements AdminRestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MemberRepository memberRepository;

    public Page<RestaurantListResponse> getRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);

        return restaurants.map(RestaurantListResponse::of);
    }

    public RestaurantDetailResponse getRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                        .build());

        return RestaurantDetailResponse.of(restaurant);
    }

    public RestaurantDetailResponse createRestaurant(RestaurantRequest restaurantRequest) {
        Member member = memberRepository.findById(restaurantRequest.getMemberId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        if(!RestaurantDataValidator.isMenuDataValid(restaurantRequest.getMenu()) ||
                !RestaurantDataValidator.isTimeDataValid(restaurantRequest.getTime()) ||
                !RestaurantDataValidator.isProvisionDataValid(restaurantRequest.getProvision()))
        {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.INVALID_INPUT_VALUE)
                    .build();
        }

        Restaurant restaurant = RestaurantRequest.toEntity(restaurantRequest, member);
        restaurantRepository.save(restaurant);

        return RestaurantDetailResponse.of(restaurant);
    }

    @Transactional
    public RestaurantDetailResponse modifyRestaurant(Long id, RestaurantRequest restaurantRequest) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                        .build());

        Member member = memberRepository.findById(restaurantRequest.getMemberId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        if(!RestaurantDataValidator.isMenuDataValid(restaurantRequest.getMenu()) ||
                !RestaurantDataValidator.isTimeDataValid(restaurantRequest.getTime()) ||
                !RestaurantDataValidator.isProvisionDataValid(restaurantRequest.getProvision()))
        {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.INVALID_INPUT_VALUE)
                    .build();
        }

        restaurant = restaurant.toBuilder()
                .members(member)
                .name(restaurantRequest.getName())
                .category(restaurantRequest.getCategory())
                .address(restaurantRequest.getAddress())
                .location(restaurantRequest.getLocation())
                .contact(restaurantRequest.getContact())
                .menu(restaurantRequest.getMenu())
                .time(restaurantRequest.getTime())
                .provision(restaurantRequest.getProvision())
                .build();

        restaurantRepository.save(restaurant);

        return RestaurantDetailResponse.of(restaurant);
    }
}
