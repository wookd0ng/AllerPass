package com.medipass.allerpass.service;

import com.medipass.allerpass.config.JwtTokenProvider;
import com.medipass.allerpass.dto.LoginResponseDto;
import com.medipass.allerpass.entity.HospitalAdmin;
import com.medipass.allerpass.repository.HospitalAdminRepository;
import com.medipass.allerpass.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final HospitalAdminRepository hospitalAdminRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String,Object> redisTemplate;


    public LoginResponseDto login(String yadmNm, String randomPassword) {
        Optional<HospitalAdmin> adminOpt = hospitalAdminRepository.findByHospital_HospitalName(yadmNm);

        if (adminOpt.isEmpty()) {
            return new LoginResponseDto("로그인 실패: 병원 정보 없음", null, null);
        }

        HospitalAdmin hospitalAdmin = adminOpt.get();
        if (!passwordEncoder.matches(randomPassword, hospitalAdmin.getTemporaryPassword())) {
            return new LoginResponseDto("로그인 실패: 비밀번호 불일치", null, null);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(hospitalAdmin, Duration.ofMinutes(30));
        String refreshToken = jwtTokenProvider.generateRefreshToken(hospitalAdmin, Duration.ofDays(100));

        // ✅ Redis에 저장
        redisTemplate.opsForValue().set("ACCESS_TOKEN:" + yadmNm, accessToken, Duration.ofMinutes(30));
        redisTemplate.opsForValue().set("REFRESH_TOKEN:" + yadmNm, refreshToken, Duration.ofDays(100));

        return new LoginResponseDto("로그인 성공", accessToken, refreshToken);
    }

}
