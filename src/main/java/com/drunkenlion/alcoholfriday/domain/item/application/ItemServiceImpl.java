package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.ItemRating;
import com.drunkenlion.alcoholfriday.domain.item.dto.ItemReviewResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final FileService fileService;

    @Override
    public Page<SearchItemResponse> search(int page, Integer size, String keyword, List<String> categories) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> search = this.itemRepository.search(categories, keyword, pageable);

        List<Item> searchItems = search.getContent();
        List<NcpFileResponse> files = searchItems.stream()
                .map(this.fileService::findAll)
                .toList();

        List<ItemRating> itemRatingList = searchItems.stream()
                .map(this::itemRating)
                .toList();

        return SearchItemResponse.of(search, files, itemRatingList);
    }

    @Override
    public FindItemResponse get(Long id) {
        Item item = this.itemRepository.get(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND)
                        .build());

        NcpFileResponse file = this.fileService.findOne(item);

        // 리뷰 평점
        ItemRating itemRating = itemRating(item);

        return FindItemResponse.of(item, file, itemRating);
    }

    // 리뷰 평점과 리뷰 개수 데이터
    private ItemRating itemRating(Item item) {
        List<Review> itemTotalReview = reviewRepository.findAllByItemIdAndDeletedAtIsNull(item.getId());

        if (itemTotalReview.isEmpty())
            return null;

        double totalScore = itemTotalReview.stream()
                .mapToDouble(Review::getScore)
                .sum();
        Double averageScore = totalScore / itemTotalReview.size();

        return ItemRating.builder()
                .itemId(item.getId())
                .avgItemScore(averageScore)
                .totalReviewCount(itemTotalReview.size())
                .build();
    }

    @Override
    public Page<ItemReviewResponse> getReviews(Long id, int page, int size) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_ITEM));
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findItemDetailReview(item, pageable);
        return reviews.map(review -> ItemReviewResponse.of(review, fileService.findAll(review)));
    }
}
