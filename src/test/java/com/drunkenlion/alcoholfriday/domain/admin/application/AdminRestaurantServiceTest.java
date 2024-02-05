package com.drunkenlion.alcoholfriday.domain.admin.application;

import com.drunkenlion.alcoholfriday.domain.admin.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminRestaurantServiceTest {
    @InjectMocks
    private AdminRestaurantServiceImpl adminRestaurantService;
    @Mock
    private RestaurantRepository restaurantRepository;

    private final Long memberId = 1L;
    private final String email = "test@example.com";
    private final String provider = "kakao_test12345";
    private final String memberName = "테스트";
    private final String nickname = "test";
    private final String role = "MEMBER";
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = null;
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;
    private final LocalDateTime memberCreatedAt = LocalDateTime.now();

    private final Long id = 1L;
    private final String category = "한식";
    private final String name = "맛있는 한식당";
    private final String address = "서울시 강남구";
    private final Point location = new Point(37.4979,127.0276);
    private final Long contact = 1012345678L;

    private Map<String, Object> menu = new HashMap<String, Object>(){{
        put("비빔밥", 8000);
        put("불고기", 12000);
    }};

    private Map<String, Object> time = new HashMap<String, Object>(){{
        put("오픈 시간", "09:00");
        put("마감 시간", "22:00");
    }};

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final int page = 0;
    private final int size = 20;

    @Test
    public void getRestaurantsTest() {
        // given
        Mockito.when(this.restaurantRepository.findAll(any(Pageable.class))).thenReturn(this.getRestaurants());

        // when
        Page<RestaurantListResponse> restaurants = this.adminRestaurantService.getRestaurants(page, size);

        // then
        List<RestaurantListResponse> content = restaurants.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(id);
        assertThat(content.get(0).getMemberNickname()).isEqualTo(nickname);
        assertThat(content.get(0).getName()).isEqualTo(name);
        assertThat(content.get(0).getCategory()).isEqualTo(category);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    private Page<Restaurant> getRestaurants() {
        List<Restaurant> list = List.of(this.getData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Restaurant>(list, pageable, list.size());
    }

    private Restaurant getData() {

        Member member = Member.builder()
                .id(memberId)
                .email(email)
                .provider(provider)
                .name(memberName)
                .nickname(nickname)
                .role(role)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(memberCreatedAt)
                .build();

        return Restaurant.builder()
                .id(id)
                .members(member)
                .category(category)
                .name(name)
                .address(address)
                .location(location)
                .contact(contact)
                .menu(menu)
                .time(time)
                .createdAt(createdAt)
                .build();
    }
}
