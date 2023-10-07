package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DposNode {
    private  long id;

    private  String voter;

    private BigDecimal stake;

    private  Integer type;

    private  Integer round;

    private Long blockNumber;

}
