package com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.dto.response.AdminQuestionResponse;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.util.AdminQuestionValidator;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminQuestionServiceImpl implements AdminQuestionService {
    private final QuestionRepository questionRepository;
    private final FileService fileService;

    @Override
    public AdminQuestionResponse findQuestion(Member member, Long id) {
        log.info("[AdminQuestionServiceImpl.findQuestion] : 접근");
        AdminQuestionValidator.hasRole(member);

        Question question =
                questionRepository.adminFindQuestion(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_QUESTION));

        NcpFileResponse all = fileService.findAll(question);
        return AdminQuestionResponse.of(question, all);
    }

    @Override
    public Page<AdminQuestionResponse> findQuestions(Member member, int page, int size) {
        log.info("[AdminQuestionServiceImpl.findQuestions] : 접근");
        AdminQuestionValidator.hasRole(member);

        Pageable pageable = PageRequest.of(page, size);
        Page<Question> findAll = questionRepository.findAll(pageable);
        return findAll.map(AdminQuestionResponse::of);
    }

    @Override
    @Transactional
    public AdminQuestionResponse updateQuestion(Long id, Member member, QuestionModifyRequest request,
                                           List<MultipartFile> files) {
        log.info("[AdminQuestionServiceImpl.updateQuestion] : 접근");
        AdminQuestionValidator.hasRole(member);

        Question question =
                questionRepository.findById(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_QUESTION));

        question.updateQuestion(request.getUpdateTitle(), request.getUpdateContent());
        questionRepository.save(question);

        NcpFileResponse file = fileService.updateFiles(question, request.getRemoveImageSeqList(), files);

        return AdminQuestionResponse.of(question, file);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id, Member member) {
        log.info("[AdminQuestionServiceImpl.deleteQuestion] : 접근");
        AdminQuestionValidator.hasRole(member);

        Question question =
                questionRepository.findByIdAndDeletedAtIsNull(id)
                        .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_QUESTION));

        question.deleteEntity();
        questionRepository.save(question);
    }
}
