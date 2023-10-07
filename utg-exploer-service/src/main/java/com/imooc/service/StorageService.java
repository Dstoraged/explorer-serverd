package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.StorageConfig;
import com.imooc.pojo.StorageRent;
import com.imooc.pojo.StorageRequest;
import com.imooc.pojo.StorageRevenue;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.utils.ResultMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface StorageService {
	
	public enum StorageOperator{
		stReq,
		stExit,
		stValid,
		chPrice,
		stRent,
		stRentPg,
		stReNew,
		stReNewPg,
		stRescind,
		creFile,
		delFile,
		stReValid,
		stProof,
		stset
	}
	/*
	boolean stroageTransaction(TransactionObject transaction,Log log,Transaction tx);
	
	List<StorageSpace> getAllStorageSpaceList();
	
	List<StorageRent> getAllStorageRentList();
	
	List<StorageRevenue> getAllStorageRevenueList();
	
	void cleanVaildStatus();
	
	Map<String, Object> getStProofTx(String device_addr,String rent_hash);
	
	int updateVaildNumber(StorageSpace space);
	
	int updateVaildNumber(StorageRent rent);
	
	int updateOrSaveStorageRevenue(StorageRevenue revenue);
	
	List<Map<String,Object>> getAmountStat();
	
	int updateSpaceAmount(StorageSpace space);
	*/
	
	
	ResultMap<?> getStorageBasicSet();
	
	ResultMap<List<StorageConfig>> getStorageConfigList(String type);
	
	ResultMap<Page<StorageSpace>> getStorageSpaceList(StorageQueryForm storageQueryForm);
	
	ResultMap<StorageSpace> getStorageSpaceInfo(String device_addr,Integer id);
	
	ResultMap<Page<StorageRent>> getStorageRentList(StorageQueryForm storageQueryForm);
	
	ResultMap<StorageRent> getStorageRentInfo(String rent_hash);

//	ResultMap<Page<StorageRequest>> getStoragePledgeList(StorageQueryForm storageQueryForm);
	
	ResultMap<Page<StorageRequest>> getStorageRequestList(StorageQueryForm storageQueryForm);
	
	ResultMap<StorageRequest> getStorageRequestById(String reqid, String device_addr, String rent_hash);
	
	ResultMap<Page<StorageRevenue>> getStorageRevenueList(StorageQueryForm storageQueryForm);
	
	ResultMap<StorageRevenue> getStorageRevenueInfo(String revenue_addr,Integer type,Integer[] types);
		
	ResultMap<?> getStoragePledgeInfo(String device_addr);
	
//	ResultMap<Page<?>> getStorageTransactionList(StorageQueryForm storageQueryForm);
	
	ResultMap<Page<?>> getPledgeNodeList(PledgeQueryForm pledgeQueryForm);
	
	ResultMap<?> getPledgeNodeStat(PledgeQueryForm pledgeQueryForm);	
	
	
	ResultMap<Page<?>> getTransactionRentList(StorageQueryForm storageQueryForm);

	ResultMap<Map<?,?>> predictStoragePledgeAmount(BigDecimal declare_space, Integer bandwidth);

	ResultMap<Map<?, ?>> calStorageLeaseReward(BigDecimal rentCapacity, BigDecimal rentPrice, BigDecimal rentDays,BigDecimal bandwidthIndex, BigDecimal storageIndex);
	
	
	ResultMap<?> updateStorageSpaceAttach(String device_addr, String txt, String pic, String address, String sign);
	
	ResultMap<?> updateStorageRentAttach(String rent_hash, String txt, String pic, String address, String sign);

	
	void getStorageSpaceAttachFile(HttpServletRequest request, HttpServletResponse response, String device_addr);
	
	void getStorageRentAttachFile(HttpServletRequest request, HttpServletResponse response, String rent_hash);
	  BigDecimal getGbUtgRate(BigDecimal totalLeaseSpace);
	  ResultMap<Page<?>> getSNListBySpHash(StorageQueryForm storageQueryForm);
}
