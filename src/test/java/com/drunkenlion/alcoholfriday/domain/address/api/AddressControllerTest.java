package com.drunkenlion.alcoholfriday.domain.address.api;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class AddressControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AddressRepository addressRepository;

    public static final String EMAIL = "test@example.com";

    @BeforeEach
    @Transactional
    void beforeEach() {
        Member member = memberRepository.findByEmail(EMAIL)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(EMAIL)
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
                        .build()));

        Address address = Address.builder()
                .member(member)
                .isPrimary(true)
                .address("서울특별시 마포구 연남동")
                .addressDetail("123-12번지")
                .postcode("123123")
                .recipient("테스트유저55")
                .phone(1012345678L)
                .request("부재시 연락주세요.")
                .build();
        addressRepository.save(address);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Test
    @DisplayName("배송지 등록")
    @WithAccount
    void createAddressTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "recipient" : "테스트유저11",
                                    "phone" : 1012345678,
                                    "request" : "부재시 문 앞",
                                    "address" : "서울특별시 마포구 상암동",
                                    "detail" : "123-12번지",
                                    "postcode" : "123123",
                                    "isPrimary" : true
                                }
                                """)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("createAddress"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.isPrimary", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.addressDetail", notNullValue()))
                .andExpect(jsonPath("$.postcode", instanceOf(String.class)))
                .andExpect(jsonPath("$.recipient", notNullValue()))
                .andExpect(jsonPath("$.phone", instanceOf(Number.class)))
                .andExpect(jsonPath("$.request", notNullValue()));
    }

    @Test
    @DisplayName("배송지 단건 조회")
    @WithAccount
    void getAddressTest() throws Exception {
        // given
        Address address = this.addressRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/addresses/" + address.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("getAddress"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.isPrimary", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.addressDetail", notNullValue()))
                .andExpect(jsonPath("$.postcode", instanceOf(String.class)))
                .andExpect(jsonPath("$.recipient", notNullValue()))
                .andExpect(jsonPath("$.phone", instanceOf(Number.class)))
                .andExpect(jsonPath("$.request", notNullValue()));
    }

    @Test
    @DisplayName("배송지 수정")
    @WithAccount
    void modifyAddressTest() throws Exception {
        // given
        Address address = this.addressRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/addresses/" + address.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "recipient" : "테스트유저11",
                                    "address" : "서울특별시 마포구 합정동",
                                    "addressDetail" : "123-12번지",
                                    "postcode" : 123123,
                                    "phone" : 1012345678,
                                    "request" : "부재시 문 앞",
                                    "isPrimary" : true
                                }
                                """))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("modifyAddress"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.isPrimary", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.addressDetail", notNullValue()))
                .andExpect(jsonPath("$.postcode", instanceOf(String.class)))
                .andExpect(jsonPath("$.recipient", notNullValue()))
                .andExpect(jsonPath("$.phone", instanceOf(Number.class)))
                .andExpect(jsonPath("$.request", notNullValue()));
    }

    @Test
    @DisplayName("배송지 삭제")
    @WithAccount
    void deleteAddressTest() throws Exception {
        // given
        Address address = this.addressRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/addresses/" + address.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("deleteAddress"));
    }
}
