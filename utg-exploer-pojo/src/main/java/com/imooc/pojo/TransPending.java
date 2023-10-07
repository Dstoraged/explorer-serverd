package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransPending {

    private String hash;

    private Date timeStamp;

    private String fromAddr;

    private  String toAddr;

    private  BigDecimal value;

    private  Long nonce;

    private  Long gasLimit;

    private  Long gasPrice;

    private String input;




}
