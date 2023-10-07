package com.imooc.pojo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class PledgeTotalData {

    @Column(name="id")
    private Long id;

    @Column(name="blocknumber")
    private Long blockNumber;

    @Column(name="starttime")
    private Date startTime;

    @Column(name="address")
    private  String address;

    @Column(name="type")
    private Integer type;

    @Column(name="value")
    private BigDecimal value;

    @Column(name="status")
    private Integer status;

    @Column(name="nodenumber")
    private  String nodeNumber;

    @Column(name="pledgetotalamount")
    private BigDecimal pledgeTotalAmount;

    @Column(name="cashtotalamount")
    private BigDecimal cashTotalAmount;

    @Column(name="punilshamount")
    private BigDecimal punilshAmount;

    @Column(name="realshnumber")
    private Long realshNumber;

    @Column(name="locknumber")
    private Long lockNumber;

    @Column(name="releaseintervalnum")
    private Long releaseIntervalNum;

    @Column(name="exittime")
    private Date exitTime;

    @Column(name="pledgeaddress")
    private  String pledgeAddress;

}
