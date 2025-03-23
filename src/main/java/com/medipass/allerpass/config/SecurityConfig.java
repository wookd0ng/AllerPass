package com.medipass.allerpass.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        List<String> permitUrls = List.of(
                "/",
                "/api/hospital/signup",
                "/api/hospital/login",
                "/api/hospital/login"
        );
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ 모든 요청 허용 (로그인 없이 접근 가능)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,permitUrls),UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화
                .formLogin(form -> form.disable()) // ✅ 로그인 폼 비활성화
                .logout(logout -> logout.disable()); // ✅ 로그아웃 기능 비활성화

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}