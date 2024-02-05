package com.drunkenlion.alcoholfriday.domain.admin.application;

import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminMemberServiceTest {
    @InjectMocks
    private AdminMemberServiceImpl adminMemberService;
    @Mock
    private MemberRepository memberRepository;

    // test를 위한 임의 변수
    private final Long id = 1L;
    private final String email = "test@example.com";
    private final String provider = "kakao_test12345";
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = "MEMBER";
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = LocalDate.now();
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final int page = 0;
    private final int size = 20;

    @Test
    public void getMembersTest() {
        // given
        Mockito.when(this.memberRepository.findAll(any(Pageable.class))).thenReturn(this.getMembers());

        // when
        Page<MemberListResponse> members = this.adminMemberService.getMembers(page, size);

        // then
        List<MemberListResponse> content = members.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(id);
        assertThat(content.get(0).getName()).isEqualTo(name);
        assertThat(content.get(0).getNickname()).isEqualTo(nickname);
        assertThat(content.get(0).getEmail()).isEqualTo(email);
        assertThat(content.get(0).getRole()).isEqualTo(role);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    private Page<Member> getMembers() {
        List<Member> list = List.of(this.getMember());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Member>(list, pageable, list.size());
    }

    private Member getMember() {
        return Member.builder()
                .id(id)
                .email(email)
                .provider(provider)
                .name(name)
                .nickname(nickname)
                .role(role)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .build();
    }
}
