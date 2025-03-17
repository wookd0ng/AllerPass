package com.medipass.allerpass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String message;
    private String accessToken;
    private String refreshToken;
}
