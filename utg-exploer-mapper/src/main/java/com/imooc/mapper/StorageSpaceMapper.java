package com.imooc.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.utils.MyMapper;

public interface StorageSpaceMapper extends MyMapper<StorageSpace> {
	
//    void insertOrUpdate(@Param("space") StorageSpace space);
	
	int insertSpace(StorageSpace space);
	
	int updateVaildNumber(StorageSpace space);
	
	int batchUpdatePledge(@Param("list")List<StorageSpace> spaceList);
	int batchUpdateSpaceRatio(@Param("list")List<StorageSpace> spaceList);
	int batchUpdateSpaceRatioBySpHash(@Param("list")List<StorageSpace> spaceList);
	int updateVaildStatus(StorageSpace space);
	
	int updateVaildProgress(StorageSpace space);
	
	int cleanVaildStatus();
	
	int updatePledgeStatus(StorageSpace space);
	
	int updateRevenueAddr(StorageSpace space);
	
	int updateBandwidth(StorageSpace space);
    
	int updateRentPrice(StorageSpace space);
	
	int updateRentSpace(StorageSpace space);
	
	int updateAmount(StorageSpace space);
	void updateSpaceNew(StorageSpace space);
	void updateStorageAmount();
	void updateSpToSnTransfer(StorageSpace space);
	StorageSpace getSpaceInfo(String address);
	
    StorageSpace getSpaceById(@Param("device_addr")String device_addr,@Param("id") Integer id);
    
    List<StorageSpace> getPageList(@Param("storageQueryForm") StorageQueryForm storageQueryForm);

    long getTotal(@Param("storageQueryForm") StorageQueryForm storageQueryForm);
    
    StorageSpace getStat(@Param("storageQueryForm") StorageQueryForm storageQueryForm);
    
    List<StorageSpace> getList(@Param("device_addr") String device_addr,@Param("revenue_addr") String revenue_addr,@Param("status") String status);
    List<Map<String,Object>> getAmountStat();
	
	
	List<?> getPledgeNodeList(@Param("pledgeQueryForm") PledgeQueryForm pledgeQueryForm);
	
	long getPledgeNodeTotal(@Param("pledgeQueryForm") PledgeQueryForm pledgeQueryForm);
	
	Map<String, Object> getPledgeNodeStat(@Param("pledgeQueryForm") PledgeQueryForm pledgeQueryForm);
	
	
	int startBandwidthMakeup(@Param("deviceAddr") String device_addr,@Param("prepledgeAmount") BigDecimal prepledge_amount,
			@Param("bwSize") int bw_size, @Param("bwRatio") double bw_ratio, @Param("rewardRatio") double reward_ratio);
	
	int updateBandwidthMakeup(StorageSpace space);
	
	int finishBandwidthMakeup(StorageSpace space);
	
	int updateSpaceAttach(StorageSpace space);
	List<StorageSpace> getSnListBySpHash(@Param("spHash") String sphash,@Param("pledgeStatus") Integer pledgeStatus);
    long getSnListBySpHashCount(@Param("spHash") String sphash);
   int  updateSpaceSpStatus(StorageSpace space);
   int updateSpaceToSp(StorageSpace space);
	void updateSpaceDelSpStatus();
}
