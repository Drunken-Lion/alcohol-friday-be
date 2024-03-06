package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepositoryImpl;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepositoryImpl restaurantLocationRepository;
    private final FileServiceImpl fileService;

    @Override
    public List<RestaurantLocationResponse> getRestaurants(double neLatitude, double neLongitude, double swLatitude, double swLongitude) {

        List<Restaurant> get = restaurantLocationRepository.getRestaurant(neLatitude, neLongitude, swLatitude, swLongitude);

        List<Long> itemIds = get.stream()
                .flatMap(restaurants -> restaurants.getRestaurantStocks()
                        .stream()
                        .map(stock -> stock.getItem().getId()))
                .toList();

        List<NcpFileResponse> files = this.fileService.findAllByEntityIds(itemIds, EntityType.ITEM.getEntityName());

        List<RestaurantLocationResponse> restaurant = get.stream()
                .map(restaurants -> RestaurantLocationResponse.of(restaurants, files))
                .collect(Collectors.toList());

        checkRestaurantStatus(restaurant);

        return restaurant;
    }

    private void checkRestaurantStatus(List<RestaurantLocationResponse> restaurantSearch) {

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

            boolean isOpen = userTime.isBefore(closeTime) && userTime.isAfter(startTime);
            boolean isClose = userTime.isBefore(closeTime) || userTime.isAfter(startTime);
            boolean isBreakTime = userTime.isBefore(breakStartTime) && userTime.isAfter(breakEndTime);

            if (isOpen && !isBreakTime) {
                statusBuilder.append("영업중");
            } else if (isClose) { // 수정된 로직
                statusBuilder.append("영업 종료");
            } else  {
                statusBuilder.append("브레이크 타임");
            }

            search.setRestaurantStatus(statusBuilder.toString());
        }
    }
}

