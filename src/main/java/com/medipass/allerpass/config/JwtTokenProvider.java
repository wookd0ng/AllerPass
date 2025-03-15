package com.medipass.allerpass.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

//    application.properties에서 secret 값 가져와서 key에 저장함.
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey){
        byte[]keyBytes= Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


}
