package com.drunkenlion.alcoholfriday.global.User;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.security.auth.UserDetailsServiceImpl;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDateTime;

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        Member member = Member.builder()
                .email(annotation.email())
                .provider(ProviderType.KAKAO)
                .name("테스트")
                .nickname("test")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        memberRepository.save(member);

        UserPrincipal userPrincipal = UserPrincipal.create(member);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities()
                );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
