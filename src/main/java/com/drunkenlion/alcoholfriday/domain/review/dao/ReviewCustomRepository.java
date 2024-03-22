package com.drunkenlion.alcoholfriday.domain.review.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {
    Page<Review> findItemDetailReview(Item item, Pageable pageable);
}
