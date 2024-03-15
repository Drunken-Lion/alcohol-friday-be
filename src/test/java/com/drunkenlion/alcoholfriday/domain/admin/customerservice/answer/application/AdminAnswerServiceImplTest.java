package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerResponse;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerSaveResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.dao.AnswerRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("[AdminAnswerServiceImplTest] 문의사항 답변 Service Test")
class AdminAnswerServiceImplTest {
    @InjectMocks
    private AdminAnswerServiceImpl adminAnswerService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("관리자는 문의 내역에 대해 답변을 작성할 수 있다.")
    void t1() {
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Member member = Member.builder().id(2L).build();

        Question question = Question.builder()
                .id(1L)
                .member(member)
                .build();

        String answerContent = "테스트 답변";

        AnswerSaveRequest request = AnswerSaveRequest.builder()
                .questionId(1L)
                .content(answerContent)
                .build();

        when(questionRepository.findById(1L)).thenReturn(
                Optional.ofNullable(question)
        );

        when(answerRepository.save(any(Answer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AdminAnswerSaveResponse response = adminAnswerService.saveAnswer(request, adminMember);

        assertThat(response.getContent()).isEqualTo(answerContent);
        assertThat(response.getMember().getId()).isEqualTo(adminMember.getId());
    }

    @Test
    @DisplayName("관리자는 자신이 작성한 답변에 대해 수정을 할 수 있다.")
    void t2() {
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();

        Answer answer = Answer.builder()
                .id(1L)
                .content("테스트 답변")
                .member(adminMember)
                .build();

        String updateContent = "수정된 답변";

        AnswerModifyRequest request = AnswerModifyRequest.builder()
                .updateContent(updateContent)
                .build();

        when(answerRepository.findById(1L)).thenReturn(
                Optional.of(answer)
        );

        AdminAnswerResponse response = adminAnswerService.updateAnswer(1L, request, adminMember);

        assertThat(response.getId()).isEqualTo(answer.getId());
        assertThat(response.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("관리자는 자신이 작성 하지 않은 답변에 대해 수정을 할 수 없다.")
    void t3() {
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Member member = Member.builder().id(2L).role(MemberRole.SUPER_VISOR).build();

        Answer answer = Answer.builder()
                .id(1L)
                .content("테스트 답변")
                .member(member)
                .build();

        String updateContent = "수정된 답변";

        AnswerModifyRequest request = AnswerModifyRequest.builder()
                .updateContent(updateContent)
                .build();

        when(answerRepository.findById(1L)).thenReturn(
                Optional.of(answer)
        );

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            adminAnswerService.updateAnswer(1L, request, adminMember);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.FORBIDDEN.getStatus());
    }

    @Test
    @DisplayName("ADMIN 권한을 가진 관리자는 모든 답변을 삭제할 수 있다.")
    void t4() {
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Member member = Member.builder().id(2L).role(MemberRole.SUPER_VISOR).build();
        Long answerId = 1L;
        Answer answer = Answer.builder()
                .id(answerId)
                .content("테스트 답변")
                .member(member)
                .build();

        when(answerRepository.findById(answerId)).thenReturn(
                Optional.ofNullable(answer)
        );

        adminAnswerService.deleteAnswer(answerId, adminMember);
        verify(answerRepository, times(1)).save(any(Answer.class));
    }

    @Test
    @DisplayName("SUPER VISOR 권한을 가진 관리자는 자신이 작성하지 않은 답변을 삭제할 수 없다.")
    void t5() {
        Member member1 = Member.builder().id(1L).role(MemberRole.SUPER_VISOR).build();
        Member member2 = Member.builder().id(2L).role(MemberRole.SUPER_VISOR).build();
        Long answerId = 1L;
        Answer answer = Answer.builder()
                .id(answerId)
                .content("테스트 답변")
                .member(member1)
                .build();

        when(answerRepository.findById(answerId)).thenReturn(
                Optional.ofNullable(answer)
        );

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            adminAnswerService.deleteAnswer(answerId, member2);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.FORBIDDEN.getStatus());
    }

    @Test
    @DisplayName("SUPER VISOR 권한을 가진 관리자는 자신이 작성한 답변을 삭제할 수 있다.")
    void t6() {
        Member member = Member.builder().id(1L).role(MemberRole.SUPER_VISOR).build();
        Long answerId = 1L;
        Answer answer = Answer.builder()
                .id(answerId)
                .content("테스트 답변")
                .member(member)
                .build();

        when(answerRepository.findById(answerId)).thenReturn(
                Optional.ofNullable(answer)
        );

        adminAnswerService.deleteAnswer(answerId, member);
        verify(answerRepository, times(1)).save(any(Answer.class));
    }
}