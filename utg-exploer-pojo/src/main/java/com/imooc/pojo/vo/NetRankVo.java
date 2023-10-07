package com.imooc.pojo.vo;

import lombok.Data;

/**
 * @author zhc 2021-11-22 13:22
 */
@Data
public class NetRankVo {

    //Income address
    private String revenue_address;
    //Effective Storage
    private Long Storage_value;
    //Proportion of computing power
    private String perComPower;
    //Output efficiency
    private String outEfficiency;
}
