package com.drunkenlion.alcoholfriday.domain.customerservice.answer.dao;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByIdAndDeletedAtIsNull(Long id);
}
