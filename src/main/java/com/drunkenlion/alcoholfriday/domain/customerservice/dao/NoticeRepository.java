package com.drunkenlion.alcoholfriday.domain.customerservice.dao;

import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
