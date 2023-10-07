package com.imooc.pojo.Form;

import java.math.BigDecimal;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.BlockQueryParam;
import lombok.Data;

@Data
public class BlockQueryForm extends BaseQueryForm<BlockQueryParam> {
    private String address;

    private String contract;

    private String revenueAddress;
    
    private String mineraddress;

    private Integer round;

    private Integer type;
    
    private Integer[] types;

    private Integer status;//

    private String startTime;//

    private String endTime;//

    private Long blockNumber;//

    private String fromAddr;

    private String toAddr;

    private  String candidate;//

    private String txType;// //0 all, 1  normal tx , CandReq , CandExit  ,FlwReq ,FlwExit , Exch

    private String ufooperator;
    
    private String param1;

    private String param2;
    
    private Boolean last24;
    
    private Long startBlock;
    
    private Long endBlock;
    
    private Long lockupnumber;
    
    private String authAddress;
    
    private String addr;
    
    private Boolean burned;
    
    private String table;
    
    private String pledgeaddress;
    
    private Boolean bytype;
    
    private BigDecimal tokenid;
    
    private String search;
    
    private String hash;
}
