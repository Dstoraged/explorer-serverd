package com.imooc.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BlockAndMinerVo {

    private  String address;

    private BigDecimal value;

    private  long blockNumber;

    private  String types;

    private Date timeStamp;

}
