package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StakingDeleGators {

    private long id;

    private String poolHash;

    private String delegator;

    private BigDecimal stakeAmount;

    private BigDecimal orderedWithDraw;

    private BigDecimal withdrawAllowed;

    private  BigDecimal orderedAllowed;

    private Integer epoch;



}
