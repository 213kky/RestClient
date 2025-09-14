package com.example.restclient.controller;

import com.example.restclient.dto.AptInfoResponse;
import com.example.restclient.dto.apartment.basic.ApartmentBasicInfo;
import com.example.restclient.dto.apartment.basic.BasicItem;
import com.example.restclient.dto.apartment.detail.ApartmentDetailInfo;
import com.example.restclient.dto.apartment.detail.DetailItem;
import com.example.restclient.dto.aptlist.Body;
import com.example.restclient.dto.aptlist.SigunguAptList3;
import com.example.restclient.dto.stanregincd.StanReginCdResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OpenApiController {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    @Value("${secretkey}")
    private String serviceKey;

    // 국토교통부_공동주택 기본 정보제공 서비스 (단지코드 필요)
    // 1 국토교통부_공동주택 상세 정보조회 -> https://apis.data.go.kr/1613000/AptBasisInfoServiceV4/getAphusDtlInfoV4
    // 2 국토교통부_공동주택 기본 정보조회 -> https://apis.data.go.kr/1613000/AptBasisInfoServiceV4/getAphusBassInfoV4
    @GetMapping("/api/apartment/basic")
    public ResponseEntity<ApartmentBasicInfo<com.example.restclient.dto.apartment.Body<BasicItem>>> getAptBasicInfo(@RequestParam String kaptCode) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1613000/AptBasisInfoServiceV4/getAphusBassInfoV4") // 기본
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", kaptCode) // 단지코드
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        log.info("최종 URI: {}", uri);

        ApartmentBasicInfo<com.example.restclient.dto.apartment.Body<BasicItem>> basicInfo = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ApartmentBasicInfo<com.example.restclient.dto.apartment.Body<BasicItem>>>() {});

        return ResponseEntity.ok().body(basicInfo);
    }

    @GetMapping("/api/apartment/detail")
    public ResponseEntity<ApartmentDetailInfo<com.example.restclient.dto.apartment.Body<DetailItem>>> getAptDetailInfo(@RequestParam String kaptCode) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1613000/AptBasisInfoServiceV4/getAphusDtlInfoV4") // 상세
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", kaptCode) // 단지코드
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        log.info("최종 URI: {}", uri);

        ApartmentDetailInfo<com.example.restclient.dto.apartment.Body<DetailItem>> detailInfo = restClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApartmentDetailInfo<com.example.restclient.dto.apartment.Body<DetailItem>>>() {});

        return ResponseEntity.ok().body(detailInfo);
    }

    // 행정안전부_행정표준코드_법정동코드
    // 법정동코드 정보의 지역코드, 시도코드, 읍면동코드, 리코드, 지역주소명 등을 조회한다.
    @GetMapping("/api/legal-dong-codes")
    public ResponseEntity<StanReginCdResponse> getLegalDongCode(
            @RequestParam String location,
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "10") Integer numOfRows
    ) {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);

        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1741000/StanReginCd/getStanReginCdList")
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", String.valueOf(pageNo)) // 페이지번호
                .queryParam("numOfRows", String.valueOf(numOfRows)) // 한 페이지 결과 수
                .queryParam("type", "json")
                .queryParam("locatadd_nm", encodedLocation)
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        log.info("최종 URI: {}" , uri);

        String responseString = restClient.get()
                .uri(uri)
                .accept(MediaType.TEXT_HTML) // 응답이 content-type text/html 임... MediaType.APPLICATION_JSON하면 HTTP ROUTING ERROR 에러남
                .retrieve()
                .toEntity(String.class).getBody();

        log.info("responseString: {}", responseString);

        StanReginCdResponse stanReginCdResponse = null;
        try {
            stanReginCdResponse = objectMapper.readValue(responseString, StanReginCdResponse.class);
        } catch (Exception e) {
            log.error("파싱 실패 에러 메시지 {}", e.getMessage());
        }

        return ResponseEntity.ok().body(stanReginCdResponse);
    }

    // 국토교통부_공동주택 단지 목록제공 서비스
    // 시도(2자리), 시군구(3자리), 읍면동(3자리), 리(2자리) 법정동코드 늘어나면 앞에 단위 코드 붙여야함
    // 검색가능 형태: 시도, 시군구, 법정동..
    // 법정동코드 == 지역코드 10자리.. 읍면동까지가 좋은데 없는듯..
    // 시군구 사용하려면 5자리
    // 국토교통부_시군구 아파트 목록, 국토교통부_법정동 아파트 목록 둘 중 하나 쓸 듯
    @GetMapping("/api/apt/sigungu") // 우선 시군구로 진행
    public ResponseEntity<SigunguAptList3<Body>> getApts(
            @RequestParam Integer sigunguCode,
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "10") Integer numOfRows
    ) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1613000/AptListService3/getSigunguAptList3")
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", String.valueOf(pageNo)) // 페이지번호
                .queryParam("numOfRows", String.valueOf(numOfRows)) // 한 페이지 결과 수
                .queryParam("sigunguCode", String.valueOf(sigunguCode)) // 시군구 코드 5자리
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        log.info("최종 URI: {}" , uri);

        SigunguAptList3<Body> sigunguAptList3 = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<SigunguAptList3<Body>>() {});

        return ResponseEntity.ok().body(sigunguAptList3);
    }

    @GetMapping("/api/apartment-info")
    public ResponseEntity<AptInfoResponse> getApartmentInfo(@RequestParam String kaptCode) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1613000/AptBasisInfoServiceV4/getAphusBassInfoV4") // 기본
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", kaptCode) // 단지코드
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        ApartmentBasicInfo<com.example.restclient.dto.apartment.Body<BasicItem>> basicInfo = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ApartmentBasicInfo<com.example.restclient.dto.apartment.Body<BasicItem>>>() {});

        uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1613000/AptBasisInfoServiceV4/getAphusDtlInfoV4") // 상세
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", kaptCode) // 단지코드
                .build(true) // 이미 인코딩된 키 인코딩 방지
                .toUri();

        ApartmentDetailInfo<com.example.restclient.dto.apartment.Body<DetailItem>> detailInfo = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ApartmentDetailInfo<com.example.restclient.dto.apartment.Body<DetailItem>>>() {});

        AptInfoResponse response = AptInfoResponse.from(basicInfo, detailInfo);

        return ResponseEntity.ok().body(response);
    }
}
