package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.StoragePool;
import com.imooc.pojo.Form.StoragePoolForm;
import com.imooc.utils.ResultMap;

public interface StoragePoolService {
	ResultMap<StoragePool> getStoragePoolInfo(String hash,String spAddr);
	ResultMap<Page<StoragePool>> getStoragePoolList(StoragePoolForm storagePoolForm);
}
