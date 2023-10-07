package com.imooc.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class DposVoterVo {
    private BigDecimal voterTotal;

    private Long maxBlock;

    private Long minBlock;

    private Integer round;

    private Long voterCount;

    private BigInteger minTime;

    private BigInteger maxTime;

    private Long nowBlock;

    private Long roundNumber;

    private Long timeStampOne;

}
