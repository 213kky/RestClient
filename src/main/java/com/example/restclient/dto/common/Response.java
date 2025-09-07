package com.example.restclient.dto.common;

import lombok.Getter;

@Getter
public class Response<T>  {

    private T body;
    private Header header;
}
