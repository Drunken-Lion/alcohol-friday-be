package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerResponse;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerSaveResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface AdminAnswerService {
    AdminAnswerSaveResponse saveAnswer(AnswerSaveRequest request, Member member);

    AdminAnswerResponse updateAnswer(Long id, AnswerModifyRequest request, Member member);

    void deleteAnswer(Long id, Member member);
}
