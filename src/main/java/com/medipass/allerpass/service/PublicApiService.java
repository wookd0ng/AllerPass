package com.medipass.allerpass.service;

import com.medipass.allerpass.dto.LoginFormDto;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PublicApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${api.key}")
    private String apiKey;
    public boolean verifyHospital(String hospitalCode, String hospitalTel){

        String apiUrl = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList"
                + "?ServiceKey" + apiKey
                + "&ykiho" + hospitalCode
                + "&telno" + hospitalTel;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            return response.getBody() != null && !response.getBody().isEmpty();
        } catch (Exception e){
            return false;
        }
    }
}
