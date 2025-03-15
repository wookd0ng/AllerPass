package com.medipass.allerpass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {
    private String accessTocken;
    private String refreshToken;
}
