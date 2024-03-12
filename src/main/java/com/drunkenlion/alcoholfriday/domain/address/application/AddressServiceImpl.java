package com.drunkenlion.alcoholfriday.domain.address.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressCreateRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressModifyRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Transactional
    @Override
    public AddressResponse createAddress(Member member, AddressCreateRequest createRequest) {
        List<Address> addresses = addressRepository.findAllByMemberId(member.getId());

        if (addresses.size() >= 3) {
            throw new BusinessException(HttpResponse.Fail.ADDRESS_LIMIT_OVER);
        }

        if (createRequest.getIsPrimary()) {
            addresses.forEach(address -> address.changePrimary(false));
        }

        Address address = AddressCreateRequest.toEntity(createRequest, member);

        return AddressResponse.of(addressRepository.save(address));
    }

    @Override
    public AddressResponse getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ADDRESS)
                        .build());

        return AddressResponse.of(address);
    }

    @Transactional
    @Override
    public AddressResponse modifyAddress(Long addressId, Long memberId, AddressModifyRequest modifyRequest) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ADDRESS)
                        .build());

        if (!address.getMember().getId().equals(memberId)) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }

        if (modifyRequest.getIsPrimary()) {
            List<Address> addresses = addressRepository.findAllByMemberId(memberId);
            addresses.forEach(addr -> addr.changePrimary(false));
        }

        address.updateAddress(modifyRequest);

        return AddressResponse.of(addressRepository.save(address));
    }
}
