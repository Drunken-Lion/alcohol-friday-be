package com.drunkenlion.alcoholfriday.domain.maker.dao;

import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MakerRepository extends JpaRepository<Maker, Long> {
}
