package com.medipass.allerpass.service;

import com.medipass.allerpass.entity.HospitalAdmin;
import com.medipass.allerpass.repository.HospitalAdminRepository;
import com.medipass.allerpass.repository.HospitalRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    private final HospitalAdminRepository hospitalAdminRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(HospitalAdminRepository hospitalAdminRepository, HospitalRepository hospitalRepository, PasswordEncoder passwordEncoder) {
        this.hospitalAdminRepository = hospitalAdminRepository;
        this.hospitalRepository = hospitalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean login(String yadmNm, String randomPassword){
        //병원 이름으로 관리자 조회
        Optional<HospitalAdmin> adminOpt = hospitalAdminRepository.findByHospital_HospitalName(yadmNm);

        //예외처리(병원정보가 없거나 비밀번호 불일치)
        if (adminOpt.isEmpty()){
            return false;
        }

        HospitalAdmin hospitalAdmin = adminOpt.get();
        return passwordEncoder.matches(randomPassword, hospitalAdmin.getTemporaryPassword());

    }


}
