package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileServiceImpl;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final FileServiceImpl fileService;

    @Override
    public List<RestaurantLocationResponse> getRestaurants(double neLatitude, double neLongitude, double swLatitude, double swLongitude) {

        List<Restaurant> get = restaurantRepository.getRestaurant(neLatitude, neLongitude, swLatitude, swLongitude);

        List<Item> items = Optional.ofNullable(get).orElseThrow(() -> BusinessException.builder().response(HttpResponse.Fail.NOT_FOUND_ITEM).build())
                .stream()
                .flatMap(restaurants -> restaurants.getRestaurantStocks()
                        .stream()
                        .map(RestaurantStock::getItem))
                .toList();

        List<NcpFileResponse> files = items.stream()
                .map(fileService::findAll)
                .filter(Objects::nonNull)
                .toList();

        List<RestaurantLocationResponse> restaurant = get.stream()
                .map(restaurants -> RestaurantLocationResponse.of(restaurants, files))
                .collect(Collectors.toList());

        getRestaurantBusinessStatus(restaurant);

        return restaurant;
    }

    private void getRestaurantBusinessStatus(List<RestaurantLocationResponse> restaurantSearch) {
        LocalTime userTime = LocalTime.now();

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        for (RestaurantLocationResponse search : restaurantSearch) {
            StringBuilder statusBuilder = new StringBuilder();
            Map<String, Object> time = search.getTime();
            Object businessHoursToday = time.get(dayOfWeek.toString());
            TimeData dayOfTime = objectMapper.convertValue(businessHoursToday, TimeData.class);

            LocalTime startTime = dayOfTime.getStartTime();
            LocalTime closeTime = dayOfTime.getEndTime();
            LocalTime breakStartTime = dayOfTime.getBreakStartTime();
            LocalTime breakEndTime = dayOfTime.getBreakEndTime();

            if (!userTime.isBefore(startTime) && !userTime.isAfter(closeTime)) {
                if (userTime.isBefore(breakStartTime) || userTime.isAfter(breakEndTime)) {
                    statusBuilder.append("영업중");
                } else {
                    statusBuilder.append("브레이크 타임");
                }
            } else {
                statusBuilder.append("영업 종료");
            }

            search.setRestaurantStatus(statusBuilder.toString());
        }
    }
}
