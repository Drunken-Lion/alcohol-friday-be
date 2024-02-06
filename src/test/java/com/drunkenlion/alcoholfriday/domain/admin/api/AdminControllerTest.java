package com.drunkenlion.alcoholfriday.domain.admin.api;

import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.util.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.util.TimeData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // 날짜 패턴 정규식
    private static final String DATETIME_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.?\\d{0,7}";
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    private Map<String, Object> getMenuTest()  {
        Map<String, Object> frame = new HashMap<>();
        frame.put("비빔밥", 8000);
        frame.put("불고기", 12000);
        return frame;
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put("holiday", true);
        allDayTime.put("etc", "명절 당일만 휴업");

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

    @BeforeEach
    @Transactional
    void beforeEach() {
            Member member = Member.builder()
                    .email("test@example.com")
                    .provider("kakao_test12345")
                    .name("테스트")
                    .nickname("test")
                    .role("MEMBER")
                    .phone(1012345678L)
                    .certifyAt(null)
                    .agreedToServiceUse(false)
                    .agreedToServicePolicy(false)
                    .agreedToServicePolicyUse(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(null)
                    .deletedAt(null)
                    .build();

            memberRepository.save(member);

            Restaurant restaurant = Restaurant.builder()
                    .members(member)
                    .category("한식")
                    .name("맛있는 한식당")
                    .address("서울시 강남구")
                    .location(new Point(37.4979,127.0276))
                    .contact(1012345678L)
                    .menu(getMenuTest())
                    .time(getTimeTest())
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
    void getMembersTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/members")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminController.class))
                .andExpect(handler().methodName("getMembers"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].nickname", notNullValue()))
                .andExpect(jsonPath("$.data[0].email", notNullValue()))
                .andExpect(jsonPath("$.data[0].role", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    void getMemberTest() throws Exception {
        // given
        Member member = this.memberRepository.findById(1L).get();

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/member/" + member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminController.class))
                .andExpect(handler().methodName("getMember"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.provider", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.nickname", notNullValue()))
                .andExpect(jsonPath("$.role", notNullValue()))
                .andExpect(jsonPath("$.phone", notNullValue()))
                .andExpect(jsonPath("$.certifyAt", anyOf(is(matchesPattern(DATE_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.agreedToServiceUse", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.agreedToServicePolicy", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.agreedToServicePolicyUse", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", anyOf(is(matchesPattern(DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(DATETIME_PATTERN)), is(nullValue()))));
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
                .andExpect(handler().handlerType(AdminController.class))
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
}
