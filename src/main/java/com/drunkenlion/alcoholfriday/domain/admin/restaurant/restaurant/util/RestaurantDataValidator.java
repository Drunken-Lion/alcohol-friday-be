package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.TimeOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestaurantDataValidator {

    public static boolean isValid(RestaurantRequest restaurantRequest) {
        return isMenuDataValid(restaurantRequest.getMenu()) &&
                isTimeDataValid(restaurantRequest.getTime()) &&
                isProvisionDataValid(restaurantRequest.getProvision());
    }

    public static boolean isMenuDataValid(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!(entry.getValue() instanceof Integer)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isTimeDataValid(Map<String, Object> map) {
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

            if (DayInfo.checkedInfo(entry.getKey())) {
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

    public static boolean isProvisionDataValid(Map<String, Object> map) {
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
