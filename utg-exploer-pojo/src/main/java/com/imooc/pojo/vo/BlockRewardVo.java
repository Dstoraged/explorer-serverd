package com.imooc.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BlockRewardVo {

    private  String minerAddress;

    private Integer  round;

    private BigDecimal rewardTotal;


}
