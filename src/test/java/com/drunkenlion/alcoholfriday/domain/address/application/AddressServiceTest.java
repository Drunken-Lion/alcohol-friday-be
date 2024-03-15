package com.drunkenlion.alcoholfriday.domain.address.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressCreateRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressModifyRequest;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AddressServiceTest {
    @InjectMocks
    private AddressServiceImpl addressService;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private MemberRepository memberRepository;

    private final Long memberId = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = MemberRole.MEMBER.getRole();
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = null;
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;

    private final Long addressId = 1L;
    private final String address = "서울특별시 마포구 연남동";
    private final String addressDetail = "123-12번지";
    private final String postcode = "123123";
    private final String recipient = "테스트";
    private final Long recipientPhone = 1012345678L;
    private final String request = "부재시 연락주세요.";
    private final Boolean isPrimaryTrue = true;
    private final Boolean isPrimaryFalse = false;

    private final String modifyAddress = "서울특별시 마포구 합정동";
    private final String modifyAddressDetail = "456-45번지";
    private final String modifyPostcode = "456456";
    private final String modifyRecipient = "테스트유저456";
    private final Long modifyRecipientPhone = 1087654321L;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = null;
    private final LocalDateTime deletedAt = null;

    @Test
    @DisplayName("배송지 등록")
    public void createAddressTest() {
        // given
        AddressCreateRequest createRequest = AddressCreateRequest.builder()
                .recipient(recipient)
                .phone(recipientPhone)
                .request(request)
                .address(address)
                .detail(addressDetail)
                .postcode(postcode)
                .isPrimary(isPrimaryTrue)
                .build();

        when(addressRepository.save(any(Address.class))).thenReturn(this.getAddressData());

        // when
        AddressResponse addressResponse = addressService.createAddress(this.getMemberData(), createRequest);

        // then
        assertThat(addressResponse.getId()).isEqualTo(addressId);
        assertThat(addressResponse.getIsPrimary()).isEqualTo(isPrimaryTrue);
        assertThat(addressResponse.getAddress()).isEqualTo(address);
        assertThat(addressResponse.getAddressDetail()).isEqualTo(addressDetail);
        assertThat(addressResponse.getPostcode()).isEqualTo(postcode);
        assertThat(addressResponse.getRecipient()).isEqualTo(recipient);
        assertThat(addressResponse.getPhone()).isEqualTo(recipientPhone);
        assertThat(addressResponse.getRequest()).isEqualTo(request);

    }

    @Test
    @DisplayName("배송지 수정")
    public void modifyAddressTest() {
        // given
        AddressModifyRequest modifyRequest = AddressModifyRequest.builder()
                .address(modifyAddress)
                .addressDetail(modifyAddressDetail)
                .postcode(modifyPostcode)
                .recipient(modifyRecipient)
                .phone(modifyRecipientPhone)
                .request(request)
                .isPrimary(isPrimaryTrue)
                .build();

        when(addressRepository.findById(any())).thenReturn(this.getAddressOne());
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        AddressResponse addressResponse = this.addressService.modifyAddress(addressId, this.getMemberData(), modifyRequest);

        // then
        assertThat(addressResponse.getId()).isEqualTo(addressId);
        assertThat(addressResponse.getIsPrimary()).isEqualTo(isPrimaryTrue);
        assertThat(addressResponse.getAddress()).isEqualTo(modifyAddress);
        assertThat(addressResponse.getAddressDetail()).isEqualTo(modifyAddressDetail);
        assertThat(addressResponse.getPostcode()).isEqualTo(modifyPostcode);
        assertThat(addressResponse.getRecipient()).isEqualTo(modifyRecipient);
        assertThat(addressResponse.getPhone()).isEqualTo(modifyRecipientPhone);
        assertThat(addressResponse.getRequest()).isEqualTo(request);
    }

    @Test
    @DisplayName("배송지 삭제 - 대표 배송지 삭제 시, 등록된 배송지가 해당 배송지 하나일 경우")
    public void successDeleteSingleAddressWhenPrimaryTest() {
        // given
        when(addressRepository.findById(addressId)).thenReturn(this.getAddressOne());
        when(addressRepository.findFirstByIdNotOrderByCreatedAtDesc(addressId)).thenReturn(Optional.empty());
        doNothing().when(addressRepository).deleteById(addressId);

        // when
        addressService.deleteAddress(addressId, this.getMemberData());

        // then
        verify(addressRepository).findById(addressId);
        verify(addressRepository).findFirstByIdNotOrderByCreatedAtDesc(addressId);
        verify(addressRepository).deleteById(addressId);
    }

    @Test
    @DisplayName("배송지 삭제 - 대표 배송지 삭제 시, 해당 배송지 외에 다른 배송지가 존재할 경우")
    public void successDeletePrimaryAddressWhenOtherExistsTest() {
        // given
        when(addressRepository.findById(addressId)).thenReturn(this.getAddressOne());
        when(addressRepository.findFirstByIdNotOrderByCreatedAtDesc(addressId)).thenReturn(Optional.of(this.getAddressData2()));
        doNothing().when(addressRepository).deleteById(addressId);

        // when
        addressService.deleteAddress(addressId, this.getMemberData());

        // then
        verify(addressRepository).findById(addressId);
        verify(addressRepository).findFirstByIdNotOrderByCreatedAtDesc(addressId);
        verify(addressRepository).deleteById(addressId);
    }

    @Test
    @DisplayName("배송지 삭제 - 대표 배송지가 아닌 배송지를 삭제할 경우")
    public void successDeleteNonPrimaryAddressTest() {
        // given
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(this.getAddressData2()));
        doNothing().when(addressRepository).deleteById(addressId);

        // when
        addressService.deleteAddress(addressId, this.getMemberData());

        // then
        verify(addressRepository).findById(addressId);
        verify(addressRepository, never()).findFirstByIdNotOrderByCreatedAtDesc(addressId);
        verify(addressRepository).deleteById(addressId);
    }

    private Optional<Member> getMemberOne() {
        return Optional.of(this.getMemberData());
    }

    private Optional<Address> getAddressOne() {
        return Optional.of(this.getAddressData());
    }

    private Member getMemberData() {
        return Member.builder()
                .id(memberId)
                .email(email)
                .provider(ProviderType.byProviderName(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.byRole(role))
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    private Address getAddressData() {
        return Address.builder()
                .id(addressId)
                .member(this.getMemberData())
                .isPrimary(isPrimaryTrue)
                .address(address)
                .addressDetail(addressDetail)
                .postcode(postcode)
                .recipient(recipient)
                .phone(recipientPhone)
                .request(request)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Address getAddressData2() {
        return Address.builder()
                .id(addressId)
                .member(this.getMemberData())
                .isPrimary(isPrimaryFalse)
                .address(address)
                .addressDetail(addressDetail)
                .postcode(postcode)
                .recipient(recipient)
                .phone(recipientPhone)
                .request(request)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
