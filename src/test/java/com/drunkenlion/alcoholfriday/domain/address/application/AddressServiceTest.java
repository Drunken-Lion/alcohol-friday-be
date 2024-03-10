package com.drunkenlion.alcoholfriday.domain.address.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressCreateRequest;
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
import static org.mockito.Mockito.when;

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
    private final String address = "서울시 마포구 연남동";
    private final String addressDetail = "123-12번지";
    private final Long postcode = 123123L;
    private final String recipient = "테스트";
    private final Long recipientPhone = 1012345678L;
    private final String request = "부재시 연락주세요.";
    private final Boolean isPrimary = true;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = null;
    private final LocalDateTime deletedAt = null;

    @Test
    @DisplayName("주소 등록")
    public void createAddressTest() {
        // given
        AddressCreateRequest createRequest = AddressCreateRequest.builder()
                .recipient(recipient)
                .phone(recipientPhone)
                .request(request)
                .address(address)
                .detail(addressDetail)
                .postcode(postcode)
                .isPrimary(isPrimary)
                .build();

        when(addressRepository.save(any(Address.class))).thenReturn(this.getAddressData());

        // when
        AddressResponse addressResponse = addressService.createAddress(this.getMemberData(), createRequest);

        // then
        assertThat(addressResponse.getId()).isEqualTo(addressId);
        assertThat(addressResponse.getIsPrimary()).isEqualTo(isPrimary);
        assertThat(addressResponse.getAddress()).isEqualTo(address);
        assertThat(addressResponse.getAddressDetail()).isEqualTo(addressDetail);
        assertThat(addressResponse.getPostcode()).isEqualTo(postcode);
        assertThat(addressResponse.getRecipient()).isEqualTo(recipient);
        assertThat(addressResponse.getPhone()).isEqualTo(recipientPhone);
        assertThat(addressResponse.getRequest()).isEqualTo(request);

    }

    private Optional<Member> getMemberOne() {
        return Optional.of(this.getMemberData());
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
                .isPrimary(isPrimary)
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
