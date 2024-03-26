package com.drunkenlion.alcoholfriday.domain.customerservice.question.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.QuestionResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.util.validate.QuestionValidator;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.file.dao.FileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("[QuestionServiceTest] 문의사항 Service Test")
class QuestionServiceTest {
    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;

    @AfterEach
    @Transactional
    public void after() {
        questionRepository.deleteAll();
        fileRepository.deleteAll();
    }

    @Test
    @DisplayName("1번 회원은 3번 회원이 작성한 게시글에 접근하면 에러가 발생한다.")
    void t1() {
        Member firstMember = Member.builder().id(1L).build();
        Member secondMember = Member.builder().id(3L).build();

        Question question = Question.builder().member(secondMember).build();

        assertThatThrownBy(() -> QuestionValidator.compareEntityIdToMemberId(question, firstMember)).isInstanceOf(
                BusinessException.class);
    }

    @Test
    @DisplayName("26번 회원 본인이 등록한 문의내역은 조회에 성공한다.")
    void t2() {
        Long questionId = 1L;
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        QuestionStatus questionStatus = QuestionStatus.INCOMPLETE;

        Member member = Member.builder().id(26L).build();

        when(questionRepository.findQuestion(questionId)).thenReturn(
                Optional.ofNullable(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .member(member)
                        .content(questionContent)
                        .status(questionStatus)
                        .build())
        );

        QuestionResponse question = questionService.findQuestion(member, questionId);

        assertAll(
                () -> assertThat(question.getId()).isEqualTo(questionId),
                () -> assertThat(question.getTitle()).isEqualTo(questionTitle),
                () -> assertThat(question.getContent()).isEqualTo(questionContent),
                () -> assertThat(question.getStatus()).isEqualTo(questionStatus)
        );
    }

