package com.example.restclient.dto.apartment;

import lombok.Getter;

@Getter
public class Body<T> {

    private T item;
}
