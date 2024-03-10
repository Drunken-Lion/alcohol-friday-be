package com.drunkenlion.alcoholfriday.domain.admin.member.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
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

    private final Long memberId = 1L;
    private final String memberEmail = "member@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String memberName = "멤버";
    private final String memberNickname = "member";
    private final MemberRole memberRole = MemberRole.MEMBER;
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = LocalDate.now();
    private final boolean agreedToServiceUse = true;
    private final boolean agreedToServicePolicy = true;
    private final boolean agreedToServicePolicyUse = true;

    private final Long adminId = 2L;
    private final String adminEmail = "admin@example.com";
    private final String adminName = "어드민";
    private final String adminNickname = "admin";
    private final MemberRole adminRole = MemberRole.ADMIN;

    private final Long superId = 3L;
    private final String superEmail = "supervisor@example.com";
    private final String superName = "슈퍼바이저";
    private final String superNickname = "supervisor";
    private final MemberRole superRole = MemberRole.SUPER_VISOR;

    private final Long super2Id = 4L;
    private final String super2Email = "supervisor2@example.com";
    private final String super2Name = "슈퍼바이저2";
    private final String super2Nickname = "supervisor2";
    private final MemberRole super2Role = MemberRole.SUPER_VISOR;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();

    private final int page = 0;
    private final int size = 20;

    private final String modifyName = "테스트 수정";
    private final String modifyNickname = "test 수정";
    private final Long modifyPhone = 1011112222L;

    @Test
    @DisplayName("회원 목록 조회 성공 - ADMIN")
    public void getMembersAdminTest() {
        // given
        Mockito.when(this.memberRepository.findAllBasedAuth(any(), any(Pageable.class))).thenReturn(this.getAllMembers());

        // when
        Page<MemberListResponse> members = this.adminMemberService.getMembers(getAdminData(), page, size);

        // then
        List<MemberListResponse> content = members.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(4);
        assertThat(content.get(0).getId()).isEqualTo(memberId);
        assertThat(content.get(0).getName()).isEqualTo(memberName);
        assertThat(content.get(0).getNickname()).isEqualTo(memberNickname);
        assertThat(content.get(0).getEmail()).isEqualTo(memberEmail);
        assertThat(content.get(0).getRole()).isEqualTo(memberRole);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("회원 상세 조회 성공 - ADMIN으로 MEMBER 조회")
    public void getMemberAdminTest() {
        // given
        Mockito.when(this.memberRepository.findById(any())).thenReturn(this.getMemberOne());

        // when
        MemberDetailResponse memberDetailResponse = this.adminMemberService.getMember(getAdminData(), memberId);

        // then
        assertThat(memberDetailResponse.getId()).isEqualTo(memberId);
        assertThat(memberDetailResponse.getEmail()).isEqualTo(memberEmail);
        assertThat(memberDetailResponse.getProvider()).isEqualTo(provider);
        assertThat(memberDetailResponse.getName()).isEqualTo(memberName);
        assertThat(memberDetailResponse.getNickname()).isEqualTo(memberNickname);
        assertThat(memberDetailResponse.getRole()).isEqualTo(memberRole);
        assertThat(memberDetailResponse.getPhone()).isEqualTo(phone);
        assertThat(memberDetailResponse.getCertifyAt()).isEqualTo(certifyAt);
        assertThat(memberDetailResponse.getAgreedToServiceUse()).isEqualTo(agreedToServiceUse);
        assertThat(memberDetailResponse.getAgreedToServicePolicy()).isEqualTo(agreedToServicePolicy);
        assertThat(memberDetailResponse.getAgreedToServicePolicyUse()).isEqualTo(agreedToServicePolicyUse);
        assertThat(memberDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(memberDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("회원 상세 조회 성공 - SUPER_VISOR 로 자기 자신 조회")
    public void getMemberSupervisorTest() {
        // given
        Mockito.when(this.memberRepository.findById(any())).thenReturn(this.getSupervisorOne());

        // when
        MemberDetailResponse memberDetailResponse = this.adminMemberService.getMember(getSupervisorData(), superId);

        // then
        assertThat(memberDetailResponse.getId()).isEqualTo(superId);
        assertThat(memberDetailResponse.getEmail()).isEqualTo(superEmail);
        assertThat(memberDetailResponse.getProvider()).isEqualTo(provider);
        assertThat(memberDetailResponse.getName()).isEqualTo(superName);
        assertThat(memberDetailResponse.getNickname()).isEqualTo(superNickname);
        assertThat(memberDetailResponse.getRole()).isEqualTo(superRole);
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
            adminMemberService.getMember(getSupervisorData(), any());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 상세 조회 실패 - SUPER_VISOR 로 ADMIN 조회")
    public void getMemberAdminFailForbiddenTest() {
        // given
        Mockito.when(this.memberRepository.findById(any())).thenReturn(this.getAdminOne());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMemberService.getMember(getSupervisorData(), adminId);
        });

        // then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 상세 조회 실패 - SUPER_VISOR 로 다른 SUPER_VISOR 조회")
    public void getMemberOtherSupervisorFailForbiddenTest() {
        // given
        Mockito.when(this.memberRepository.findById(any())).thenReturn(this.getSupervisor2One());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMemberService.getMember(getSupervisorData(), super2Id);
        });

        // then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 수정 성공 - ADMIN으로 MEMBER 수정")
    public void modifyMemberAdminTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .name(modifyName)
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .role(MemberRole.ADMIN)
                .build();

        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getMemberOne());
        Mockito.when(this.memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberDetailResponse modifiedMember = this.adminMemberService.modifyMember(getAdminData(), memberId, memberModifyRequest);

        // then
        assertThat(modifiedMember.getId()).isEqualTo(memberId);
        assertThat(modifiedMember.getName()).isEqualTo(modifyName);
        assertThat(modifiedMember.getNickname()).isEqualTo(modifyNickname);
        assertThat(modifiedMember.getPhone()).isEqualTo(modifyPhone);
        assertThat(modifiedMember.getRole()).isEqualTo(MemberRole.ADMIN);
    }

    @Test
    @DisplayName("회원 수정 성공 - SUPER_VISOR로 MEMBER 수정")
    public void modifyMemberSupervisorForMemberTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .name(modifyName)
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .role(MemberRole.STORE_MANAGER)
                .build();

        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getMemberOne());
        Mockito.when(this.memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberDetailResponse modifiedMember = this.adminMemberService.modifyMember(getSupervisorData(), memberId, memberModifyRequest);

        // then
        assertThat(modifiedMember.getId()).isEqualTo(memberId);
        assertThat(modifiedMember.getName()).isEqualTo(modifyName);
        assertThat(modifiedMember.getNickname()).isEqualTo(modifyNickname);
        assertThat(modifiedMember.getPhone()).isEqualTo(modifyPhone);
        assertThat(modifiedMember.getRole()).isEqualTo(MemberRole.STORE_MANAGER);
    }

    @Test
    @DisplayName("회원 수정 성공 - SUPER_VISOR로 자기자신의 정보 수정")
    public void modifyMemberSupervisorForMineTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .name(modifyName)
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .role(MemberRole.ADMIN)
                .build();

        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getSupervisorOne());
        Mockito.when(this.memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberDetailResponse modifiedMember = this.adminMemberService.modifyMember(getSupervisorData(), superId, memberModifyRequest);

        // then
        assertThat(modifiedMember.getId()).isEqualTo(superId);
        assertThat(modifiedMember.getName()).isEqualTo(modifyName);
        assertThat(modifiedMember.getNickname()).isEqualTo(modifyNickname);
        assertThat(modifiedMember.getPhone()).isEqualTo(modifyPhone);
        assertThat(modifiedMember.getRole()).isEqualTo(MemberRole.SUPER_VISOR); // SUPER_VISOR는 ADMIN, SUPER_VISOR의 권한을 수정할 수 없다. 자기자신도 불가.
    }

    @Test
    @DisplayName("회원 수정 실패 - 찾을 수 없는 회원")
    public void modifyMemberFailNotFoundTest() {
        // given
        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMemberService.modifyMember(getAdminData(), memberId, any());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 수정 실패 - SUPER_VISOR로 다른 SUPER_VISOR 수정")
    public void modifyMemberOtherSupervisorFailForbiddenTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .name(modifyName)
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .role(MemberRole.ADMIN)
                .build();

        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getSupervisor2One());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMemberService.modifyMember(getSupervisorData(), super2Id, memberModifyRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 수정 실패 - SUPER_VISOR로 ADMIN 수정")
    public void modifyMemberSupervisorFailForbiddenTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .name(modifyName)
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .role(MemberRole.ADMIN)
                .build();

        Mockito.when(this.memberRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getAdminOne());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminMemberService.modifyMember(getSupervisorData(), adminId, memberModifyRequest);
        });

        // then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    private Page<Member> getAllMembers() {
        List<Member> list = List.of(
                getMemberData(),
                getAdminData(),
                getSupervisorData(),
                getSupervisor2Data());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Member>(list, pageable, list.size());
    }

    private Optional<Member> getMemberOne() {
        return Optional.of(this.getMemberData());
    }

    private Optional<Member> getAdminOne() {
        return Optional.of(this.getAdminData());
    }

    private Optional<Member> getSupervisorOne() {
        return Optional.of(this.getSupervisorData());
    }

    private Optional<Member> getSupervisor2One() {
        return Optional.of(this.getSupervisor2Data());
    }

    private Member getMemberData() {
        return Member.builder()
                .id(memberId)
                .email(memberEmail)
                .provider(ProviderType.byProviderName(provider))
                .name(memberName)
                .nickname(memberNickname)
                .role(memberRole)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Member getAdminData() {
        return Member.builder()
                .id(adminId)
                .email(adminEmail)
                .provider(ProviderType.byProviderName(provider))
                .name(adminName)
                .nickname(adminNickname)
                .role(adminRole)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Member getSupervisorData() {
        return Member.builder()
                .id(superId)
                .email(superEmail)
                .provider(ProviderType.byProviderName(provider))
                .name(superName)
                .nickname(superNickname)
                .role(superRole)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Member getSupervisor2Data() {
        return Member.builder()
                .id(super2Id)
                .email(super2Email)
                .provider(ProviderType.byProviderName(provider))
                .name(super2Name)
                .nickname(super2Nickname)
                .role(super2Role)
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
