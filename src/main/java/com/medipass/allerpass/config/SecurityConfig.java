package com.medipass.allerpass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/hospital-admin/register", "/hospital-admin/verify-hospital", "/hospital-admin/send-email-verification", "/hospital-admin/verify-email-code").permitAll() // ✅ 인증 없이 접근 가능
                        .requestMatchers("/dashboard").authenticated() // ✅ 로그인한 사용자만 접근 가능
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login") // ✅ 로그인 페이지 설정
                        .defaultSuccessUrl("/dashboard", true) // ✅ 로그인 성공 후 이동할 페이지
                        .failureUrl("/login?error") // 로그인 실패 시 이동
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // ✅ CSRF 비활성화 (테스트용)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}