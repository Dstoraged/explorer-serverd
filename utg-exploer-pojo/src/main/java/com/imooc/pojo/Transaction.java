package com.imooc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imooc.form.BasePo;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class Transaction extends BasePo {
    @Column(name = "hash")
    private String hash;

    @Column(name = "istrunk")
    private Integer isTrunk;

    @Column(name = "timestamp")
    private Date timeStamp;

    @Column(name = "fromaddr")
    private String fromAddr;

    @Column(name = "toaddr")
    private String toAddr;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "nonce")
    private Long nonce;

    @Column(name = "gaslimit")
    private Long gasLimit;

    @Column(name = "gasprice")
    private Long gasPrice;

    @Column(name = "status")
    private  Integer  status;

    @Column(name = "gasused")
    private Long gasUsed;

    @Column(name = "cumulative")
    private Long cumulative;

    @Column(name = "blockhash")
    private String  blockHash;

    @Column(name = "blocknumber")
    private Long blockNumber;

    @Column(name = "blockindex")
    private Integer blockIndex;

    @Column(name = "input")
    private String  input;

    @Column(name = "contract")
    private String  contract;

    @Column(name = "error")
    private String error;

    @Column(name = "internal")
    private Integer  internal;

    @Column(name="type")
    private  Integer type;

    @Column(name="ufoprefix")
    private  String  ufoprefix;

    @Column(name="ufoversion")
    private  String ufoversion;

    @Column(name="ufooperator")
    private  String ufooperator;

    @Column(name="param1")
    private  String param1;

    @Column(name="param2")
    private  String param2;

    @Column(name="param3")
    private BigDecimal param3;

    @Column(name="param4")
    private  BigDecimal param4;

    @Column(name="param5")
    private  String param5;

    @Column(name="param6")
    private  String param6;

    private Integer blockDays;
    
    private Map<String,Object> data = new HashMap<>(); //**"data":{"param1":"x","param2":"xx",param3":"xxx"}**

    public  void dataBuild(String param1,String param2,String param3,String param4,String param5,String param6 ){
        if(param1!=null){
            data.put("param1",param1);
            this.param1 = param1;
        }
        if(param2!=null){
            data.put("param2",param2);
            this.param2 = param2;
        }
        if(param3!=null){
            data.put("param3",param3);
            this.param3 = new BigDecimal(param3);
        }
        if(param4!=null){
            data.put("param4",param4);
            this.param4 =  new BigDecimal(param4);
        }
        if(param5!=null){
            data.put("param5",param5);
            this.param5 = param5;
        }
        if(param6!=null){
            data.put("param6",param6);
            this.param6 = param6;
        }
    }
    
    public String toString() {
		String str = "Transaction [hash=" + hash + ",blocknumber=" + blockNumber + ",form=" + fromAddr + ",to=" + toAddr + ",contract=" + contract 
				+ ",ufooperator=" + ufooperator + ",status=" + status + ",input size="+ (input == null ? 0 : input.length());
		if (param1 != null)
			str += ",param1=" + param1;
		if (param2 != null)
			str += ",param2=" + param2;
		if (param3 != null)
			str += ",param3=" + param3;
		if (param4 != null)
			str += ",param4=" + param4;
		if (param5 != null)
			str += ",param5=" + param5;
		if (param6 != null)
			str += ",param6=" + param6;
		str += "]";
		return str;
	}

}
