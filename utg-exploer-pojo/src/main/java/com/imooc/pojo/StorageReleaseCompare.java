package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageReleaseCompare {

	@Id
	@Column(name = "id")
	private Long id;
	
	private Integer blockdays;
	
	private Long startblock;
	
	private Long endblock;
		
	private String revenueaddress;
	
    private BigDecimal release_stat;

    private BigDecimal release_pay;

    private BigDecimal offset;
    
    private Date updatetime;
}
