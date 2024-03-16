package com.drunkenlion.alcoholfriday.domain.address.api;

import com.drunkenlion.alcoholfriday.domain.address.application.AddressService;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressCreateRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressModifyRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
@Tag(name = "v1-addresses", description = "배송지 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "배송지 등록", description = "회원의 배송지 정보 등록")
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @RequestBody AddressCreateRequest createRequest) {
        AddressResponse addressResponse = addressService.createAddress(userPrincipal.getMember(), createRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addressResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(addressResponse);
    }

    @Operation(summary = "배송지 조회", description = "회원의 배송지 단건 조회")
    @GetMapping("{id}")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable("id") Long addressId,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        AddressResponse addressResponse = addressService.getAddress(addressId, userPrincipal.getMember());
        return ResponseEntity.ok().body(addressResponse);
    }

    @Operation(summary = "배송지 수정", description = "회원의 배송지 정보 수정")
    @PutMapping("{id}")
    public ResponseEntity<AddressResponse> modifyAddress(@PathVariable("id") Long addressId,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @RequestBody AddressModifyRequest modifyRequest) {

        AddressResponse addressResponse = addressService.modifyAddress(
                addressId, userPrincipal.getMember(), modifyRequest);

        return ResponseEntity.ok().body(addressResponse);
    }

    @Operation(summary = "배송지 삭제", description = "회원의 배송지 삭제")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") Long addressId,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {

        addressService.deleteAddress(addressId, userPrincipal.getMember());

        return ResponseEntity.noContent().build();
    }
}
