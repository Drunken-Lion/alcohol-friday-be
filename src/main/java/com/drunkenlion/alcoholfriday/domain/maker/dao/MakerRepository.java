package com.drunkenlion.alcoholfriday.domain.maker.dao;

import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MakerRepository extends JpaRepository<Maker, Long> {
    Optional<Maker> findByIdAndDeletedAtIsNull(Long id);

    Optional<Maker> findByName(String name);
}
