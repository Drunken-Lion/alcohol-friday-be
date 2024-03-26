package com.drunkenlion.alcoholfriday.global.security.config;

import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer
                                .FrameOptionsConfig::sameOrigin
                        )
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        //관리자 - 공지사항 관리
                        .requestMatchers("/v1/admin/notices/**")
                        .hasRole(MemberRole.ADMIN.getRole())

                        // 관리자 - 회원 관리
                        .requestMatchers("/v1/admin/members/**")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.SUPER_VISOR.getRole())

                        // 관리자 - 고객센터 문의사항 답변
                        .requestMatchers(HttpMethod.POST, "/v1/admin/answers")
                        .hasAnyRole(
                                MemberRole.ADMIN.getRole(),
                                MemberRole.SUPER_VISOR.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/answers/**")
                        .hasAnyRole(
                                MemberRole.ADMIN.getRole(),
                                MemberRole.SUPER_VISOR.getRole())
                        .requestMatchers(HttpMethod.DELETE, "/v1/admin/answers/**")
                        .hasAnyRole(
                                MemberRole.ADMIN.getRole(),
                                MemberRole.SUPER_VISOR.getRole())

                        // 관리자 - 매장 발주 관리 (Owner)
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurant-orders/{id:\\d+}/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.POST, "/v1/admin/restaurant-orders/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurant-orders/{id:\\d+}/cancel/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurant-orders/products")
                        .hasRole(MemberRole.OWNER.getRole())

                        //관리자 - 매장 발주 환불 관리 (Owner)
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurant-order-refunds/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.POST, "/v1/admin/restaurant-order-refunds/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurant-order-refunds/{id:\\d+}/cancel/owner")
                        .hasRole(MemberRole.OWNER.getRole())

                        // 관리자 - 매장 발주 장바구니 관리 (Owner)
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurant-order-carts/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.POST, "/v1/admin/restaurant-order-carts/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurant-order-cart-details/{id:\\d+}/owner")
                        .hasRole(MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.DELETE, "/v1/admin/restaurant-order-cart-details/{id:\\d+}/owner")
                        .hasRole(MemberRole.OWNER.getRole())

                        // 관리자 - 매장 발주 관리 (Admin & StoreManager)
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurant-orders")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurant-orders/{id:\\d+}")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurant-orders/{id:\\d+}/reject")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())

                        //관리자 - 매장 발주 환불 관리 (Admin & StoreManager)
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurant-order-refunds")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurant-order-refunds/{id:\\d+}")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurant-order-refunds/{id:\\d+}/reject")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())

                        // 관리자 - 매장 재고 관리
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurants/{id:\\d+}/stocks")
                        .hasAnyRole(
                                MemberRole.ADMIN.getRole(),
                                MemberRole.STORE_MANAGER.getRole(),
                                MemberRole.OWNER.getRole())

                        // 관리자 - 매장 관리
                        .requestMatchers(HttpMethod.POST, "/v1/admin/restaurants")
                        .hasAnyRole(MemberRole.ADMIN.getRole())
                        .requestMatchers(HttpMethod.GET, "/v1/admin/restaurants/**")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/restaurants/**")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.OWNER.getRole())
                        .requestMatchers(HttpMethod.DELETE, "/v1/admin/restaurants/**")
                        .hasAnyRole(MemberRole.ADMIN.getRole())

                        // 관리자 - 제품 재고 관리
                        .requestMatchers(HttpMethod.GET, "/v1/admin/products/{id:\\d+}/stocks")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())
                        .requestMatchers(HttpMethod.PUT, "/v1/admin/products/{id:\\d+}/stocks")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())

                        // 관리자 - 제품, 상품, 제조사, 카테고리 관리
                        .requestMatchers(
                                "/v1/admin/items/**",
                                "/v1/admin/products/**",
                                "/v1/admin/makers/**",
                                "/v1/admin/category-classes/**",
                                "/v1/admin/categories/**",
                                "/v1/admin/orders/**")
                        .hasAnyRole(MemberRole.ADMIN.getRole(), MemberRole.STORE_MANAGER.getRole())

                        // 관리자
                        .requestMatchers("/v1/admin/**")
                        .hasAnyRole(
                                MemberRole.ADMIN.getRole(),
                                MemberRole.SUPER_VISOR.getRole(),
                                MemberRole.OWNER.getRole(),
                                MemberRole.STORE_MANAGER.getRole())

                        // 고객센터 - 질문
                        .requestMatchers(HttpMethod.POST, "/v1/questions").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/questions/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/questions/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/questions/**").authenticated()

                        .requestMatchers("/v1/members/me/**", "/v1/addresses/**", "/v1/orders/**",
                                "/v1/carts/**", "/v1/payments/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/v1/restaurants/**", "/v1/items/**", "/v1/notices/**", "/v1/products/**").permitAll()
                        .requestMatchers("/v1/auth/**", "/error", "/docs").permitAll()

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**"))
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**"))
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .requestMatchers(new AntPathRequestMatcher("/css/**"))
                .requestMatchers(new AntPathRequestMatcher("/js/**"))
                .requestMatchers(new AntPathRequestMatcher("/img/**"))
                .requestMatchers(new AntPathRequestMatcher("/lib/**"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
            Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
