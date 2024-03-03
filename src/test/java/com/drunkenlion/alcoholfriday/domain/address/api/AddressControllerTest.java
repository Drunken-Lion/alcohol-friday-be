package com.drunkenlion.alcoholfriday.domain.address.api;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
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
        Member member = memberRepository.findByEmail(EMAIL).get();

        Address address = Address.builder()
                .member(member)
                .isPrimary(true)
                .address("서울특별시 마포구 연남동")
                .detail("123-12번지")
                .postcode(123123L)
                .recipient("테스트유저55")
                .phone(1012345678L)
                .request("부재시 연락주세요.")
                .build();
        addressRepository.save(address);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        addressRepository.deleteAll();
    }

    @Test
    @DisplayName("주소 등록")
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
                                    "postcode" : 123123,
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
                .andExpect(jsonPath("$.postcode", instanceOf(Number.class)))
                .andExpect(jsonPath("$.recipient", notNullValue()))
                .andExpect(jsonPath("$.phone", instanceOf(Number.class)))
                .andExpect(jsonPath("$.request", notNullValue()));
    }
}