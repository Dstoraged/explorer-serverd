package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageGlobalStat {
	
	@Id
	@Column(name = "id")
    private Integer id;
	
	@Column(name = "blocknumber")
	private Long blocknumber;
	
	@Column(name = "blocktime")
	private Date blocktime;
	
	@Column(name = "sttime")
    private Date sttime;
	
	@Column(name = "ratio")
	private BigDecimal ratio;
	
	@Column(name = "capacity")
	private BigDecimal capacity;
		
	@Column(name = "revenue_num")
	private Long revenue_num;
	
	@Column(name = "storage_num")
	private Long storage_num;
		
	@Column(name = "rent_num")
	private Long rent_num;
	
	@Column(name = "increase_space")
	private BigDecimal increase_space;
	
	@Column(name = "increase_rent")
    private BigDecimal increase_rent;
	
	@Column(name = "storage_space")
	private BigDecimal storage_space;
	
	@Column(name = "rent_space")
	private BigDecimal rent_space;
	
	@Column(name = "pledge_amount")
	private BigDecimal pledge_amount;
	
	@Column(name = "storage_amount")
	private BigDecimal storage_amount;
	
	@Column(name = "storage_release")
	private BigDecimal storage_release;
	
	@Column(name = "rent_amount")
	private BigDecimal rent_amount;
	
	@Column(name = "rent_release")
	private BigDecimal rent_release;
	
}
