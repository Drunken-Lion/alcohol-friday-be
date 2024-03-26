package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.util;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RestaurantConvertor {
    private static final String OPEN = "영업중";
    private static final String CLOSE = "영업 종료";
    private static final String BREAK_TIME = "브레이크 타임";
    private static final String IN_STOCK = "재고있음";
    private static final String NO_STOCK = "재고없음";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String ETC = "etc";

    public static Map<String, Object> getBusinessTime(Restaurant restaurant) {
        // 반환 객체
        Map<String, Object> convertTime = new LinkedHashMap<>();

        // 레스토랑 영업시간
        Map<String, Object> businessTime = restaurant.getTime();

        // ETC 존재 여부 확인을 위해 Optional 사용
        Optional<Object> opEtc = Optional.ofNullable(businessTime.get(ETC));

        if (opEtc.isPresent() && !opEtc.get().equals("")) {
            convertTime.put(ETC, opEtc.get());
        }

        // 요일별 영업 시간 변환
        for (DayInfo dayInfo : DayInfo.values()) {
            Map<String, String> day = new LinkedHashMap<>();
            Object restaurantBusinessTimeInfo = businessTime.get(dayInfo.name());
            TimeData timeData = OBJECT_MAPPER.convertValue(restaurantBusinessTimeInfo, TimeData.class);

            day.put("businessTime", "휴업");

            if (timeData.isBusinessStatus()) {
                day.put("businessTime", timeData.getStartTime() + " ~ " + timeData.getEndTime());
            }

            if (timeData.isBreakBusinessStatus()) {
                day.put("breakTime", timeData.getBreakStartTime() + " ~ " + timeData.getBreakEndTime());
            }

            convertTime.put(dayInfo.name(), day);
        }

        return convertTime;
    }

    public static String getBusinessStatus(Restaurant restaurant) {
        // 오늘 요일 확인
        String today = LocalDate.now().getDayOfWeek().toString();

        // 현재 시간 확인
        LocalTime currentTime = LocalTime.now();

        // 오늘의 레스토랑 영업시간
        Object restaurantBusinessTimeInfo = restaurant.getTime().get(today);

        // JSON to TimeData
        TimeData timeData = OBJECT_MAPPER.convertValue(restaurantBusinessTimeInfo, TimeData.class);

        LocalTime openTime = timeData.getStartTime();
        LocalTime closeTime = timeData.getEndTime();
        LocalTime breakStartTime = timeData.getBreakStartTime();
        LocalTime breakEndTime = timeData.getBreakEndTime();
        boolean businessStatus = timeData.isBusinessStatus();
        boolean breakBusinessStatus = timeData.isBreakBusinessStatus();

        // 휴무일인가?
        if (!businessStatus) {
            return CLOSE;
        }

        // 브레이크타임인가?
        if (breakBusinessStatus && currentTime.isAfter(breakStartTime) && currentTime.isBefore(breakEndTime)) {
            return BREAK_TIME;
        }

        // 영업시간인가?
        if (currentTime.isAfter(openTime) && currentTime.isBefore(closeTime)) {
            return OPEN;
        }

        return CLOSE;
    }
}
