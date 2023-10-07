package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageRevenue {

	@Id
	@Column(name = "revenueid")
	private Integer revenueid;
	
	@Column(name = "revenue_addr")
	private String revenueAddr;

	@Column(name = "ratio")
	private BigDecimal ratio;
	
	@Column(name = "capacity")
	private BigDecimal capacity;
	
	private BigDecimal storage_amount;
	private BigDecimal storage_release;
	private BigDecimal storage_burnt;
	private BigDecimal rent_amount;
	private BigDecimal rent_release;
	private BigDecimal rent_burnt;
	
	@Column(name = "updatetime")
	private Date updatetime;	

	private BigDecimal totalamount;
	private BigDecimal releaseamount;	
	private BigDecimal burntamount;
	
	private BigDecimal declareSpace;
}
