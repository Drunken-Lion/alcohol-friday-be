package com.drunkenlion.alcoholfriday.global.user;

import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.security.auth.UserDetailsServiceImpl;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        Member member = memberRepository.findByEmail(annotation.email())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(annotation.email())
                        .provider(annotation.provider())
                        .name("테스트")
                        .nickname("test")
                        .role(annotation.role())
                        .phone(1012345678L)
                        .certifyAt(null)
                        .agreedToServiceUse(true)
                        .agreedToServicePolicy(true)
                        .agreedToServicePolicyUse(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .deletedAt(null)
                        .build()));

        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(member.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
