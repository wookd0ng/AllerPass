package com.medipass.allerpass.controller;

import com.medipass.allerpass.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginService loginService;


    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @Operation(summary = "병원 로그인 API", description = "병원 이름과 비밀번호를 입력하여 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String yadMnM, @RequestParam String randomPassword) {
        boolean isVaild = loginService.login(yadMnM,randomPassword);
        if (isVaild){
            return ResponseEntity.ok(Map.of("message","로그인 성공!"));
        } else {
            return ResponseEntity.status(401).body(Map.of("Message","로그인 실패!"));
        }
    }
}

