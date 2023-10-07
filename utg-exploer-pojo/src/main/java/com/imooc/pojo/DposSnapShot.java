package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DposSnapShot {

    private  long blockNumber;

    private Date headertTime;

    private  Integer confirmedNumber;

    private  Date loopStartTime;

    private  Integer period;

    private  Integer minerReward;

    private BigDecimal voterBalance;




}
