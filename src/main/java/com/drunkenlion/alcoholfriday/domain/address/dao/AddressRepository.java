package com.drunkenlion.alcoholfriday.domain.address.dao;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByMemberIdOrderByIsPrimaryDescCreatedAtDesc(Long memberId);

    List<Address> findAllByMemberId(Long memberId);

    Optional<Address> findFirstByIdNotOrderByCreatedAtDesc(Long addressId);

    Optional<Address> findByMemberAndIsPrimaryIsTrue(Member member);
}
