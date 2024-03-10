package com.drunkenlion.alcoholfriday.domain.member.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Member 회원_관리자 = memberRepository.save(Member.builder()
                .email("admin1@example.com")
                .provider(ProviderType.KAKAO)
                .name("관리자")
                .nickname("admin")
                .role(MemberRole.ADMIN)
                .phone(1041932693L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert SuperVisor
        Member 회원_슈퍼바이저1 = memberRepository.save(Member.builder()
                .email("supervisor1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor1")
                .nickname("Supervisor1")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        Member 회원_슈퍼바이저2 = memberRepository.save(Member.builder()
                .email("supervisor2@example.com")
                .provider(ProviderType.KAKAO)
                .name("Supervisor2")
                .nickname("Supervisor2")
                .role(MemberRole.SUPER_VISOR)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert StoreManager
        Member 회원_스토어매니저1 = memberRepository.save(Member.builder()
                .email("storeManager1@example.com")
                .provider(ProviderType.KAKAO)
                .name("StoreManager1")
                .nickname("StoreManager1")
                .role(MemberRole.STORE_MANAGER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert Owner
        Member 회원_사장1 = memberRepository.save(Member.builder()
                .email("owner1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Owner1")
                .nickname("Owner1")
                .role(MemberRole.OWNER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        // Insert Member
        Member 회원_일반회원1 = memberRepository.save(Member.builder()
                .email("member1@example.com")
                .provider(ProviderType.KAKAO)
                .name("Member1")
                .nickname("Member1")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("전체 회원 조회 - ADMIN")
    void findAllBasedAuthAdminTest() {
        // given
        Member 회원_관리자 = memberRepository.findByEmail("admin1@example.com").get();
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Member> search = memberRepository.findAllBasedAuth(회원_관리자, pageable);

        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(6);
    }

    @Test
    @DisplayName("전체 회원 조회 - SUPER_VISOR")
    void findAllBasedAuthSupervisorTest() {
        // given
        Member 회원_슈퍼바이저1 = memberRepository.findByEmail("supervisor1@example.com").get();
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Member> search = memberRepository.findAllBasedAuth(회원_슈퍼바이저1, pageable);

        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(4);
    }
}