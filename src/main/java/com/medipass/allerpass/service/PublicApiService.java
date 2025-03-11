package com.medipass.allerpass.service;

import com.medipass.allerpass.constant.Role;
import com.medipass.allerpass.entity.Hospital;
import com.medipass.allerpass.entity.HospitalAdmin;
import com.medipass.allerpass.repository.HospitalAdminRepository;
import com.medipass.allerpass.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PublicApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final HospitalRepository hospitalRepository;
    private final HospitalAdminRepository hospitalAdminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${api.key}")
    private String apiKey;

    public PublicApiService(HospitalRepository hospitalRepository, HospitalAdminRepository hospitalAdminRepository, PasswordEncoder passwordEncoder) {
        this.hospitalRepository = hospitalRepository;
        this.hospitalAdminRepository = hospitalAdminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 병원 코드와 전화번호를 검증하는 메서드
     */
    public boolean verifyHospital(String hospitalCode, String hospitalTel) {
        String apiUrl = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList"
                + "?ServiceKey=" + apiKey
                + "&ykiho=" + hospitalCode
                + "&_type=json"; // telno 필터링이 안 되므로 제외

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) return false;

            // 응답 데이터 구조 탐색
            Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            if (bodyMap == null) return false;

            // ✅ "items" 존재 여부 확인
            Object itemsObject = bodyMap.get("items");
            if (!(itemsObject instanceof Map)) return false;

            Map<String, Object> itemsMap = (Map<String, Object>) itemsObject;
            Object itemObject = itemsMap.get("item");

            List<Map<String, Object>> hospitalList;

            if (itemObject instanceof List) {
                hospitalList = (List<Map<String, Object>>) itemObject;
            } else if (itemObject instanceof Map) {
                hospitalList = List.of((Map<String, Object>) itemObject); // 단일 데이터를 리스트로 변환
            } else {
                return false;
            }

            // ✅ 병원 정보가 있는지 확인
            for (Map<String, Object> hospital : hospitalList) {
                String apiTel = (String) hospital.get("telno");
                if (apiTel != null && apiTel.replace("-", "").equals(hospitalTel.replace("-", ""))) {
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("API 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 무작위 비밀번호 생성 메서드
    public String generateRandomPassword(int length){
        String characters="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i=0; i<length; i++){
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }
    /**
     * 병원 코드(SGGU CD)를 이용하여 병원 정보를 검증하는 테스트 메서드
     */
    public ResponseEntity<?> testHospitalCode(String dgsbjtCd, String yadmNm) {
        String apiUrl = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList"
                + "?ServiceKey=" + apiKey
                + "&yadmNm=" + yadmNm
                + "&dgsbjtCd=" + dgsbjtCd
                + "&_type=json"; // telno 필터링이 안 되므로 제외

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            Map<String, Object> responseBody = response.getBody();

            // ✅ API 응답 확인
            System.out.println("API 응답: " + responseBody);
            if (responseBody == null) return ResponseEntity.status(400).body("응답 데이터가 없습니다.");


            // ✅ 데이터 구조 탐색
            Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            if (bodyMap == null) return ResponseEntity.status(400).body("body 데이터가 없습니다.");


            // ✅ "items" 존재 여부 확인
            Object itemsObject = bodyMap.get("items");
            if (!(itemsObject instanceof Map)) return ResponseEntity.status(400).body("값이 틀립니다. 다시한번 입력해주세요.");


            Map<String, Object> itemsMap = (Map<String, Object>) itemsObject;
            Object itemObject = itemsMap.get("item");

            // 병원 정보를 담을 변수
            String realHospitalName = null;
            String addr = null;
            String telno = null;

            if (itemObject instanceof List) {
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemObject;
                if (!itemList.isEmpty()) {
                    realHospitalName = (String) itemList.get(0).get("yadmNm");
                    addr = (String) itemList.get(0).get("addr");
                    telno = (String) itemList.get(0).get("telno");
                    System.out.println("저장 전 telno 값: " + telno);
                }
            } else if (itemObject instanceof Map) {
                Map<String, Object> itemMap = (Map<String, Object>) itemObject;
                realHospitalName = (String) itemMap.get("yadmNm");
                addr = (String) itemMap.get("addr");
                telno = (String) itemMap.get("telno");
                System.out.println("저장 전 telno 값: " + telno);
            } else {
                System.out.println("❌ item의 데이터 형식이 예상과 다릅니다: " + itemObject.getClass().getName());
                return ResponseEntity.status(400).body("item 데이터 형식이 올바르지 않습니다.");
            }

            // ✅ 병원 정보 정상 반환 (디버깅)
            System.out.println("🔹 반환될 병원 정보: " + realHospitalName + ", 주소: " + addr + ", 전화번호: " + telno);

            Optional<Hospital> existingHospital = hospitalRepository.findByTelno(telno.replaceAll("-","".trim()));
            System.out.println("DB에서 검색한 병원: " + existingHospital);
            if(existingHospital.isPresent()){
                return ResponseEntity.status(200).body("이미 등록된 병원입니다.");
            }
//            System.out.println("중복 로직 제대로 안됨");

            // ✅ 병원 DB에 저장
            Hospital newHospital = new Hospital();
            newHospital.setHospitalName(realHospitalName);
            newHospital.setAddress(addr);
            newHospital.setTelno(telno.replaceAll("-", "").trim()); // 🚀 통일된 형식으로 저장
            hospitalRepository.save(newHospital);

            // 무작위 비밀번호 생성
            String randomPassword = generateRandomPassword(6);

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(randomPassword);
            //병원 관리자 생성
            HospitalAdmin hospitalAdmin = HospitalAdmin.createAdmin(newHospital);
            hospitalAdmin.setTemporaryPassword(encodedPassword);
            hospitalAdmin.setHospitalVerified(true);
            hospitalAdminRepository.save(hospitalAdmin);


            // ✅ 결과 데이터를 Map으로 정리하여 반환
            Map<String, Object> result = new HashMap<>();
            result.put("병원명", realHospitalName);
            result.put("주소", addr);
            result.put("전화번호", telno);
            result.put("비밀번호", randomPassword);


            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.out.println("API 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류 발생");
        }
    }
}