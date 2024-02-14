package com.drunkenlion.alcoholfriday.global.security.jwt.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.drunkenlion.alcoholfriday.global.security.jwt.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String refreshToken);

    Optional<RefreshToken> findByMemberId(Long memberId);
}
