package com.example.restclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/api/test")
    public ResponseEntity test() {
        String serviceKey = "인코딩키";

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

}
