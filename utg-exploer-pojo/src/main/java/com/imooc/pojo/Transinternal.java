package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transinternal {
    private  long id;  //id

    private  String transHash;

    private  Integer transIndex;

    private String  fromAddr;

    private String toAddr;

    private BigDecimal value;

    private Long gasLimit;

    private Long gasUsed;

    private String blockHash;

    private Long blockNumber;

    private String type;

    private String contract;

    private String callType;

    private String input;

    private String createdCode;

    private String initCode;

    private String outPut;

    private String traceAddress;



}
