package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DposProposal {

    private  String  hash;

    private  Long receivedNumber;

    private  Integer proposalType;

    private  String proposer;

    private  BigDecimal currentDeposit;

    private  Integer validationLoopcnt;

    private   Integer decisionCount;

    private   String targetAddress;

    private   Integer minerReward;

    private   BigDecimal voterBalance;

    private   BigDecimal proposalDeposit;

    private   String scHash;

    private  Integer scBlockCount;

    private  BigDecimal scBlockReward;

    private  BigDecimal screntFee;

    private  BigDecimal screntRate;

    private Integer screntLength;





















}
