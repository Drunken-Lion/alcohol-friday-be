package com.drunkenlion.alcoholfriday.domain.admin.category.api;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryClassRequest;
import com.drunkenlion.alcoholfriday.domain.admin.category.dto.CategoryRequest;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import java.util.LinkedHashMap;
import java.util.List;
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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class AdminCategoryControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryClassRepository categoryClassRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        CategoryClass 카테고리_대분류1 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 1")
                        .build());

        Category 카테고리_소분류1 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류1")
                        .categoryClass(카테고리_대분류1)
                        .build());
    }

    @AfterEach
    @Transactional
    void afterEach() {
        categoryClassRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("카테고리 대분류 목록 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getCategoryClassesTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/category-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("getCategoryClasses"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("카테고리 대분류 상세 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getCategoryClassTest() throws Exception {
        // given
        CategoryClass categoryClass = this.categoryClassRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/category-classes/" + categoryClass.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("getCategoryClass"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("카테고리 대분류 등록 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void createCategoryClassTest() throws Exception {
        //given
        CategoryClassRequest categoryClassRequest = CategoryClassRequest.builder()
                .categoryFirstName("테스트 카테고리 대분류 1")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/category-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(categoryClassRequest))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("createCategoryClass"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("카테고리 대분류 수정 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void modifyCategoryClassTest() throws Exception {
        // given
        CategoryClass categoryClass = this.categoryClassRepository.findAll().get(0);

        CategoryClassRequest categoryClassRequest = CategoryClassRequest.builder()
                .categoryFirstName("테스트 카테고리 대분류 1 수정")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/category-classes/" + categoryClass.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(categoryClassRequest))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("modifyCategoryClass"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("카테고리 대분류 삭제 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void deleteCategoryClassTest() throws Exception {
        // given
        CategoryClass categoryClass = this.categoryClassRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/category-classes/" + categoryClass.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("deleteCategoryClass"));
    }


    @Test
    @DisplayName("카테고리 소분류 목록 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getCategoriesTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("getCategories"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("카테고리 소분류 상세 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getCategoryTest() throws Exception {
        // given
        Category category = this.categoryRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("getCategory"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("카테고리 소분류 등록 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void createCategoryTest() throws Exception {
        // then
        Long categoryFirstId = this.categoryClassRepository.findAll().get(0).getId();

        CategoryRequest categoryRequest = CategoryRequest.builder()
                .categoryFirstId(categoryFirstId)
                .categoryLastName("테스트 카테고리 소분류1")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(post("/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(categoryRequest))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("createCategory"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("카테고리 소분류 수정 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void modifyCategoryTest() throws Exception {
        // given
        Long categoryFirstId = this.categoryClassRepository.findAll().get(0).getId();
        Category category = this.categoryRepository.findAll().get(0);

        CategoryRequest categoryRequest = CategoryRequest.builder()
                .categoryFirstId(categoryFirstId)
                .categoryLastName("테스트 카테고리 소분류1 수정")
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(categoryRequest))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("modifyCategory"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))));
    }

    @Test
    @DisplayName("카테고리 소분류 삭제 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void deleteCategoryTest() throws Exception {
        // given
        Category category = this.categoryRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminCategoryController.class))
                .andExpect(handler().methodName("deleteCategory"));
    }
}
