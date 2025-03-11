package com.medipass.allerpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequestDTO {
    @Schema(description = "병원 이름", example = "가톨릭대학교인천성모병원")
    private  String yadmNm;
    @Schema(description = "비밀번호", example = "Ly&9HK")
    private  String randomPassword;

}
