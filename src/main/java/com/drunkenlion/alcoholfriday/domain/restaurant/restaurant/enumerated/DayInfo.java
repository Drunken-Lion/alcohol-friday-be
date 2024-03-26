package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated;

public enum DayInfo {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static boolean checkedInfo(String info) {
        for (DayInfo day : DayInfo.values()) {
            if (day.name().equals(info)) {
                return true;
            }
        }

        return false;
    }
}
