package com.drunkenlion.alcoholfriday.domain.customerservice.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final FileService fileService;

    @Override
    @Transactional
    public QuestionSaveResponse saveQuestion(QuestionRequest request, Member member) {
        Question question = QuestionRequest.toEntity(request, member);
        fileService.uploadFiles(request.getFiles(), question.getId(), EntityType.QUESTION);
        return QuestionSaveResponse.of(questionRepository.save(question));
    }
}
