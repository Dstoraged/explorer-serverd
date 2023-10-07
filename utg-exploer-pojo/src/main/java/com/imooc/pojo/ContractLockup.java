package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import lombok.Data;

@Data
public class ContractLockup {

    @Column(name="id")
    private Long id;

    @Column(name="contract")
    private String contract;

    @Column(name="address")
    private  String address;
    
    @Column(name="lockupnumber")
    private Long lockupnumber;
    
    @Column(name="type")
    private Integer type;
    
    @Column(name="txhash")
    private  String txhash;
    
    @Column(name="logindex")
    private  Integer logindex;
    
    @Column(name="pickupamount")
    private BigDecimal pickupamount;
        
    @Column(name="lockupamount")
    private BigDecimal lockupamount;

    @Column(name="remainamount")
    private BigDecimal remainamount;
    
    @Column(name="lockupperiod")
    private  Long lockupperiod;
    
    @Column(name="releaseperiod")
    private  Long releaseperiod;

    @Column(name="releaseinterval")
    private  Long releaseinterval;
    
    @Column(name="cancelnumber")
    private  Long cancelnumber;
    
    @Column(name="cancelamount")
    private BigDecimal cancelamount;
    
    @Column(name="createtime")
    private Date createtime;
    
    @Column(name="updatetime")
    private Date updatetime;    

    
    @Column(name="pickamount")
    private BigDecimal pickamount;
    
    @Column(name="pickcount")
    private Long pickcount;
}
