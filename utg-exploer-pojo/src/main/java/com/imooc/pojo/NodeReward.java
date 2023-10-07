package com.imooc.pojo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;
@Data
public class NodeReward {
	@Id
	@Column(name = "reward_id")
	private Long rewardId;
	@Column(name = "target_address")
	private String targetAddress;
	@Column(name = "node_address")
	private String nodeAddress;
	@Column(name = "revenue_address")
	private String revenueAddress;
	@Column(name = "node_type")
	private String nodeType;
	@Column(name = "reward_type")
	private Integer rewardType;
	@Column(name = "leiji_amount")
	private BigDecimal leijiAmount;
	@Column(name = "reward_amount")
	private BigDecimal rewardAmount;
	@Column(name = "block_number")
	private Long blockNumber;
	@Column(name = "reward_time")
	private Date rewardTime;
	@Column(name = "instime")
	private Date instime;

}
