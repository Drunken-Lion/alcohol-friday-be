package com.drunkenlion.alcoholfriday.domain.customerservice.dao;

import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
