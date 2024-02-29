package com.drunkenlion.alcoholfriday.domain.admin.member.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;

import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminMemberServiceTest {
    @InjectMocks
    private AdminMemberServiceImpl adminMemberService;
    @Mock
    private MemberRepository memberRepository;

    private final Long id = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final MemberRole role = MemberRole.MEMBER;
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = LocalDate.now();
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();

    private final int page = 0;
    private final int size = 20;

    private final String modifyName = "테스트 수정";
    private final String modifyNickname = "test 수정";
    private final MemberRole modifyRole = MemberRole.ADMIN;
    private final Long modifyPhone = 1011112222L;

    @Test
    @DisplayName("회원 목록 조회 성공")
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

    @Test
    @DisplayName("회원 상세 조회 성공")
    public void getMemberTest() {
        // given
        Mockito.when(this.memberRepository.findById(any())).thenReturn(this.getOne());

        // when
        MemberDetailResponse memberDetailResponse = this.adminMemberService.getMember(id);

        // then
        assertThat(memberDetailResponse.getId()).isEqualTo(id);
        assertThat(memberDetailResponse.getEmail()).isEqualTo(email);
        assertThat(memberDetailResponse.getProvider()).isEqualTo(provider);
        assertThat(memberDetailResponse.getName()).isEqualTo(name);
        assertThat(memberDetailResponse.getNickname()).isEqualTo(nickname);
        assertThat(memberDetailResponse.getRole()).isEqualTo(role);
        assertThat(memberDetailResponse.getPhone()).isEqualTo(phone);
        assertThat(memberDetailResponse.getCertifyAt()).isEqualTo(certifyAt);
        assertThat(memberDetailResponse.getAgreedToServiceUse()).isEqualTo(agreedToServiceUse);
        assertThat(memberDetailResponse.getAgreedToServicePolicy()).isEqualTo(agreedToServicePolicy);
        assertThat(memberDetailResponse.getAgreedToServicePolicyUse()).isEqualTo(agreedToServicePolicyUse);
        assertThat(memberDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(memberDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("회원 상세 조회 실패 - 찾을 수 없는 회원")
    public void getMemberFailNotFoundTest() {
        // given
        Mockito.when(this.memberRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMemberService.getMember(id);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 수정 성공")
    public void modifyMemberTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .name(modifyName)
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .role(modifyRole)
                .build();

        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(this.getOne());
        Mockito.when(this.memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberDetailResponse modifiedMember = this.adminMemberService.modifyMember(id, memberModifyRequest);

        // then
        assertThat(modifiedMember.getId()).isEqualTo(id);
        assertThat(modifiedMember.getName()).isEqualTo(modifyName);
        assertThat(modifiedMember.getNickname()).isEqualTo(modifyNickname);
        assertThat(modifiedMember.getPhone()).isEqualTo(modifyPhone);
        assertThat(modifiedMember.getRole()).isEqualTo(modifyRole);
    }

    @Test
    @DisplayName("회원 수정 조회 실패 - 찾을 수 없는 회원")
    public void modifyMemberFailNotFoundTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .name(modifyName)
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .role(modifyRole)
                .build();

        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMemberService.modifyMember(id, memberModifyRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }

    private Page<Member> getMembers() {
        List<Member> list = List.of(this.getData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Member>(list, pageable, list.size());
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
                .role(role)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
