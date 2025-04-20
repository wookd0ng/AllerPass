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

@Configuration // 해당 클래스가 스프링 설정 클래스임을 나타냄
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
@RequiredArgsConstructor // final 필드를 가진 생성자를 자동으로 생성해줌 (DI 주입용)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 유효성 검사용 커스텀 컴포넌트 주입

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 로그인 없이 접근을 허용할 URL 리스트 (예: 회원가입, 로그인 API 등)
        List<String> permitUrls = List.of(
                "/", // 루트 경로
                "/api/hospital/signup", // 병원 회원가입 API
                "/api/hospital/login",  // 병원 로그인 API
                "/api/hospital/login"   // 중복이긴 한데, 문제는 없음
        );

        http
                // 요청 URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청을 인증 없이 허용 (현재 이 설정 때문에 JWT 인증이 무의미해짐)
                )
                // UsernamePasswordAuthenticationFilter 이전에 JWT 필터 삽입 (AccessToken 검증 필터)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, permitUrls),
                        UsernamePasswordAuthenticationFilter.class
                )
                // CSRF 보호 비활성화 (JWT 사용 시 기본적으로 비활성화함)
                .csrf(csrf -> csrf.disable())
                // 스프링 시큐리티 기본 로그인 폼 비활성화 (REST API에서는 필요 없음)
                .formLogin(form -> form.disable())
                // 스프링 시큐리티 기본 로그아웃 기능 비활성화 (필요 시 수동 구현)
                .logout(logout -> logout.disable());

        return http.build(); // 설정을 바탕으로 SecurityFilterChain 객체 생성
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 Bean (BCrypt 방식 사용)
    }
}