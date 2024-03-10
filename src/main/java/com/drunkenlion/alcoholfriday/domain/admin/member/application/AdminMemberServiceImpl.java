package com.drunkenlion.alcoholfriday.domain.admin.member.application;

import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {
    private final MemberRepository memberRepository;

    @Override
    public Page<MemberListResponse> getMembers(Member authMember, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> members = memberRepository.findAllBasedAuth(authMember, pageable);

        return members.map(MemberListResponse::of);
    }

    @Override
    public MemberDetailResponse getMember(Member authMember, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        // 슈퍼바이저는 어드민 및 타 슈퍼바이저를 제외한 전체 유저만 조회 가능
        if (!authMember.getRole().equals(MemberRole.ADMIN) &&
                !authMember.getId().equals(id) &&
                (member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.SUPER_VISOR))
        ) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.FORBIDDEN)
                    .build();
        }

        return MemberDetailResponse.of(member);
    }

    @Override
    @Transactional
    public MemberDetailResponse modifyMember(Member authMember, Long id, MemberModifyRequest memberModifyRequest) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        // 슈퍼바이저는 어드민 및 타 슈퍼바이저를 제외한 전체 유저만 수정 가능
        if (!authMember.getRole().equals(MemberRole.ADMIN) &&
                !authMember.getId().equals(id) &&
                (member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.SUPER_VISOR))
        ) {
            throw BusinessException.builder()
                    .response(HttpResponse.Fail.FORBIDDEN)
                    .build();
        }

        member = member.toBuilder()
                .name(memberModifyRequest.getName())
                .nickname(memberModifyRequest.getNickname())
                .phone(memberModifyRequest.getPhone())
                .build();

        // ADMIN 은 모든 권한 수정 가능
        // SUPER_VISOR는 ADMIN, SUPER_VISOR 제외한 권한 수정 가능
        if (authMember.getRole().equals(MemberRole.ADMIN) ||
                (!member.getRole().equals(MemberRole.ADMIN) && !member.getRole().equals(MemberRole.SUPER_VISOR))
        ) {
            member = member.toBuilder()
                    .role(memberModifyRequest.getRole())
                    .build();
        }

        memberRepository.save(member);

        return MemberDetailResponse.of(member);
    }
}
