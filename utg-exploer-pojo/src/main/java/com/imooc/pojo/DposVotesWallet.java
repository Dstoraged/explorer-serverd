package com.imooc.pojo;

import com.imooc.form.BasePo;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
@Data
public class DposVotesWallet  extends BasePo {
    private  long id;

    private Date loopStartTime;

    private  String candiDate;

    private  String voter;

    private BigDecimal stake;

    private long blockNumber;

    private Integer round;

    private  BigDecimal totalStake;

    private Long timeStampOne;

    private BigInteger avgBlockTime;

    private Long exitBlockNumber;

    private Integer isExit;

    private String txHash;
}
