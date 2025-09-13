package com.example.restclient.dto.apartment.detail;

import com.example.restclient.dto.common.Response;
import lombok.Getter;

@Getter
public class ApartmentDetailInfo<T> {

    private Response<T> response;
}
