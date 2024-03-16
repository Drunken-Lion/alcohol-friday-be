package com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.dto.response.AdminQuestionResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface AdminQuestionService {
    AdminQuestionResponse findQuestion(Member member, Long id);

    Page<AdminQuestionResponse> findQuestions(Member member, int page, int size);

    AdminQuestionResponse updateQuestion(Long id, Member member, QuestionModifyRequest request, List<MultipartFile> files);

    void deleteQuestion(Long id, Member member);
}
