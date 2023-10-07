package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import lombok.Data;

@Data
public class NodePledge {

	@Column(name="id")
    private Long id;

	@Column(name="node_address")
	private String  node_address;
	
	@Column(name="pledge_address")
	private String  pledge_address;
	
	@Column(name="pledge_hash")
	private String  pledge_hash;
	
	@Column(name="pledge_amount")
    private BigDecimal pledge_amount;
	
	@Column(name="pledge_status")
    private Integer pledge_status;
	
	@Column(name="pledge_number")
    private Long pledge_number;
	
    @Column(name="pledge_time")
    private Date pledge_time;

    @Column(name="unpledge_hash")
	private String unpledge_hash;
    
    @Column(name="unpledge_number")
    private Long unpledge_number;
	
    @Column(name="unpledge_time")
    private Date unpledge_time;
    
    @Column(name="unpledge_type")
    private Integer unpledge_type;
    
    
    @Column(name="node_number")
    private Long node_number;
    
    @Column(name="manage_address")
    private String manage_address;
    
    @Column(name="ratio")
    private BigDecimal ratio;
    
    @Column(name="totalamount")
    private BigDecimal totalamount;
    
    @Column(name="releaseamount")
    private BigDecimal releaseamount;
    
    @Column(name="burntamount")
    private BigDecimal burntamount;
    
    @Column(name="sp_hash")
    private String sp_hash;
    
    @Column(name="node_type")
    private String node_type;
    
    private Double entrustRate; 
    private boolean  isTransfer;
    private boolean  isEntrustExit;
}
