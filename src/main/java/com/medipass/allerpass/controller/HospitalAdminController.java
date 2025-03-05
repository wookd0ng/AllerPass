package com.medipass.allerpass.controller;

import com.medipass.allerpass.service.PublicApiService;
import com.medipass.allerpass.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Hospital Admin API", description = "병원 관리자 인증 및 회원가입 API")
@RestController
@RequestMapping("/api/hospital-admin")
@RequiredArgsConstructor
@Validated
public class HospitalAdminController {

    private final PublicApiService publicApiService; // 공공 API 서비스

//    완전 간단한 병원인증 테스트 API
    @Operation(summary = "병원 코드만 테스트", description = "병원코드만 입력하여 API와의 인증 확인")
    @GetMapping("/just-test")
    public ResponseEntity<?> testHospitalCode(@RequestParam String dgsbjtCd	, @RequestParam String yadmNm){
        // ✅ Swagger 요청 로그 출력
        System.out.println("Swagger 요청: dgsbjtCd=" + dgsbjtCd + ", yadmNm=" + yadmNm);

        // ✅ 병원 정보 가져오기
        ResponseEntity<?> hospitalResponse = publicApiService.testHospitalCode(dgsbjtCd, yadmNm);
        System.out.println("🔍 hospitalResponse 상태 코드: " + hospitalResponse.getStatusCode());
        System.out.println("🔍 hospitalResponse 본문: " + hospitalResponse.getBody());

        // ✅ 병원 정보가 존재하면 그대로 반환
        if (hospitalResponse.getStatusCode().is2xxSuccessful()) {
            return hospitalResponse;
        } else {
            return ResponseEntity.badRequest().body("해당 병원이 확인되지 않습니다.");
        }
    }

//    /**
//     * ✅ 병원 코드 인증 API
//     * 사용자가 입력한 병원 코드와 전화번호를 공공 API를 통해 검증 후 인증 상태를 업데이트함.
//     */
//    @Operation(summary = "병원 코드 인증", description = "공공 API를 이용해 병원 코드와 전화번호를 검증 후 인증 상태를 저장합니다.")
//    @GetMapping("/verify-hospital")
//    public ResponseEntity<?> verifyHospital(
//            @RequestParam String email,
//            @RequestParam String hospitalCode,
//            @RequestParam String hospitalTel) {
//
//        // ✅ 공공 API를 통해 병원 코드 검증
//        boolean isValid = publicApiService.verifyHospital(hospitalCode, hospitalTel);
//        if (!isValid) {
//            return ResponseEntity.badRequest().body("유효하지 않은 병원 코드 또는 전화번호입니다.");
//        }
//
//        // ✅ 병원 인증 완료 상태 저장
//        hospitalAdminService.activeHospitalVerification(hospitalCode, email);
//
//        return ResponseEntity.ok("병원 코드 및 전화번호 인증 성공");
//    }

//    /**
//     * ✅ 이메일 인증 코드 발송 API
//     * 병원 코드가 인증된 후 이메일 인증 코드가 전송됨.
//     */
//    @Operation(summary = "이메일 인증 코드 발송", description = "이메일 인증을 위해 인증 코드를 전송합니다.")
//    @PostMapping("/send-email-verification")
//    public ResponseEntity<?> sendEmailVerification(@RequestParam String email, @RequestParam String hospitalCode) {
//        // ✅ 병원 코드가 인증되었는지 확인
//        boolean isHospitalVerified = hospitalAdminService.isHospitalVerified(hospitalCode, email);
//        if (!isHospitalVerified) {
//            return ResponseEntity.badRequest().body("병원 코드 인증을 먼저 완료해야 합니다.");
//        }
//
//        // ✅ 이메일 인증 코드 발송
//        String verificationCode = emailService.sendVerificationCode(email);
//
//        return ResponseEntity.ok("이메일 인증 코드가 전송되었습니다.");
//    }
//
//    /**
//     * ✅ 이메일 인증 코드 검증 API
//     * 사용자가 입력한 이메일 인증 코드를 검증 후 인증 상태를 업데이트함.
//     */
//    @Operation(summary = "이메일 인증 코드 검증", description = "사용자가 입력한 이메일 인증 코드가 올바른지 검증 후 인증 상태를 저장합니다.")
//    @PostMapping("/verify-email-code")
//    public ResponseEntity<?> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
//        boolean isValid = emailService.verifyCode(email, code);
//        if (!isValid) {
//            return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
//        }
//
//        return ResponseEntity.ok("이메일 인증 성공");
//    }

//    /**
//     * ✅ 회원가입 API
//     * 이메일 인증 및 병원 코드 인증이 완료된 사용자만 회원가입을 진행할 수 있음.
//     */
//    @Operation(summary = "병원 관리자 회원가입", description = "이메일 및 병원 코드 인증이 완료된 사용자의 회원가입을 처리합니다.")
//    @PostMapping("/signup")
//    public ResponseEntity<?> registerHospitalAdmin(@Valid @RequestBody LoginFormDto loginFormDto) {
//        try {
//            // 1. 병원 코드 인증 여부 확인
//            boolean isHospitalVerified = hospitalAdminService.isHospitalVerified(
//                    loginFormDto.getHospitalCode(), loginFormDto.getEmail()
//            );
//            if (!isHospitalVerified) {
//                return ResponseEntity.badRequest().body("병원 코드 인증을 먼저 완료해야 합니다.");
//            }
//
//            // 2. 이메일 인증 여부 확인
//            boolean isEmailVerified = emailService.isEmailVerified(loginFormDto.getEmail());
//            if (!isEmailVerified) {
//                return ResponseEntity.badRequest().body("이메일 인증을 먼저 완료해야 합니다.");
//            }
//
//            // 3. 회원가입 진행
//            hospitalAdminService.registerAdmin(
//                    loginFormDto.getHospitalCode(),
//                    loginFormDto.getAdminName(),
//                    loginFormDto.getEmail(),
//                    loginFormDto.getPassword(),
//                    Role.HOSPITAL_ADMIN
//            );
//
//            return ResponseEntity.ok("회원가입이 완료되었습니다.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
}