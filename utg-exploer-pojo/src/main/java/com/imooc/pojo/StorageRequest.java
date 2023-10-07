package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageRequest {
	
	public enum ReqStatus{
		failed(0), success(1), pending(2), expired(3)
		;		
		private int value;
		ReqStatus(int value){
			this.value = value;
		}
		public int value(){
			return value;
		}
		public static ReqStatus parse(int value){
			for(ReqStatus status: ReqStatus.values()){
				if(status.value == value)
					return status;
			}
			return null;
		}
	}
	
	@Id
	@Column(name = "reqid")
	private Long reqid;
	
	@Column(name = "reqhash")
	private String reqhash;
	
	@Column(name = "storageid")
	private Long storageid;
	
	@Column(name = "rentid")
	private Long rentid;
	
	@Column(name = "device_addr")
	private String deviceAddr;
	
	@Column(name = "rent_hash")
	private String rentHash;
	
	@Column(name = "req_type")
	private String reqType;
	
	@Column(name = "req_status")
	private Integer reqStatus;
	
	@Column(name = "req_number")
	private Long reqNumber;
	
	@Column(name = "req_space")
	private BigDecimal reqSpace;
	
	@Column(name = "rent_price")
	private BigDecimal rentPrice;
	
	@Column(name = "rent_time")
	private Integer rentTime;
		
	@Column(name = "rent_amount")
	private BigDecimal rentAmount;
	
	@Column(name = "pledge_status")
	private Integer pledgeStatus;
	
	@Column(name = "instime")
	private Date instime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	
	@Column(name = "pledge_addr")
	private String pledge_addr;
	
	@Column(name = "revenue_addr")
	private String revenue_addr;

	@Column(name = "rent_addr")
	private String rent_addr;
	
	@Column(name = "rent_status")
	private Integer rent_status;
}
