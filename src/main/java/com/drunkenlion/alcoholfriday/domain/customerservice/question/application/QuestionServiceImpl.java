package com.drunkenlion.alcoholfriday.domain.customerservice.question.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.QuestionResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.util.validate.QuestionValidator;
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
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final FileService fileService;

    @Override
    @Transactional
    public QuestionSaveResponse saveQuestion(QuestionSaveRequest request, List<MultipartFile> files, Member member) {
        log.info("[QuestionServiceImpl.saveQuestion] : 접근");
        Question question = questionRepository.save(QuestionSaveRequest.toEntity(request, member));
        NcpFileResponse ncpFileResponse = fileService.saveFiles(question, files);
        return QuestionSaveResponse.of(question, ncpFileResponse);
    }

    @Override
    public QuestionResponse findQuestion(Member member, Long id) {
        log.info("[QuestionServiceImpl.findQuestion] : 접근");
        Question question =
                questionRepository.findQuestion(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_QUESTION));

        QuestionValidator.compareEntityIdToMemberId(question, member);

        NcpFileResponse all = fileService.findAll(question);
        return QuestionResponse.of(question, all);
    }

    @Override
    public Page<QuestionResponse> findQuestions(Member member, int page, int size) {
        log.info("[QuestionServiceImpl.findAll] : 접근");
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> findAll = questionRepository.findMember(member, pageable);
        return findAll.map(QuestionResponse::of);
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long id, Member member, QuestionModifyRequest request,
                                           List<MultipartFile> files) {
        log.info("[QuestionServiceImpl.updateQuestion] : 접근");
        Question question =
                questionRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_QUESTION));

        QuestionValidator.compareEntityIdToMemberId(question, member);

        if (question.getStatus().equals(QuestionStatus.COMPLETE)) {
            throw new BusinessException(Fail.BAD_REQUEST);
        }

        question.updateQuestion(request.getUpdateTitle(), request.getUpdateContent());
        questionRepository.save(question);

        NcpFileResponse file = fileService.updateFiles(question, request.getRemoveImageSeqList(), files);

        return QuestionResponse.of(question, file);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id, Member member) {
        log.info("[QuestionServiceImpl.deleteQuestion] : 접근");
        Question question =
                questionRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_QUESTION));

        QuestionValidator.compareEntityIdToMemberId(question, member);

        if (question.getStatus().equals(QuestionStatus.COMPLETE)) {
            throw new BusinessException(Fail.BAD_REQUEST);
        }

        question.deleteEntity();
        questionRepository.save(question);
    }
}
