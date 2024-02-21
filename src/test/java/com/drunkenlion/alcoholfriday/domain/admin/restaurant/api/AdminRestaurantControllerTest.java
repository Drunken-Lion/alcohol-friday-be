package com.drunkenlion.alcoholfriday.domain.admin.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminRestaurantControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    // 날짜 패턴 정규식
    private static final String DATETIME_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.?\\d{0,7}";
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    private Map<String, Object> getMenuTest() {
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
                .endTime(LocalTime.of(22, 0))
                .breakBusinessStatus(true)
                .breakStartTime(LocalTime.of(15, 0))
                .breakEndTime(LocalTime.of(17, 0))
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

    @BeforeEach
    @Transactional
    void beforeEach() {
        Member member = Member.builder()
                .email("test@example.com")
                .provider(ProviderType.KAKAO)
                .name("테스트")
                .nickname("test")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        memberRepository.save(member);

        final Coordinate coordinate = new Coordinate(126.984634, 37.569833);
        Point restaurant_location = geometryFactory.createPoint(coordinate);
        Restaurant restaurant = Restaurant.builder()
                .members(member)
                .category("한식")
                .name("맛있는 한식당")
                .address("서울시 강남구")
                .location(restaurant_location)
                .contact(1012345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .createdAt(LocalDateTime.now())
                .build();

        restaurantRepository.save(restaurant);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    @Test
    void getRestaurantsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminRestaurantController.class))
                .andExpect(handler().methodName("getRestaurants"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].memberNickname", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].category", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    void getRestaurantTest() throws Exception {
        // given
        Restaurant restaurant = this.restaurantRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurants/" + restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminRestaurantController.class))
                .andExpect(handler().methodName("getRestaurant"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberNickname", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.category", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.location", notNullValue()))
                .andExpect(jsonPath("$.contact", instanceOf(Number.class)))
                .andExpect(jsonPath("$.menu", instanceOf(Map.class)))
                .andExpect(jsonPath("$.time", instanceOf(Map.class)))
                .andExpect(jsonPath("$.provision", instanceOf(Map.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    void createRestaurantTest() throws Exception {
        // given
        Long memberId = this.memberRepository.findAll().get(0).getId();

        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "memberId": %d,
                                  "name": "test 매장",
                                  "category": "test 카테고리",
                                  "address": "test 주소",
                                  "location": {
                                    "x": 10.123456,
                                    "y": 15.321654
                                  },
                                  "contact": 212354678,
                                  "menu": {
                                    "test 메뉴1": 10000,
                                    "test 메뉴2": 20000,
                                    "test 메뉴3": 30000
                                  },
                                  "time": {
                                    "HOLIDAY": true,
                                    "ETC": "명절 당일만 휴업",
                                    "MONDAY": {"businessStatus":true,"startTime":[9,0],"endTime":[11,0],"breakBusinessStatus":true,"breakStartTime":[15,0],"breakEndTime":[17,0]},
                                    "TUESDAY": {"businessStatus":true,"startTime":[9,0],"endTime":[22,0],"breakBusinessStatus":true,"breakStartTime":[15,0],"breakEndTime":[17,0]},
                                    "WEDNESDAY": {"businessStatus":true,"startTime":[9,0],"endTime":[22,0],"breakBusinessStatus":true,"breakStartTime":[15,0],"breakEndTime":[17,0]},
                                    "THURSDAY": {"businessStatus":true,"startTime":[9,0],"endTime":[22,0],"breakBusinessStatus":true,"breakStartTime":[15,0],"breakEndTime":[17,0]},
                                    "FRIDAY": {"businessStatus":true,"startTime":[9,0],"endTime":[22,0],"breakBusinessStatus":true,"breakStartTime":[15,0],"breakEndTime":[17,0]},
                                    "SATURDAY": {"businessStatus":true,"startTime":[9,0],"endTime":[22,0],"breakBusinessStatus":true,"breakStartTime":[15,0],"breakEndTime":[17,0]},
                                    "SUNDAY": {"businessStatus":true,"startTime":[9,0],"endTime":[22,0],"breakBusinessStatus":true,"breakStartTime":[15,0],"breakEndTime":[17,0]}
                                  },
                                  "provision": {
                                    "PET": true,
                                    "PARKING": true,
                                    "GROUP_MEETING": true,
                                    "PHONE_RESERVATION": true,
                                    "WIFI": true,
                                    "GENDER_SEPARATED_RESTROOM": true,
                                    "PACKAGING": true,
                                    "WAITING_AREA": true,
                                    "BABY_CHAIR": true,
                                    "WHEELCHAIR_ACCESSIBLE_ENTRANCE": true,
                                    "WHEELCHAIR_ACCESSIBLE_SEAT": true,
                                    "DISABLED_PARKING_AREA": true
                                  }
                                }
                                """, memberId))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminRestaurantController.class))
                .andExpect(handler().methodName("createRestaurant"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberNickname", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.category", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.location", notNullValue()))
                .andExpect(jsonPath("$.contact", instanceOf(Number.class)))
                .andExpect(jsonPath("$.menu", instanceOf(Map.class)))
                .andExpect(jsonPath("$.time", instanceOf(Map.class)))
                .andExpect(jsonPath("$.provision", instanceOf(Map.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("매장 삭제 성공")
    void deleteRestaurantTest() throws Exception {
        // given
        Restaurant restaurant = this.restaurantRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/restaurants/" + restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminRestaurantController.class))
                .andExpect(handler().methodName("deleteRestaurant"));
    }
}
