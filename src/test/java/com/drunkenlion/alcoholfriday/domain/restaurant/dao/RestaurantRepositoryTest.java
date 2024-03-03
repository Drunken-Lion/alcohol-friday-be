package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RestaurantRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    private double neLatitude = 37.5567635;
    private double neLongitude = 126.8529193;
    private double swLatitude = 37.5482577;
    private double swLongitude = 126.8421905;


    private Map<String, Object> getMenuTest() {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("비빔밥", 8000);
        frame.put("돌솥 비빔밥", 5000);
        frame.put("불고기", 12000);
        return frame;
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put(TimeOption.HOLIDAY.toString(), true);
        allDayTime.put(TimeOption.ETC.toString(), "여름/겨울 방학 휴업");

        TimeData timeData = TimeData.builder()
                .businessStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .breakBusinessStatus(true)
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
                .email("owner1@example.com")
                .provider(ProviderType.KAKAO)
                .name("toss")
                .nickname("toss")
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

        final Coordinate coordinate = new Coordinate(126.842299, 37.549636);
        Point restaurant_location = geometryFactory.createPoint(coordinate);
        Restaurant restaurant = Restaurant.builder()
                .members(member)
                .category("학식")
                .name("우정산 폴리텍대학")
                .address("우정산 서울강서 캠퍼스")
                .location(restaurant_location)
                .contact(1012345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .createdAt(LocalDateTime.now())
                .build();

        restaurantRepository.save(restaurant);

        Member 회원_관리자 = memberRepository.save(Member.builder()
                .email("admin1@example.com")
                .provider(ProviderType.KAKAO)
                .name("관리자")
                .nickname("admin")
                .role(MemberRole.ADMIN)
                .phone(1041932693L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        final Coordinate 가게1_좌표 = new Coordinate(0, 0);
        final Point 가게1_위치 = geometryFactory.createPoint(가게1_좌표);
        Restaurant 가게1 = restaurantRepository.save(Restaurant.builder()// 1
                .members(회원_관리자)
                .category("퓨전 음식점")
                .name("원주")
                .address("서울특별시 종로구 종로8길 16")
                .location(가게1_위치) // 위도, 경도
                .contact(027331371L)
                .menu(Map.of("김치찌개", 8000, "순두부", 8000, "제육볶음", 8000, "황태국", 8000))
                .time(getTimeTest())
                .createdAt(LocalDateTime.now())
                .provision(getProvisionTest())
                .build());
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    @Test
    @DisplayName("전체 매장 조회 - ADMIN")
    void findAllBasedAuthAdminTest() {
        // given
        Member 회원_관리자 = memberRepository.findByEmail("admin1@example.com").get();
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Restaurant> search = restaurantRepository.findAllBasedAuth(회원_관리자, pageable);

        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("전체 매장 조회 - OWNER")
    void findAllBasedAuthOwnerTest() {
        // given
        Member 회원_사장1 = memberRepository.findByEmail("owner1@example.com").get();
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Restaurant> search = restaurantRepository.findAllBasedAuth(회원_사장1, pageable);

        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("범위 내의 모든 레스토랑 정보 찾기")
    public void nearbyRestaurant() {
        //give
        double restaurantLatitude = 37.549636;
        double restaurantLongitude = 126.842299;

        //when
        List<Restaurant> polygon = restaurantRepository.findAllWithinPolygon(neLatitude, neLongitude, swLatitude, swLongitude);

        //then
        assertThat(polygon.get(0).getCategory()).isEqualTo("학식");
        assertThat(polygon.get(0).getName()).isEqualTo("우정산 폴리텍대학");
        assertThat(polygon.get(0).getAddress()).isEqualTo("우정산 서울강서 캠퍼스");;
        assertThat(polygon.get(0).getLocation().getX()).isEqualTo(restaurantLongitude);;
        assertThat(polygon.get(0).getLocation().getY()).isEqualTo(restaurantLatitude);;
    }
}
