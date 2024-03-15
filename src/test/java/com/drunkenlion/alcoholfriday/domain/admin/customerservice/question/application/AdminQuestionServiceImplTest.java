package com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.dto.response.AdminQuestionResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("[AdminQuestionServiceImplTest] 문의사항 Service Test")
class AdminQuestionServiceImplTest {
    @InjectMocks
    private AdminQuestionServiceImpl adminQuestionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileService fileService;
    @Test
    @DisplayName("관리자는 모든 문의내역을 조회할 수 있다.")
    void t1() {
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Member member = Member.builder().id(2L).build();

        Long questionId = 1L;
        String questionTitle = "zzz";
        String questionContent = "zzz";
        QuestionStatus status = QuestionStatus.INCOMPLETE;

        when(questionRepository.findAll(any(Pageable.class))).thenReturn(
                getQuestion(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .content(questionContent)
                        .member(member)
                        .status(status)
                        .build())
        );

        Page<AdminQuestionResponse> pageQuestions = adminQuestionService.findQuestions(adminMember, 0, 10);

        List<AdminQuestionResponse> questions = pageQuestions.getContent();

        assertThat(questions).isInstanceOf(List.class);
        assertThat(questions.size()).isEqualTo(1);
        assertThat(questions.get(0).getId()).isEqualTo(questionId);
        assertThat(questions.get(0).getTitle()).isEqualTo(questionTitle);
        assertThat(questions.get(0).getContent()).isEqualTo(questionContent);
        assertThat(questions.get(0).getStatus()).isEqualTo(status);
        assertThat(questions.get(0).getDeleteAt()).isNull();
    }

    @Test
    @DisplayName("관리자가 아니면 조회가 되지 않는다.")
    void t2() {
        Member member = Member.builder().id(1L).role(MemberRole.MEMBER).build();

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            adminQuestionService.findQuestions(member, 0, 10);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.FORBIDDEN.getStatus());
    }

    @Test
    @DisplayName("관리자는 문의사항 수정을 할 수 있다.")
    void t3() {
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Member member = Member.builder().id(2L).build();

        Long questionId = 1L;
        String questionTitle = "zzz";
        String questionContent = "zzz";
        QuestionStatus status = QuestionStatus.INCOMPLETE;

        String updateQuestionTitle = "수정된 제목";
        String updateQuestionContent = "수정된 내용";

        List<MultipartFile> files = new ArrayList<>();
        QuestionModifyRequest modify =
                QuestionModifyRequest
                        .builder()
                        .updateTitle(updateQuestionTitle)
                        .updateContent(updateQuestionContent)
                        .build();

        when(questionRepository.findById(questionId)).thenReturn(
                Optional.ofNullable(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .content(questionContent)
                        .member(member)
                        .status(status)
                        .build())
        );

        AdminQuestionResponse data =
                adminQuestionService.updateQuestion(questionId, adminMember, modify, files);

        assertThat(data.getId()).isEqualTo(questionId);
        assertThat(data.getTitle()).isEqualTo(updateQuestionTitle);
        assertThat(data.getContent()).isEqualTo(updateQuestionContent);
        assertThat(data.getStatus()).isEqualTo(status);
        assertThat(data.getDeleteAt()).isNull();
    }

    @Test
    @DisplayName("관리자는 문의사항 삭제 할 수 있다.")
    void t4() {
        Member adminMember = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Member member = Member.builder().id(2L).build();

        Long questionId = 1L;
        String questionTitle = "zzz";
        String questionContent = "zzz";
        QuestionStatus status = QuestionStatus.INCOMPLETE;

        when(questionRepository.findByIdAndDeletedAtIsNull(questionId)).thenReturn(
                Optional.ofNullable(Question.builder()
                        .id(questionId)
                        .title(questionTitle)
                        .content(questionContent)
                        .member(member)
                        .status(status)
                        .build())
        );

        adminQuestionService.deleteQuestion(questionId, adminMember);
        verify(questionRepository, times(1)).save(any(Question.class));
    }


    private Page<Question> getQuestion(Question question) {
        List<Question> list = List.of(question);
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<Question>(list, pageable, list.size());
    }
}