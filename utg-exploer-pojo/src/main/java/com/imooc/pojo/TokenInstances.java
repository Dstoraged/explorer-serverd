package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TokenInstances {

    private  long id;

    private  String contract;

    private BigDecimal tokenId;

    private String tokenUri;
    private  String metaData;



}
