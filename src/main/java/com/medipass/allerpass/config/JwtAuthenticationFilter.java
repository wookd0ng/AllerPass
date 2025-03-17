package com.medipass.allerpass.config;

import com.medipass.allerpass.entity.Hospital;
import com.medipass.allerpass.entity.HospitalAdmin;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1️⃣ 요청 헤더에서 JWT 가져오기
        String token = resolveToken(request);

        // 2️⃣ 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.getClaims(token);
            String hospitalName = claims.getSubject();
            boolean isVerified = claims.get("hospital_verified", Boolean.class);

            HospitalAdmin hospitalAdmin = new HospitalAdmin();
            Hospital hospital = new Hospital();
            hospital.setHospitalName(hospitalName);
            hospitalAdmin.setHospitalVerified(isVerified);

            // 3️⃣ 인증 객체 생성 후 Security Context에 저장
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(hospitalAdmin, token, Collections.singletonList(new SimpleGrantedAuthority("ROLE_HOSPITAL_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 4️⃣ 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}