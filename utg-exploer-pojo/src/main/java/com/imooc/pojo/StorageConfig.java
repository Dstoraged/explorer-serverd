package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageConfig {

	@Id
	@Column(name = "type")
	private String type;
	
	@Id
	@Column(name = "seq")
	private Integer seq;
	
	@Column(name = "min")
    private BigDecimal min;

    @Column(name = "max")
    private BigDecimal max;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "updatetime")
    private Date updatetime;
}
