package com.imooc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Punished {
    @Column(name="id")
    private Long id;

    @Column(name="address")
    private  String address;

    @Column(name="type")
    private  Integer type;//Type 1: Absent block 2: Not participating in witness

    @Column(name="round")
    private  Integer round;

    @Column(name="timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date timeStamp;

    @Column(name="fractions")
    private Long fractions;

    @Column(name="pledgeamount")
    private BigDecimal pledgeAmount;

    @Column(name="addressname")
    private String  addressName;

    @Column(name="blocknumber")
    private Long blockNumber;

}
