package com.drunkenlion.alcoholfriday.domain.address.dao;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByMemberIdOrderByIsPrimaryDescCreatedAtDesc(Long memberId);

    List<Address> findAllByMemberId(Long memberId);
}
