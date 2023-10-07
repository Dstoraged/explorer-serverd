package com.imooc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UtgNodeMiner {

    @Column(name="id")
    private Long id;

    @Column(name="node_address")
    private String  node_address;

    @Column(name="revenue_address")
    private String  revenue_address;

    @Column(name="manage_address")
    private String manage_address;
    
    /*1 Candidate node 2 Witness node 3 Exit node*/
    @Column(name="node_type")
    private Integer node_type;

    @Column(name="fractions")
    private Integer fractions;
    
    @Column(name="rate")
    private Integer rate;

    @Column(name = "pledge_amount")
    private BigDecimal pledge_amount;

    @Column(name = "total_amount")
    private BigDecimal total_amount;    
    
    @Column(name="blocknumber")
    private Long blocknumber;

    @Column(name = "join_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date join_time;

    @Column(name = "sync_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date sync_time;
    
    @Column(name="punish_block")
    private Long punish_block;
    
	@Column(name = "exit_type")
	private Integer exit_type;

    private BigDecimal leftamount;

    private BigDecimal lockamount;

    private BigDecimal totalamount;
    
    private BigDecimal releaseamount;

    private BigDecimal exitlockamount;

    private BigDecimal exitreleaseamount;

    private BigDecimal pledgelockamount;

    private BigDecimal pledgereleaseamount;    
    
    private Long manage_pledge_count;

}
