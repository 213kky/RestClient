package com.example.restclient.dto.aptlist;

import java.util.List;
import lombok.Getter;

@Getter
public class Body {

    private List<Item> items;
    private int numOfRows;
    private int pageNo;
    private int totalCount;
}
