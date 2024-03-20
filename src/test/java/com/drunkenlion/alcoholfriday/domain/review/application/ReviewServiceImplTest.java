package com.drunkenlion.alcoholfriday.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.dto.request.ReviewSaveRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewSaveResponse;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("[ReviewServiceImplTest] 리뷰 Service Test")
class ReviewServiceImplTest {
    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private FileService fileService;

    @AfterEach
    @Transactional
    public void after() {
        reviewRepository.deleteAll();
        orderDetailRepository.deleteAll();
    }

    @Test
    @DisplayName("리뷰 저장")
    public void t1() {
        Long id = 1L;
        Double score = 4.0d;
        String reviewContent = "좋아요~!";
        Member member = Member.builder().id(id).build();
        List<MultipartFile> files = new ArrayList<>();

        ReviewSaveRequest request = ReviewSaveRequest.builder().orderDetailId(id).score(score).content(reviewContent)
                .build();

        Order order = Order.builder().id(id).member(member).orderStatus(OrderStatus.DELIVERED).build();
        OrderDetail orderDetail = OrderDetail.builder().id(id).order(order).build();

        Mockito.when(orderDetailRepository.findById(id)).thenReturn(
                Optional.ofNullable(orderDetail)
        );

        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewSaveResponse response = reviewService.saveReview(request, files, member);

        assertThat(response.getScore()).isEqualTo(score);
        assertThat(response.getContent()).isEqualTo(reviewContent);
        assertThat(response.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("리뷰 수정")
    public void t2() {
        Long id = 1L;
        Double score = 4.0d;
        String reviewContent = "좋아요~!";

        Double updateScore = 3.0d;
        String updateReviewContent = "수정 좋아요~!";
        List<Integer> removeSeq = List.of();

        ReviewModifyRequest request =
                ReviewModifyRequest.builder()
                        .updateScore(updateScore)
                        .updateContent(updateReviewContent)
                        .removeImageSeqList(removeSeq)
                        .build();

        Member member = Member.builder().id(id).build();
        List<MultipartFile> files = new ArrayList<>();

        Review review = Review.builder().id(id).score(score).content(reviewContent).member(member).build();

        Mockito.when(reviewRepository.findById(id)).thenReturn(
                Optional.ofNullable(review)
        );

        ReviewModifyResponse response = reviewService.updateReview(id, request, member, files);

        assertThat(response.getScore()).isEqualTo(updateScore);
        assertThat(response.getContent()).isEqualTo(updateReviewContent);
        assertThat(response.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("작성한 리뷰 조회")
    public void t3() {
        Long id = 1L;
        Double score = 4.0d;
        String reviewContent = "좋아요~!";
        Member member = Member.builder().id(id).build();

        Item item = Item.builder().id(id).name("테스트상품1").build();
        Order order = Order.builder().id(id).member(member).orderStatus(OrderStatus.DELIVERED).build();
        OrderDetail orderDetail = OrderDetail.builder().id(id).order(order).item(item).build();

        List<MultipartFile> files = new ArrayList<>();

        Review review = Review.builder().id(id).score(score).content(reviewContent).member(member).orderDetail(orderDetail).item(item).build();

        Mockito.when(reviewRepository.findByMember(eq(member), any(Pageable.class))).thenReturn(getReviews(review));


        Page<ReviewResponse> reviews = reviewService.getReviews(member, 0, 10);
        List<ReviewResponse> content = reviews.getContent();
        ReviewResponse response = content.get(0);

        assertThat(response.getScore()).isEqualTo(score);
        assertThat(response.getContent()).isEqualTo(reviewContent);
        assertThat(response.getOrderDetail().getOrderDetailId()).isEqualTo(id);
        assertThat(response.getOrderDetail().getItemName()).isEqualTo(item.getName());
    }

    @Test
    @DisplayName("작성하지 않은 리뷰 조회")
    public void t4() {
        Long id = 1L;
        Member member = Member.builder().id(id).build();

        Item item = Item.builder().id(id).name("테스트상품1").build();
        Order order = Order.builder().id(id).member(member).orderStatus(OrderStatus.DELIVERED).build();
        OrderDetail orderDetail = OrderDetail.builder().id(id).order(order).item(item).build();

        List<MultipartFile> files = new ArrayList<>();

        Mockito.when(orderDetailRepository.findOrderDetailsMember(eq(member), any(Pageable.class))).thenReturn(
                getOrderDetails(orderDetail)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDetail> orderDetailsMember = orderDetailRepository.findOrderDetailsMember(member, pageable);
        List<OrderDetail> content = orderDetailsMember.getContent();
        OrderDetail response = content.get(0);

        assertThat(content.size()).isEqualTo(1);
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getItem().getName()).isEqualTo(item.getName());
        assertThat(response.getOrder().getOrderStatus()).isEqualTo(order.getOrderStatus());
    }

    private Page<Review> getReviews(Review review) {
        List<Review> list = List.of(
                review
        );

        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<Review>(list, pageable, list.size());
    }

    private Page<OrderDetail> getOrderDetails(OrderDetail orderDetail) {
        List<OrderDetail> list = List.of(
                orderDetail
        );

        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<OrderDetail>(list, pageable, list.size());
    }
}