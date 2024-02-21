package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MemberServiceTest {
    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;

    private final Long id = 1L;
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
                .id(id)
                .nickname(memberModifyRequest.getNickname())
                .provider(ProviderType.ofProvider(provider))
                .phone(memberModifyRequest.getPhone())
                .build();

        Mockito.when(this.memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberResponse memberResponse = this.memberService.modifyMember(member, memberModifyRequest);

        // then
        assertThat(memberResponse.getId()).isEqualTo(id);
        assertThat(memberResponse.getNickname()).isEqualTo(modifyNickname);
        assertThat(memberResponse.getPhone()).isEqualTo(modifyPhone);
    }

    private Optional<Member> getOne() {
        return Optional.of(this.getData());
    }

    private Member getData() {
        return Member.builder()
                .id(id)
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
}
