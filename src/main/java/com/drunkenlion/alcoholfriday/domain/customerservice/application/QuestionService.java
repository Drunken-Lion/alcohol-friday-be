package com.drunkenlion.alcoholfriday.domain.customerservice.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {
    QuestionSaveResponse saveQuestion(QuestionSaveRequest request, List<MultipartFile> files, Member member);
}
