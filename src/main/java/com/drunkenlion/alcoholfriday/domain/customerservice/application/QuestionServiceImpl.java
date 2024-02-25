package com.drunkenlion.alcoholfriday.domain.customerservice.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final FileService fileService;

    @Override
    @Transactional
    public QuestionSaveResponse saveQuestion(QuestionSaveRequest request, List<MultipartFile> files, Member member) {
        Question question = questionRepository.save(QuestionSaveRequest.toEntity(request, member));
        fileService.saveFiles(question, files);
        return QuestionSaveResponse.of(question);
    }
}
