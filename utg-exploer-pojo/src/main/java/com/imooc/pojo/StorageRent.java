package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class StorageRent {

	public enum RentStatus{
		failed(-1), 
		applying(0), 
		normal(1), 
		rescinding(2), 
		recycling(3), 
		breach(4), 
		expired(6), 
		unaccepted(7)
		;		
		private int value;
		RentStatus(int value){
			this.value = value;
		}
		public int value(){
			return value;
		}
		public static RentStatus parse(int value){
			for(RentStatus rentStatus: RentStatus.values()){
				if(rentStatus.value == value)
					return rentStatus;
			}
			return null;
		}
	}
	
	@Id
	@Column(name = "rentid")
	private Long rentid;
	
	@Column(name = "rent_hash")
	private String rentHash;
	
	@Column(name = "device_addr")
	private String deviceAddr;
	
	@Column(name = "rent_addr")
	private String rentAddr;
	
	@Column(name = "pledge_addr")
	private String pledgeAddr;	
		
	@Column(name = "pledge_amount")
	private BigDecimal pledgeAmount;
	
	@Column(name = "rent_space")
	private BigDecimal rentSpace;
	
	@Column(name = "rent_price")
	private BigDecimal rentPrice;
	
	@Column(name = "rent_time")
	private Integer rentTime;
	
	@Column(name = "rent_number")
	private Long rentNumber;
	
	@Column(name = "rent_amount")
	private BigDecimal rentAmount;
	
	@Column(name = "rent_status")
	private Integer rentStatus;
	
	@Column(name = "renew_status")
	private Integer renewStatus;
	
	@Column(name = "renew_reqhash")
	private String renewReqhash;
		
	@Column(name = "renew_number")
	private Long renewNumber;
	
	@Column(name = "renew_time")
	private Integer renewTime;
	
	@Column(name = "recev_amount")
	private BigDecimal recevAmount;
		
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
	
	@Column(name = "vaild_time")
	private Date vaildTime;
	
	@Column(name = "fail_days")
	private Integer failDays;
	
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
	
	private Integer vaildTxStatus;
	
	private String revenue_addr;
}
