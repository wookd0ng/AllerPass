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

    private final HospitalRepository hospitalRepository;
    private final HospitalAdminRepository hospitalAdminRepository;

    public HospitalAdmin saveMember(String hospitalCode, String adminName, String email, String password){
        // 1. 병원 존재 여부 확인
        Hospital hospital = hospitalRepository.findByHospitalCode(hospitalCode)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 병원 코드입니다."));

        // 2. 동일 병원에 동일한 관리자 이름이 있는지 확인
        if(hospitalAdminRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("해당 병원에 동일한 이름의 관리자가 존재합니다.");
        }

        // 3. 병원 관리자 생성
        HospitalAdmin hospitalAdmin = new HospitalAdmin(hospital, adminName, password, email, Role.HOSPITAL_ADMIN);
        return hospitalAdminRepository.save(hospitalAdmin);

    }

}
