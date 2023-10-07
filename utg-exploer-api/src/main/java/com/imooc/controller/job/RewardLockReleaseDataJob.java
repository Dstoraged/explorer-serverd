package com.imooc.controller.job;

import com.alibaba.fastjson.JSONObject;
import com.imooc.Utils.AgentSvcTask;
import com.imooc.Utils.TimeSpend;
import com.imooc.job.service.SpPoolService;
import com.imooc.job.service.StatisticService;
import com.imooc.job.service.StorageService;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.TransferMiner;
import com.imooc.pojo.UtgCltStoragedata;
import com.imooc.service.util.TxDataParse;
import com.imooc.utils.Constants;
import com.imooc.utils.HttpUtils;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.AlienSnapshot;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhc 2021-11-18 13:56
 */
public class RewardLockReleaseDataJob extends AgentSvcTask {
    private static Logger logger = LoggerFactory.getLogger(RewardLockReleaseDataJob.class);
    private  UtgStorageMinerMapper utgStorageMinerMapper;
    private TransferMinerMapper transferMinerMapper;
    private StorageService storageService;
    private StatisticService statisticService;
    private SpPoolService spService;
    private Long blockNumber;
    private Web3j web3j;
    private  Date timeStamp;
    private Date yesterday;
    private Long dayOneNumber;
//    private String decimals = "1000000000000000000";
//    private String blockRewardType = "3";
    private String leaseRewardType = "4";
    private String bandwidthRewardType = "5";
    private String posplexitRewardType = "6";
    private String posexitRewardType = "7";
    private String spLockRewardType = "8";
    private String spEntrustlockRewardType = "9";
    private String spEntrustExitRewardType = "10";
    private String spExitRewardType = "11";
    private String stEntrustExitRewardType = "12";
    private String stEntrustRewardType = "13";
    private static Integer retryTimes = 2;

    public RewardLockReleaseDataJob(Date yesterday, Date timeStamp, Web3j web3j, Long blockNumber, Long dayOneNumber,
    		UtgStorageMinerMapper utgStorageMinerMapper,TransferMinerMapper transferMinerMapper,StorageService storageService,StatisticService statisticService,SpPoolService  spService) {
        this.yesterday = yesterday;
        this.timeStamp = timeStamp;
        this.web3j = web3j;
        this.blockNumber = blockNumber;
        this.dayOneNumber = dayOneNumber;
        this.utgStorageMinerMapper = utgStorageMinerMapper;
        this.transferMinerMapper = transferMinerMapper;
        this.storageService = storageService;
        this.statisticService = statisticService;
        this.spService=spService;
    }

    protected  void runTask() {
        long starttime = System.currentTimeMillis();
        logger.info("RewardLockReleaseJob at block "+blockNumber+" start .....");
        /*try {
            // node -- candidatepledge
            AlienSnapshot.Snapshot candidatepledge=web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"candidatepledge").send().getSnapshot();
            if(candidatepledge!=null)
            	pledgeLockRelease(candidatepledge.getCandidatepledge(), 3, 4);
            
            //miner -- flowminerpledge	
            AlienSnapshot.Snapshot flowminerpledge=web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"flowminerpledge").send().getSnapshot();
            if(flowminerpledge!=null)
            	pledgeLockRelease(flowminerpledge.getFlowminerpledge(), 7, 8);
           
            AlienSnapshot.Snapshot posplexit = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"posplexit").send().getSnapshot();  
            if(posplexit!=null)
            	flowrevenvelock(posplexit.getFlowrevenve(), 17, 18 , posplexitRewardType);
            
            AlienSnapshot.Snapshot posexit=web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"posexit").send().getSnapshot();
            if(posexit!=null)
            	flowrevenvelock(posexit.getFlowrevenve(), 19, 20, posexitRewardType);
            
            
        //    AlienSnapshot.Snapshot leaselock = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"flowlock").send().getSnapshot();
        //    if(leaselock!=null)
        //    	flowrevenvelock(leaselock.getFlowrevenve(), 5, 6, leaseRewardType);
            Map<String, Map<String, Map<String, Object>>> leaselock = getRevenveRelease(blockNumber,"flowlock");
            if(leaselock!=null)
            	flowrevenvelock(leaselock, 5, 6, leaseRewardType);
            
//            //flow lock and release
       //     rewardLockRelease();            
       //     AlienSnapshot.Snapshot bandwidthlock = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"bandwidthlock").send().getSnapshot();
       //     if(bandwidthlock!=null)
       //     	bandwidthlock(bandwidthlock.getFlowrevenve(), 9, 10, bandwidthRewardType);
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"bandwidthlock");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 9, 10, bandwidthRewardType);

            
            AddressBalanceModifyJob.getInstance().putq(new Addresses(Constants.addressZero,blockNumber));
            //Re-release the last record
            new TransferAddJob(yesterday,blockNumber,utgStorageMinerMapper,transferMinerMapper).start();
            
            storageService.updateRevenueAmount(blockNumber);
            storageService.updateStorageAmount(blockNumber);            
			long dayBlocknumber = (blockNumber / dayOneNumber) * dayOneNumber;
			Block ethBlock = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(dayBlocknumber), false).send().getResult();
			Date blocktime = new Date(ethBlock.getTimestamp().longValue()*1000);						
			storageService.updateSpaceRewardStat(dayBlocknumber);
			storageService.saveRevenueStat(dayBlocknumber, blocktime);
			storageService.saveGlobalStat(dayBlocknumber, blocktime);
			
	        statisticService.updateRewardStat(blockNumber);
        }catch (Exception e) {
            logger.error("RewardLockReleaseJob error,"+e.getMessage(),e);
        }*/
        
        plegeExitRelease(0);
        storageRentRelease(0);
        storageSpaceRelease(0);
        storageSpLockRelease(0);
        storageSpEntrustRelease(0);
        storageSpExitRelease(0);
        storageSpEntrustExitRelease(0);
        storageSnEntrustExitRelease(0);
        storageSnEntrustRewardRelease(0);
        rewardStatistic(0);
        
        long endtime = System.currentTimeMillis();
        logger.info("RewardLockReleaseJob at block "+blockNumber+" end ,spends total second ="+(endtime-starttime)/1000);
    }

    
    protected void plegeExitRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Pledge exit release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Pledge exit release will retry "+retry+" times.");
    	try {
    		logger.info("Pledge exit release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();
	    	// node -- candidatepledge
	        AlienSnapshot.Snapshot candidatepledge=web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"candidatepledge").send().getSnapshot();
	        if(candidatepledge!=null)
	        	pledgeLockRelease(candidatepledge.getCandidatepledge(), 3, 4);
	        
	        //miner -- flowminerpledge	
	        AlienSnapshot.Snapshot flowminerpledge=web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"flowminerpledge").send().getSnapshot();
	        if(flowminerpledge!=null)
	        	pledgeLockRelease(flowminerpledge.getFlowminerpledge(), 7, 8);
	       
