package com.imooc.pojo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StatGlobal {
	private Integer id;
	private Long blocknumber;
	private Long tx_count;
	private BigDecimal total_burnt;	
	private BigDecimal total_pledge;
	private BigDecimal total_storage;	
	private BigDecimal last_mintage;
	private String next_election;	
	
	private BigDecimal total_locked;
	private BigDecimal total_amount;
	private BigDecimal total_release;
	private BigDecimal block_amount;
	private BigDecimal block_release;
	private BigDecimal block_burnt;
	private BigDecimal storage_amount;
	private BigDecimal storage_release;
	private BigDecimal storage_burnt;
	private BigDecimal rent_amount;
	private BigDecimal rent_release;
	private BigDecimal rent_burnt;
	private BigDecimal reward_burnt;
	private BigDecimal fee_burnt;
	
	private BigDecimal tx_burnt;
	private BigDecimal pledge_burnt;
	private BigDecimal lease_his;
}
