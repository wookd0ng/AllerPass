package com.medipass.allerpass.controller;

import com.medipass.allerpass.dto.LoginFormDto;
import com.medipass.allerpass.service.HospitalAdminService;
import com.medipass.allerpass.service.PublicApiService;
import com.medipass.allerpass.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Hospital Api", description = "병원 인증 관련 API")
@RestController
@RequestMapping("/api/hospital-admin")
@RequiredArgsConstructor
@Validated
public class HospitalAdminController {

    private final HospitalAdminService hospitalAdminService;
    private final PublicApiService publicApiService; // 공공 API 호출 서비스
    private final EmailService emailService; // 이메일 인증 서비스
    private final PasswordEncoder passwordEncoder;



    /**
     * 병원 관리자 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerHospitalAdmin(@Valid @RequestBody LoginFormDto loginFormDto) {
        try {
            // 1. 병원 코드, 전화 번호 검증
            boolean isValidHospital = publicApiService.verifyHospital(loginFormDto.getHospitalCode(), loginFormDto.getHospitalTel());
            if (!isValidHospital) {
                return ResponseEntity.badRequest().body("유효하지 않은 병원 코드입니다.");
            }

            // 2. 이메일 인증 검증
            boolean isEmailVerified = emailService.isEmailVerified(loginFormDto.getEmail());
            if (!isEmailVerified) {
                return ResponseEntity.badRequest().body("이메일 인증이 완료되지 않았습니다.");
            }

            // 3. 비밀번호 확인 검증
            if (!loginFormDto.getPassword().equals(loginFormDto.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
            }

            // 4. 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(loginFormDto.getPassword());

            // 5. 병원 관리자 저장
            hospitalAdminService.saveMember(
                    loginFormDto.getHospitalCode(),
                    loginFormDto.getAdminName(),
                    loginFormDto.getEmail(),
                    encodedPassword
            );



        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return null;
    }

    /**
     * 병원 코드 인증 API (공공 API 연동)
     */
    @Operation(summary = "병원 코드 인증", description = "공공 API를 이용해 병원 코드와 전화번호를 검증합니다.")

    @GetMapping("/verify-hospital")
    public ResponseEntity<?> verifyHospital(@RequestParam String hospitalCode, @RequestParam String hospitalTel) {
        boolean isValid = publicApiService.verifyHospital(hospitalCode, hospitalTel);
        if (!isValid) {
            return ResponseEntity.badRequest().body("유효하지 않은 병원 코드 또는 전화번호입니다.");
        }
        return ResponseEntity.ok("병원 코드 및 전화번호 인증 성공");
    }

    /**
     * 이메일 인증 코드 발송 API
     */
    @PostMapping("/send-email-verification")
    public ResponseEntity<?> sendEmailVerification(@RequestParam String email) {
        String verificationCode = emailService.sendVerificationCode(email);
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    /**
     * 이메일 인증 코드 검증 API
     */
    @PostMapping("/verify-email-code")
    public ResponseEntity<?> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = emailService.verifyCode(email, code);
        if (!isValid) {
            return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
        }
        return ResponseEntity.ok("이메일 인증 성공");
    }


}

