//package com.medipass.allerpass.service;
//
//import com.medipass.allerpass.constant.Role;
//import com.medipass.allerpass.entity.Hospital;
//import com.medipass.allerpass.entity.HospitalAdmin;
//import com.medipass.allerpass.repository.HospitalAdminRepository;
//import com.medipass.allerpass.repository.HospitalRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class HospitalAdminService {
//
//    private final HospitalAdminRepository hospitalAdminRepository;
//    private final HospitalRepository hospitalRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    // 병원 인증 완료 상태 저장
//    @Transactional
//    public void activeHospitalVerification(String hospitalCode, Long adminId){
//        hospitalAdminRepository.findByAdminId(adminId).ifPresent(admin -> {
//            admin.setHospitalVerified(true);
//            hospitalAdminRepository.save(admin);
//
//        });
//    }
//
//    //이메일 인증 완료 상태 저장
//    @Transactional
//    public void activeEmailVerification(Long adminId){
//        hospitalAdminRepository.findByAdminId(adminId).ifPresent(admin->{
//            admin.setEmailVerfied(true); //lombok의 setter 사용
//            hospitalAdminRepository.save(admin);
//        });
//    }
//
////    // 병원 인증 여부 확인
////    public boolean isHospitalVerified(String hospitalCode, String email){
////        return hospitalAdminRepository.findByEmail(email)
////                .filter(admin -> admin.getHospital().getHospitalCode().equals(hospitalCode))
////                .map(HospitalAdmin::isHospitalVerified)
////                .orElse(false);
////    }
//
//    // 이메일 인증 여부 확인
//    public boolean isEmailVerified(Long adminId){
//        return hospitalAdminRepository.findByAdminId(adminId)
//                .map(HospitalAdmin::isEmailVerfied)
//                .orElse(false);
//    }
//
//
//    @Transactional
//    public HospitalAdmin registerAdmin(String hospitalCode, String adminName, String password, Role role) {
//        // 1. 병원 코드로 정보 조회
//        Hospital hospital = hospitalRepository.findByHospitalName(hospitalCode)
//                .orElseThrow(()->new IllegalArgumentException("해당 병원을 찾을 수 없습니다."));
//
//        // 3. 비밀번호 암호화
//        String encodedPassword = passwordEncoder.encode(password);
//        // 4. HospitalAdmin 객체 생성
//        HospitalAdmin hospitalAdmin = HospitalAdmin.createAdmin(hospital, encodedPassword);
//        // 5. 병원 관리자 지정
//        return hospitalAdminRepository.save(hospitalAdmin);
//
//    }
//
//
//
//}
