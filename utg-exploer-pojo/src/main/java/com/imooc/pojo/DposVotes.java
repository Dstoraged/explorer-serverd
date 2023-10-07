package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
public class DposVotes {

    private  long id;

    private  Date loopStartTime;

    private  String candiDate;

    private  String voter;

    private BigDecimal stake;

    private long blockNumber;

    private Integer round;

    private  BigDecimal totalStake;

    private Long timeStampOne;

    private BigInteger avgBlockTime;

    private String txHash;

}
