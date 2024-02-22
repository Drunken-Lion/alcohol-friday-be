package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MemberServiceTest {
    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private QuestionRepository questionRepository;

    private final Long memberId = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = MemberRole.MEMBER.getRole();
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = null;
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = null;
    private final LocalDateTime deletedAt = null;

    private final String modifyNickname = "수정테스트";
    private final Long modifyPhone = 1011112222L;

    private final Long questionId = 1L;
    private final String title = "문의 제목1";
    private final String content = "문의 내용1";
    private final String questionStatus = QuestionStatus.COMPLETE.getLabel();

    private final int page = 0;
    private final int size = 5;

    @Test
    @DisplayName("회원 정보 수정")
    public void modifyMemberTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .build();

        // When
        Member member = Member.builder()
                .id(memberId)
                .nickname(memberModifyRequest.getNickname())
                .provider(ProviderType.ofProvider(provider))
                .phone(memberModifyRequest.getPhone())
                .build();

        when(this.memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberResponse memberResponse = this.memberService.modifyMember(member, memberModifyRequest);

        // then
        assertThat(memberResponse.getId()).isEqualTo(memberId);
        assertThat(memberResponse.getNickname()).isEqualTo(modifyNickname);
        assertThat(memberResponse.getPhone()).isEqualTo(modifyPhone);
    }

    @Test
    @DisplayName("나의 문의내역 조회")
    public void getMyQuestions() {
        // given
        when(this.questionRepository.findByMember_IdOrderByCreatedAtDesc(any(), any(Pageable.class))).thenReturn(this.getQuestions());

        // when
        Page<MemberQuestionListResponse> questions = this.memberService.getMyQuestions(memberId, page, size);

        // then
        List<MemberQuestionListResponse> content = questions.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(questionId);
        assertThat(content.get(0).getTitle()).isEqualTo(title);
        assertThat(content.get(0).getQuestionStatus()).isEqualTo(questionStatus);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
    }

    private Page<Question> getQuestions() {
        List<Question> list = List.of(this.getQuestionData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(list, pageable, list.size());
    }

    private Optional<Member> getMemberOne() {
        return Optional.of(this.getMemberData());
    }

    private Member getMemberData() {
        return Member.builder()
                .id(memberId)
                .email(email)
                .provider(ProviderType.ofProvider(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.ofRole(role))
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    private Question getQuestionData() {
        Member member = getMemberData();

        return Question.builder()
                .id(questionId)
                .member(member)
                .title(title)
                .content(content)
                .status(QuestionStatus.ofStatus(questionStatus))
                .createdAt(createdAt)
                .build();
    }
}
