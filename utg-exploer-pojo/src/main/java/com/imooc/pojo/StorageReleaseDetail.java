package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageReleaseDetail{

	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "rewardid")
	private Long rewardid;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "revenueaddress")
	private String revenueaddress;
	
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "blocknumber")
	private Long blocknumber;
	
	@Column(name = "totalamount")
	private BigDecimal totalamount;
	
	@Column(name = "releaseonce")
	private BigDecimal releaseonce;
	
	@Column(name = "releasetype")
	private Integer releasetype;
	    
	@Column(name = "updatetime")
    private Date updatetime;
}
