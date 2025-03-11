package com.medipass.allerpass.repository;

import com.medipass.allerpass.entity.Hospital;
import com.medipass.allerpass.entity.HospitalAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalAdminRepository extends JpaRepository<HospitalAdmin,Long> {
    Optional<HospitalAdmin> findByAdminId(Long adminId);
    Optional<HospitalAdmin> findByHospital_HospitalName(String yadmNm); // ✅ 병원 ID로 관리자 조회
    // findBy 메서드에서 평문 비밀번호를 직접 검색하면 보안문제가 생겨서, 항상 암호화된 상태로 저장해야 하고
    // 검증 시 matches()를 통해 비교해야 함
    // 비밀번호 검증은 서비스 레이어에서 하는게 좋음
    // 비밀번호 검증 로직을 분리하면 유지 보수성 증가

}
