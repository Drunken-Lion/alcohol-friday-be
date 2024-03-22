package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeCustomRepository {

    Optional<Notice> findByIdAndDeletedAtIsNull(Long id);
}
