package com.drunkenlion.alcoholfriday.global.common.enumerated;

import lombok.Getter;
/*
* 방향
* */
@Getter
public enum Direction {

    /*
     * 북쪽
     * */
    NORTH(0.0),
    /*
     * 서쪽
     * */
    WEST(270.0),
    /*
     * 남쪽
     * */
    SOUTH(180.0),
    /*
     * 동쪽
     * */
    EAST(90.0),
    /*
     * 북서쪽
     * */
    NORTHWEST(315.0),
    /*
     * 남서쪽
     * */
    SOUTHWEST(225.0),
    /*
     * 남동쪽
     * */
    SOUTHEAST(135.0),
    /*
     * 북동쪽
     * */
    NORTHEAST(45.0);
    /*
     * 방향 각도
     * */
    private final Double bearing;

    Direction(Double bearing) {
        this.bearing = bearing;
    }
}
