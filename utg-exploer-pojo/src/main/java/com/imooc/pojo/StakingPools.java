package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StakingPools {

    private  String hash;

    private String miningAddress;

    private  Integer isActive;

    private  Integer isDeleted;

    private   Integer delegatorsCount;

    private BigDecimal stakedAmount;

    private BigDecimal selfAmount;

    private Integer isValidator;

    private Integer validatorCount;

    private  Integer isBanned;

    private  Integer bannedCount;

    private  BigDecimal likeliHood;

    private  BigDecimal stakedRatio;

    private Date banneduntil;

}
