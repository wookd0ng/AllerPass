package com.medipass.allerpass.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginFormDto {
    //병원 회원가입 화면에서 넘어오는 가입정보
    //아마 병원코드, 관리자 이름, 비밀번호, 비밀번호 재확인
    private String hospitalCode; // 병원 코드
    private String hospitalTel; // 병원 전화번호 추가
    private String email; // 이메일
    private String adminName; // 관리자 이름
    private String password; // 설정할 비밀번호
    private String confirmPassword; // 비밀번호 재확인
}
