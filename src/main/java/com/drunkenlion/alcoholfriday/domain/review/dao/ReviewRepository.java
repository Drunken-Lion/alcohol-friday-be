package com.drunkenlion.alcoholfriday.domain.review.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
    Page<Review> findAllByMemberIdAndDeletedAtIsNull(Long memberId, Pageable pageable);

    Page<Review> findByMember(Member member, Pageable pageable);

    Optional<Review> findByOrderDetail(OrderDetail orderDetail);

    List<Review> findAllByItemIdAndDeletedAtIsNull(Long itemId);
}
