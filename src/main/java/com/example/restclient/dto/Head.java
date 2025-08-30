package com.example.restclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Head {
    private Integer totalCount;
    private String numOfRows;
    private String pageNo;
    private String type;

    @JsonProperty("RESULT")
    private Result result;
}