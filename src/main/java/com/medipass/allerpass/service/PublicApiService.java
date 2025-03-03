package com.medipass.allerpass.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PublicApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.key}")
    private String apiKey;

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

            if (!(itemObject instanceof List)) return false;
            List<Map<String, Object>> hospitalList = (List<Map<String, Object>>) itemObject;

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

    /**
     * 병원 코드(SGGU CD)를 이용하여 병원 정보를 검증하는 테스트 메서드
     */
    public boolean testHospitalCode(String sgguCd) {
        String apiUrl = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList"
                + "?ServiceKey=" + apiKey
                + "&sgguCd=" + sgguCd
                + "&_type=json"; // telno 필터링이 안 되므로 제외

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            Map<String, Object> responseBody = response.getBody();

            // ✅ API 응답 확인
            System.out.println("API 응답: " + responseBody);
            if (responseBody == null) return false;

            // ✅ 데이터 구조 탐색
            Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            if (bodyMap == null) return false;

            // ✅ "items" 존재 여부 확인
            Object itemsObject = bodyMap.get("items");
            if (!(itemsObject instanceof Map)) return false;

            Map<String, Object> itemsMap = (Map<String, Object>) itemsObject;
            Object itemObject = itemsMap.get("item");

            // ✅ "item"이 리스트인지 확인
            if (!(itemObject instanceof List)) {
                System.out.println("❌ 'item'이 리스트가 아닙니다. 올바른 JSON 응답인지 확인하세요.");
                return false;
            }

            List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemObject;
            if (itemList.isEmpty()) {
                System.out.println("❌ 'item' 리스트가 비어 있습니다.");
                return false;
            }

            // ✅ 첫 번째 병원 데이터 출력
            Map<String, Object> firstItem = itemList.get(0);
            System.out.println("첫 번째 병원 데이터: " + firstItem);
            return true;

        } catch (Exception e) {
            System.out.println("API 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}