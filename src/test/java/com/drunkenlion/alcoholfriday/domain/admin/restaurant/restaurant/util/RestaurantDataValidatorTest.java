package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.util.RestaurantDataValidator;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestaurantDataValidatorTest {
    @Test
    @DisplayName("매장 메뉴 Validation 체크")
    public void testIsMenuDataValid() {
        Map<String, Object> validMenuData = new HashMap<>();
        validMenuData.put("menu1", 10000);
        validMenuData.put("menu2", 20000);

        assertTrue(RestaurantDataValidator.isMenuDataValid(validMenuData));

        Map<String, Object> invalidMenuData = new HashMap<>();
        invalidMenuData.put("menu1", "invalid data");

        assertFalse(RestaurantDataValidator.isMenuDataValid(invalidMenuData));
    }

    @Test
    @DisplayName("매장 영업시간 Validation 체크")
    public void testIsTimeDataValid() {

        TimeData timeData = TimeData.builder()
                .businessStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22,0))
                .breakBusinessStatus(true)
                .breakStartTime(LocalTime.of(15,0))
                .breakEndTime(LocalTime.of(17,0))
                .build();

        Map<String, Object> validTimeData = new LinkedHashMap<>();
        validTimeData.put(TimeOption.HOLIDAY.toString(), true);
        validTimeData.put(TimeOption.ETC.toString(), "명절 당일만 휴업");

        for (DayInfo value : DayInfo.values()) {
            validTimeData.put(value.toString(), timeData);
        }

        assertTrue(RestaurantDataValidator.isTimeDataValid(validTimeData));

        Map<String, Object> invalidTimeData = new HashMap<>();
        invalidTimeData.put(TimeOption.HOLIDAY.toString(), "invalid data");
        invalidTimeData.put(TimeOption.ETC.toString(), "명절 당일만 휴업");

        for (DayInfo value : DayInfo.values()) {
            invalidTimeData.put(value.toString(), timeData);
        }

        assertFalse(RestaurantDataValidator.isTimeDataValid(invalidTimeData));
    }

    @Test
    @DisplayName("매장 편의시설 Validation 체크")
    public void testIsProvisionDataValid() {
        Map<String, Object> validProvisionData = new HashMap<>();
        for (Provision provision : Provision.values()) {
            validProvisionData.put(provision.name(), true);
        }

        assertTrue(RestaurantDataValidator.isProvisionDataValid(validProvisionData));

        Map<String, Object> invalidProvisionData = new HashMap<>();
        invalidProvisionData.put("invalid data", true);

        assertFalse(RestaurantDataValidator.isProvisionDataValid(invalidProvisionData));
    }
}
