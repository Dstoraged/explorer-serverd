package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StorageRevenue;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.utils.MyMapper;

public interface StorageRevenueMapper extends MyMapper<StorageRevenue>{

	StorageRevenue getRevenueInfo(@Param("revenue_addr")String address);
	
	StorageRevenue getRevenueInfoTyped(@Param("revenue_addr")String address, @Param("type") Integer type, @Param("types") Integer[] types);	
	
	StorageRevenue getRevenueStat(@Param("revenue_addr")String address, @Param("type") Integer type, @Param("types") Integer[] types);
    
	List<StorageRevenue> getAllList();
	
    List<StorageRevenue> getPageList(@Param("storageQueryForm") StorageQueryForm storageQueryForm);

    long getTotal(@Param("storageQueryForm") StorageQueryForm storageQueryForm);
    
    void updateRevenueAmount();
}
