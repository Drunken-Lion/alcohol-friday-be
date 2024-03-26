package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application.AdminRestaurantService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantAdminDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/restaurants")
@Tag(name = "v1-admin-restaurant", description = "관리자 매장 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminRestaurantController {
    private final AdminRestaurantService adminRestaurantService;

    @Operation(summary = "전체 매장 조회", description = "관리자 권한에 대한 전체 매장 조회")
    @GetMapping
    public ResponseEntity<PageResponse<RestaurantListResponse>> getRestaurants(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<RestaurantListResponse> pageResponse = PageResponse.of(this.adminRestaurantService.getRestaurants(userPrincipal.getMember(), page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 상세 조회", description = "관리자 권한에 대한 매장 상세 조회")
    @GetMapping("{id}")
    public ResponseEntity<RestaurantAdminDetailResponse> getRestaurant(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id
    ) {
        RestaurantAdminDetailResponse restaurantDetailResponse = adminRestaurantService.getRestaurant(userPrincipal.getMember(), id);
        return ResponseEntity.ok().body(restaurantDetailResponse);
    }

    @Operation(summary = "매장 등록", description = "관리자 권한에 대한 매장 등록")
    @PostMapping
    public ResponseEntity<RestaurantAdminDetailResponse> createRestaurant(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody RestaurantRequest restaurantRequest
    ) {
        RestaurantAdminDetailResponse restaurantDetailResponse = adminRestaurantService.createRestaurant(userPrincipal.getMember(), restaurantRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(restaurantDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(restaurantDetailResponse);
    }

    @Operation(summary = "매장 수정", description = "관리자 권한에 대한 매장 수정")
    @PutMapping("{id}")
    public ResponseEntity<RestaurantAdminDetailResponse> modifyRestaurant(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id,
            @Valid @RequestBody RestaurantRequest restaurantRequest
    ) {
        RestaurantAdminDetailResponse restaurantDetailResponse = adminRestaurantService.modifyRestaurant(userPrincipal.getMember(), id, restaurantRequest);
        return ResponseEntity.ok().body(restaurantDetailResponse);
    }

    @Operation(summary = "매장 삭제", description = "관리자 권한에 대한 매장 삭제")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id
    ) {
        adminRestaurantService.deleteRestaurant(userPrincipal.getMember(), id);
        return ResponseEntity.noContent().build();
    }
}
