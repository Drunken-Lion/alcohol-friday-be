package com.drunkenlion.alcoholfriday.domain.admin.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.util.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.util.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.util.TimeOption;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public RestaurantDetailResponse createRestaurant(RestaurantCreateRequest restaurantCreateRequest) {
        Member member = memberRepository.findById(restaurantCreateRequest.getMemberId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        if(!isMenuDataValid(restaurantCreateRequest.getMenu()) ||
                !isTimeDataValid(restaurantCreateRequest.getTime()) ||
                !isProvisionDataValid(restaurantCreateRequest.getProvision()))
        {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.INVALID_INPUT_VALUE)
                    .build();
        }

        Restaurant restaurant = RestaurantCreateRequest.toEntity(restaurantCreateRequest, member);
        restaurantRepository.save(restaurant);

        return RestaurantDetailResponse.of(restaurant);
    }

    private boolean isMenuDataValid(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!(entry.getValue() instanceof Integer)) {
                return false;
            }
        }

        return true;
    }

    private boolean isTimeDataValid(Map<String, Object> map) {
        if (map.size() != DayInfo.values().length + TimeOption.values().length) return false;

        List<String> enumList = new ArrayList<>();
        for (DayInfo day : DayInfo.values()) {
            enumList.add(day.name());
        }
        for (TimeOption option : TimeOption.values()) {
            enumList.add(option.name());
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!enumList.contains(entry.getKey())) {
                return false;
            }

            if (TimeOption.HOLIDAY.name().equals(entry.getKey())) {
                if (!(entry.getValue() instanceof Boolean)) return false;
            }

            if (TimeOption.ETC.name().equals(entry.getKey())) {
                if (!(entry.getValue() instanceof String)) return false;
            }

            if (DayInfo.MONDAY.name().equals(entry.getKey()) ||
                    DayInfo.TUESDAY.name().equals(entry.getKey()) ||
                    DayInfo.WEDNESDAY.name().equals(entry.getKey()) ||
                    DayInfo.THURSDAY.name().equals(entry.getKey()) ||
                    DayInfo.FRIDAY.name().equals(entry.getKey()) ||
                    DayInfo.SATURDAY.name().equals(entry.getKey()) ||
                    DayInfo.SUNDAY.name().equals(entry.getKey())
            ) {
                if (entry.getValue() instanceof LinkedHashMap) {
                    LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) entry.getValue();
                    for (String key : linkedHashMap.keySet()) {
                        if (key.equals("businessStatus") ||
                                key.equals("breakBusinessStatus")
                        ) {
                            if (!(linkedHashMap.get(key) instanceof Boolean)) return false;
                        } else {
                            if (!(linkedHashMap.get(key) instanceof ArrayList<?> list)) return false;

                            for (Object item : list) {
                                if (!(item instanceof Integer)) return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isProvisionDataValid(Map<String, Object> map) {
        if (map.size() != Provision.values().length) return false;

        List<String> enumList = new ArrayList<>();
        for (Provision day : Provision.values()) {
            enumList.add(day.name());
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!enumList.contains(entry.getKey()) ||
                    !(entry.getValue() instanceof Boolean)
            ) {
                return false;
            }
        }

        return true;
    }
}
