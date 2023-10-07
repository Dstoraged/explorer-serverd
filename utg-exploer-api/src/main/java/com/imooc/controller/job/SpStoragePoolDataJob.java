package com.imooc.controller.job;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.job.service.SpPoolService;

public class SpStoragePoolDataJob  extends AgentSvcTask{
	private static Logger logger = LoggerFactory.getLogger(SpStoragePoolDataJob.class);
	private Long blockNumber;
	private String url;
	private SpPoolService spService;
    private Map<Long,BigInteger> blockTimeMap;
    public SpStoragePoolDataJob(String url, Long blockNumber,  SpPoolService spService,Map<Long,BigInteger> blockTimeMap) {
    	this.url = url;
        this.blockNumber = blockNumber;
        this.spService = spService;
        this.blockTimeMap=blockTimeMap;
        if(this.blockTimeMap==null) {
        	this.blockTimeMap=new HashMap<Long, BigInteger>();
        }
    }
    
	protected  void runTask() {
        long starttime = System.currentTimeMillis();
        logger.info("Storage Pool clt Job start .....");
        this.spService.getAllSpByMainClt(url,blockNumber,blockTimeMap);
        long endtime = System.currentTimeMillis();
        logger.info("Storage Pool clt  end ,spends total second ="+(endtime-starttime)/1000);
    }
	
}
