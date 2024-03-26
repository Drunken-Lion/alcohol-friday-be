package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeData {
    private boolean businessStatus; // 영업 여부
    private LocalTime startTime; // 영업 시작 시간
    private LocalTime endTime; // 영업 종료 시간

    private boolean breakBusinessStatus; // 영업 여부
    private LocalTime breakStartTime; // 브레이크 타임 시작 시간
    private LocalTime breakEndTime; // 브레이크 타임 종료 시간
}
