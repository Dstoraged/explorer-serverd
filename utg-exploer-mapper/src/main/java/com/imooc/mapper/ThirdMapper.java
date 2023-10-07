package com.imooc.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.NodePledge;
import com.imooc.pojo.StorageRent;
import com.imooc.pojo.StorageRevenue;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.ThirdapiLog;
import com.imooc.pojo.TransferMiner;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.utils.MyMapper;

public interface ThirdMapper extends MyMapper<TransferMiner> {

	List<String> getBlacklist();
	
	int saveLog(@Param("log") ThirdapiLog log);
	
	
	StorageRevenue getRevenueInfo(@Param("revenue_addr")String address);
	
	List<StorageSpace> getRevenueSpaceList(@Param("revenue_addr") String revenue_addr);
	
	List<StorageRent> getRevenueRentList(@Param("device_addr") String device_addr);
	
	List<TransferMiner> getReleaseList(@Param("revenueaddress") String revenueaddress, @Param("types") String... type);
    
    List<TransferMiner> getRevenueListDaterange(@Param("starttime") Date starttime,@Param("endtime") Date endtime, @Param("revenueaddress") String revenueaddress, @Param("types") String... type);
        
    List<TransferMiner> getRevenueListBlockrange(@Param("startblock") Long startblock,@Param("endblock") Long endblock, @Param("revenueaddress") String revenueaddress, @Param("types") String... type);
    
    
    List<UtgNodeMiner> getPosNodes(@Param("type") Integer type,@Param("address") String address);
    
    UtgNodeMiner getPosNodeByAddress(@Param("address") String address);
    
    List<NodePledge> getNodePledgeList(@Param("node_address") String node_address,@Param("pledge_status") Integer pledge_status);
}
