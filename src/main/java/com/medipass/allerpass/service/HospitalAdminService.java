package com.medipass.allerpass.service;

import com.medipass.allerpass.constant.Role;
import com.medipass.allerpass.entity.Hospital;
import com.medipass.allerpass.entity.HospitalAdmin;
import com.medipass.allerpass.repository.HospitalAdminRepository;
import com.medipass.allerpass.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalAdminService {

    private final HospitalAdminRepository hospitalAdminRepository;

    // 병원 인증 완료 상태 저장
    @Transactional
    public void activeHospitalVerification(String hospitalCode, String email){
        hospitalAdminRepository.findByEmail(email).ifPresent(admin -> {
            admin.setHospitalVerified(true);
            hospitalAdminRepository.save(admin);

        });
    }

    //이메일 인증 완료 상태 저장
    @Transactional
    public void activeEmailVerification(String email){
        hospitalAdminRepository.findByEmail(email).ifPresent(admin->{
            admin.setEmailVerfied(true); //lombok의 setter 사용
            hospitalAdminRepository.save(admin);
        });
    }

    // 병원 인증 여부 확인
    public boolean isHospitalVerified(String hospitalCode, String email){
        return hospitalAdminRepository.findByEmail(email)
                .filter(admin -> admin.getHospital().getHospitalCode().equals(hospitalCode))
                .map(HospitalAdmin::isHospitalVerified)
                .orElse(false);
    }

    // 이메일 인증 여부 확인
    public boolean isEmailVerified(String email){
        return hospitalAdminRepository.findByEmail(email)
                .map(HospitalAdmin::isEmailVerfied)
                .orElse(false);
    }


//    public HospitalAdmin saveMember(String hospitalCode, String adminName, String email, String password){
//        // 1. 병원 존재 여부 확인
//        Hospital hospital = hospitalRepository.findByHospitalCode(hospitalCode)
//                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 병원 코드입니다."));
//
//        // 2. 동일 병원에 동일한 관리자 이름이 있는지 확인
//        if(hospitalAdminRepository.findByEmail(email).isPresent()){
//            throw new IllegalArgumentException("해당 병원에 동일한 이름의 관리자가 존재합니다.");
//        }
//
//        // 3. 병원 관리자 생성
//        HospitalAdmin hospitalAdmin = new HospitalAdmin(hospital, adminName, password, email, Role.HOSPITAL_ADMIN);
//        return hospitalAdminRepository.save(hospitalAdmin);
//
//    }



}
