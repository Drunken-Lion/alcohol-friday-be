package com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.api;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.dao.AnswerRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SpringBootTest
class AdminQuestionControllerTest {
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
    @DisplayName("문의 사항 전체 조회")
    @WithAccount(role = MemberRole.ADMIN)
    void t1() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        Member member2 = memberRepository.save(Member.builder()
                .email("4415kim@test.com")
                .provider(ProviderType.KAKAO)
                .name("4415kim")
                .nickname("4415kim")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build());


        Question question = questionRepository.save(
                Question.builder()
                        .title("test title")
                        .content("test content")
                        .member(member2)
                        .status(QuestionStatus.INCOMPLETE)
                        .build()
        );

        Answer answer = answerRepository.save(
                Answer.builder()
                        .question(question)
                        .member(member)
                        .content("test answer content")
                        .build()
        );
        answer.addQuestion(question);
        answerRepository.save(answer);
        questionRepository.save(question);

        ResultActions actions = mvc
                .perform(get("/v1/admin/questions"))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminQuestionController.class))
                .andExpect(handler().methodName("findQuestions"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data.[0].title", notNullValue()))
                .andExpect(jsonPath("$.data.[0].content", notNullValue()))
                .andExpect(jsonPath("$.data.[0].status", notNullValue()))
                .andExpect(jsonPath("$.data.[0].member.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data.[0].member.name", notNullValue()))
                .andExpect(jsonPath("$.data.[0].member.nickname", notNullValue()))
                .andExpect(jsonPath("$.data.[0].member.email", notNullValue()))
                .andExpect(jsonPath("$.data.[0].answers.[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data.[0].answers.[0].content", notNullValue()))
        ;
    }

    @Test
    @DisplayName("문의 사항 상세 조회")
    @WithAccount(role = MemberRole.ADMIN)
    void t2() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        Member member2 = memberRepository.save(Member.builder()
                .email("4415kim@test.com")
                .provider(ProviderType.KAKAO)
                .name("4415kim")
                .nickname("4415kim")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build());


        Question question = questionRepository.save(
                Question.builder()
                        .title("test title")
                        .content("test content")
                        .member(member2)
                        .status(QuestionStatus.INCOMPLETE)
                        .build()
        );

        Answer answer = answerRepository.save(
                Answer.builder()
                        .question(question)
                        .member(member)
                        .content("test answer content")
                        .build()
        );
        answer.addQuestion(question);
        answerRepository.save(answer);
        questionRepository.save(question);

        ResultActions actions = mvc
                .perform(get("/v1/admin/questions/" + question.getId()))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminQuestionController.class))
                .andExpect(handler().methodName("findQuestion"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.member.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.member.name", notNullValue()))
                .andExpect(jsonPath("$.member.nickname", notNullValue()))
                .andExpect(jsonPath("$.member.email", notNullValue()))
                .andExpect(jsonPath("$.answers.[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.answers.[0].content", notNullValue()))
        ;
    }

    @Test
    @DisplayName("문의 사항 수정")
    @WithAccount(role = MemberRole.ADMIN)
    void t5() throws Exception {
        Member member2 = memberRepository.save(Member.builder()
                .email("4415kim@test.com")
                .provider(ProviderType.KAKAO)
                .name("4415kim")
                .nickname("4415kim")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build());


        Question question = questionRepository.save(
                Question.builder()
                        .title("test title")
                        .content("test content")
                        .member(member2)
                        .status(QuestionStatus.INCOMPLETE)
                        .build()
        );

        QuestionModifyRequest build = QuestionModifyRequest.builder()
                .updateTitle("zzzz")
                .updateContent("zzzz")
                .removeImageSeqList(List.of())
                .build();

        MockMultipartFile mockMultipartFile = JsonConvertor.mockBuild(build, "request");
        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg();

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
                "/v1/admin/questions/" + question.getId());
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        ResultActions actions = mvc
                .perform(builder
                        .file(mockMultipartFile)
                        .file(multipartFile1)
                )
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminQuestionController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.member.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.member.name", notNullValue()))
                .andExpect(jsonPath("$.member.nickname", notNullValue()))
                .andExpect(jsonPath("$.member.email", notNullValue()))
                .andExpect(jsonPath("$.file", notNullValue()))
        ;
    }

    @Test
    @DisplayName("문의 사항 삭제")
    @WithAccount(role = MemberRole.ADMIN)
    void t6() throws Exception {
        Member member2 = memberRepository.save(Member.builder()
                .email("4415kim@test.com")
                .provider(ProviderType.KAKAO)
                .name("4415kim")
                .nickname("4415kim")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build());


        Question question = questionRepository.save(
                Question.builder()
                        .title("test title")
                        .content("test content")
                        .member(member2)
                        .status(QuestionStatus.INCOMPLETE)
                        .build()
        );


        ResultActions actions = mvc
                .perform(delete("/v1/admin/questions/" + question.getId())
                )
                .andDo(print());

        actions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminQuestionController.class))
                .andExpect(handler().methodName("delete"))
        ;
    }
}