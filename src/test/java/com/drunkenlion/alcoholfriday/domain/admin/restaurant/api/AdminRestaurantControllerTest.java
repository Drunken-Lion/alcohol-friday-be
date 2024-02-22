package com.drunkenlion.alcoholfriday.domain.admin.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.file.dao.FileRepository;
import com.drunkenlion.alcoholfriday.global.ncp.application.NcpS3ServiceImpl;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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

    @Autowired
    private RestaurantStockRepository restaurantStockRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private NcpS3ServiceImpl ncpS3Service;

    @Autowired
    private FileRepository fileRepository;

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

        List<Item> items = LongStream.rangeClosed(1, 2).mapToObj(i -> {
            return Item.builder()
                    .id(i)
                    .name("itemName" + i)
                    .price(BigDecimal.valueOf(i))
                    .info("info")
                    .build();
        }).collect(Collectors.toList());

        itemRepository.saveAll(items);

        List<RestaurantStock> restaurantStocks = items.stream().map(item -> {
            return RestaurantStock.builder()
                    .id(item.getId())
                    .item(item)
                    .restaurant(restaurant)
                    .quantity(100L)
                    .createdAt(LocalDateTime.now())
                    .build();
        }).collect(Collectors.toList());

        restaurantStockRepository.saveAll(restaurantStocks);

        List<NcpFile> ncpFiles = items.stream().map(item -> {
            File file = new File(getClass().getClassLoader().getResource("img/gayoung.jpeg").getFile());
            InputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            MultipartFile mpf = null;
            try {
                mpf = new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<MultipartFile> files = List.of(mpf);
            return ncpS3Service.ncpUploadFiles(files, item.getId(), EntityType.ITEM.getEntityName());
        }).collect(Collectors.toList());

        fileRepository.saveAll(ncpFiles);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    @Test
    @DisplayName("매장 목록 조회 성공")
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
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("매장 상세 조회 성공")
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
                .andExpect(jsonPath("$.longitude", instanceOf(Number.class)))
                .andExpect(jsonPath("$.latitude", instanceOf(Number.class)))
                .andExpect(jsonPath("$.contact", instanceOf(Number.class)))
                .andExpect(jsonPath("$.menu", instanceOf(Map.class)))
                .andExpect(jsonPath("$.time", instanceOf(Map.class)))
                .andExpect(jsonPath("$.provision", instanceOf(Map.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.stockItemInfos[0].stockItemId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.stockItemInfos[0].stockItemName", notNullValue()))
                .andExpect(jsonPath("$.stockItemInfos[0].stockQuantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.stockItemInfos[0].stockItemFile", notNullValue()));
    }

    @Test
    @DisplayName("매장 등록 성공")
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
                                  "longitude": 10.123456,
                                  "latitude": 15.321654,
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
                .andExpect(jsonPath("$.longitude", instanceOf(Number.class)))
                .andExpect(jsonPath("$.latitude", instanceOf(Number.class)))
                .andExpect(jsonPath("$.contact", instanceOf(Number.class)))
                .andExpect(jsonPath("$.menu", instanceOf(Map.class)))
                .andExpect(jsonPath("$.time", instanceOf(Map.class)))
                .andExpect(jsonPath("$.provision", instanceOf(Map.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.stockItemInfos", notNullValue()));
    }

    @Test
    @DisplayName("매장 수정 성공")
    void modifyRestaurantTest() throws Exception {
        // given
        Long memberId = this.memberRepository.findAll().get(0).getId();
        Restaurant restaurant = this.restaurantRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/restaurants/" + restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "memberId": %d,
                                  "name": "test 매장",
                                  "category": "test 카테고리",
                                  "address": "test 주소",         
                                  "longitude": 10.123456,
                                  "latitude": 15.321654,
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
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminRestaurantController.class))
                .andExpect(handler().methodName("modifyRestaurant"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.memberNickname", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.category", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.longitude", instanceOf(Number.class)))
                .andExpect(jsonPath("$.latitude", instanceOf(Number.class)))
                .andExpect(jsonPath("$.contact", instanceOf(Number.class)))
                .andExpect(jsonPath("$.menu", instanceOf(Map.class)))
                .andExpect(jsonPath("$.time", instanceOf(Map.class)))
                .andExpect(jsonPath("$.provision", instanceOf(Map.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.stockItemInfos[0].stockItemId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.stockItemInfos[0].stockItemName", notNullValue()))
                .andExpect(jsonPath("$.stockItemInfos[0].stockQuantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.stockItemInfos[0].stockItemFile", notNullValue()));
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
