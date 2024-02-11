package com.drunkenlion.alcoholfriday.domain.admin.store.api;

import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
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

import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminStoreControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MakerRepository makerRepository;

    // 날짜 패턴 정규식
    private static final String DATETIME_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.?\\d{0,7}";
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

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
    void getMakersTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/store/makers")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminStoreController.class))
                .andExpect(handler().methodName("getMakers"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].region", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    void getMakerTest() throws Exception {
        // given
        Maker maker = this.makerRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/store/makers/" + maker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminStoreController.class))
                .andExpect(handler().methodName("getMaker"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.detail", notNullValue()))
                .andExpect(jsonPath("$.region", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(DATETIME_PATTERN)), is(nullValue()))));
    }
}
