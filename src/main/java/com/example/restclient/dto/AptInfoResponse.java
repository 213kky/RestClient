package com.example.restclient.dto;

import com.example.restclient.dto.apartment.Body;
import com.example.restclient.dto.apartment.basic.ApartmentBasicInfo;
import com.example.restclient.dto.apartment.basic.BasicItem;
import com.example.restclient.dto.apartment.detail.ApartmentDetailInfo;
import com.example.restclient.dto.apartment.detail.DetailItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AptInfoResponse {

    private String kaptUsedate;     // 사용 승인일
    private Integer kaptdaCnt;      // 세대 수
    private String kaptDongCnt;     // 동 수
    private String codeAptNm;       // 건물 유형
    private Integer kaptTopFloor;   // 최고 층
    private Integer kaptMparea60;   // 60㎡ 이하
    private Integer kaptMparea85;   // 85㎡ 이하
    private Integer kaptMparea135;  // 135㎡ 이하
    private Integer kaptMparea136;  // 136㎡ 이상
    private String kaptTel;         // 관리사무소 전화
    private String kaptdPcnt;        // 주차대수 지상
    private String kaptdPcntu;       //  주차대수 지하
    private String codeHeatNm;     // 난방 방식
    private String kaptdWtimebus;    // 버스정류장 접근성
    private String subwayLine;       // 지하철 노선
    private String subwayStation;    // 지하철역
    private String kaptdWtimesub;    // 지하철 접근 시간
    private String convenientFacility; // 편의시설
    private String educationFacility;  // 교육시설
    private Integer groundElChargerCnt;     // 지상 전기차 충전기
    private Integer undergroundElChargerCnt; // 지하 전기차 충전기
    private String welfareFacility;     // 단지내 시설 복지시설
    private String kaptdCccnt;          // CCTV 대수

    public static AptInfoResponse from(ApartmentBasicInfo<Body<BasicItem>> basic, ApartmentDetailInfo<Body<DetailItem>> detail) {
        BasicItem basicItem = basic.getResponse().getBody().getItem();
        DetailItem detailItem = detail.getResponse().getBody().getItem();

        return AptInfoResponse.builder()
                .kaptUsedate(basicItem.getKaptUsedate())
                .kaptdaCnt(basicItem.getKaptdaCnt())
                .kaptDongCnt(basicItem.getKaptDongCnt())
                .codeAptNm(basicItem.getCodeAptNm())
                .kaptTopFloor(basicItem.getKaptTopFloor())
                .kaptMparea60(basicItem.getKaptMparea60())
                .kaptMparea85(basicItem.getKaptMparea85())
                .kaptMparea135(basicItem.getKaptMparea135())
                .kaptMparea136(basicItem.getKaptMparea136())
                .kaptTel(basicItem.getKaptTel())
                .kaptdPcnt(detailItem.getKaptdPcnt())
                .kaptdPcntu(detailItem.getKaptdPcntu())
                .codeHeatNm(basicItem.getCodeHeatNm())
                .kaptdWtimebus(detailItem.getKaptdWtimebus())
                .subwayLine(detailItem.getSubwayLine())
                .subwayStation(detailItem.getSubwayStation())
                .kaptdWtimesub(detailItem.getKaptdWtimesub())
                .convenientFacility(detailItem.getConvenientFacility())
                .educationFacility(detailItem.getEducationFacility())
                .groundElChargerCnt(detailItem.getGroundElChargerCnt())
                .undergroundElChargerCnt(detailItem.getUndergroundElChargerCnt())
                .welfareFacility(detailItem.getWelfareFacility())
                .kaptdCccnt(detailItem.getKaptdCccnt())
                .build();
    }
}
