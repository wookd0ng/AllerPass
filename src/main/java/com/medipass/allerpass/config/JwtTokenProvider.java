package com.medipass.allerpass.config;

import com.medipass.allerpass.entity.HospitalAdmin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

// JWT 토큰 발급
@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    // application.properties에서 secret 값 가져와서 key에 저장함.
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey){
        byte[]keyBytes= Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT Secret Key Initialized Successfully");

    }

    // 다른 클래스에서도 쓸 수 있도록
    public Key getKey() {
        return key;
    }

    // AccessToken 생성
    public String generateAccessToken(HospitalAdmin hospitalAdmin, Duration expiry){
        Date now = new Date();
        Date expiredAt = new Date(now.getTime()+expiry.toMillis());
        return makeToken(now, expiredAt, hospitalAdmin);
    }

    // JWT 내부에 담을 정보 포함
    private String makeToken(Date now, Date expiredAt, HospitalAdmin hospitalAdmin){
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("AllerPass")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .setSubject(hospitalAdmin.getHospital().getHospitalName())
                .claim("hospital_name",hospitalAdmin.getHospital().getHospitalName())
                .claim("hospital_verified", hospitalAdmin.isHospitalVerified())
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(HospitalAdmin hospitalAdmin, Duration expiry){
        Date now = new Date();
        Date expiredAt = new Date(now.getTime()+expiry.toMillis());
        return Jwts.builder()
                .setSubject(hospitalAdmin.getHospital().getHospitalName())
                .setExpiration(expiredAt)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e){
            log.error("JWT 검증 실패 : {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
