package com.example.restclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class StanReginCdResponse {

    @JsonProperty("StanReginCd")
    private List<StanReginCd> stanReginCd;
}
