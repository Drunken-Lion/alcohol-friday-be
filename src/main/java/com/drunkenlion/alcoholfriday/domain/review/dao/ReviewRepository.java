package com.drunkenlion.alcoholfriday.domain.review.dao;

import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
