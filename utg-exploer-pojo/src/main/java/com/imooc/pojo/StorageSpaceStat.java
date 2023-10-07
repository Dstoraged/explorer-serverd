package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageSpaceStat {
	
	@Id
	@Column(name = "id")
    private Long id;
	
	@Column(name = "blocknumber")
	private Long blocknumber;
	
	@Column(name = "blocktime")
	private Date blocktime;
	
	@Column(name = "sttime")
    private Date sttime;
		
	@Column(name = "storageid")
    private Long storageid;
	
	@Column(name = "device_addr")
    private String device_addr;
	
	@Column(name = "revenue_addr")
    private String revenue_addr;
	
	@Column(name = "pledge_status")
	private Integer pledge_status;
	
	@Column(name = "pledge_amount")
	private BigDecimal pledge_amount;
	
	@Column(name = "rent_space")
	private BigDecimal rent_space;
	
	@Column(name = "rent_price")
	private BigDecimal rent_price;
	
	@Column(name = "rent_num")
	private Long rent_num;

	@Column(name = "bw_size")
	private BigDecimal bw_size;
	
	@Column(name = "bw_ratio")
	private BigDecimal bw_ratio;

	@Column(name = "valid_number")
	private Long valid_number;
	
	@Column(name = "vaild_status")
	private Integer vaild_status;
	
	@Column(name = "vaild_progress")
	private BigDecimal vaild_progress;

	@Column(name = "total_amount")
	private BigDecimal total_amount;	
//	private BigDecimal total_release
	
	@Column(name = "storage_amount")
	private BigDecimal storage_amount;
	
//	@Column(name = "storage_release")
//	private BigDecimal storage_release
	
	@Column(name = "rent_amount")
	private BigDecimal rent_amount;
	
//	@Column(name = "rent_release")
//	private BigDecimal rent_release
	
}
