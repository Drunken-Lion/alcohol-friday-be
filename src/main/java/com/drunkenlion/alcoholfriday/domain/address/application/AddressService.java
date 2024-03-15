package com.drunkenlion.alcoholfriday.domain.address.application;

import com.drunkenlion.alcoholfriday.domain.address.dto.AddressCreateRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressModifyRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface AddressService {
    AddressResponse createAddress(Member member, AddressCreateRequest createRequest);

    AddressResponse getAddress(Long addressId, Member member);

    AddressResponse modifyAddress(Long addressId, Member member, AddressModifyRequest modifyRequest);

    void deleteAddress(Long addressId, Member member);
}
