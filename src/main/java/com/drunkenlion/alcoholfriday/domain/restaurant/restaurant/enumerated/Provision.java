package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import java.util.List;

/**
 * 편의 시설
 */
public enum Provision {
    /**
     * 반려동물 허용
     */
    PET,
    /**
     * 주차 가능
     */
    PARKING,
    /**
     * 단체모임 가능
     */
    GROUP_MEETING,
    /**
     * 전화예약 가능
     */
    PHONE_RESERVATION,
    /**
     * wifi 제공
     */
    WIFI,
    /**
     * 남녀 화장실 구분
     */
    GENDER_SEPARATED_RESTROOM,
    /**
     * 포장 가능
     */
    PACKAGING,
    /**
     * 대기공간 제공
     */
    WAITING_AREA,
    /**
     * 유아의자 제공
     */
    BABY_CHAIR,
    /**
     * 출입구 휠체어 이용 가능
     */
    WHEELCHAIR_ACCESSIBLE_ENTRANCE,
    /**
     * 좌석 휠체어 이용 가능
     */
    WHEELCHAIR_ACCESSIBLE_SEAT,
    /**
     * 장애인 주차 구역
     */
    DISABLED_PARKING_AREA,
    ;

    public static List<Provision> getProvisions(Restaurant restaurant) {
        return restaurant.getProvision().entrySet().stream()
                .filter(entry -> (boolean) entry.getValue())
                .map(entry -> Provision.valueOf(entry.getKey()))
                .toList();
    }
}
