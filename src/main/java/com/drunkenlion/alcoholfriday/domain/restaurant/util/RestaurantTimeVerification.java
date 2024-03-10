package com.drunkenlion.alcoholfriday.domain.restaurant.util;

import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class RestaurantTimeVerification {
    private static final String OPEN = "영업중";
    private static final String CLOSE = "영업 종료";
    private static final String BREAK_TIME = "브레이크 타임";
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void getRestaurantBusinessStatus(List<RestaurantLocationResponse> restaurantSearch , LocalTime userTime) {

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        objectMapper.registerModule(new JavaTimeModule());

        for (RestaurantLocationResponse search : restaurantSearch) {
            StringBuilder statusBuilder = new StringBuilder();
            Map<String, Object> time = search.getTime();
            Object businessHoursToday = time.get(dayOfWeek.toString());
            TimeData dayOfTime = objectMapper.convertValue(businessHoursToday, TimeData.class);

            LocalTime openTime = dayOfTime.getStartTime();
            LocalTime closeTime = dayOfTime.getEndTime();
            LocalTime breakStartTime = dayOfTime.getBreakStartTime();
            LocalTime breakEndTime = dayOfTime.getBreakEndTime();

            if (!userTime.isBefore(openTime) && !userTime.isAfter(closeTime)) {
                if (userTime.isBefore(breakStartTime) || userTime.isAfter(breakEndTime)) {
                    statusBuilder.append(OPEN);
                } else {
                    statusBuilder.append(BREAK_TIME);
                }
            } else {
                statusBuilder.append(CLOSE);
            }

            search.setRestaurantStatus(statusBuilder.toString());
        }
    }
}
