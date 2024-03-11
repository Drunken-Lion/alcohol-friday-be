package com.drunkenlion.alcoholfriday.domain.admin.maker.api;

import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerRequest;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class AdminMakerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MakerRepository makerRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Maker maker = Maker.builder()
                .name("test 제조사")
                .address("서울 강동구 아리수로 46")
                .detail("1052호")
                .region("서울특별시")
                .build();

        makerRepository.save(maker);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        makerRepository.deleteAll();
    }

    @Test
    @DisplayName("제조사 목록 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getMakersTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/makers")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminMakerController.class))
                .andExpect(handler().methodName("getMakers"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].region", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("제조사 상세 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getMakerTest() throws Exception {
        // given
        Maker maker = this.makerRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/makers/" + maker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminMakerController.class))
                .andExpect(handler().methodName("getMaker"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.detail", notNullValue()))
                .andExpect(jsonPath("$.region", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("제조사 등록 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void createMakerTest() throws Exception {
        // given
        MakerRequest makerRequest = MakerRequest.builder()
                .name("test 제조사")
                .address("test 주소")
                .detail("test 상세주소")
                .region("test 제조지역")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/makers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(makerRequest))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminMakerController.class))
                .andExpect(handler().methodName("createMaker"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.detail", notNullValue()))
                .andExpect(jsonPath("$.region", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("제조사 수정 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void modifyMakerTest() throws Exception {
        // given
        Maker maker = this.makerRepository.findAll().get(0);

        MakerRequest makerRequest = MakerRequest.builder()
                .name("test 제조사 수정")
                .address("test 주소 수정")
                .detail("test 상세주소 수정")
                .region("test 제조지역 수정")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/makers/" + maker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(makerRequest))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminMakerController.class))
                .andExpect(handler().methodName("modifyMaker"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.detail", notNullValue()))
                .andExpect(jsonPath("$.region", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("제조사 삭제 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void deleteMakerTest() throws Exception {
        // given
        Maker maker = this.makerRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/makers/" + maker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminMakerController.class))
                .andExpect(handler().methodName("deleteMaker"));
    }
}
