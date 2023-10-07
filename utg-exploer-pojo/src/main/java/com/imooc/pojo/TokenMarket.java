package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TokenMarket {
    private  long id;

    private  String contract;

    private Date marketDate;

    private BigDecimal closingPrice;

    private BigDecimal openingPrice;






}