    @Test
    @DisplayName("26번 회원 본인이 등록하지 않은 문의 내역은 조회에 실패한다.")
    void t3() {
        Long questionId = 7L;
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        QuestionStatus status = QuestionStatus.COMPLETE;

        Member firstMember = Member.builder().id(26L).build();
        Member secondMember = Member.builder().id(2L).build();

        when(questionRepository.findQuestion(questionId)).thenReturn(
                Optional.ofNullable(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .member(secondMember)
                        .content(questionContent)
                        .status(status)
                        .build())
        );

        assertThatThrownBy(() -> questionService.findQuestion(firstMember, questionId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("1번 회원이 등록한 문의내역은 답변이 달리기 전 삭제가 가능하다.")
    void t4() {
        Long questionId = 1L;
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        QuestionStatus questionStatus = QuestionStatus.INCOMPLETE;
        Member member = Member.builder().id(1L).build();

        when(questionRepository.findByIdAndDeletedAtIsNull(questionId)).thenReturn(
                Optional.ofNullable(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .member(member)
                        .content(questionContent)
                        .status(questionStatus)
                        .build())
        );

        questionService.deleteQuestion(questionId, member);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    @DisplayName("1번 회원이 등록한 문의내역에 답변이 달리면 삭제가 불가능하다.")
    void t5() {
        Long questionId = 1L;
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        QuestionStatus questionStatus = QuestionStatus.INCOMPLETE;
        Member member = Member.builder().id(1L).build();
        Question question = Question.builder()
                .id(questionId)
                .title(questionTitle)
                .member(member)
                .content(questionContent)
                .status(questionStatus)
                .build();

        Answer answer = Answer.builder().id(1L).build();
        answer.addQuestion(question);

        when(questionRepository.findByIdAndDeletedAtIsNull(questionId)).thenReturn(
                Optional.of(question)
        );

        assertThatThrownBy(() -> questionService.deleteQuestion(questionId, member)).isInstanceOf(
                BusinessException.class);
    }

    @Test
    @DisplayName("이미 삭제된 문의내역은 삭제가 불가능하다.")
    void t6() {
        Long questionId = 1L;
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        QuestionStatus questionStatus = QuestionStatus.INCOMPLETE;
        Member member = Member.builder().id(1L).build();
        Question question = Question.builder()
                .id(questionId)
                .title(questionTitle)
                .member(member)
                .content(questionContent)
                .status(questionStatus)
                .build();

        question.deleteEntity();

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            questionService.deleteQuestion(questionId, member);
        });

        assertThat(businessException.getStatus()).isEqualTo(HttpResponse.Fail.NOT_FOUND.getStatus());


    }

    @Test
    @DisplayName("자신이 작성한 게시물에 대해서는 수정이 가능하다.")
    void t7() {
        Long questionId = 1L;
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        QuestionStatus questionStatus = QuestionStatus.INCOMPLETE;
        Member member = Member.builder().id(1L).build();

        String updateQuestionTitle = "수정된 제목";
        String updateQuestionContent = "수정된 내용";
        List<MultipartFile> files = new ArrayList<>();
        QuestionModifyRequest modify = QuestionModifyRequest.builder().updateTitle(updateQuestionTitle)
                .updateContent(updateQuestionContent).build();

        when(questionRepository.findByIdAndDeletedAtIsNull(questionId)).thenReturn(
                Optional.ofNullable(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .member(member)
                        .content(questionContent)
                        .status(questionStatus)
                        .build())
        );

        QuestionResponse updateQuestion = questionService.updateQuestion(questionId, member, modify, files);

        assertAll(
                () -> assertThat(updateQuestion.getId()).isEqualTo(questionId),
                () -> assertThat(updateQuestion.getTitle()).isEqualTo(updateQuestionTitle),
                () -> assertThat(updateQuestion.getContent()).isEqualTo(updateQuestionContent)
        );
    }

    @Test
    @DisplayName("자신이 작성하지 않은 게시물에 대해서는 수정이 불가능하다.")
    void t8() {
        Long questionId = 1L;
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        QuestionStatus questionStatus = QuestionStatus.INCOMPLETE;
        Member firstMember = Member.builder().id(1L).build();

        Member secondMember = Member.builder().id(3L).build();

        String updateQuestionTitle = "수정된 제목";
        String updateQuestionContent = "수정된 내용";
        List<MultipartFile> files = new ArrayList<>();
        QuestionModifyRequest modify = QuestionModifyRequest.builder().updateTitle(updateQuestionTitle)
                .updateContent(updateQuestionContent).build();

        when(questionRepository.findByIdAndDeletedAtIsNull(questionId)).thenReturn(
                Optional.ofNullable(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .member(firstMember)
                        .content(questionContent)
                        .status(questionStatus)
                        .build())
        );

        assertThatThrownBy(() -> questionService.updateQuestion(questionId, secondMember, modify, files)).isInstanceOf(
                BusinessException.class);
    }

    @Test
    @DisplayName("존재하지 않은 문의내역은 조회할 수 없다.")
    void t9() {
        Long findQuestionId = 100L;
        Member member = Member.builder().id(1L).build();

        assertThatThrownBy(() -> questionService.findQuestion(member, findQuestionId)).isInstanceOf(
                BusinessException.class);
    }

    @Test
    @DisplayName("문의내역 저장")
    void t10() {
        String questionTitle = "일반 문의 제목 1";
        String questionContent = "일반 문의 내용 1";
        Member member = Member.builder().id(1L).build();
        List<MultipartFile> files = new ArrayList<>();

        QuestionSaveRequest request = QuestionSaveRequest.builder()
                .title(questionTitle)
                .content(questionContent)
                .build();

        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));
        QuestionSaveResponse response = questionService.saveQuestion(request, files, member);

        assertAll(
                () -> assertThat(response.getTitle()).isEqualTo(questionTitle),
                () -> assertThat(response.getContent()).isEqualTo(questionContent),
                () -> assertThat(response.getMember().getId()).isEqualTo(member.getId())
        );
    }
}