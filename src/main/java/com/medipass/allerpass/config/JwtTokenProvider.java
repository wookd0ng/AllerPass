package com.medipass.allerpass.config;

import com.medipass.allerpass.entity.HospitalAdmin;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private final Key key;  // JWT 서명용 Key

    @Value("${jwt.access-token-expiration-minutes}")
    private long accessTokenMinutes;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenMinutes;

    // application.yml 에서 secretKey 주입받아서 Key 생성
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            RedisTemplate<String, String> redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);  // Base64로 디코딩
        this.key = Keys.hmacShaKeyFor(keyBytes);  // HMAC-SHA256 방식으로 키 생성
        this.redisTemplate = redisTemplate;
        log.info("JWT Secret Key Initialized Successfully");
    }

    // AccessToken 생성 (유효시간 Duration으로 외부 주입)
    public String generateAccessToken(HospitalAdmin hospitalAdmin, Duration expiry) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + accessTokenMinutes * 60 * 1000);  // Duration -> ms 변환
        return makeToken(now, expiredAt, hospitalAdmin);  // 공통 토큰 생성 메서드 호출
    }

    // RefreshToken 생성 (Redis에 저장)
    public String generateRefreshToken(HospitalAdmin hospitalAdmin, Duration expiry) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + refreshTokenMinutes * 24 * 60 * 60 * 1000);
        String refreshToken = Jwts.builder()
                .setSubject(hospitalAdmin.getHospital().getHospitalName())  // 병원 이름 저장
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Redis에 저장 (key = RT:{병원이름}, value = 토큰)
        redisTemplate.opsForValue().set(
                "RT:" + hospitalAdmin.getHospital().getHospitalName(),
                refreshToken,
                expiry.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    // JWT 생성 공통 로직 (Access, Refresh 모두 사용 가능)
    private String makeToken(Date now, Date expiredAt, HospitalAdmin hospitalAdmin) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 타입 명시
                .setIssuer("AllerPass")  // 발급자
                .setIssuedAt(now)  // 발급 시간
                .setExpiration(expiredAt)  // 만료 시간
                .setSubject(hospitalAdmin.getHospital().getHospitalName())  // 주제 (병원이름)
                .claim("hospital_name", hospitalAdmin.getHospital().getHospitalName())  // 병원 이름 claim
                .claim("hospital_verified", hospitalAdmin.isHospitalVerified())  // 병원 인증 여부 claim
                .signWith(key, SignatureAlgorithm.HS256)  // 서명
                .compact();
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)  // 서명 키 설정
                    .build()
                    .parseClaimsJws(token);  // 검증 및 파싱
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT 만료됨: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
        }
        return false;
    }

    // JWT에서 Claims (데이터) 꺼내기
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();  // Payload(Claims) 부분 반환
    }

    // JWT 토큰에서 인증 정보 추출 (Spring Security 연동용)
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);  // 토큰에서 클레임 꺼냄
        String hospitalName = claims.get("hospital_name", String.class);  // 병원이름 추출
        UserDetails userDetails = new User(hospitalName, "", List.of());  // Spring Security UserDetails 생성
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Redis에서 RefreshToken 조회
    public String getRefreshToken(String hospitalName) {
        return redisTemplate.opsForValue().get("RT:" + hospitalName);
    }

    // Redis에서 RefreshToken 삭제 (로그아웃 처리용)
    public void deleteRefreshToken(String hospitalName) {
        redisTemplate.delete("RT:" + hospitalName);
    }
}