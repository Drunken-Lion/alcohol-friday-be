package com.drunkenlion.alcoholfriday.domain.customerservice.question.dao;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionCustomRepository {
    Page<Question> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Optional<Question> findByIdAndDeletedAtIsNull(Long id);
}
