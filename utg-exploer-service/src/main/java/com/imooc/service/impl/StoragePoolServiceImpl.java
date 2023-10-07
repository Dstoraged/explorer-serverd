package com.imooc.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.StoragePoolMapper;
import com.imooc.pojo.StoragePool;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Form.StoragePoolForm;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.service.StoragePoolService;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class StoragePoolServiceImpl implements StoragePoolService{
protected static Logger logger = LoggerFactory.getLogger(StoragePoolServiceImpl.class);
	
	@Autowired
	private StoragePoolMapper poolDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<StoragePool> getStoragePoolInfo(String hash,String spAddr) {
		spAddr = Constants.prefixAddress(spAddr);
		hash = Constants.prefixAddress(hash);
		StoragePool pool;
		if(hash==null)
			pool = poolDao.getPoolInfo(spAddr);
		else
			pool = poolDao.getPoolInfo(hash);
		if(pool!=null){			
			return ResultMap.getSuccessfulResult(pool);
		}else{
			return ResultMap.getFailureResult("query no data");
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<StoragePool>> getStoragePoolList(StoragePoolForm storagePoolForm) {		
		Page page = storagePoolForm.newFormPage();
		Long pageSize = page.getSize();
		Long pageCurrent = page.getCurrent();
		pageCurrent = (pageCurrent - 1) * pageSize;
		storagePoolForm.setCurrent(pageCurrent);
		
		List<StoragePool> list = poolDao.getPageList(storagePoolForm);
		long total = poolDao.getTotal(storagePoolForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}
}
