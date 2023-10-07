package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StorageRequest;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.utils.MyMapper;

public interface StorageRequestMapper extends MyMapper<StorageRequest> {

//    void insertOrUpdate(@Param("request") StorageRequest request);
	
	int insertRequest(StorageRequest request);
	
	int updateRequest(StorageRequest request);
	
    StorageRequest getRequestById(@Param("reqid") String reqid, @Param("device_addr") String device_addr, @Param("rent_hash") String rent_hash);
    
    StorageRequest getRequestInfo(@Param("device_addr") String device_addr,@Param("rent_hash") String rent_hash,@Param("req_type") String req_type);
    
    List<StorageRequest> getPageList(@Param("storageQueryForm") StorageQueryForm storageQueryForm);

    long getTotal(@Param("storageQueryForm") StorageQueryForm storageQueryForm);

    List<StorageRequest> getExpiredRenewRequest(@Param("rent_hash")String rent_hash,@Param("renewList")List<String> renewList);
    
    int updateRenewStatus(@Param("rent_hash")String rent_hash,@Param("renewList")List<String> renewList);
    
    int cleanRentRequest(@Param("rent_hash")String rent_hash);
}
