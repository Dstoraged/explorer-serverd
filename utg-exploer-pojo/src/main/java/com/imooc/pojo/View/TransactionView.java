package com.imooc.pojo.View;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class TransactionView {
    private String hash;

    private Integer isTrunk;

    private Date timeStamp;

    private String fromAddr;

    private String toAddr;

    private BigDecimal value;

    private Long nonce;

    private Long gasLimit;

    private Long gasPrice;

    private  Integer  status;

    private Long gasUsed;

    private Long cumulative;

    private String  blockHash;

    private Long blockNumber;

    private Integer blockIndex;

    private String  input;

    private String  contract;

    private String error;

    private Integer  internal;

    private String amount;


    private Integer type;//

    private String balance;

    private  String  ufoprefix; //

    private  String ufoversion; //

    private  String ufooperator; //

    private  String param1;

    private  String param2;

    private  BigDecimal param3;

    private  BigDecimal param4;

    private  String param5;

    private  String param6;
}
