package com.example.restclient;

import com.example.restclient.dto.StanReginCdResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    @Value("${secretkey}")
    private String serviceKey;

    // 국토교통부_공동주택 기본 정보제공 서비스 (단지코드 필요)
    @GetMapping("/api/test/0")
    public ResponseEntity test() {
        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1613000/AptBasisInfoServiceV4/getAphusDtlInfoV4")
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", "A15876402")
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        log.info("최종 URI: {}" , uri);

        return restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
    }

    // 행정안전부_행정표준코드_법정동코드
    // 법정동코드 정보의 지역코드, 시도코드, 읍면동코드, 리코드, 지역주소명 등을 조회한다.
    @GetMapping("/api/test/1")
    public ResponseEntity<?> test1() {
        String location = "경기도";
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);

        String pageNo = "1";
        String numOfRows = "10";

        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1741000/StanReginCd/getStanReginCdList")
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo) // 페이지번호
                .queryParam("numOfRows", numOfRows) // 한 페이지 결과 수
                .queryParam("type", "json")
                .queryParam("locatadd_nm", encodedLocation)
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        log.info("최종 URI: {}" , uri);

        String responseString = restClient.get()
                .uri(uri)
                .accept(MediaType.TEXT_HTML) // 응답이 content-type text/html 임... MediaType.APPLICATION_JSON하면 HTTP ROUTING ERROR 에러남
                .retrieve()
                .toEntity(String.class).toString();

        log.info("responseString: {}", responseString);

        StanReginCdResponse stanReginCdResponse = null;
        try {
            // 1. JSON 시작 위치
            int start = responseString.indexOf("{");
            int end = responseString.lastIndexOf("}");

            // 2. JSON 추출
            String jsonPart = responseString.substring(start, end + 1);
            log.info("jsonPart: {}", jsonPart);

            stanReginCdResponse = objectMapper.readValue(jsonPart, StanReginCdResponse.class);
        } catch (Exception e) {
            log.error("파싱 실패 에러 메시지 {}", e.getMessage());
        }

        return ResponseEntity.ok().body(stanReginCdResponse);
    }
}
