package com.drunkenlion.alcoholfriday.domain.address.dao;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
