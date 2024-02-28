package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    @Transactional
    @Override
    public MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest) {
        if (memberRepository.existsByNickname(modifyRequest.getNickname())) {
            throw new BusinessException(HttpResponse.Fail.NICKNAME_IN_USE);
        }

        member = member.toBuilder()
                .nickname(modifyRequest.getNickname())
                .phone(modifyRequest.getPhone())
                .build();

        return MemberResponse.of(memberRepository.save(member));
    }

    @Override
    public Page<MemberQuestionListResponse> getMyQuestions(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);

        return questionPage.map(MemberQuestionListResponse::of);
    }

    @Override
    public Page<MemberOrderListResponse> getMyOrders(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);

        return orderPage.map(MemberOrderListResponse::of);
    }

    @Override
    public List<AddressResponse> getMyAddresses(Long memberId) {
        List<Address> addresses = addressRepository.findAllByMemberIdOrderByIsPrimaryDescCreatedAtDesc(memberId);

        if (addresses.isEmpty()) throw new BusinessException(HttpResponse.Fail.NOT_FOUND_ADDRESSES);

        return addresses.stream().map(AddressResponse::of).toList();
    }
}
