package com.drunkenlion.alcoholfriday.global.init;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.customerservice.dao.AnswerRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.file.application.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final MakerRepository makerRepository;
    private final FileServiceImpl fileService;
    private final CategoryClassRepository categoryClassRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AddressRepository addressRepository;
    private final NoticeRepository noticeRepository;
    private final ItemRepository itemRepository;
    private final ItemProductRepository itemProductRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final RestaurantStockRepository restaurantStockRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewRepository reviewRepository;

    @Bean
    @Order(3)
    public ApplicationRunner initNotProd() {
        return args -> {
            if (memberRepository.findById(1L).isPresent()) {
                return;
            }

            addData();
        };
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put("holiday", true);
        allDayTime.put("etc", "명절 당일만 휴업");

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

    /**
     * 직접 이미지를 삽입하는 메서드
     */
    private void insertImage(BaseEntity entity, List<MultipartFile> files) {
        fileService.saveFiles(entity, files);
    }

    public void addData() throws Exception {
        Member 회원_김태섭 = memberRepository.save(Member.builder()
                .email("smileby95@nate.com")
                .provider(ProviderType.KAKAO)
                .name("김태섭")
                .nickname("seop")
                .role(MemberRole.ADMIN)
                .phone(1041932693L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_황중석 = memberRepository.save(Member.builder()
                .email("hjs7949@naver.com")
                .provider(ProviderType.KAKAO)
                .name("황중석")
                .nickname("황중석")
                .role(MemberRole.ADMIN)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_유민석 = memberRepository.save(Member.builder()
                .email("ekslws123@nate.com")
                .provider(ProviderType.KAKAO)
                .name("유민석")
                .nickname("유민석")
                .role(MemberRole.ADMIN)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_최동철 = memberRepository.save(Member.builder()
                .email("cheek0805@naver.com")
                .provider(ProviderType.KAKAO)
                .name("최동철")
                .nickname("최동철")
                .role(MemberRole.ADMIN)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_이아영 = memberRepository.save(Member.builder()
                .email("arong16@naver.com")
                .provider(ProviderType.KAKAO)
                .name("이아영")
                .nickname("아롱사태")
                .role(MemberRole.ADMIN)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert SuperVisor
        Member 회원_슈퍼바이저1 = memberRepository.save(Member.builder()
                .email("supervisor1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor1")
                .nickname("Supervisor1")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_슈퍼바이저2 = memberRepository.save(Member.builder()
                .email("supervisor2@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor2")
                .nickname("Supervisor2")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_슈퍼바이저3 = memberRepository.save(Member.builder()
                .email("supervisor3@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor3")
                .nickname("Supervisor3")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_슈퍼바이저4 = memberRepository.save(Member.builder()
                .email("supervisor4@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor4")
                .nickname("Supervisor4")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_슈퍼바이저5 = memberRepository.save(Member.builder()
                .email("supervisor5@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor5")
                .nickname("Supervisor5")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert StoreManager
        Member 회원_스토어매니저1 = memberRepository.save(Member.builder()
                .email("storeManager1@example.com")
                .provider(ProviderType.KAKAO)
                .name("StoreManager1")
                .nickname("StoreManager1")
                .role(MemberRole.STORE_MANAGER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_스토어매니저2 = memberRepository.save(Member.builder()
                .email("storeManager2@example.com")
                .provider(ProviderType.KAKAO)
                .name("StoreManager2")
                .nickname("StoreManager2")
                .role(MemberRole.STORE_MANAGER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_스토어매니저3 = memberRepository.save(Member.builder()
                .email("storeManager3@example.com")
                .provider(ProviderType.KAKAO)
                .name("StoreManager3")
                .nickname("StoreManager3")
                .role(MemberRole.STORE_MANAGER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_스토어매니저4 = memberRepository.save(Member.builder()
                .email("storeManager4@example.com")
                .provider(ProviderType.KAKAO)
                .name("StoreManager4")
                .nickname("StoreManager4")
                .role(MemberRole.STORE_MANAGER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_스토어매니저5 = memberRepository.save(Member.builder()
                .email("storeManager5@example.com")
                .provider(ProviderType.KAKAO)
                .name("StoreManager5")
                .nickname("StoreManager5")
                .role(MemberRole.STORE_MANAGER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert Owner
        Member 회원_사장1 = memberRepository.save(Member.builder()
                .email("owner1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner1")
                .nickname("Owner1")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장2 = memberRepository.save(Member.builder()
                .email("owner2@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner2")
                .nickname("Owner2")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장3 = memberRepository.save(Member.builder()
                .email("owner3@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner3")
                .nickname("Owner3")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장4 = memberRepository.save(Member.builder()
                .email("owner4@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner4")
                .nickname("Owner4")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장5 = memberRepository.save(Member.builder()
                .email("owner5@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner5")
                .nickname("Owner5")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장6 = memberRepository.save(Member.builder()
                .email("owner6@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner6")
                .nickname("Owner6")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장7 = memberRepository.save(Member.builder()
                .email("owner7@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner7")
                .nickname("Owner7")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장8 = memberRepository.save(Member.builder()
                .email("owner8@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner8")
                .nickname("Owner8")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장9 = memberRepository.save(Member.builder()
                .email("owner9@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner9")
                .nickname("Owner9")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_사장10 = memberRepository.save(Member.builder()
                .email("owner10@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner10")
                .nickname("Owner10")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert Member
        Member 회원_일반회원1 = memberRepository.save(Member.builder()
                .email("member1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member1")
                .nickname("Member1")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원2 = memberRepository.save(Member.builder()
                .email("member2@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member2")
                .nickname("Member2")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원3 = memberRepository.save(Member.builder()
                .email("member3@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member3")
                .nickname("Member3")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원4 = memberRepository.save(Member.builder()
                .email("member4@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member4")
                .nickname("Member4")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원5 = memberRepository.save(Member.builder()
                .email("member5@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member5")
                .nickname("Member5")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원6 = memberRepository.save(Member.builder()
                .email("member6@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member6")
                .nickname("Member6")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원7 = memberRepository.save(Member.builder()
                .email("member7@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member7")
                .nickname("Member7")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원8 = memberRepository.save(Member.builder()
                .email("member8@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member8")
                .nickname("Member8")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원9 = memberRepository.save(Member.builder()
                .email("member9@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member9")
                .nickname("Member9")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_일반회원10 = memberRepository.save(Member.builder()
                .email("member10@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member10")
                .nickname("Member10")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        final Coordinate 가게1_좌표 = new Coordinate(126.983857, 37.569343);
        final Point 가게1_위치 = geometryFactory.createPoint(가게1_좌표);
        Restaurant 가게1 = restaurantRepository.save(Restaurant.builder()// 1
                .members(회원_사장1)
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

        final Coordinate 가게2_좌표 = new Coordinate(126.983857, 37.569343);
        final Point 가게2_위치 = geometryFactory.createPoint(가게2_좌표);
        Restaurant 가게2 = restaurantRepository.save(Restaurant.builder()// 2
                .members(회원_사장2)
                .category("치킨 전문점")
                .name("구도로통닭")
                .address("서울특별시 종로구 우정국로2길 16")
                .location(가게2_위치) // 위도, 경도
                .contact(1041932693L)
                .menu(Map.of("통닭구이", 11000, "양념통닭구이", 13000))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게3_좌표 = new Coordinate(126.983857, 37.569343);
        final Point 가게3_위치 = geometryFactory.createPoint(가게3_좌표);
        Restaurant 가게3 = restaurantRepository.save(Restaurant.builder()// 3
                .members(회원_사장3)
                .category("해산물 요리 전문식당")
                .name("금보포차")
                .address("서울특별시 종로구 종로8길 13 2층")
                .location(가게3_위치) // 위도, 경도
                .contact(1041932693L)
                .menu(Map.of("해신탕", 130000, "메로구이", 13000))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게4_좌표 = new Coordinate(126.983857, 37.569343);
        final Point 가게4_위치 = geometryFactory.createPoint(가게4_좌표);
        Restaurant 가게4 = restaurantRepository.save(Restaurant.builder() // 4
                .members(회원_사장4)
                .category("음식점")
                .name("종로삼계탕")
                .address("서울특별시 종로구 종로8길 21")
                .location(가게4_위치) // 위도, 경도
                .contact(1041932693L)
                .menu(Map.of("삼계탕", 16000, "한방삼계탕", 17000, "들깨삼계탕", 17000, "황태국", 8000))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게5_좌표 = new Coordinate(126.983857, 37.569343);
        final Point 가게5_위치 = geometryFactory.createPoint(가게5_좌표);
        Restaurant 가게5 = restaurantRepository.save(Restaurant.builder() // 5
                .members(회원_사장5)
                .category("베트남 음식점")
                .name("에머이")
                .address("서울특별시 종로구 종로12길 6-20")
                .location(가게5_위치) // 위도, 경도
                .contact(27330588L)
                .menu(Map.of("차돌 쌀국수", 13000, "불고기 쌀국수", 12000, "양지 쌀국수", 10900, "매운 쌀국수", 11900))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게6_좌표 = new Coordinate(126.984634, 37.569833);
        final Point 가게6_위치 = geometryFactory.createPoint(가게6_좌표);
        Restaurant 가게6 = restaurantRepository.save(Restaurant.builder() // 6
                .members(회원_사장6)
                .category("베트남 음식점")
                .name("에머이")
                .address("서울특별시 종로구 종로12길 6-20")
                .location(가게6_위치) // 위도, 경도
                .contact(27330588L)
                .menu(Map.of("차돌 쌀국수", 13000, "불고기 쌀국수", 12000, "양지 쌀국수", 10900, "매운 쌀국수", 11900))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게7_좌표 = new Coordinate(126.985152, 37.570418);
        final Point 가게7_위치 = geometryFactory.createPoint(가게7_좌표);
        Restaurant 가게7 = restaurantRepository.save(Restaurant.builder() // 7
                .members(회원_사장7)
                .category("참치 전문점")
                .name("이춘복참치")
                .address("서울특별시 종로구 종로2가 9")
                .location(가게7_위치) // 위도, 경도
                .contact(27234558L)
                .menu(Map.of("혼마구로정식", 30000, "특정식", 22000, "일반정식", 15000, "혼마구로초밥", 30000))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게8_좌표 = new Coordinate(126.986076, 37.570413);
        final Point 가게8_위치 = geometryFactory.createPoint(가게8_좌표);
        Restaurant 가게8 = restaurantRepository.save(Restaurant.builder() // 8
                .members(회원_사장8)
                .category("샌드위치 가게")
                .name("써브웨이")
                .address("서울특별시 종로구 삼성로 151 통일빌딩")
                .location(가게8_위치) // 위도, 경도
                .contact(27370034L)
                .menu(Map.of("혼마구로정식", 30000, "특정식", 22000, "일반정식", 15000, "혼마구로초밥", 30000))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게9_좌표 = new Coordinate(126.984152, 37.569940);
        final Point 가게9_위치 = geometryFactory.createPoint(가게9_좌표);
        Restaurant 가게9 = restaurantRepository.save(Restaurant.builder() // 9
                .members(회원_사장9)
                .category("패스트푸드점")
                .name("맘스터치")
                .address("서울특별시 종로구 종로2가 102-1")
                .location(가게9_위치) // 위도, 경도
                .contact(27387771L)
                .menu(Map.of("싸이버거", 7000, "불싸이버거", 7500, "치즈듬뿍싸이버거", 8000))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        final Coordinate 가게10_좌표 = new Coordinate(126.988497, 37.569555);
        final Point 가게10_위치 = geometryFactory.createPoint(가게8_좌표);
        Restaurant 가게10 = restaurantRepository.save(Restaurant.builder() // 10
                .members(회원_사장10)
                .category("한식당")
                .name("뚝배기집")
                .address("서울특별시 종로구 종로16길 12")
                .location(가게10_위치) // 위도, 경도
                .contact(222655744L)
                .menu(Map.of("우렁된장", 5500, "김치찌개", 5500, "순두부찌개", 5000))
                .time(getTimeTest())
                .provision(getProvisionTest())
                .build());

        // Insert Maker
        Maker 제조사_국순당 = makerRepository.save( // 1
                Maker.builder()
                        .name("(주)국순당")
                        .address("강원도 횡성군 둔내면 강변로 975")
                        .detail("101")
                        .region("강원도")
                        .build());

        Maker 제조사_청산녹수 = makerRepository.save( // 2
                Maker.builder()
                        .name("농업회사법인 (주) 청산녹수")
                        .address("전라남도 장성군 장성읍 남양촌길 (백계리) 19")
                        .detail("101")
                        .region("전라남도")
                        .build());

        Maker 제조사_한국애플리즈 = makerRepository.save( // 3
                Maker.builder()
                        .name("(주)한국애플리즈")
                        .address("경상북도 의성군 단촌면 일직점곡로 755")
                        .detail("101")
                        .region("경상북도")
                        .build());

        Maker 제조사_명세주가 = makerRepository.save( // 4
                Maker.builder()
                        .name("농업회사법인 명세주가(주)")
                        .address("충청북도 청주시 상당구 가덕면 한계길 32-5")
                        .detail("101")
                        .region("충청북도")
                        .build());

        Maker 제조사_청도감와인 = makerRepository.save( // 5
                Maker.builder()
                        .name("청도감와인(주)")
                        .address("경상북도 청도군 풍각면 봉길1길27")
                        .detail("101")
                        .region("경상북도")
                        .build());

        Maker 제조사_배상면주가 = makerRepository.save( // 6
                Maker.builder()
                        .name("(주)배상면주가")
                        .address("경기도 포천시 화현면 화동로 432번길 25")
                        .detail("101")
                        .region("경기도")
                        .build());

        Maker 제조사_술소리 = makerRepository.save( // 7
                Maker.builder()
                        .name("농업회사법인(유)술소리")
                        .address("전라북도 남원시 시묘길 130(오암동)")
                        .detail("101")
                        .region("전라북도")
                        .build());

        Maker 제조사_한국삼산 = makerRepository.save( // 8
                Maker.builder()
                        .name("농업회사법인 한국산삼(주)")
                        .address("충청남도 공주시 계룡면 여서울2길9")
                        .detail("101")
                        .region("충청남도")
                        .build());

        Maker 제조사_고창선운산 = makerRepository.save( // 9
                Maker.builder()
                        .name("농업회사법인 고창선운산(유)")
                        .address("전라북도 고창군 심원면 심원로 270-73")
                        .detail("101")
                        .region("전라북도")
                        .build());

        Maker 제조사_그린영농조합법인 = makerRepository.save( // 10
                Maker.builder()
                        .name("그린영농조합법인")
                        .address("경기도 안산시 단원구 뻐꾹산길 107(대부북동)")
                        .detail("101")
                        .region("경기도")
                        .build());

        // Insert Category
        CategoryClass 카테고리_대분류1 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 1")
                        .build());

        CategoryClass 카테고리_대분류2 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 2")
                        .build());

        CategoryClass 카테고리_대분류3 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 3")
                        .build());

        CategoryClass 카테고리_대분류4 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 4")
                        .build());

        CategoryClass 카테고리_대분류5 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 5")
                        .build());

        Category 카테고리_소분류1 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류1")
                        .categoryClass(카테고리_대분류1)
                        .build()
        );

        Category 카테고리_소분류2 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류2")
                        .categoryClass(카테고리_대분류2)
                        .build()
        );

        Category 카테고리_소분류3 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류3")
                        .categoryClass(카테고리_대분류2)
                        .build()
        );

        Category 카테고리_소분류4 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류4")
                        .categoryClass(카테고리_대분류2)
                        .build()
        );

        Category 카테고리_소분류5 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류5")
                        .categoryClass(카테고리_대분류3)
                        .build()
        );

        Category 카테고리_소분류6 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류6")
                        .categoryClass(카테고리_대분류3)
                        .build()
        );

        Category 카테고리_소분류7 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류7")
                        .categoryClass(카테고리_대분류4)
                        .build()
        );

        Category 카테고리_소분류8 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류8")
                        .categoryClass(카테고리_대분류4)
                        .build()
        );

        Category 카테고리_소분류9 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류9")
                        .categoryClass(카테고리_대분류5)
                        .build());

        Category 카테고리_소분류10 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류10")
                        .categoryClass(카테고리_대분류5)
                        .build()
        );

        // Insert Product
        Product 제품_국순당_프리바이오 = productRepository.save(Product.builder()
                .name("1000억 막걸리 프리바이오")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_국순당)
                .category(카테고리_소분류1)
                .build());

        Product 제품_국순당_유산균막걸리 = productRepository.save(Product.builder()
                .name("1000억 유산균막걸리")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_국순당)
                .category(카테고리_소분류2)
                .build());

        Product 제품_청산녹수_52C = productRepository.save(Product.builder()
                .name("52C")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("정제수, 주정, 쌀증류식소주원액, 오이")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_청산녹수)
                .category(카테고리_소분류3)
                .build());

        Product 제품_청산녹수_골디락스 = productRepository.save(Product.builder()
                .name("G12 골디락스")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("정제수, 멥쌀(국내산), 찹쌀(국내산), 누룩, 종국, 효모, 밀함유")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_청산녹수)
                .category(카테고리_소분류4)
                .build());

        Product 제품_한국애플리즈_피치 = productRepository.save(Product.builder()
                .name("The 찾을수록 피치")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("사과와인, 정제수, 정제주정 외 제품별 농축액(복숭아, 포도, 사과, 감귤, 생강, 커피향 등)")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_한국애플리즈)
                .category(카테고리_소분류5)
                .build());

        Product 제품_한국애플리즈_모스카토 = productRepository.save(Product.builder()
                .name("The 찾을수록 모스카토")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("사과와인, 정제수, 정제주정 외 제품별 농축액(복숭아, 포도, 사과, 감귤, 생강, 커피향 등)")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_한국애플리즈)
                .category(카테고리_소분류6)
                .build());

        Product 제품_명세주가_가덕막걸리 = productRepository.save(Product.builder()
                .name("가덕 순쌀 막걸리")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("국내산 쌀, 효모, 조효소재, 물엿, 아스파탐, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_명세주가)
                .category(카테고리_소분류7)
                .build());

        Product 제품_명세주가_매실향기 = productRepository.save(Product.builder()
                .name("매실향기 담 술")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("국내산 쌀, 효모, 조효소재, 물엿, 아스파탐, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_명세주가)
                .category(카테고리_소분류8)
                .build());

        Product 제품_청도감와인_감그린레귤러 = productRepository.save(Product.builder()
                .name("감그린 레귤러")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("감(국내산)")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_청도감와인)
                .category(카테고리_소분류9)
                .build());

        Product 제품_청도감와인_감그린스페셜 = productRepository.save(Product.builder()
                .name("감그린 스페셜")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5L)
                .ingredient("감(국내산)")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_청도감와인)
                .category(카테고리_소분류10)
                .build());

        // Insert Question
        Question 문의_일반1 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반2 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원2)
                        .title("일반 문의 제목 2")
                        .content("일반 문의 내용 2")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반3 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원3)
                        .title("일반 문의 제목 3")
                        .content("일반 문의 내용 3")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반4 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원4)
                        .title("일반 문의 제목 4")
                        .content("일반 문의 내용 4")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반5 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원5)
                        .title("일반 문의 제목 5")
                        .content("일반 문의 내용 5")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        Question 문의_일반6 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원6)
                        .title("일반 문의 제목 6")
                        .content("일반 문의 내용 6")
                        .status(QuestionStatus.INCOMPLETE)
                        .build());

        Question 문의_일반7 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원7)
                        .title("일반 문의 제목 7")
                        .content("일반 문의 내용 7")
                        .status(QuestionStatus.INCOMPLETE)
                        .build());

        Question 문의_일반8 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원8)
                        .title("일반 문의 제목 8")
                        .content("일반 문의 내용 8")
                        .status(QuestionStatus.INCOMPLETE)
                        .build());

        Question 문의_일반9 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원9)
                        .title("일반 문의 제목 9")
                        .content("일반 문의 내용 9")
                        .status(QuestionStatus.INCOMPLETE)
                        .build());

        Question 문의_일반10 = questionRepository.save(
                Question.builder()
                        .member(회원_일반회원10)
                        .title("일반 문의 제목 10")
                        .content("일반 문의 내용 10")
                        .status(QuestionStatus.INCOMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        questionRepository.save(
                Question.builder()
                        .member(회원_일반회원1)
                        .title("일반 문의 제목 1")
                        .content("일반 문의 내용 1")
                        .status(QuestionStatus.COMPLETE)
                        .build());

        // Insert Answer
        Answer 문의_답변1 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 1")
                        .build());

        문의_답변1.addQuestion(문의_일반1);
        answerRepository.save(문의_답변1);

        Answer 문의_답변2 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저2)
                        .content("일반 문의 답변 2")
                        .build());

        문의_답변2.addQuestion(문의_일반1);
        answerRepository.save(문의_답변2);

        Answer 문의_답변3 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저1)
                        .content("일반 문의 답변 3")
                        .build());

        문의_답변3.addQuestion(문의_일반3);
        answerRepository.save(문의_답변3);

        Answer 문의_답변4 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저4)
                        .content("일반 문의 답변 4")
                        .build());
        문의_답변4.addQuestion(문의_일반4);
        answerRepository.save(문의_답변4);

        Answer 문의_답변5 = answerRepository.save(
                Answer.builder()
                        .member(회원_슈퍼바이저5)
                        .question(문의_일반5)
                        .content("일반 문의 답변 5")
                        .build());
        문의_답변5.addQuestion(문의_일반5);
        answerRepository.save(문의_답변5);

        // Insert Address
        Address 주소_일반회원1_1 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원1)
                        .isPrimary(true)
                        .address("경기도 포천시 일동면 운악청계로 1597")
                        .addressDetail("101호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원1_2 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원1)
                        .isPrimary(false)
                        .address("경기도 포천시 일동면 운악청계로 1598")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원2_1 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원2)
                        .isPrimary(true)
                        .address("강원도 홍천군 남면 어두원이길 143")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원2_2 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원2)
                        .isPrimary(false)
                        .address("강원도 홍천군 남면 어두원이길 143")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원3_1 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원3)
                        .isPrimary(true)
                        .address("전라남도 장성군 장성읍 남양촌길 (백계리) 19")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원3_2 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원3)
                        .isPrimary(false)
                        .address("전라남도 장성군 장성읍 남양촌길 (백계리) 19")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원4_1 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원4)
                        .isPrimary(true)
                        .address("강원도 양구군 방산면 칠전길 12-7")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원4_2 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원4)
                        .isPrimary(false)
                        .address("강원도 양구군 방산면 칠전길 12-7")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원5_1 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원5)
                        .isPrimary(true)
                        .address("경상북도 의성군 단촌면 일직점곡로 755")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        Address 주소_일반회원5_2 = addressRepository.save(
                Address.builder()
                        .member(회원_일반회원5)
                        .isPrimary(false)
                        .address("경상북도 의성군 단촌면 일직점곡로 755")
                        .addressDetail("102호")
                        .postcode(1234L)
                        .build());

        // Insert Notice
        Notice 공지사항1 = noticeRepository.save(
                Notice.builder()
                        .member(회원_김태섭)
                        .title("2024년 근로‧자녀장려금 체험수기 공모전 입상자")
                        .content("지난해 11월 1일부터 12월 31일까지 실시한 「근로·자녀장려금 수급자 체험수기 공모전」수상작을 발표합니다.(붙임 파일 참조)\n"
                                + "\n"
                                + "입상작에 선정되신 걸 축하드리며, 공모에 참여해주신 모든 분들께 진심으로 감사드립니다.\n"
                                + "\n")
                        .build());

        Notice 공지사항2 = noticeRepository.save(
                Notice.builder()
                        .member(회원_김태섭)
                        .title("2023년 7급 공무원 채용시험 합격자 임용관계서류 제출안내")
                        .content(
                                "2023년 7급 채용시험에 최종 합격하신 것을 진심으로 축하드리며, 채용후보자가 제출해야 할 임용관계서류 및 향후 일정을 아래와 같이 안내하여 드립니다.\n"
                                        + "\n"
                                        + "- 임용관계서류는 등기우편으로만 접수하며, 서류제출 등 관련 문의는 접수처로 문의하시기 바랍니다.\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "1. 제출대상\n"
                                        + "\n"
                                        + "2023년 7급 국가공무원 공개경쟁채용시험 최종 합격자로서 인사혁신처에 채용후보자 등록을 마치고 국세청으로 배치된 자\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "2. 제출기한 : 2024.2.20.(화)까지\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "3. 제출방법 : 접수처에 등기우편으로 제출\n"
                                        + "\n"
                                        + "- (7급 세무직) 주민등록상 주소지 관할 접수처에 등기우편으로 제출\n"
                                        + "\n"
                                        + "- (7급 전산직) 대전지방국세청 운영지원과 인사팀에 등기우편으로 제출\n"
                                        + "\n"
                                        + "- 제출하는 모든 서류는 “스테플러 사용 금지”(스캔 작업 예정)\n"
                                        + "\n"
                                        + "- 학업의 계속을 사유로 임용유예를 신청하는 경우는 임용유예 신청 당시의 학업을 중단없이 계속하여 진행하는 것을 의미하며 임용후보자는 학업에 필요한 기간을 유예기간으로 작성하여야 함(재학증명서, 현재 취득 학점 및 학업을 마칠 때 까지 필요한 학점을 확인할 수 있는 학칙 등 증빙을 함께 제출)\n"
                                        + "\n"
                                        + "- 채용후보자의 학업 등으로 인한 임용유예 기간은 원칙적으로 ’25.2.28.까지만 허용할 예정 (질병 및 출산 등 불가피한 사유시 예외)\n"
                                        + "\n"
                                        + "- 학업 등에 필요한 기간이 1년 미만인 경우 1년 미만으로 작성하고(예, 2024.3.1.∼2024.6.30.), 1년 이상인 경우에는 1년으로 작성(2024.3.1.∼2025.2.28.) 후 추후 질병 등 불가피한 사유 시 연장 신청(임용관계서류 접수 시 임용유예를 신청하는 경우 유예시작일은 2024.3.1.로 통일)\n"
                                        + "\n"
                                        + "- 임용유예 사유가 종료되거나 임용유예 신청기간 만료되기 1개월 전에 임용유예철회원 및 관련 증명서를 임용관계서류 접수한 지방청에 제출\n"
                                        + "\n"
                                        + "* 신원진술서의 개인정보제공동의는 후보자 및 가족 전원의 서명 필요\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "4. 제출서류, 접수처 문의\n"
                                        + "\n"
                                        + "○채용후보자 임용관계서류 제출 및 임용포기(유예) 신청에 관한 사항은 ‘붙임1,2’를 확인하고 위 기간 내에 반드시 제출하시기 바랍니다.\n"
                                        + "\n"
                                        + "- 서울지방국세청 : (02)2114-2253, 2254\n"
                                        + "\n"
                                        + "- 중부지방국세청 : (031)888-4247,4286\n"
                                        + "\n"
                                        + "- 인천지방국세청 : (032)718-6246, 6249\n"
                                        + "\n"
                                        + "- 대전지방국세청 : (042)615-2246, 2247\n"
                                        + "\n"
                                        + "- 광주지방국세청 : (062)236-7243, 7248\n"
                                        + "\n"
                                        + "- 대구지방국세청 : (053)661-7244, 7248\n"
                                        + "\n"
                                        + "- 부산지방국세청 : (051)750-7247, 7249\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "5. 참고사항\n"
                                        + "\n"
                                        + "○임용결격 사유가 없으면 신규교육, 실무수습 등을 거쳐 일선 세무서에 배치․임용할 예정입니다.(교육원 사정에 따라 교육일정 변경될 수 있음)\n"
                                        + "\n"
                                        + "- (세무직)’24.7.15. ∼ ’24.10.4. (12주)\n"
                                        + "\n"
                                        + "- (전산직)’24.4.8. ∼ ’24.5.3. (4주)\n"
                                        + "\n"
                                        + "○신규교육 전 사전학습 및 세부 교육일정은 개별 문자 및 국세공무원교육원 홈페이지(taxstudy.nts.go.kr)를 통해 안내(공지)할 예정입니다.\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "6. 붙임자료\n"
                                        + "\n"
                                        + "○붙임1) 채용후보자 임용관계서류 제출 안내\n"
                                        + "\n"
                                        + "○붙임2) 임용관계서식 및 작성요령\n"
                                        + "\n"
                                        + "○붙임3) 주요 질의응답(Q&A)\n"
                                        + "\n")
                        .build());

        Notice 공지사항3 = noticeRepository.save(
                Notice.builder()
                        .member(회원_김태섭)
                        .title("제58회 납세자의 날 모범납세자 포상후보자 공개검증")
                        .content(
                                "2024.3.3. '제58회 납세자의 날'을 맞이하여 납세의무를 성실하게 이행하고 나눔의 문화 확산에 기여한 모범납세자(국세청장 표창 이상) 포상후보자를 사전공개 합니다.\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "첨부파일을 참고하시어 포상후보자의 포상에 대한 의견을 아래 e-mail이나 Fax를 통해 1.25.(목)까지 제출하여 주시기 바랍니다.\n"
                                        + "\n"
                                        + "※ 의견 제출 시 제출자의 성명·생년월일·연락처 반드시 기재\n"
                                        + "\n"
                                        + "보내주신 의견은 진위 여부를 확인한 후 공적심의자료 등으로 활용할 예정이며, 별도의 회신을 하지 않습니다.\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "메일주소:park0817@nts.go.kr, Fax번호:050-3114-5155\n"
                                        + "\n"
                                        + "첨부 : '제58회 납세자의 날' 포상후보자 공개검증 명단")
                        .build());

        Notice 공지사항4 = noticeRepository.save(
                Notice.builder()
                        .member(회원_김태섭)
                        .title("제58회 납세자의날 포상후보자 공개검증")
                        .content(
                                "2024.3.3. '제58회 납세자의 날'을 맞이하여 납세의무를 성실하게 이행하고 나눔의 문화 확산에 기여한 모범납세자(국세청장 표창 이상) 포상후보자를 사전공개 합니다.\n"
                                        + "\n"
                                        + "첨부파일을 참고하시어 포상후보자의 포상에 대한 의견을 아래 e-mail이나 Fax를 통해 2.9.(금)까지 제출하여 주시기 바랍니다.\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "※ 의견 제출 시 제출자의 성명·생년월일·연락처 반드시 기재\n"
                                        + "\n"
                                        + "보내주신 의견은 진위 여부를 확인한 후 공적심의자료 등으로 활용할 예정이며, 별도의 회신을 하지 않습니다.\n"
                                        + "\n"
                                        + "메일주소:park0817@nts.go.kr, Fax번호:050-3114-5155\n"
                                        + "\n"
                                        + "첨부 : '제58회 납세자의 날' 포상후보자 공개검증 명단\n"
                                        + "* 포상수량 조정으로 포상후보자 공개검증 재실시")
                        .build());

        Notice 공지사항5 = noticeRepository.save(
                Notice.builder()
                        .member(회원_유민석)
                        .title("2023년 7급 공무원 채용시험 합격자 임용관계서류 제출안내")
                        .content(
                                "2023년 7급 채용시험에 최종 합격하신 것을 진심으로 축하드리며, 채용후보자가 제출해야 할 임용관계서류 및 향후 일정을 아래와 같이 안내하여 드립니다.\n"
                                        + "\n"
                                        + "- 임용관계서류는 등기우편으로만 접수하며, 서류제출 등 관련 문의는 접수처로 문의하시기 바랍니다.\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "1. 제출대상\n"
                                        + "\n"
                                        + "2023년 7급 국가공무원 공개경쟁채용시험 최종 합격자로서 인사혁신처에 채용후보자 등록을 마치고 국세청으로 배치된 자\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "2. 제출기한 : 2024.2.20.(화)까지\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "3. 제출방법 : 접수처에 등기우편으로 제출\n"
                                        + "\n"
                                        + "- (7급 세무직) 주민등록상 주소지 관할 접수처에 등기우편으로 제출\n"
                                        + "\n"
                                        + "- (7급 전산직) 대전지방국세청 운영지원과 인사팀에 등기우편으로 제출\n"
                                        + "\n"
                                        + "- 제출하는 모든 서류는 “스테플러 사용 금지”(스캔 작업 예정)\n"
                                        + "\n"
                                        + "- 학업의 계속을 사유로 임용유예를 신청하는 경우는 임용유예 신청 당시의 학업을 중단없이 계속하여 진행하는 것을 의미하며 임용후보자는 학업에 필요한 기간을 유예기간으로 작성하여야 함(재학증명서, 현재 취득 학점 및 학업을 마칠 때 까지 필요한 학점을 확인할 수 있는 학칙 등 증빙을 함께 제출)\n"
                                        + "\n"
                                        + "- 채용후보자의 학업 등으로 인한 임용유예 기간은 원칙적으로 ’25.2.28.까지만 허용할 예정 (질병 및 출산 등 불가피한 사유시 예외)\n"
                                        + "\n"
                                        + "- 학업 등에 필요한 기간이 1년 미만인 경우 1년 미만으로 작성하고(예, 2024.3.1.∼2024.6.30.), 1년 이상인 경우에는 1년으로 작성(2024.3.1.∼2025.2.28.) 후 추후 질병 등 불가피한 사유 시 연장 신청(임용관계서류 접수 시 임용유예를 신청하는 경우 유예시작일은 2024.3.1.로 통일)\n"
                                        + "\n"
                                        + "- 임용유예 사유가 종료되거나 임용유예 신청기간 만료되기 1개월 전에 임용유예철회원 및 관련 증명서를 임용관계서류 접수한 지방청에 제출\n"
                                        + "\n"
                                        + "* 신원진술서의 개인정보제공동의는 후보자 및 가족 전원의 서명 필요\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "4. 제출서류, 접수처 문의\n"
                                        + "\n"
                                        + "○채용후보자 임용관계서류 제출 및 임용포기(유예) 신청에 관한 사항은 ‘붙임1,2’를 확인하고 위 기간 내에 반드시 제출하시기 바랍니다.\n"
                                        + "\n"
                                        + "- 서울지방국세청 : (02)2114-2253, 2254\n"
                                        + "\n"
                                        + "- 중부지방국세청 : (031)888-4247,4286\n"
                                        + "\n"
                                        + "- 인천지방국세청 : (032)718-6246, 6249\n"
                                        + "\n"
                                        + "- 대전지방국세청 : (042)615-2246, 2247\n"
                                        + "\n"
                                        + "- 광주지방국세청 : (062)236-7243, 7248\n"
                                        + "\n"
                                        + "- 대구지방국세청 : (053)661-7244, 7248\n"
                                        + "\n"
                                        + "- 부산지방국세청 : (051)750-7247, 7249\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "5. 참고사항\n"
                                        + "\n"
                                        + "○임용결격 사유가 없으면 신규교육, 실무수습 등을 거쳐 일선 세무서에 배치․임용할 예정입니다.(교육원 사정에 따라 교육일정 변경될 수 있음)\n"
                                        + "\n"
                                        + "- (세무직)’24.7.15. ∼ ’24.10.4. (12주)\n"
                                        + "\n"
                                        + "- (전산직)’24.4.8. ∼ ’24.5.3. (4주)\n"
                                        + "\n"
                                        + "○신규교육 전 사전학습 및 세부 교육일정은 개별 문자 및 국세공무원교육원 홈페이지(taxstudy.nts.go.kr)를 통해 안내(공지)할 예정입니다.\n"
                                        + "\n"
                                        + "\n"
                                        + "\n"
                                        + "6. 붙임자료\n"
                                        + "\n"
                                        + "○붙임1) 채용후보자 임용관계서류 제출 안내\n"
                                        + "\n"
                                        + "○붙임2) 임용관계서식 및 작성요령\n"
                                        + "\n"
                                        + "○붙임3) 주요 질의응답(Q&A)")
                        .build());

        // Insert Item
        Item 상품_1 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("프리바이오 막걸리 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("국순당 프리바이오 막걸리 10개입")
                        .category(카테고리_소분류1)
                        .build());

        Item 상품_2 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("유산균막걸리 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("국순당 유산균막걸리 10개입")
                        .category(카테고리_소분류2)
                        .build());

        Item 상품_3 = itemRepository.save(
                Item.builder()
                        .type(ItemType.PROMOTION)
                        .name("52C 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("청산녹수 52C 10개입")
                        .category(카테고리_소분류3)
                        .build());

        Item 상품_4 = itemRepository.save(
                Item.builder()
                        .type(ItemType.PROMOTION)
                        .name("골디락스 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("청산녹수 골디락스 10개입")
                        .category(카테고리_소분류4)
                        .build());

        Item 상품_5 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("The 찾을수록 피치 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("한국애플리즈 The 찾을수록 피치 10개입")
                        .category(카테고리_소분류5)
                        .build());

        Item 상품_6 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("The 찾을수록 모스카토 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("한국애플리즈 The 찾을수록 모스카토 10개입")
                        .category(카테고리_소분류6)
                        .build());

        Item 상품_7 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("가덕 순쌀 막걸리 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("명세주가 가덕 순쌀 막걸리 10개입")
                        .category(카테고리_소분류7)
                        .build());

        Item 상품_8 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("매실향기 담 술 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("명세주가 매실향기 담 술 10개입")
                        .category(카테고리_소분류8)
                        .build());

        Item 상품_9 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("감그린레귤러 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("청도감와인 감그린레귤러 10개입")
                        .category(카테고리_소분류9)
                        .build());

        Item 상품_10 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("감그린스페셜 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("청도감와인 감그린스페셜 10개입")
                        .category(카테고리_소분류10)
                        .build());

        // Insert ItemProduct
        ItemProduct 상품상세1 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_1)
                        .product(제품_국순당_프리바이오)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세2 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_2)
                        .product(제품_국순당_유산균막걸리)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세3 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_3)
                        .product(제품_청산녹수_52C)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세4 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_4)
                        .product(제품_청산녹수_골디락스)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세5 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_5)
                        .product(제품_한국애플리즈_피치)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세6 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_6)
                        .product(제품_한국애플리즈_모스카토)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세7 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_7)
                        .product(제품_명세주가_가덕막걸리)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세8 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_8)
                        .product(제품_명세주가_매실향기)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세9 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_9)
                        .product(제품_청도감와인_감그린레귤러)
                        .quantity(100L)
                        .build());

        ItemProduct 상품상세10 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_10)
                        .product(제품_청도감와인_감그린스페셜)
                        .quantity(100L)
                        .build());

        for (Member member : memberRepository.findAll()) {
            cartRepository.save(
                    Cart.builder()
                            .member(member)
                            .build());
        }

        Cart 장바구니_회원1 = cartRepository.findByMember(memberRepository.findByEmail("member1@example.com").get()).get();
        Cart 장바구니_회원2 = cartRepository.findByMember(memberRepository.findByEmail("member2@example.com").get()).get();
        Cart 장바구니_회원3 = cartRepository.findByMember(memberRepository.findByEmail("member3@example.com").get()).get();
        Cart 장바구니_회원4 = cartRepository.findByMember(memberRepository.findByEmail("member4@example.com").get()).get();
        Cart 장바구니_회원5 = cartRepository.findByMember(memberRepository.findByEmail("member5@example.com").get()).get();
        Cart 장바구니_회원6 = cartRepository.findByMember(memberRepository.findByEmail("member6@example.com").get()).get();
        Cart 장바구니_회원7 = cartRepository.findByMember(memberRepository.findByEmail("member7@example.com").get()).get();
        Cart 장바구니_회원8 = cartRepository.findByMember(memberRepository.findByEmail("member8@example.com").get()).get();
        Cart 장바구니_회원9 = cartRepository.findByMember(memberRepository.findByEmail("member9@example.com").get()).get();
        Cart 장바구니_회원10 = cartRepository.findByMember(memberRepository.findByEmail("member10@example.com").get()).get();

        CartDetail 장바구니_상품1 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원1)
                        .item(상품_1)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품2 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원2)
                        .item(상품_2)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품3 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원3)
                        .item(상품_3)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품4 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원4)
                        .item(상품_4)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품5 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원5)
                        .item(상품_5)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품6 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원6)
                        .item(상품_6)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품7 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원7)
                        .item(상품_7)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품8 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원8)
                        .item(상품_8)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품9 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원9)
                        .item(상품_9)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품10 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원10)
                        .item(상품_10)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품1_2 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원1)
                        .item(상품_2)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품1_3 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원1)
                        .item(상품_3)
                        .quantity(100L)
                        .build());

        CartDetail 장바구니_상품1_4 = cartDetailRepository.save(
                CartDetail.builder()
                        .cart(장바구니_회원1)
                        .item(상품_4)
                        .quantity(100L)
                        .build());

        RestaurantStock 가게_상품1_1 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_국순당_유산균막걸리)
                        .quantity(100L)
                        .restaurant(가게1)
                        .build());

        RestaurantStock 가게_상품1_2 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_명세주가_가덕막걸리)
                        .quantity(100L)
                        .restaurant(가게1)
                        .build());

        RestaurantStock 가게_상품2_1 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_국순당_유산균막걸리)
                        .quantity(100L)
                        .restaurant(가게2)
                        .build());

        RestaurantStock 가게_상품2_2 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_명세주가_가덕막걸리)
                        .quantity(100L)
                        .restaurant(가게2)
                        .build());

        RestaurantStock 가게_상품3_1 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_국순당_유산균막걸리)
                        .quantity(100L)
                        .restaurant(가게3)
                        .build());

        RestaurantStock 가게_상품3_2 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_명세주가_가덕막걸리)
                        .quantity(100L)
                        .restaurant(가게3)
                        .build());

        RestaurantStock 가게_상품4_1 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_국순당_유산균막걸리)
                        .quantity(100L)
                        .restaurant(가게4)
                        .build());

        RestaurantStock 가게_상품4_2 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_명세주가_가덕막걸리)
                        .quantity(100L)
                        .restaurant(가게4)
                        .build());

        RestaurantStock 가게_상품5_1 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_국순당_유산균막걸리)
                        .quantity(100L)
                        .restaurant(가게5)
                        .build());

        RestaurantStock 가게_상품5_2 = restaurantStockRepository.save(
                RestaurantStock.builder()
                        .product(제품_명세주가_가덕막걸리)
                        .quantity(100L)
                        .restaurant(가게5)
                        .build());

        // Order 테스트 데이터
        com.drunkenlion.alcoholfriday.domain.order.entity.Order 주문_1 =
                orderRepository.save(
                        com.drunkenlion.alcoholfriday.domain.order.entity.Order.builder()
                                .orderNo("주문_1")
                                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                                .price(BigDecimal.valueOf(20000))
                                .deliveryPrice(BigDecimal.valueOf(3000))
                                .totalPrice(BigDecimal.valueOf(23000))
                                .recipient("테스트회원5")
                                .phone(1012345678L)
                                .address("서울시 마포구 연남동")
                                .addressDetail("123-12번지")
                                .description("부재 시 문앞에 놓아주세요.")
                                .postcode(123123L)
                                .member(회원_일반회원5)
                                .build()
                );

        com.drunkenlion.alcoholfriday.domain.order.entity.Order 주문_2 =
                orderRepository.save(
                        com.drunkenlion.alcoholfriday.domain.order.entity.Order.builder()
                                .orderNo("주문_2")
                                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                                .price(BigDecimal.valueOf(20000))
                                .deliveryPrice(BigDecimal.valueOf(3000))
                                .totalPrice(BigDecimal.valueOf(23000))
                                .recipient("테스트회원5")
                                .phone(1012345678L)
                                .address("서울시 마포구 연남동")
                                .addressDetail("123-12번지")
                                .description("부재 시 문앞에 놓아주세요.")
                                .postcode(123123L)
                                .member(회원_일반회원5)
                                .build()
                );

        // OrderDetail 테스트 데이터
        OrderDetail 주문상품_1 = orderDetailRepository.save(
                OrderDetail.builder()
                        .itemPrice(상품_1.getPrice())
                        .quantity(1L)
                        .totalPrice(상품_1.getPrice())
                        .item(상품_1)
                        .order(주문_1)
                        .review(null)
                        .build()
        );

        OrderDetail 주문상품_2 = orderDetailRepository.save(
                OrderDetail.builder()
                        .itemPrice(상품_2.getPrice())
                        .quantity(1L)
                        .totalPrice(상품_2.getPrice())
                        .item(상품_2)
                        .order(주문_1)
                        .review(null)
                        .build()
        );

        OrderDetail 주문상품_3 = orderDetailRepository.save(
                OrderDetail.builder()
                        .itemPrice(상품_3.getPrice())
                        .quantity(1L)
                        .totalPrice(상품_3.getPrice())
                        .item(상품_3)
                        .order(주문_1)
                        .review(null)
                        .build()
        );

        // Review 테스트 데이터
        Review 리뷰_1 = reviewRepository.save(
                Review.builder()
                        .score(5L)
                        .content("맛있어요")
                        .item(상품_1)
                        .orderDetail(주문상품_1)
                        .member(회원_일반회원5)
                        .build()
        );

    }

}
