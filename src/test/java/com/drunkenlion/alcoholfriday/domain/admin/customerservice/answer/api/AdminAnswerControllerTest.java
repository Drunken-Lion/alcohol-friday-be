package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.api;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.dao.AnswerRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.AfterEach;
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

@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SpringBootTest
class AdminAnswerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @AfterEach
    @Transactional
    public void after() {
        questionRepository.deleteAll();
        memberRepository.deleteAll();
        answerRepository.deleteAll();
    }

    @Test
    @DisplayName("문의사항 답변 등록")
    @WithAccount(role = MemberRole.ADMIN)
    void t1() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Question question = questionRepository.save(
                Question.builder()
                        .title("test title")
                        .content("test content")
                        .member(member)
                        .status(QuestionStatus.INCOMPLETE)
                        .build()
        );

        AnswerSaveRequest request = AnswerSaveRequest.builder()
                .questionId(question.getId())
                .content("답변 테스트")
                .build();

        ResultActions actions = mvc
                .perform(post("/v1/admin/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(request)))
                .andDo(print());

        actions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminAnswerController.class))
                .andExpect(handler().methodName("saveAnswer"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)));
    }

    @Test
    @DisplayName("문의사항 답변 수정")
    @WithAccount(role = MemberRole.ADMIN)
    void t2() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Question question = questionRepository.save(
                Question.builder()
                        .title("test title")
                        .content("test content")
                        .member(member)
                        .status(QuestionStatus.INCOMPLETE)
                        .build()
        );

        Answer answer = answerRepository.save(Answer.builder()
                .content("답변 테스트")
                .member(member)
                .build());

        answer.addQuestion(question);
        questionRepository.save(question);
        answerRepository.save(answer);

        AnswerModifyRequest request = AnswerModifyRequest.builder().updateContent("수정 답변 테스트").build();

        ResultActions actions = mvc
                .perform(put("/v1/admin/answers/" + answer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(request)))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminAnswerController.class))
                .andExpect(handler().methodName("updateAnswer"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)));
    }

    @Test
    @DisplayName("문의사항 답변 삭제")
    @WithAccount(role = MemberRole.ADMIN)
    void t3() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();

        Question question = questionRepository.save(
                Question.builder()
                        .title("test title")
                        .content("test content")
                        .member(member)
                        .status(QuestionStatus.INCOMPLETE)
                        .build()
        );

        Answer answer = answerRepository.save(Answer.builder()
                .content("답변 테스트")
                .member(member)
                .build());

        answer.addQuestion(question);
        questionRepository.save(question);
        answerRepository.save(answer);

        AnswerModifyRequest request = AnswerModifyRequest.builder().updateContent("수정 답변 테스트").build();

        ResultActions actions = mvc
                .perform(delete("/v1/admin/answers/" + answer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        actions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminAnswerController.class))
                .andExpect(handler().methodName("deleteAnswer"));
    }
}