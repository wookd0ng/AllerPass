package com.medipass.allerpass.config;

import com.medipass.allerpass.entity.Hospital;
import com.medipass.allerpass.entity.HospitalAdmin;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 요청에서 전달된 토큰 가져오기
        String token = (String) authentication.getCredentials();

        // 토큰이 유효한지 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AuthenticationException("Invalid JWT token") {};
        }

        // 토큰에서 클레임(정보) 추출
        Claims claims = jwtTokenProvider.getClaims(token);
        String hospitalName = claims.getSubject(); // 병원 이름 (JWT subject에 저장됨)
        boolean isVerified = claims.get("hospital_verified", Boolean.class); // 검증 여부 가져오기

        // HospitalAdmin 객체 생성 (실제 엔티티가 필요하면 DB에서 조회 가능)
        HospitalAdmin hospitalAdmin = new HospitalAdmin();
        Hospital hospital = new Hospital();
        hospital.setHospitalName(hospitalName);
        hospitalAdmin.setHospital(hospital);
        hospitalAdmin.setHospitalVerified(isVerified);

        // 인증된 객체 반환 (Spring Security 인증 처리)
        return new UsernamePasswordAuthenticationToken(
                hospitalAdmin, token, Collections.singletonList(new SimpleGrantedAuthority("ROLE_HOSPITAL_ADMIN"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}