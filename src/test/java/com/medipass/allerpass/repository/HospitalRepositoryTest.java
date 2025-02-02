package com.medipass.allerpass.repository;

import com.medipass.allerpass.entity.Hospital;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional // 테스트 실행 후 롤백 처리하여 DB 데이터 유지 안 되도록 설정
class HospitalRepositoryTest {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Test
    @DisplayName("병원 정보 저장 테스트")
    public void createHospitalTest() {
        // Given (테스트할 데이터 준비)
        Hospital hospital = new Hospital();
        hospital.setHospitalCode("HOSP-12345");
        hospital.setHospitalName("서울 중앙 병원");
        hospital.setAddress("서울특별시 강남구");

        // 빈 리스트로 초기화
        hospital.setHospitalAdmins(Collections.emptyList());
        hospital.setPatients(Collections.emptyList());
        hospital.setAllergies(Collections.emptyList());

        // When (DB에 저장)
        Hospital savedHospital = hospitalRepository.save(hospital);

        // Then (저장된 데이터 검증)
        assertNotNull(savedHospital.getHospitalId()); // 자동 생성된 ID 확인
        assertEquals("HOSP-12345", savedHospital.getHospitalCode());
        assertEquals("서울 중앙 병원", savedHospital.getHospitalName());
        assertEquals("서울특별시 강남구", savedHospital.getAddress());

        // 저장된 데이터 출력
        System.out.println(savedHospital.toString());
    }
}