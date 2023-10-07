package com.imooc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imooc.form.BasePo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhc 2021-09-15 14:41
 */
@Data
public class UtgStorageMiner extends BasePo {

    @Id
    private Long id;

    @Column(name = "miner_addr")
    private String miner_addr;

    @Column(name = "revenue_address")
    private String revenue_address;

    @Column(name = "pay_address")
    private String pay_address;

    @Column(name = "line_type")
    private String line_type;

    /*Mode 1 Traffic mining, 2 Bandwidth mining*/
    @Column(name = "miner_mode")
    private Integer miner_mode;

    /*1 Mining 2 To be pledged (binding income address) 3 Exit*/
    @Column(name = "miner_status")
    private Integer miner_status;

    //0 not joined 1 joined the mining pool
    @Column(name = "addpool")
    private Integer addpool;

    /* Mbps*/
    @Column(name = "bandwidth")
    private BigDecimal bandwidth;


    @Column(name = "pledge_amount")
    private BigDecimal pledge_amount;


    @Column(name = "miner_storage")
    private Long miner_storage;

    /*srt*/
    @Column(name = "paysrt")
    private BigDecimal paysrt;

    @Column(name = "revenue_amount")
    private BigDecimal revenue_amount;

    @Column(name = "lock_amount")
    private BigDecimal lock_amount;

    @Column(name = "release_amount")
    private BigDecimal release_amount;

    @Column(name = "draw_amount")
    private BigDecimal draw_amount;

    @Column(name = "blocknumber")
    private Long blocknumber;

    @Column(name = "join_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date join_time;

    @Column(name = "sync_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date sync_time;

    private BigDecimal balance;

    private BigDecimal surplussrt;

    private  BigDecimal  bdtotalamount;
    private  BigDecimal  bdreleaseamount;

    public UtgStorageMiner() {
    }
    public UtgStorageMiner(String miner_addr, String revenue_address, Integer miner_status, Long blocknumber, Date sync_time) {
        this.miner_addr = miner_addr;
        this.revenue_address = revenue_address;
        this.miner_status = miner_status;
        this.blocknumber = blocknumber;
        this.sync_time = sync_time;
    }
}
