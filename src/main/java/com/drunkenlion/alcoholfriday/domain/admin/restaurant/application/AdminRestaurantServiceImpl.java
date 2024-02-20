package com.drunkenlion.alcoholfriday.domain.admin.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantStockItemResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.util.RestaurantDataValidator;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminRestaurantServiceImpl implements AdminRestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantStockRepository restaurantStockRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

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

        return RestaurantDetailResponse.of(restaurant, getRestaurantStockItemResponseList(restaurant));
    }

    public RestaurantDetailResponse createRestaurant(RestaurantRequest restaurantRequest) {
        Member member = memberRepository.findById(restaurantRequest.getMemberId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        if (!RestaurantDataValidator.isMenuDataValid(restaurantRequest.getMenu()) ||
                !RestaurantDataValidator.isTimeDataValid(restaurantRequest.getTime()) ||
                !RestaurantDataValidator.isProvisionDataValid(restaurantRequest.getProvision())) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.INVALID_INPUT_VALUE)
                    .build();
        }

        Restaurant restaurant = RestaurantRequest.toEntity(restaurantRequest, member);
        restaurantRepository.save(restaurant);

        return RestaurantDetailResponse.of(restaurant, getRestaurantStockItemResponseList(restaurant));
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

        if (!RestaurantDataValidator.isMenuDataValid(restaurantRequest.getMenu()) ||
                !RestaurantDataValidator.isTimeDataValid(restaurantRequest.getTime()) ||
                !RestaurantDataValidator.isProvisionDataValid(restaurantRequest.getProvision())) {
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

        return RestaurantDetailResponse.of(restaurant, getRestaurantStockItemResponseList(restaurant));
    }

    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                        .build());

        if (restaurant.getDeletedAt() != null) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                    .build();
        }

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

    private List<RestaurantStockItemResponse> getRestaurantStockItemResponseList(Restaurant restaurant) {
        List<RestaurantStock> restaurantStocks = restaurantStockRepository.findByRestaurantAndDeletedAtIsNull(restaurant);
        List<RestaurantStockItemResponse> stockItemInfos = new ArrayList<>();

        if (!restaurantStocks.isEmpty()) {
            List<Long> entityIds = restaurantStocks.stream()
                    .map(rs -> rs.getItem().getId())
                    .collect(Collectors.toList());

            List<NcpFileResponse> ncpFiles = fileService.findAllByEntityIds(entityIds, EntityType.ITEM.getEntityName());

            for (RestaurantStock restaurantStock : restaurantStocks) {
                Optional<NcpFileResponse> targetFile = ncpFiles.stream()
                        .filter(file -> file.getEntityId().equals(restaurantStock.getItem().getId()))
                        .findFirst();

                stockItemInfos.add(RestaurantStockItemResponse.of(restaurantStock, targetFile.orElse(null)));
            }
        }

        return stockItemInfos;
    }
}
