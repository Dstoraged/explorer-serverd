package com.imooc.pojo.vo;

import com.imooc.form.BasePo;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class TransferMinerVo extends BasePo {

    private Long id;


    private String txHash;


    private Integer type;


    private  Integer logIndex;


    private  String address;


    private BigDecimal value;


    private String blockHash;


    private Long blockNumber;


    private Date startTime;


    private BigDecimal totalAmount;


    private BigDecimal leftAmount;

    private Long avgBlockTime;

    private Long lockNumber;

    private Long gasPrice;

    private Long gasUsed;

    private Long gasLimit;

    private Long dayNumberThree;

    private Long dayNumberSix;

    private Long yearNumberSix;

    private Long yearNumberOne;

    private BigDecimal profitValReward;

    private Long unLockNumber;

    private Long logLength;
    private  String nodeNumber;

    private  BigDecimal presentAmount;

    private  Long lockNumHeight;

    private  BigDecimal pledgeAmount;

    private  String pledgeAddress;

    private BigDecimal punilshAmount;

    private  String receiveaddress;

    private  Long releaseHeigth;

    private  Long releaseInterval;

    private Date curtime;

    private  BigDecimal releaseamount;

    private  String revenueaddress;

    private String revenue_address;
    private BigDecimal pledge_amount;
    private Integer nstatus;
    
    
    private BigDecimal burntratio;    
    private String burntaddress;    
    private BigDecimal burntamount;

}
