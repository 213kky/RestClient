package com.example.restclient.dto.apartment.basic;

import com.example.restclient.dto.common.Response;
import lombok.Getter;

@Getter
public class ApartmentBasicInfo<T> {

    private Response<T> response;
}
