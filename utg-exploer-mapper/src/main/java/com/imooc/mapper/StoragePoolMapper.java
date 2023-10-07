package com.imooc.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StoragePool;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Form.StoragePoolForm;
import com.imooc.utils.MyMapper;

public interface StoragePoolMapper extends   MyMapper<StoragePool>{
	
	int insertPool(StoragePool pool);
	int updatePool(StoragePool pool);
	StoragePool getPoolInfo(String id);
	int updatePoolPledge(StoragePool pool);
    List<StoragePool> getPageList(@Param("storagePoolForm") StoragePoolForm storagePoolForm);
    long getTotal(@Param("storagePoolForm") StoragePoolForm storagePoolForm);
    List<StoragePool> getStoragePoolList(@Param("storagePoolForm") StoragePoolForm storagePoolForm);
    int batchUpdateSp(@Param("list")List<StoragePool> spoolList);
    int batchUpdateSpSnNum(@Param("list")Collection<StoragePool> spoolList);
    void updateStoragePoolAmount();
    void updateStoragePoolSn();
}
