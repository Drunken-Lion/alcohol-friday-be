package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberService {
    MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest);

    Page<MemberQuestionListResponse> getMyQuestions(Long memberId, int page, int size);

    Page<MemberOrderListResponse> getMyOrders(Long memberId, int page, int size);

    List<AddressResponse> getMyAddresses(Long memberId);
}