	        AlienSnapshot.Snapshot posplexit = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"posplexit").send().getSnapshot();  
	        if(posplexit!=null)
	        	flowrevenvelock(posplexit.getFlowrevenve(), 17, 18 , posplexitRewardType);
	        
	        AlienSnapshot.Snapshot posexit=web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"posexit").send().getSnapshot();
	        if(posexit!=null)
	        	flowrevenvelock(posexit.getFlowrevenve(), 19, 20, posexitRewardType);
	        logger.info("Pledge exit release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Pledge exit release error,"+e.getMessage(),e);
            plegeExitRelease(retry++);		//Retry;
        }
    }

    protected void storageRentRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge rent release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge rent release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge rent release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		
        //    AlienSnapshot.Snapshot leaselock = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"flowlock").send().getSnapshot();
        //    if(leaselock!=null)
        //    	flowrevenvelock(leaselock.getFlowrevenve(), 5, 6, leaseRewardType);
            Map<String, Map<String, Map<String, Object>>> leaselock = getRevenveRelease(blockNumber,"flowlock");
            if(leaselock!=null)
            	flowrevenvelock(leaselock, 5, 6, leaseRewardType);
    		
    		logger.info("Strorge rent release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge rent release error,"+e.getMessage(),e);
            storageRentRelease(retry++);		//Retry;
        }
    }
    
    protected void storageSpaceRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge space release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge space release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge space release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		      
       //     AlienSnapshot.Snapshot bandwidthlock = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"bandwidthlock").send().getSnapshot();
       //     if(bandwidthlock!=null)
       //     	bandwidthlock(bandwidthlock.getFlowrevenve(), 9, 10, bandwidthRewardType);
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"bandwidthlock");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 9, 10, bandwidthRewardType);
    		
    		logger.info("Strorge space release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge space release error,"+e.getMessage(),e);
            storageSpaceRelease(retry++);		//Retry;
        }
    }
    
    protected void rewardStatistic(int retry){
    	if(retry>retryTimes){
    		logger.warn("Reward statistic has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Reward statistic will retry "+retry+" times.");
    	try {
    		logger.info("Reward statistic at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();
    		
    		AddressBalanceModifyJob.getInstance().putq(new Addresses(Constants.addressZero,blockNumber));            
            new TransferAddJob(yesterday,blockNumber,utgStorageMinerMapper,transferMinerMapper).start();
            
            storageService.updateRevenueAmount(blockNumber);
            storageService.updateStorageAmount(blockNumber);            
			long dayBlocknumber = (blockNumber / dayOneNumber) * dayOneNumber;
			Block ethBlock = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(dayBlocknumber), false).send().getResult();
			Date blocktime = new Date(ethBlock.getTimestamp().longValue()*1000);						
			storageService.updateSpaceRewardStat(dayBlocknumber);
			storageService.saveRevenueStat(dayBlocknumber, blocktime);
			storageService.saveGlobalStat(dayBlocknumber, blocktime);			
	        statisticService.updateRewardStat(blockNumber);
            spService.updateStoragePoolAmount(blockNumber);
            spService.updateStoragePoolSnNum(blockNumber);
            
    		logger.info("Reward statistic at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Reward statistic error,"+e.getMessage(),e);
            rewardStatistic(retry++);		//Retry;
        }
    }
    
    
    /**
     * lockRelease record
     * @param candidatepledge flowminerpledge
     * @param i2
     * @param i3
     */
    private void pledgeLockRelease(Map<String, Map<String, Object>> candidatepledge, int lockType, int releaseType) {
        if (null != candidatepledge && candidatepledge.size() > 0) {
        	logger.info("blocknumber=" + blockNumber + " candidatepledge=" + candidatepledge.size());
            Map<String,Addresses> addressMap = new HashMap<>();
            TransferMiner tf = new TransferMiner();
            tf.setType(lockType);
            List<TransferMiner> lockList = transferMinerMapper.getLockByType(tf);
            Map<String,TransferMiner> lockMap = new HashMap<>();
            for (TransferMiner lock:lockList) {
                lockMap.put(lock.getAddress(),lock);
            }
            List<TransferMiner> insertList = new ArrayList<>();
            List<TransferMiner> updateList = new ArrayList<>();
            List<TransferMiner> releaseList = new ArrayList<>();
            for (Map.Entry<String, Map<String, Object>> cand : candidatepledge.entrySet()) {
                Map<String, Object> lockValTmp = cand.getValue();
                List<Map<String, Object>>  blockRewardList=new ArrayList();
                if(lockValTmp!=null ) {
                	if(lockValTmp.containsKey("lockamount")) {
                		blockRewardList.add(lockValTmp);
                	}else {
                		for(Object obj:lockValTmp.values()) {
                			blockRewardList.add((Map<String, Object>)obj);
                		}
                	}
                }

                String address = cand.getKey(); 
                for(Map<String, Object> lockVal:blockRewardList) {
                	 Long startblocknumber = Long.parseLong(lockVal.get("startblocknumber").toString());
                     BigDecimal lockamount = new BigDecimal(lockVal.get("lockamount").toString());
                     BigDecimal playment = new BigDecimal(lockVal.get("playment").toString());
                     String revenueaddress = lockVal.get("revenueaddress").toString();
                     
                     TransferMiner node = lockMap.get(cand.getKey());
                     if(node == null){
                     	TransferMiner transferMiner = new TransferMiner();                    
                         transferMiner.setTxHash(UUID.randomUUID().toString());
                         transferMiner.setAddress(address);
                         transferMiner.setBlockNumber(startblocknumber);
                         transferMiner.setTotalAmount(lockamount);
                         transferMiner.setType(lockType);
                         transferMiner.setCurtime(timeStamp);
                   //      transferMiner.setReleaseHeigth(Long.parseLong(lockVal.get("releaseperiod").toString()));
                   //      transferMiner.setReleaseInterval(Long.parseLong(lockVal.get("releaseinterval").toString()));
                   //      transferMiner.setLockNumHeight(Long.parseLong(lockVal.get("lockperiod").toString()));
                         transferMiner.setRevenueaddress(revenueaddress);
                   //      transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+transferMiner.getLockNumHeight());
                         transferMiner.setReleaseamount(playment);                            
                         insertList.add(transferMiner);
                         if(playment.compareTo(BigDecimal.ZERO)!=0){
                         	TransferMiner transRelease= new TransferMiner();
                             transRelease.setTxHash(UUID.randomUUID().toString());
                             transRelease.setRevenueaddress(revenueaddress);
                             transRelease.setAddress(address);
                             transRelease.setBlockNumber(startblocknumber);
                             transRelease.setType(releaseType);
                             transRelease.setUnLockNumber(blockNumber);
                             transRelease.setCurtime(timeStamp);
                             transRelease.setReleaseamount(playment);         
                             releaseList.add(transRelease);                        
                             if(null!=transferMiner.getRevenueaddress()){
                                 addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                             }
                         }
                     }else{
                   //  if (startblocknumber.intValue() != 0 &&node != null) {
                         if (node.getReleaseamount()!=null && node.getReleaseamount().compareTo(playment) == -1) { //plament>node release
                             TransferMiner transferMiner = new TransferMiner();
                             transferMiner.setTxHash(UUID.randomUUID().toString());
                             transferMiner.setAddress(cand.getKey());
                             transferMiner.setBlockNumber(node.getBlockNumber());
                             transferMiner.setType(releaseType);
                             transferMiner.setRevenueaddress(lockVal.get("revenueaddress").toString());
                             transferMiner.setCurtime(timeStamp);
                             transferMiner.setReleaseamount(playment.subtract(node.getReleaseamount()));
                             releaseList.add(transferMiner);
                             //updateMiner
                             node.setReleaseamount(playment);
                             updateList.add(node);
                             if(null!=transferMiner.getRevenueaddress()){
                                 addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                             }
                         }
                     }
                     if(insertList.size()>0&&insertList.size()/Constants.BATCHCOUNT>=1){
                         transferMinerMapper.insertBatch(insertList);
                         insertList = new ArrayList<>();
                     }
                     if(updateList.size()>0&&updateList.size()/Constants.BATCHCOUNT>=1){
                         transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
                         updateList = new ArrayList<>();
                     } 
                     if(releaseList.size()>0&&releaseList.size()/Constants.BATCHCOUNT>=1){
                         transferMinerMapper.insertReleaseBatch(releaseList);
                         releaseList = new ArrayList<>();
                     }
                }               
               
            }
            if(insertList.size()>0){
                transferMinerMapper.insertBatch(insertList);
            }
            if(updateList.size()>0){
                transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
            }
            if(releaseList.size()>0){
                transferMinerMapper.insertReleaseBatch(releaseList);
            }
            for (Map.Entry<String,Addresses> address : addressMap.entrySet()) {
                AddressBalanceModifyJob.getInstance().putq(address.getValue());
            }
        }
    }
    
    private Map<String, Map<String, Map<String, Object>>> getRevenveRelease(Long blockNumber, String type) {
		try {
			int step = 100000;
			Integer startblock = 0;
			Integer endblock = 0;
			Map<String, Map<String, Map<String, Object>>> revenveRelease = new HashMap<>();
			TimeSpend timeSpend0 = new TimeSpend();
			for (int i = 0; i <= (blockNumber / step ); i++) {
				startblock = endblock + 1;				
				endblock = (i + 1) * step > blockNumber ? blockNumber.intValue() : ((i + 1) * step);
				TimeSpend timeSpend = new TimeSpend();
				AlienSnapshot response = web3j.alienSnapshotReleaseAtNumber2(blockNumber.intValue(), type, startblock, endblock).send();
				logger.info(type+" alienSnapshotReleaseAtNumber2 at "+blockNumber+" from "+startblock +" to "+endblock+" spend "+timeSpend.getSpendTime());
				if (response.hasError() || response.getSnapshot() == null) {
					logger.warn(type+" alienSnapshotReleaseAtNumber2 " + blockNumber + " error:" + response.getError().getMessage());
					return null;
				}
				timeSpend = new TimeSpend();
				Map<String, Map<String, Map<String, Object>>> map = response.getSnapshot().getFlowrevenve();
				
				for (String address : map.keySet()) {
					Map<String, Map<String, Object>> increaseData = map.get(address);
					Map<String, Object> increaseLockbalance = increaseData.get("lockbalance");
					Map<String, Object> increaseLockbalance1 = increaseData.get("lockbalanceV1");
					Map<String, Map<String, Object>> fullyData = revenveRelease.get(address);
					if (fullyData == null) {
						fullyData = new HashMap<>();
						revenveRelease.put(address, fullyData);
					}
					Map<String, Object> fullLockbalance = fullyData.get("lockbalance");
					if (fullLockbalance == null) {
						fullLockbalance = new HashMap<>();
						fullyData.put("lockbalance", fullLockbalance);
					}
					fullLockbalance.putAll(increaseLockbalance);
					if(increaseLockbalance1!=null) {
						fullLockbalance.putAll(increaseLockbalance1);
					}
					
				}
			}
			logger.info(type+" alienSnapshotReleaseAtNumber2 at "+blockNumber+" total "+revenveRelease.size()+" spend "+timeSpend0.getSpendTime());
			return revenveRelease;
		} catch (Exception e) {
			logger.warn("getRevenveRelease error", e);
			throw new RuntimeException(e);
		}
	}
    
    private long tfCount =0;
    private static int BATCHCOUNT = 1000;
    
    @SuppressWarnings("unchecked")
	private void handelBandwidthlock(Map<String, Map<String, Map<String, Object>>> flowrevenve,int lockType,int releaseType,String chainType) {
        List<UtgCltStoragedata> cltList = new ArrayList<>();       
        List<TransferMiner> insertList = new ArrayList<>();
        List<TransferMiner> updateList = new ArrayList<>();
        List<TransferMiner> releaseList = new ArrayList<>();
        long bandwidthlockReleaseStarttime = System.currentTimeMillis();
        logger.info("handelBandwidthlock at blocknumber="+blockNumber+"  start .....");
    //    Map<String, Map<String, Map<String, Object>>> flowrevenve= bandwidthlock.getFlowrevenve();   
        if(null!=flowrevenve&&flowrevenve.size()>0) {
            Map<String,Addresses> addressMap = new HashMap<>();
            logger.info("handelBandwidthlock blocknumber=" + blockNumber + " size=" + flowrevenve.size());
            Map<String,Long> maxBlockMap = new HashMap<>();
            List<TransferMiner> maxBlockNumberList = transferMinerMapper.getMaxBlockNumberTransferGroupByAddress();
            for (TransferMiner vo:maxBlockNumberList) {
                maxBlockMap.put(vo.getAddress().toLowerCase(),vo.getBlockNumber());
            }
            TimeSpend timeSpend = new TimeSpend();
            Map<String,TransferMiner> tm9Map = new HashMap<>(10000);
//            TransferMiner tf = new TransferMiner();
//            tf.setType(lockType);
//            List<TransferMiner> tm9 = transferMinerMapper.getTransferMinerList(tf);            
//            for (TransferMiner vo:tm9) {
//                tm9Map.put(vo.getAddress().toLowerCase()+'_'+vo.getBlockNumber(),vo);
//            }
//            logger.info("load transferMiner type"+lockType+" data list size "+tm9.size()+" map size "+tm9Map.size()+" spend "+timeSpend.getSpendTime());
            
            tfCount =0;
            transferMinerMapper.queryByType(lockType, new ResultHandler<TransferMiner>() {
    			@Override
    			public void handleResult(ResultContext<? extends TransferMiner> resultContext) {
    				TransferMiner vo = resultContext.getResultObject();
    				tm9Map.put(vo.getAddress().toLowerCase()+'_'+vo.getBlockNumber()+'_'+vo.getPledgeAddress(),vo);
    				tfCount++;
    				if (tfCount % 10000 == 0)
    					logger.info("bandwidthlock load " + tfCount + " transfer_miner record.");
    			}
    		  });
            logger.info("bandwidthlock load transferMiner data size "+tm9Map.size()+" spend "+timeSpend.getSpendTime());
            
            List<String> excludeList = transferMinerMapper.getRevenueaddressExclude();             
            int lockupInsert = 0;
            int lockupUpdate = 0;
            int releaseCount = 0;
            long recordCount = 0;
            for(Map.Entry<String, Map<String, Map<String, Object>>> record:flowrevenve.entrySet()){
                String address = record.getKey();
                Map<String, Map<String, Object>> rMap = record.getValue();
                Map<String, Object> lockbalance = rMap.get("lockbalance");
                //flowLock and bandwidthrewardlock maxblockNumber
                Long maxBlockNumber = maxBlockMap.get(address.toLowerCase())==null?0L:maxBlockMap.get(address.toLowerCase());
                maxBlockMap.remove(address.toLowerCase());
                //day  bandwidthreward
                UtgCltStoragedata clt = new UtgCltStoragedata(address);
                clt.setBlocknumber(blockNumber);
                clt.setBandwidthreward(BigDecimal.ZERO);
                clt.setReport_time(yesterday);
                clt.setInstime(new Date());
                if (null != lockbalance && lockbalance.size() > 0) {
                    for (Map.Entry<String, Object> lockB: lockbalance.entrySet()) {
                     //   Map<String, Object> lock = (Map<String, Object>)lockB.getValue();
                        Long bNumber = Long.parseLong(lockB.getKey());
                        Date blockDate = getBlockDate(bNumber);
                        Map<String,Map<String, Object>> lockV = (Map<String,  Map<String, Object>>)lockB.getValue();
                        Map<String, Object> bandwidthRewardTmp = lockV.get(chainType);
                        List<Map<String, Object>>  blockRewardList=new ArrayList();
                        if(bandwidthRewardTmp!=null ) {
                        	if(bandwidthRewardTmp.containsKey("lockamount")) {
                        		blockRewardList.add(bandwidthRewardTmp);
                        	}else {
                        		for(Object obj:bandwidthRewardTmp.values()) {
                        			blockRewardList.add((Map<String, Object>)obj);
                        		}
                        	}
                        }
                        for(Map<String, Object> bandwidthReward:blockRewardList) {
                        	String revenueaddress = bandwidthReward.get("revenueaddress").toString();               	
                        	if(excludeList.contains(revenueaddress.toLowerCase())){
                        		logger.debug("revenueaddress "+revenueaddress+" for address "+address+" has been exclude from transfer_miner data");
                        		continue;
                        	}    
                        	String sourceAddress=bandwidthReward.containsKey("contractaddress") ? bandwidthReward.get("contractaddress").toString() : null;
                            if(sourceAddress!=null&&sourceAddress.contains("0000000000000")) {
                            	sourceAddress=null;
                            }
                        	BigDecimal lockamount = new BigDecimal(bandwidthReward.get("lockamount").toString());
                            BigDecimal playment = new BigDecimal(bandwidthReward.get("playment").toString());							
                            String burntaddress   = bandwidthReward.get("burnaddress")!=null ? bandwidthReward.get("burnaddress").toString() : null;
                            BigDecimal burntamount = bandwidthReward.get("burnamount")!=null ? new BigDecimal(bandwidthReward.get("burnamount").toString()) : BigDecimal.ZERO;
                            BigDecimal burntratio = bandwidthReward.get("burnratio")!=null 
									? new BigDecimal(bandwidthReward.get("burnratio").toString()).divide(new BigDecimal(10000)) : BigDecimal.ZERO;
                            if(bNumber>maxBlockNumber){
                                clt.setBandwidthreward(TxDataParse.add(clt.getBandwidthreward(),lockamount));
                            }
                            String key=address.toLowerCase()+"_"+bNumber+"_"+sourceAddress;
                            TransferMiner tm = tm9Map.get(key);
                            tm9Map.remove(key);
//                            TransferMiner tm = transferMinerMapper.getLockedRewardByTypeAddressBlock(lockType,address,bNumber);
                            if(null==tm){
                                TransferMiner transferMiner= new TransferMiner();
                                transferMiner.setTxHash(UUID.randomUUID().toString());
                                transferMiner.setAddress(address);
                                transferMiner.setBlockNumber(bNumber);
                                transferMiner.setTotalAmount(lockamount);
                                transferMiner.setType(lockType);
                                transferMiner.setCurtime(blockDate);
                                transferMiner.setStartTime(timeStamp);
                                if(sourceAddress!=null&&!sourceAddress.contains("000000000000000000000")) {
                                	transferMiner.setPledgeAddress(sourceAddress);
                                }
                                transferMiner.setReleaseHeigth(Long.parseLong(bandwidthReward.get("releaseperiod").toString()));
                                transferMiner.setReleaseInterval(Long.parseLong(bandwidthReward.get("releaseinterval").toString()));
                                transferMiner.setLockNumHeight(Long.parseLong(bandwidthReward.get("lockperiod").toString()));
                                transferMiner.setRevenueaddress(revenueaddress);
                                transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+transferMiner.getLockNumHeight());
                                transferMiner.setReleaseamount(playment);
                                transferMiner.setBurntratio(burntratio);
                                transferMiner.setBurntaddress(burntaddress);
                                transferMiner.setBurntamount(burntamount); 
                                insertList.add(transferMiner);
                                lockupInsert++;
                                if(playment.compareTo(BigDecimal.ZERO)!=0){
                                    TransferMiner transRelease= new TransferMiner();
                                    transRelease.setTxHash(UUID.randomUUID().toString());
                                    transRelease.setRevenueaddress(revenueaddress);
                                    transRelease.setAddress(address);
                                    transRelease.setBlockNumber(bNumber);
                                    transRelease.setType(releaseType);
                                    transRelease.setUnLockNumber(blockNumber);
                                    transRelease.setCurtime(timeStamp);
                                    transRelease.setReleaseamount(playment);
                                    transRelease.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
                                    transRelease.setBurntaddress(burntaddress);                                    
                                    transRelease.setBurntamount(burntamount);
                                    if(sourceAddress!=null&&!sourceAddress.contains("000000000000000000000")) {
                                    	transRelease.setPledgeAddress(sourceAddress);
                                    }
                                    releaseList.add(transRelease);	
                                    releaseCount ++;
                                    if(null!=transferMiner.getRevenueaddress()){
                                        addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                                    }
                                }
                            }else{
                                if(tm.getReleaseamount()!=null && tm.getReleaseamount().compareTo(playment) == -1){
                                    TransferMiner transferMiner= new TransferMiner();
                                    transferMiner.setTxHash(UUID.randomUUID().toString());
                                    transferMiner.setRevenueaddress(revenueaddress);
                                    transferMiner.setAddress(address);
                                    transferMiner.setBlockNumber(bNumber);
                                    transferMiner.setType(releaseType);
                                    transferMiner.setUnLockNumber(blockNumber);
                                    transferMiner.setCurtime(timeStamp);
                                    if(sourceAddress!=null&&!sourceAddress.contains("000000000000000000000")) {
                                    	transferMiner.setPledgeAddress(sourceAddress);
                                    }
                                    transferMiner.setReleaseamount(playment.subtract(tm.getReleaseamount()));
                                    transferMiner.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
                                    transferMiner.setBurntaddress(burntaddress);
                                    BigDecimal lastBurntamount = tm.getBurntamount()==null ? BigDecimal.ZERO : tm.getBurntamount();
                                    transferMiner.setBurntamount(burntamount.subtract(lastBurntamount));                                 
                                    releaseList.add(transferMiner);
                                    releaseCount ++;
                                    tm.setReleaseamount(playment);
                                    tm.setBurntratio(burntratio);
                                    tm.setBurntaddress(burntaddress);
                                    tm.setBurntamount(burntamount);
                                    
                                    updateList.add(tm);
                                    lockupUpdate ++;
                                    if(null!=transferMiner.getRevenueaddress()){
                                        addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                                    }
                                }
                            }
                            if(insertList.size()>0&&insertList.size()/BATCHCOUNT>=1){
                            	TimeSpend ts = new TimeSpend();
                                transferMinerMapper.insertBatch(insertList);
                                logger.info("bandwidthlock insert "+insertList.size()+" record spend "+ts.getSpendTime());
                                insertList = new ArrayList<>();
                            }
                            if(updateList.size()>0&&updateList.size()/BATCHCOUNT>=1){
                            	TimeSpend ts = new TimeSpend();
                                transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
                                logger.info("bandwidthlock update "+updateList.size()+" record spend "+ts.getSpendTime());
                                updateList = new ArrayList<>();
                            }
                            if(releaseList.size()>0&&releaseList.size()/BATCHCOUNT>=1){
                            	TimeSpend ts = new TimeSpend();
                                transferMinerMapper.insertReleaseBatch(releaseList);
                                logger.info("bandwidthlock release "+releaseList.size()+" record spend "+ts.getSpendTime());
                                releaseList = new ArrayList<>();
                            }
                            recordCount ++;
                            if(recordCount%10000==0)
                            	logger.info("bandwidthlock handle "+recordCount+" reward data");
                        }
                    }
                }
                //day bandwidthreward
                if(clt.getBandwidthreward().compareTo(BigDecimal.ZERO)!=0){
                    clt.setProfitamount(clt.getBandwidthreward());
                    clt.setFwflag(1);
                    cltList.add(clt);
                }
                if(cltList.size()>0&&cltList.size()/Constants.BATCHCOUNT>=1){
                    utgStorageMinerMapper.batchSaveFlwDataDay(cltList);
                    cltList = new ArrayList<>();
                }
            }
            if(cltList.size()>0){
                utgStorageMinerMapper.batchSaveFlwDataDay(cltList);
            }
            if(insertList.size()>0){
            	TimeSpend ts = new TimeSpend();
                transferMinerMapper.insertBatch(insertList);
                logger.info("bandwidthlock insert "+insertList.size()+" record spend "+ts.getSpendTime());
            }
            if(updateList.size()>0){
            	TimeSpend ts = new TimeSpend();
                transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
                logger.info("bandwidthlock update "+updateList.size()+" record spend "+ts.getSpendTime());
            }
            if(releaseList.size()>0){
            	TimeSpend ts = new TimeSpend();
                transferMinerMapper.insertReleaseBatch(releaseList);
                logger.info("bandwidthlock release "+releaseList.size()+" record spend "+ts.getSpendTime());
            }
            for (Map.Entry<String,Addresses> address : addressMap.entrySet()) {
                AddressBalanceModifyJob.getInstance().putq(address.getValue());
            }
            logger.info("handelBandwidthlock at blocknumber="+blockNumber+" total "+recordCount+" data , lockup insert "+lockupInsert+", update "+lockupUpdate+" and release "+releaseCount+
            		" spend "+timeSpend.getSpendTime());        
        }
        long bandwidthlockReleaseEndtime = System.currentTimeMillis();
        logger.info("handelBandwidthlock at blocknumber="+blockNumber+" bandwidthlockRelease end ,spends total second ="+(bandwidthlockReleaseEndtime-bandwidthlockReleaseStarttime)/1000);
    }
    
    @SuppressWarnings("unchecked")
    private void flowrevenvelock(Map<String, Map<String, Map<String, Object>>> flowrevenve,int lockType,int releaseType,String chainType) {     
        if(null!=flowrevenve&&flowrevenve.size()>0) {
        	List<TransferMiner> insertList = new ArrayList<>();
        	List<TransferMiner> updateList = new ArrayList<>();
        	List<TransferMiner> releaseList = new ArrayList<>();
            Map<String,Addresses> addressMap = new HashMap<>();
            logger.info("revenvelock type["+lockType+"] at blocknumber=" + blockNumber + " size=" + flowrevenve.size());
//            Map<String,Long> maxBlockMap = new HashMap<>();
//            List<TransferMiner> maxBlockNumberList = transferMinerMapper.getMaxBlockNumberTransferGroupByAddress();
//            for (TransferMiner vo:maxBlockNumberList) {
//                maxBlockMap.put(vo.getAddress().toLowerCase(),vo.getBlockNumber());
//            }
            
            TimeSpend timeSpend = new TimeSpend();
            Map<String,TransferMiner> existMap = new HashMap<>(10000);
//            TransferMiner tf = new TransferMiner();
//            tf.setType(lockType);
//            List<TransferMiner> existList = transferMinerMapper.getTransferMinerList(tf);
//            for (TransferMiner vo:existList) {
//            	existMap.put(vo.getAddress().toLowerCase()+'_'+vo.getBlockNumber(),vo);
//            }
//            logger.info("load transferMiner type"+lockType+" data list size "+existList.size()+" map size "+existMap.size()+" spend "+timeSpend.getSpendTime());

            tfCount = 0;      
            transferMinerMapper.queryByType(lockType, new ResultHandler<TransferMiner>() {
    			@Override
    			public void handleResult(ResultContext<? extends TransferMiner> resultContext) {
    				TransferMiner vo = resultContext.getResultObject();
    				existMap.put(vo.getAddress().toLowerCase()+'_'+vo.getBlockNumber()+'_'+vo.getPledgeAddress(),vo);
    				tfCount++;
    				if (tfCount % 10000 == 0)
    					logger.info("revenvelock type["+lockType+"] load " + tfCount + " transfer_miner record.");
    			}
    		});
            logger.info("revenvelock type["+lockType+"] load transferMiner data size "+existMap.size()+" spend "+timeSpend.getSpendTime());
            
            List<String> excludeList = transferMinerMapper.getRevenueaddressExclude();    
            int lockupInsert = 0;
            int lockupUpdate = 0;
            int releaseCount = 0;
            long recordCount = 0;            
            for(Map.Entry<String, Map<String, Map<String, Object>>> record:flowrevenve.entrySet()){
                String address = record.getKey();
                Map<String, Map<String, Object>> rMap = record.getValue();
                Map<String, Object> lockbalanceV0 = rMap.get("lockbalance");
                Map<String, Object> lockbalanceV1 = rMap.get("lockbalanceV1");
                Map<String, Object> lockbalance=new HashMap<String, Object>();
                if(lockbalanceV0!=null) {
                	lockbalance.putAll(lockbalanceV0);
                }
                if(lockbalanceV1!=null) {
                	lockbalance.putAll(lockbalanceV1);
                }
                //flowLock and bandwidthrewardlock maxblockNumber
//                Long maxBlockNumber = maxBlockMap.get(address.toLowerCase())==null?0L:maxBlockMap.get(address.toLowerCase());
//                maxBlockMap.remove(address.toLowerCase());
                if ( lockbalance.size() > 0) {
                    for (Map.Entry<String, Object> lockB: lockbalance.entrySet()) {
//                        Map<String, Object> lock = (Map<String, Object>)lockB.getValue();
                        Long bNumber = Long.parseLong(lockB.getKey());
                        Date blockDate = getBlockDate(bNumber);
                        Map<String,Map<String, Object>> lockV = (Map<String,  Map<String, Object>>)lockB.getValue();
                        Map<String, Object> rewardDataTmp = lockV.get(chainType);
                        List<Map<String, Object>>  blockRewardList=new ArrayList();
                        if(rewardDataTmp!=null ) {
                        	if(rewardDataTmp.containsKey("lockamount")) {
                        		blockRewardList.add(rewardDataTmp);
                        	}else {
                        		for(Object obj:rewardDataTmp.values()) {
                        			blockRewardList.add((Map<String, Object>)obj);
                        		}
                        	}
                        }
                        for(Map<String, Object> rewardData:blockRewardList) {                     	
                            	String revenueaddress = rewardData.get("revenueaddress").toString();               	
                            	if(excludeList.contains(revenueaddress.toLowerCase())){
                            		logger.debug("revenueaddress "+revenueaddress+" for address "+address+" has been exclude from transfer_miner data");		//TODO
                            		continue;
                            	}
                                BigDecimal lockamount = new BigDecimal(rewardData.get("lockamount").toString());
                                BigDecimal playment = new BigDecimal(rewardData.get("playment").toString());                            
    							String burntaddress = rewardData.get("burnaddress")!=null ? rewardData.get("burnaddress").toString() : null;
    							BigDecimal burntamount = rewardData.get("burnamount")!=null ? new BigDecimal(rewardData.get("burnamount").toString()) : BigDecimal.ZERO;
    							BigDecimal burntratio = rewardData.get("burnratio")!=null
    									? new BigDecimal(rewardData.get("burnratio").toString()).divide(new BigDecimal(10000)) : BigDecimal.ZERO;						
    							String sourceAddress=rewardData.containsKey("contractaddress") ? rewardData.get("contractaddress").toString() : null;
    							//sourceAddress=sourceAddress.contains("000000000000000000000")?null:sourceAddress;
    							TransferMiner tm = existMap.get(address.toLowerCase()+"_"+bNumber+'_'+sourceAddress);
                                existMap.remove(address.toLowerCase()+"_"+bNumber);
//    							TransferMiner tm = transferMinerMapper.getLockedRewardByTypeAddressBlock(lockType,address,bNumber);		
                                if(null==tm){
                                    TransferMiner transferMiner= new TransferMiner();
                                    transferMiner.setTxHash(UUID.randomUUID().toString());
                                    transferMiner.setAddress(address);
                                    transferMiner.setBlockNumber(bNumber);
                                    transferMiner.setTotalAmount(lockamount);
                                    transferMiner.setType(lockType);                                
                                    transferMiner.setCurtime(blockDate);
                                    transferMiner.setStartTime(timeStamp);
                                    transferMiner.setReleaseHeigth(Long.parseLong(rewardData.get("releaseperiod").toString()));
                                    transferMiner.setReleaseInterval(Long.parseLong(rewardData.get("releaseinterval").toString()));
                                    transferMiner.setLockNumHeight(Long.parseLong(rewardData.get("lockperiod").toString()));
                                    transferMiner.setRevenueaddress(revenueaddress);
    								transferMiner.setPledgeAddress(sourceAddress);
                                    transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+transferMiner.getLockNumHeight());
                                    transferMiner.setReleaseamount(playment);
                                    transferMiner.setBurntratio(burntratio);
                                    transferMiner.setBurntaddress(burntaddress);
                                    transferMiner.setBurntamount(burntamount); 
                                    insertList.add(transferMiner);
                                    lockupInsert++;
                                    if(playment.compareTo(BigDecimal.ZERO)!=0){
                                        TransferMiner transRelease= new TransferMiner();
                                        transRelease.setTxHash(UUID.randomUUID().toString());
                                        transRelease.setRevenueaddress(revenueaddress);
                                        transRelease.setPledgeAddress(sourceAddress);
                                        transRelease.setAddress(address);
                                        transRelease.setBlockNumber(bNumber);
                                        transRelease.setType(releaseType);
                                        transRelease.setUnLockNumber(blockNumber);
                                        transRelease.setCurtime(timeStamp);
                                        transRelease.setReleaseamount(playment);
                                        transRelease.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
                                        transRelease.setBurntaddress(burntaddress);
                                        transRelease.setBurntamount(burntamount);
                                        releaseList.add(transRelease);	
                                        releaseCount ++;
                                        if(null!=transferMiner.getRevenueaddress()){
                                            addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                                        }
                                    }
                                }else{
                                    if(tm.getReleaseamount()!=null && tm.getReleaseamount().compareTo(playment) == -1){ //plament>node release
                                        TransferMiner transferMiner= new TransferMiner();
                                        transferMiner.setTxHash(UUID.randomUUID().toString());
                                        transferMiner.setRevenueaddress(revenueaddress);
                                        transferMiner.setPledgeAddress(rewardData.containsKey("contractaddress") ? rewardData.get("contractaddress").toString() : null);
                                        transferMiner.setAddress(address);
                                        transferMiner.setBlockNumber(bNumber);
                                        transferMiner.setType(releaseType);
                                        transferMiner.setUnLockNumber(blockNumber);
                                        transferMiner.setCurtime(timeStamp);
                                        transferMiner.setReleaseamount(playment.subtract(tm.getReleaseamount()));
                                        transferMiner.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
                                        transferMiner.setBurntaddress(burntaddress);
                                        BigDecimal lastBurntamount = tm.getBurntamount()==null ? BigDecimal.ZERO : tm.getBurntamount();
                                        transferMiner.setBurntamount(burntamount.subtract(lastBurntamount));                                    
                                        releaseList.add(transferMiner);
                                        releaseCount ++;
                                        tm.setReleaseamount(playment);
                                        tm.setBurntratio(burntratio);
                                        tm.setBurntaddress(burntaddress);
                                        tm.setBurntamount(burntamount);
                                        updateList.add(tm);
                                        lockupUpdate ++;
                                        if(null!=transferMiner.getRevenueaddress()){
                                            addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                                        }
                                    }
                                }
                                if(insertList.size()>0&&insertList.size()/BATCHCOUNT>=1){
                                	TimeSpend ts = new TimeSpend();
                                    transferMinerMapper.insertBatch(insertList);
                                    logger.info("revenvelock type["+lockType+"] insert "+insertList.size()+" record spend "+ts.getSpendTime());
                                    insertList = new ArrayList<>();
                                }
                                if(updateList.size()>0&&updateList.size()/BATCHCOUNT>=1){
                                	TimeSpend ts = new TimeSpend();
                                    transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
                                    logger.info("revenvelock type["+lockType+"] update "+updateList.size()+" record spend "+ts.getSpendTime());
                                    updateList = new ArrayList<>();
                                }  
                                if(releaseList.size()>0&&releaseList.size()/BATCHCOUNT>=1){
                                	TimeSpend ts = new TimeSpend();
                                    transferMinerMapper.insertReleaseBatch(releaseList);
                                    logger.info("revenvelock type["+lockType+"] release "+releaseList.size()+" record spend "+ts.getSpendTime());
                                    releaseList = new ArrayList<>();
                                } 
                                recordCount ++;
                                if(recordCount%10000==0)
                                	logger.info("revenvelock type["+lockType+"] handle "+recordCount+" reward data");                            
                            
                        }
                        
                        
                    }
                }                
            }  
            if(insertList.size()>0){
            	TimeSpend ts = new TimeSpend();
                transferMinerMapper.insertBatch(insertList);
                logger.info("revenvelock type["+lockType+"] insert "+insertList.size()+" record spend "+ts.getSpendTime());
            }
            if(updateList.size()>0){
            	TimeSpend ts = new TimeSpend();
                transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
                logger.info("revenvelock type["+lockType+"] update "+updateList.size()+" record spend "+ts.getSpendTime());
            }
            if(releaseList.size()>0){
            	TimeSpend ts = new TimeSpend();
                transferMinerMapper.insertReleaseBatch(releaseList);
                logger.info("revenvelock type["+lockType+"] release "+releaseList.size()+" record spend "+ts.getSpendTime());
            }
            logger.info("revenvelock type["+lockType+"] at blocknumber="+blockNumber+" total "+recordCount+" data ,lockup insert "+lockupInsert+", update "+lockupUpdate+" and release "+releaseCount+
            		" spend "+timeSpend.getSpendTime()); 
            for (Map.Entry<String,Addresses> address : addressMap.entrySet()) {
                AddressBalanceModifyJob.getInstance().putq(address.getValue());
            }
        }
    }
    
	private Map<Long, Date> blockDateMap = new HashMap<>();
	private Date getBlockDate(Long blockNumber) {
		Date blockDate = blockDateMap.get(blockNumber);
		if (blockDate == null) {
			try {
				EthBlock response = web3j.ethGetBlockByNumber2(new DefaultBlockParameterNumber(blockNumber), true).send();
				blockDate = new Date(response.getBlock().getTimestamp().longValue() * 1000);
				blockDateMap.put(blockNumber, blockDate);
			} catch (IOException e) {
				logger.warn("ethGetBlockByNumber error:", e);
				blockDate = timeStamp;
			}
		}
		return blockDate;
	}
	private Map<String, Map<String, Map<String, Object>>> getSnapshotReleaseAtNumber2(Long blockNumber, String type,long  startblock,long endblock){
		
		return null;
	}
	protected void storageSpLockRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge pool splock release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge pool splock release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge pool splock release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		      
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"splock");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 21, 22, spLockRewardType);
    		
    		logger.info("Strorge pool splock release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge pool splock release error,"+e.getMessage(),e);
            storageSpaceRelease(retry++);		//Retry;
        }
    }
	protected void storageSpEntrustRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge pool Entrust release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge pool Entrust release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge pool Entrust release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		      
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"spentrustlock");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 23, 24, spEntrustlockRewardType);
    		
    		logger.info("Strorge pool Entrust release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge pool Entrust release error,"+e.getMessage(),e);
            storageSpaceRelease(retry++);		//Retry;
        }
    }
	protected void storageSpExitRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge pool SpExit release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge pool SpExit release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge pool SpExit release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		      
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"spexit");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 25, 26, spExitRewardType);
    		
    		logger.info("Strorge pool SpExit release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge pool SpExit release error,"+e.getMessage(),e);
            storageSpaceRelease(retry++);		//Retry;
        }
    }
	protected void storageSpEntrustExitRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge pool EntrustExit release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge pool EntrustExit release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge pool EntrustExit release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		      
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"spentrustexit");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 27, 28, spEntrustExitRewardType);
    		
    		logger.info("Strorge pool EntrustExit release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge pool EntrustExit release error,"+e.getMessage(),e);
            storageSpaceRelease(retry++);		//Retry;
        }
    }
	protected void storageSnEntrustExitRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge  EntrustExit release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge  EntrustExit release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge  EntrustExit release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		      
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"stpentrustexit");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 13, 14, stEntrustExitRewardType);
    		
    		logger.info("Strorge  EntrustExit release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge  EntrustExit release error,"+e.getMessage(),e);
            storageSpaceRelease(retry++);		//Retry;
        }
    }
	protected void storageSnEntrustRewardRelease(int retry){
    	if(retry>retryTimes){
    		logger.warn("Strorge  Entrust release has retry "+retry+" times and skip.");
    		return;
    	}
    	if(retry>0)
    		logger.warn("Strorge  Entrust release will retry "+retry+" times.");
    	try {
    		logger.info("Strorge  Entrust release at "+blockNumber+" start");
    		TimeSpend timeSpend = new TimeSpend();    		      
            Map<String, Map<String, Map<String, Object>>> bandwidthlock = getRevenveRelease(blockNumber,"stpentrust");
            if(bandwidthlock!=null)
            	handelBandwidthlock(bandwidthlock, 11, 12, stEntrustRewardType);
    		
    		logger.info("Strorge  Entrust release at "+blockNumber+" finish, spend "+timeSpend.getSpendTime());
    	}catch (Exception e) {
            logger.error("Strorge  Entrust release error,"+e.getMessage(),e);
            storageSpaceRelease(retry++);		//Retry;
        }
    }
}
