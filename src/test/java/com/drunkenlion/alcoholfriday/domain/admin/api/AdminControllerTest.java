package com.drunkenlion.alcoholfriday.domain.admin.api;

import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

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

    // 날짜 패턴 정규식
    private static final String DATETIME_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.?\\d{0,7}";
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

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
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    void getMembersTest() throws Exception {
        // given

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
}
