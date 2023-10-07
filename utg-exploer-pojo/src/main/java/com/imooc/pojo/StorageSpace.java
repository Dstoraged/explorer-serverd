package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageSpace {
	
	public enum PledgeStatus{
		failed(-1), 
		normal(0), 
		exiting(1), 
		removing(5), 
		deleted(6)
		;		
		private int value;
		PledgeStatus(int value){
			this.value = value;
		}
		public int value(){
			return value;
		}
		public static PledgeStatus parse(int value){
			for(PledgeStatus pledgeStatus: PledgeStatus.values()){
				if(pledgeStatus.value == value)
					return pledgeStatus;
			}
			return null;
		}
	}
	
	@Id
	@Column(name = "storageid")
	private Long storageid;
	
	@Column(name = "device_addr")
	private String deviceAddr;
	
	@Column(name = "pledge_addr")
	private String pledgeAddr;
	
	@Column(name = "revenue_addr")
	private String revenueAddr;
	
	@Column(name = "pledge_status")
	private Integer pledgeStatus;
	
	@Column(name = "pledge_number")
	private Long pledgeNumber;
	
	@Column(name = "pledge_amount")
	private BigDecimal pledgeAmount;
	
	@Column(name = "prepledge_amount")
	private BigDecimal prepledgeAmount;
	
	@Column(name = "pensate_amount")
	private BigDecimal pensateAmount;
	
	@Column(name = "declare_space")
	private BigDecimal declareSpace;
	
	@Column(name = "free_space")
	private BigDecimal freeSpace;
	
	@Column(name = "rent_price")
	private BigDecimal rentPrice;
	
	@Column(name = "rent_num")
	private Long rentNum;
	
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	
	@Column(name = "storage_amount")
	private BigDecimal storageAmount;
	
	@Column(name = "storage_release")
	private BigDecimal storageRelease;
	
	@Column(name = "storage_burnt")
	private BigDecimal storageBurnt;
	
	@Column(name = "rent_amount")
	private BigDecimal rentAmount;
	
	@Column(name = "rent_release")
	private BigDecimal rentRelease;
	
	@Column(name = "rent_burnt")
	private BigDecimal rentBurnt;
	
	@Column(name = "valid_number")
	private Long validNumber;
	
	@Column(name = "succ_number")
	private Long succNumber;
	
	@Column(name = "fail_count")
	private Integer failCount;
	
	@Column(name = "vaild_status")
	private Integer vaildStatus;
	
	@Column(name = "vaild24_status")
	private Integer vaild24Status;
	
	@Column(name = "vaild_progress")
	private BigDecimal vaildProgress;
	
	@Column(name = "vaild_time")
	private Date vaildTime;
	
	@Column(name = "fail_days")
	private Integer failDays;
	
	@Column(name = "bw_size")
	private BigDecimal bwSize;
	
	@Column(name = "bw_ratio")
	private BigDecimal bwRatio;
	
	@Column(name = "reward_ratio")
	private BigDecimal rewardRatio;
	
	@Column(name = "bw_changed")
	private Integer bwChanged;
	
	@Column(name = "instime")
	private Date instime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "attach_text")
	private String attachText;
	
	@Column(name = "attach_pic")
	private String attachPic;
	
	@Column(name = "attach_picmd5")
	private String attachPicmd5;
	
	@Column(name = "attach_time")
	private Date attachTime;
	
	private BigDecimal ratio;	
	private BigDecimal capacity;
	
	@Column(name = "manager_addr")
	private String managerAddr;
	
	@Column(name = "entrust_rate")
	private BigDecimal entrustRate;
	
	@Column(name = "sp_hash")
	private String spHash;
	
	@Column(name = "sp_height")
	private Long spHeight;
	
	@Column(name = "sp_jointime")
	private Date spJointime;
	
	@Column(name = "hav_amount")
	private BigDecimal havAmount;
	
	@Column(name = "manager_amount")
	private BigDecimal managerAmount;
	
	@Column(name = "manager_height")
	private Long managerHeight;
	@Column(name = "active_height")
	private Long activeHeight;
	private Double spaceRate;
	private Integer joinState;
	private Integer feeRate;
}
