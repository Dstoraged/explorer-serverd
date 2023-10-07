package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StorageRent;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.utils.MyMapper;

public interface StorageRentMapper extends MyMapper<StorageRent> {

//    void insertOrUpdate(@Param("rent")StorageRent rent);
    
	int insertRent(StorageRent rent);
	
	int updateRent(StorageRent rent);
	
	int updateVaildNumber(StorageRent rent);
	
	int batchUpdatePledge(@Param("list")List<StorageRent> rentList);
	
	int updateVaildStatus(StorageRent rent);
	
	int cleanVaildStatus();
	
	int updateRentStatus(StorageRent rent);
	
	int updateRenewRequest(StorageRent rent);
	
	int updateRentRenew(StorageRent rent);
    
    StorageRent getRentInfo(String hash);
    
    List<StorageRent> getPageList(@Param("storageQueryForm") StorageQueryForm storageQueryForm);

    long getTotal(@Param("storageQueryForm") StorageQueryForm storageQueryForm);
    
    List<StorageRent> getList(@Param("device_addr") String device_addr,@Param("rent_hash") String rent_hash,@Param("status") String status);
    
    int updateRentAttach(StorageRent rent);
}

