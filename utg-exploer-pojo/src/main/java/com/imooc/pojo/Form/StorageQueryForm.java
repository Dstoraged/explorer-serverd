package com.imooc.pojo.Form;

import com.imooc.form.BaseParam;
import com.imooc.form.BaseQueryForm;
import com.imooc.utils.Constants;

public class StorageQueryForm extends BaseQueryForm<BaseParam>{
	
	private String addr;
	private String device_addr;	
	private String rent_hash;	
	private String pledge_addr;	
	private String revenue_addr;	
	private String rent_addr;	
	private Integer status;
	private Integer type;	
	private String[] req_types;
	private Integer vaild24_status;
	private String txhash;
	private String[] rent_hashs;
	private Integer[] types;
	private String spHash;
	private String managerAddr;
	private Integer []statusList;
	
	public Integer[] getStatusList() {
		return statusList;
	}
	public void setStatusList(Integer[] statusList) {
		this.statusList = statusList;
	}
	public String getManagerAddr() {
		return managerAddr;
	}
	public void setManagerAddr(String managerAddr) {
		this.managerAddr = managerAddr;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	private String query;
	
	public String getSpHash() {
		return spHash;
	}
	public void setSpHash(String spHash) {
		this.spHash = spHash;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {		
		addr = Constants.prefixAddress(addr);
		this.addr = addr;
	}

	public String getDevice_addr() {				
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		device_addr = Constants.prefixAddress(device_addr);
		this.device_addr = device_addr;
	}

	public String getRent_hash() {
		return rent_hash;
	}

	public void setRent_hash(String rent_hash) {
		this.rent_hash = rent_hash;
	}

	public String getPledge_addr() {
		return pledge_addr;
	}

	public void setPledge_addr(String pledge_addr) {
		pledge_addr = Constants.prefixAddress(pledge_addr);
		this.pledge_addr = pledge_addr;
	}

	public String getRevenue_addr() {
		return revenue_addr;
	}

	public void setRevenue_addr(String revenue_addr) {
		revenue_addr = Constants.prefixAddress(revenue_addr);
		this.revenue_addr = revenue_addr;
	}

	public String getRent_addr() {
		return rent_addr;
	}

	public void setRent_addr(String rent_addr) {
		rent_addr = Constants.prefixAddress(rent_addr);
		this.rent_addr = rent_addr;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}

	public String[] getReq_types() {
		return req_types;
	}
	public void setReq_types(String[] req_types) {
		this.req_types = req_types;
	}

	
	public Integer getVaild24_status() {
		return vaild24_status;
	}
	public void setVaild24_status(Integer vaild24_status) {
		this.vaild24_status = vaild24_status;
	}
	

	public String getTxhash() {
		return txhash;
	}
	public void setTxhash(String txhash) {
		this.txhash = txhash;
	}

	public String[] getRent_hashs() {
		return rent_hashs;
	}
	public void setRent_hashs(String[] rent_hashs) {
		this.rent_hashs = rent_hashs;
	}
	public Integer[] getTypes() {
		return types;
	}
	public void setTypes(Integer[] types) {
		this.types = types;
	}
}
