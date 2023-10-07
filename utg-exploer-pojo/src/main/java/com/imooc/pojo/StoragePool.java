package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StoragePool {
	@Id
	@Column(name = "hash")
	private String hash;
	
	@Column(name = "sp_addr")
	private String spAddr;
	
	@Column(name = "manager_addr")
	private String managerAddr;
	
	@Column(name = "pledge_addr")
	private String pledgeAddr;
	
	@Column(name = "revenue_addr")
	private String revenueAddr;
	
	@Column(name = "active_status")
	private Integer activeStatus;
	
	@Column(name = "total_capacity")
	private BigDecimal totalCapacity;
	
	@Column(name = "used_capacity")
	private BigDecimal usedCapacity;
	
	@Column(name = "pledge_amount")
	private BigDecimal pledgeAmount;
	
	@Column(name = "hav_amount")
	private BigDecimal havAmount;
	
	@Column(name = "manager_amount")
	private BigDecimal managerAmount;
	
	@Column(name = "sp_amount")
	private BigDecimal spAmount;
	
	@Column(name = "sp_release")
	private BigDecimal spRelease;
	
	@Column(name = "sp_burnt")
	private BigDecimal spBurnt;
	
	@Column(name = "fee_rate")
	private Integer feeRate;
	@Column(name = "entrust_rate")
	private Integer entrustRate;
	
	@Column(name = "storage_ratio")
	private Double storageRatio;
	
	@Column(name = "active_height")
	private Long activeHeight;
	@Column(name = "createtime")
	private Date createtime;
	
	@Column(name = "instime")
	private Date instime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "sn_num")
	private Integer snNum;
	private Integer isManagerPledge;

	private BigDecimal snSpaceCapacity;
	private String unbind;
}
