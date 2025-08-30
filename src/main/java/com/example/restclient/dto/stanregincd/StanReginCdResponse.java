package com.example.restclient.dto.stanregincd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class StanReginCdResponse {

    @JsonProperty("StanReginCd")
    private List<StanReginCd> stanReginCd;
}
