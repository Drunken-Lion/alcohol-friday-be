package com.drunkenlion.alcoholfriday.domain.customerservice.question.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.QuestionResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {
    QuestionSaveResponse saveQuestion(QuestionSaveRequest request, List<MultipartFile> files, Member member);

    QuestionResponse findQuestion(Member member, Long id);

    Page<QuestionResponse> findQuestions(Member member, int page, int size);
    
    QuestionResponse updateQuestion(Long id, Member member, QuestionModifyRequest request, List<MultipartFile> files);

    void deleteQuestion(Long id, Member member);
}
