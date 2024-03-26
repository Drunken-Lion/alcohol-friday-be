package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantAdminDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockProductResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.util.RestaurantDataValidator;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private final RestaurantStockRepository restaurantStockRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    public Page<RestaurantListResponse> getRestaurants(Member authMember, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> restaurants = restaurantRepository.findAllBasedAuth(authMember, pageable);

        return restaurants.map(RestaurantListResponse::of);
    }

    public RestaurantAdminDetailResponse getRestaurant(Member authMember, Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                        .build());

        if (!authMember.getRole().equals(MemberRole.ADMIN) && !authMember.getId().equals(restaurant.getMember().getId())) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.FORBIDDEN)
                    .build();
        }

        return RestaurantAdminDetailResponse.of(restaurant, getRestaurantStockItemResponseList(restaurant));
    }

    public RestaurantAdminDetailResponse createRestaurant(Member authMember, RestaurantRequest restaurantRequest) {
        if (!authMember.getRole().equals(MemberRole.ADMIN)) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.FORBIDDEN)
                    .build();
        }

        Member member = memberRepository.findByIdAndDeletedAtIsNull(restaurantRequest.getMemberId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        if (!RestaurantDataValidator.isValid(restaurantRequest)) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.INVALID_INPUT_VALUE)
                    .build();
        }

        Restaurant restaurant = RestaurantRequest.toEntity(restaurantRequest, member);
        restaurantRepository.save(restaurant);

        return RestaurantAdminDetailResponse.of(restaurant, getRestaurantStockItemResponseList(restaurant));
    }

    @Transactional
    public RestaurantAdminDetailResponse modifyRestaurant(Member authMember, Long id, RestaurantRequest restaurantRequest) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                        .build());

        Member member = memberRepository.findByIdAndDeletedAtIsNull(restaurantRequest.getMemberId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        if (!authMember.getRole().equals(MemberRole.ADMIN) && !authMember.getId().equals(member.getId())) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.FORBIDDEN)
                    .build();
        }

        if (!RestaurantDataValidator.isMenuDataValid(restaurantRequest.getMenu()) ||
                !RestaurantDataValidator.isTimeDataValid(restaurantRequest.getTime()) ||
                !RestaurantDataValidator.isProvisionDataValid(restaurantRequest.getProvision())) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.INVALID_INPUT_VALUE)
                    .build();
        }

        restaurant = restaurant.toBuilder()
                .name(restaurantRequest.getName())
                .category(restaurantRequest.getCategory())
                .address(restaurantRequest.getAddress())
                .location(Restaurant.genPoint(restaurantRequest.getLongitude(), restaurantRequest.getLatitude()))
                .contact(restaurantRequest.getContact())
                .menu(restaurantRequest.getMenu())
                .time(restaurantRequest.getTime())
                .provision(restaurantRequest.getProvision())
                .build();

        if (authMember.getRole().equals(MemberRole.ADMIN)) {
            restaurant = restaurant.toBuilder()
                    .member(member)
                    .build();
        }

        restaurantRepository.save(restaurant);

        return RestaurantAdminDetailResponse.of(restaurant, getRestaurantStockItemResponseList(restaurant));
    }

    @Transactional
    public void deleteRestaurant(Member authMember, Long id) {
        if (!authMember.getRole().equals(MemberRole.ADMIN)) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.FORBIDDEN)
                    .build();
        }

        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                        .build());

        // 매장에 관련된 매장 재고 삭제 처리
        List<RestaurantStock> restaurantStocks = restaurantStockRepository.findByRestaurantAndDeletedAtIsNull(restaurant);
        if (!restaurantStocks.isEmpty()) {
            restaurantStocks = restaurantStocks.stream()
                    .map(restaurantStock -> restaurantStock.toBuilder()
                            .deletedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            restaurantStockRepository.saveAll(restaurantStocks);
        }

        restaurant = restaurant.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        restaurantRepository.save(restaurant);
    }

    private List<RestaurantStockProductResponse> getRestaurantStockItemResponseList(Restaurant restaurant) {
        List<RestaurantStock> restaurantStocks = restaurantStockRepository.findByRestaurantAndDeletedAtIsNull(restaurant);
        List<RestaurantStockProductResponse> stockProductInfos = new ArrayList<>();

        if (!restaurantStocks.isEmpty()) {
            for (RestaurantStock restaurantStock : restaurantStocks) {
                Product product = restaurantStock.getProduct();
                NcpFileResponse ncpResponse = fileService.findOne(product);

                stockProductInfos.add(RestaurantStockProductResponse.of(restaurantStock, ncpResponse));
            }
        }

        return stockProductInfos;
    }
}
