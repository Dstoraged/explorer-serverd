package com.imooc.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.StoragePool;
import com.imooc.pojo.Form.StoragePoolForm;
import com.imooc.service.StoragePoolService;
import com.imooc.utils.ResultMap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@Api(value = "storagePool", tags = "sp")
@RequestMapping("/spPool")
public class StoragePoolController {
	@Autowired
    private StoragePoolService poolService;
	@ApiOperation(value = "getStoragePoolList")
	@PostMapping("/getStoragePoolList")
	public ResultMap<Page<StoragePool>> getStoragePoolList(@Valid @RequestBody StoragePoolForm storagegPoolForm){
		return poolService.getStoragePoolList(storagegPoolForm);
	}
	
	@ApiOperation(value = "getStoragePoolInfo")
	@RequestMapping("/getStoragePoolInfo/{hash}")
	public ResultMap<StoragePool> getStoragePoolInfo(@PathVariable("hash") String hash){
		return poolService.getStoragePoolInfo(hash,null);
	}
	
}
