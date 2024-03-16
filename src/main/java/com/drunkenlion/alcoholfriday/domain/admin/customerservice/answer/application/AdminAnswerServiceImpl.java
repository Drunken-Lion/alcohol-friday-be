package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.application;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerResponse;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.util.AdminAnswerValidator;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.dao.AnswerRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminAnswerServiceImpl implements AdminAnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public AdminAnswerSaveResponse saveAnswer(AnswerSaveRequest request, Member member) {
        AdminAnswerValidator.hasRole(member);
        Question question =
                questionRepository.findById(request.getQuestionId())
                        .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_QUESTION));

        AdminAnswerValidator.isDeleted(question);

        Answer answer = answerRepository.save(AnswerSaveRequest.toEntity(request, question, member));
        answer.addQuestion(question);
        questionRepository.save(question);

        return AdminAnswerSaveResponse.of(answer);
    }

    @Override
    @Transactional
    public AdminAnswerResponse updateAnswer(Long id, AnswerModifyRequest request, Member member) {
        AdminAnswerValidator.hasRole(member);
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_ANSWER));

        if (!answer.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }

        AdminAnswerValidator.isDeleted(answer);

        answer.updateContent(request.getUpdateContent());
        answerRepository.save(answer);
        return AdminAnswerResponse.of(answer);
    }

    @Override
    public void deleteAnswer(Long id, Member member) {
        AdminAnswerValidator.hasRole(member);
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_ANSWER));

        AdminAnswerValidator.isDeleted(answer);

        // SUPER VISOR 권한의 경우 자신이 작성한 답변만 삭제할 수 있다.
        if (!member.getRole().equals(MemberRole.ADMIN) && !answer.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }

        answer.deleteEntity();
        answerRepository.save(answer);
    }
}
