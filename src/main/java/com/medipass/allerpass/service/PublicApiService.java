package com.medipass.allerpass.service;

import com.medipass.allerpass.dto.LoginFormDto;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PublicApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${api.key}")
    private String apiKey;
    public boolean verifyHospital(String hospitalCode, String hospitalTel){

        String apiUrl = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList"
                + "?ServiceKey=" + apiKey
                + "&ykiho=" + hospitalCode
//                + "&telno" + hospitalTel; telno는 필터링 안되서 제외해도 됨
                + "&_type=json"; // telno 필터링이 안 되므로 제외

        try {
            // Map 사용 이유 -> 동적으로 데이터 받음
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            //ResponseEntity에서 데이터를 map<String, object>로 반환해서 형식을 맞춘거고, getBody를 통해서 http 응답 바디를 가져옴
            Map<String, Object> responseBody = response.getBody();

            if(responseBody == null) return false;

            Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            Map<String, Object> itemMap = (Map<String, Object>) bodyMap.get("item");

            if(itemMap == null) return false;

            // Map.get은 반환타입이 Object만 반환해서, Map은 <String, Object> 타입으로 다뤄야하기 때문에
            // 형변환을 해준거임 그래서 앞에서 Map 선언했는데 또 괄호에 선언한거임
            List<Map<String,Object>> hospitalList = (List<Map<String,Object>>) itemMap.get("item");

            for (Map<String,Object> hospital : hospitalList){
                String apiTel = (String) hospital.get("telno");

                if(apiTel != null && apiTel.replace("-","").equals(hospitalTel.replace("-",""))){
                    return true;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    public boolean testHospitalCode(String hospitalCode){

    }
}
