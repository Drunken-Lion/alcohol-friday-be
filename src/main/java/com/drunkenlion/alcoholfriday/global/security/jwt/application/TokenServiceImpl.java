package com.drunkenlion.alcoholfriday.global.security.jwt.application;

import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.security.jwt.dao.RefreshTokenRepository;
import com.drunkenlion.alcoholfriday.global.security.jwt.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RefreshToken createRefreshToken(String token, Date expiryDate, Authentication authentication) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiryDate(expiryDate.toInstant())
                .member(getMember(authentication.getName()))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.INVALID_TOKEN));
    }

    @Transactional
    public void deleteRefreshToken(String email) {
        Member member = getMember(email);

        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresent(refreshTokenRepository::delete);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다."));
    }
}
