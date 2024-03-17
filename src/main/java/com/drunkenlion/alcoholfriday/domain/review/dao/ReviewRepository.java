package com.drunkenlion.alcoholfriday.domain.review.dao;

import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByMemberIdAndDeletedAtIsNull(Long memberId, Pageable pageable);

    List<Review> findAllByItemIdAndDeletedAtIsNull(Long itemId);
}
