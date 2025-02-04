package com.medipass.allerpass.service;

import com.medipass.allerpass.constant.Role;
import com.medipass.allerpass.dto.LoginFormDto;
import com.medipass.allerpass.entity.Hospital;
import com.medipass.allerpass.entity.HospitalAdmin;
import com.medipass.allerpass.repository.HospitalAdminRepository;
import com.medipass.allerpass.repository.HospitalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class HospitalAdminServiceTest {

    @Autowired
    private HospitalAdminService hospitalAdminService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalAdminRepository hospitalAdminRepository; // 누락된 repository 추가
    @Autowired
    private PasswordEncoder passwordEncoder;
    public HospitalAdmin createHospitalAdmin() {
        LoginFormDto loginFormDto = new LoginFormDto();
        loginFormDto.setHospitalCode("ABCDE");
        loginFormDto.setAdminName("홍길동");

        // 비밀번호 암호화 적용
        String rawPassword = "1234";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        //병원 엔티티를 먼저 저장
        Hospital hospital = new Hospital();
        hospital.setHospitalCode("ABCDE");
        hospital.setHospitalName("테스트병원");
        hospitalRepository.save(hospital);

        HospitalAdmin hospitalAdmin = new HospitalAdmin(
                hospital,
                loginFormDto.getAdminName(),
                "admin@hospital.com",
                encodedPassword,
                Role.HOSPITAL_ADMIN
        );
        hospitalAdminRepository.save(hospitalAdmin);
        return hospitalAdmin;
    }

    @Test
    void 병원_관리자_회원가입_테스트() {
        // Given
        HospitalAdmin hospitalAdmin = createHospitalAdmin();

        // When
        HospitalAdmin savedAdmin = hospitalAdminRepository.findByEmail("admin@hospital.com").orElse(null);

        // Then
        assertNotNull(savedAdmin, "병원 관리자가 저장되지 않았습니다.");
        assertEquals("홍길동", savedAdmin.getAdminName(), "관리자 이름이 일치하지 않습니다.");
        assertTrue(passwordEncoder.matches("1234", savedAdmin.getPassword()), "비밀번호가 암호화되지 않았습니다.");
    }
}