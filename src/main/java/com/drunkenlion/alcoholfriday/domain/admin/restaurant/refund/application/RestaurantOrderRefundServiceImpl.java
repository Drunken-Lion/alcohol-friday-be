package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantInfoRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantOrderRefundDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantOrderRefundResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantOrderRefundServiceImpl implements RestaurantOrderRefundService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantOrderRefundRepository restaurantOrderRefundRepository;
    private final RestaurantOrderRefundDetailRepository restaurantOrderRefundDetailRepository;
    private final FileService fileService;

    @Override
    public Page<RestaurantOrderRefundResponse> getRestaurantOrderRefunds(RestaurantInfoRequest request, int page, int size) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT)
                        .build());

        Pageable pageable = PageRequest.of(page, size);

        Page<RestaurantOrderRefund> refundPage = restaurantOrderRefundRepository.findByRestaurant(restaurant, pageable);

        List<RestaurantOrderRefundResponse> refundResponses = refundPage.getContent().stream()
                .map(refund -> {
                    List<RestaurantOrderRefundDetail> refundDetails = restaurantOrderRefundDetailRepository.findByRestaurantOrderRefund(refund);
                    List<RestaurantOrderRefundDetailResponse> refundDetailResponses = refundDetails.stream()
                            .map(refundDetail -> {
                                NcpFileResponse file = fileService.findOne(refundDetail.getProduct());
                                return RestaurantOrderRefundDetailResponse.of(refundDetail, file);
                            })
                            .collect(Collectors.toList());

                    return RestaurantOrderRefundResponse.of(refund, refundDetailResponses);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(refundResponses, pageable, refundPage.getTotalElements());
    }
}
