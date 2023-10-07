package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class DposHardWare {

    private String address;

    private BigDecimal processor;

    private BigDecimal memory;

    private BigDecimal storage;

    private BigDecimal bandWith;

    private String introduction;




}
