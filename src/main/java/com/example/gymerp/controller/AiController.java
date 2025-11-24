package com.example.gymerp.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v1/ai")
public class AiController {

    private final RestTemplate restTemplate = new RestTemplate();

    // ===============================
    // [1] 회원 예측 (직접 입력 기반)
    // ===============================
    @PostMapping("/predict/members")
    public Map<String, Object> predictMembers(@RequestBody Map<String, Object> requestData) {
        String pythonUrl = "http://localhost:5000/predict/members";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, requestData, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of("error", "Python 서버 연결 실패: " + e.getMessage());
        }
    }

    // ===============================
    // [2] 회원 예측 (DB 기반 자동 예측 + 저장)
    // ===============================
    @PostMapping("/predict/members/from-db")
    public Map<String, Object> predictMembersFromDB() {
        String pythonUrl = "http://localhost:5000/predict/members/from-db";

        try {
            // 입력값 없음(null)
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, null, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of("error", "Python 서버 연결 실패: " + e.getMessage());
        }
    }
}
