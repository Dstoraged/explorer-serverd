package com.imooc.pojo;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class NodeExit {

    @Column(name="id")
    private Long id;

    @Column(name="timestamp")
    private Date timeStamp;

    @Column(name="addressname")
    private String  addressName;

    @Column(name="pledgeamount")
    private BigDecimal pledgeAmount;

    @Column(name="deductionamount")
    private BigDecimal deductionAmount;

    @Column(name="tractamount")
    private BigDecimal tractAmount;

    @Column(name="lockstartnumber")
    private Long lockStartNumber;

    @Column(name="locknumber")
    private Long lockNumber;

    @Column(name="releasenumber")
    private Long releaseNumber;

    @Column(name="releaseinterval")
    private Long releaseInterval;











}
