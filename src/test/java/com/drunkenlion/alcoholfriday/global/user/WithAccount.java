package com.drunkenlion.alcoholfriday.global.user;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFactory.class)
public @interface WithAccount {
    String email() default "test@example.com";
    ProviderType provider() default ProviderType.KAKAO;
    MemberRole role() default MemberRole.MEMBER;
}
