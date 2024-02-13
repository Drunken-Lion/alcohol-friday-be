package com.drunkenlion.alcoholfriday.domain.admin.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.application.AdminMemberServiceImpl;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ProviderType;

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
	private final ProviderType provider = ProviderType.KAKAO;
	private final String name = "테스트";
	private final String nickname = "test";
	private final String role = "MEMBER";
	private final Long phone = 1012345678L;
	private final LocalDate certifyAt = null;
	private final boolean agreedToServiceUse = false;
	private final boolean agreedToServicePolicy = false;
	private final boolean agreedToServicePolicyUse = false;
	private final LocalDateTime createdAt = LocalDateTime.now();
	private final LocalDateTime updatedAt = null;
	private final LocalDateTime deletedAt = null;
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

	@Test
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
		assertThat(memberDetailResponse.getDeletedAt()).isEqualTo(deletedAt);
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
			.updatedAt(updatedAt)
			.deletedAt(deletedAt)
			.build();
	}
}
