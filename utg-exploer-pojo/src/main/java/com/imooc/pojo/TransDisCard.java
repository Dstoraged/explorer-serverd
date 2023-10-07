package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransDisCard {
    private  String  transHash;

    private Date timeStamp;

    private  String fromAddr;

    private  String toAddr;

    private BigDecimal value;

    private Long nonce;

    private  long gasLimit;

    private  long gasPrice;

    private  String input;


}
