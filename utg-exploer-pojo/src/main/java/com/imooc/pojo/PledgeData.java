package com.imooc.pojo;

import com.imooc.form.BasePo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PledgeData extends BasePo {


    @Column(name="id")
    private Long id;

    @Column(name="txhash")
    private String txHash;

    @Column(name="type")
    private Integer type;

    @Column(name="logindex")
    private  Integer logIndex;

    @Column(name="address")
    private  String address;

    @Column(name="value")
    private BigDecimal value;

    @Column(name="blockhash")
    private String blockHash;

    @Column(name="blocknumber")
    private Long blockNumber;

    @Column(name="starttime")
    private Date startTime;

    @Column(name="totalamount")
    private BigDecimal totalAmount;

    @Column(name="leftamount")
    private BigDecimal leftAmount;

    @Column(name="status")
    private Integer status;

    @Column(name="gasused")
    private Long gasUsed;

    @Column(name="gasprice")
    private Long gasPrice;

    @Column(name="gaslimit")
    private Long gasLimit;

    @Column(name="unlocknumber")
    private Long unLockNumber;

    @Column(name="loglength")
    private Long logLength;

    @Column(name="nodenumber")
    private  String nodeNumber;

    @Column(name="presentamount")
    private  BigDecimal presentAmount;

    @Column(name="locknumheigth")
    private  Long lockNumHeight;

    @Column(name="pledgeamount")
    private  BigDecimal pledgeAmount;

    @Column(name="pledgeaddress")
    private  String pledgeAddress;

    @Column(name="punilshamount")
    private BigDecimal punilshAmount;

    @Column(name="receiveaddress")
    private  String receiveaddress;

    @Column(name="releaseheigth")
    private  Long releaseHeigth;

    @Column(name="releaseinterval")
    private  Long releaseInterval;

    @Column(name="pledgetotalamount")
    private  BigDecimal pledgeTotalAmount;

}
