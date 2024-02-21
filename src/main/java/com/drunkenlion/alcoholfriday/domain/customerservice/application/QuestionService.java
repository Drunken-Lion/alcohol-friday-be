package com.drunkenlion.alcoholfriday.domain.customerservice.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface QuestionService {
    QuestionSaveResponse saveQuestion(QuestionRequest request, Member member);
}
