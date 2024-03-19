package com.drunkenlion.alcoholfriday.domain.review.application;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.response.QuestionResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.dto.request.ReviewSaveRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewSaveResponse;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.domain.review.util.ReviewValidator;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final FileService fileService;

    /**
     * 리뷰 등록
     */
    @Override
    @Transactional
    public ReviewSaveResponse saveReview(ReviewSaveRequest request,
                                         List<MultipartFile> files,
                                         Member member) {
        log.info("[ReviewServiceImpl.saveReview] : 접근");
        OrderDetail orderDetail = orderDetailRepository.findById(request.getOrderDetailId())
                .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_ORDER_DETAIL));

        ReviewValidator.compareEntityIdToMemberId(orderDetail.getOrder(), member);
        ReviewValidator.checkedStatus(orderDetail.getOrder());

        Optional<Review> byOrderDetail = reviewRepository.findByOrderDetail(orderDetail);

        if (byOrderDetail.isPresent() && byOrderDetail.get().getDeletedAt() == null) {
            throw new BusinessException(Fail.EXIST_REVIEW);
        }

        Review review = ReviewSaveRequest.toEntity(request, member);
        review.addOrderDetail(orderDetail);
        review.addItem(orderDetail.getItem());
        reviewRepository.save(review);

        NcpFileResponse ncpFileResponse = fileService.saveFiles(review, files);
        return ReviewSaveResponse.of(review, ncpFileResponse);
    }

    /**
     * 리뷰 작성을 하지 않은 주문 내역 목록
     */
    @Override
    public Page<ReviewOrderDetailResponse> getUnwrittenReviews(Member member, int page, int size) {
        log.info("[ReviewServiceImpl.getReviews] : 접근");
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDetail> findAll = orderDetailRepository.findOrderDetailsMember(member, pageable);
        return findAll.map(
                orderDetail -> ReviewOrderDetailResponse.of(orderDetail, fileService.findOne(orderDetail.getItem())));
    }

    /**
     * 작성한 리뷰 목록
     */
    @Override
    public Page<ReviewResponse> getReviews(Member member, int page, int size) {
        log.info("[ReviewServiceImpl.getReviews] : 접근");
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> findAll = reviewRepository.findByMember(member, pageable);
        return findAll.map(review -> ReviewResponse.of(review, fileService.findOne(review)));
    }

    /**
     * 리뷰 수정
     */
    @Override
    @Transactional
    public ReviewModifyResponse updateReview(Long id, ReviewModifyRequest request, Member member,
                                             List<MultipartFile> files) {
        log.info("[ReviewServiceImpl.updateReview] : 접근");
        Review review = reviewRepository.findById(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_REVIEW));
        ReviewValidator.compareEntityIdToMemberId(review, member);
        review.updateReview(request.getUpdateScore(), request.getUpdateContent());
        reviewRepository.save(review);

        NcpFileResponse ncpFileResponse = fileService.updateFiles(review, request.getRemoveImageSeqList(), files);
        return ReviewModifyResponse.of(review, ncpFileResponse);
    }

    /**
     * 리뷰 삭제 (물리적 삭제)
     */
    @Override
    @Transactional
    public void deleteReview(Long id, Member member) {
        log.info("[ReviewServiceImpl.deleteReview] : 접근");
        Review review = reviewRepository.findById(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_REVIEW));
        ReviewValidator.compareEntityIdToMemberId(review, member);
        fileService.deleteFiles(review);
        reviewRepository.delete(review);
    }
}
