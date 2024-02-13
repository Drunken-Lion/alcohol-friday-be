package com.drunkenlion.alcoholfriday.global.init;

import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.geo.Point;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Profile("!prod & !test")
@RequiredArgsConstructor
@Configuration
public class NotProd {
    @Autowired
    @Lazy
    private NotProd self;

    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;

    @Bean
    @Order(3)
    public ApplicationRunner initNotProd() {
        return args -> {
            self.work1();
        };
    }

    private Map<String, Object> getMenuTest()  {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("비빔밥", 8000);
        frame.put("불고기", 12000);
        return frame;
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put(TimeOption.HOLIDAY.toString(), true);
        allDayTime.put(TimeOption.ETC.toString(), "명절 당일만 휴업");

        TimeData timeData = TimeData.builder()
                .businessStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22,0))
                .breakBusinessStatus(true)
                .breakStartTime(LocalTime.of(15,0))
                .breakEndTime(LocalTime.of(17,0))
                .build();

        for (DayInfo value : DayInfo.values()) {
            allDayTime.put(value.toString(), timeData);
        }

        return allDayTime;
    }

    private Map<String, Object> getProvisionTest() {
        Map<String, Object> frame = new LinkedHashMap<>();

        for (Provision value : Provision.values()) {
            frame.put(value.toString(), true);
        }
        return frame;
    }

    @Transactional
    public void work1() throws IOException {
        if (memberRepository.findById(1L).isPresent()) {
            return;
        }

        IntStream.range(0, 50).forEach(i -> {
            Member member = Member.builder()
                    .email("test" + i + "@example.com")
                    .provider("kakao_test12345" + i)
                    .name("테스트" + i)
                    .nickname("test" + i)
                    .role("MEMBER")
                    .phone(1012345678L)
                    .certifyAt(null)
                    .agreedToServiceUse(false)
                    .agreedToServicePolicy(false)
                    .agreedToServicePolicyUse(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            memberRepository.save(member);

            Restaurant restaurant = Restaurant.builder()
                    .members(member)
                    .category("한식" + member.getId())
                    .name("맛있는 한식당" + member.getId())
                    .address("서울시 강남구")
                    .location(new Point(37.4979,127.0276)) // 위도, 경도
                    .contact(1012345678L)
                    .menu(getMenuTest())
                    .time(getTimeTest())
                    .provision(getProvisionTest())
                    .createdAt(LocalDateTime.now())
                    .build();

            restaurantRepository.save(restaurant);
        });
    }
}
