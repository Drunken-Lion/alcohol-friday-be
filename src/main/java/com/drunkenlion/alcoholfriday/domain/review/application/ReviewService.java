package com.drunkenlion.alcoholfriday.domain.review.application;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.review.dto.request.ReviewSaveRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewSaveResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
    ReviewSaveResponse saveReview(ReviewSaveRequest request, List<MultipartFile> files, Member member);

    Page<ReviewOrderDetailResponse> getUnwrittenReviews(Member member, int page, int size);

    Page<ReviewResponse> getReviews(Member member, int page, int size);

    ReviewModifyResponse updateReview(Long id, ReviewModifyRequest request, Member member, List<MultipartFile> files);

    void deleteReview(Long id, Member member);
}
