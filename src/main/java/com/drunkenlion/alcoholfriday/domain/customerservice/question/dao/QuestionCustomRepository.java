package com.drunkenlion.alcoholfriday.domain.customerservice.question.dao;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionCustomRepository {
    Page<Question> findMember(Member member, Pageable pageable);

    Optional<Question> findQuestion(Long id);

    Optional<Question> adminFindQuestion(Long id);
}
