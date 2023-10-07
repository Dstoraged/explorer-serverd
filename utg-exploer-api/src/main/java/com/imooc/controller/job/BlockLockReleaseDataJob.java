package com.imooc.controller.job;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.Utils.TransferAddUtil;
import com.imooc.job.service.StatisticService;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.TransferMiner;
import com.imooc.pojo.UtgCltStoragedata;
import com.imooc.service.util.TxDataParse;
import com.imooc.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.AlienSnapshot;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhc 2021-11-18 13:56
 */
public class BlockLockReleaseDataJob extends AgentSvcTask {
    private static Logger logger = LoggerFactory.getLogger(BlockLockReleaseDataJob.class);
    private  UtgStorageMinerMapper utgStorageMinerMapper;
    private TransferMinerMapper transferMinerMapper;
    private StatisticService statisticService;
    private Long blockNumber;
    private Web3j web3j;
    private  Date timeStamp;
    private Date yesterday;
    private Long dayOneNumber;
    private String decimals = "1000000000000000000";
    private String blockRewardType = "3";
    private String bandwidthRewardType = "5";

    public BlockLockReleaseDataJob(Date yesterday, Date timeStamp, Web3j web3j, Long blockNumber, Long dayOneNumber,
    		UtgStorageMinerMapper utgStorageMinerMapper,TransferMinerMapper transferMinerMapper,StatisticService statisticService) {
        this.yesterday = yesterday;
        this.timeStamp = timeStamp;
        this.web3j = web3j;
        this.blockNumber = blockNumber;
        this.dayOneNumber = dayOneNumber;
        this.utgStorageMinerMapper = utgStorageMinerMapper;
        this.transferMinerMapper = transferMinerMapper;
        this.statisticService  = statisticService;
    }

    protected  void runTask() {
        long starttime = System.currentTimeMillis();
        logger.info("BlockLockReleaseDataJob at block "+blockNumber+" start .....");
        try {
            //block lock and release
            blockLockRelease();
            TransferAddUtil t = new TransferAddUtil();
            t.transferAdd(transferMinerMapper,blockNumber,1);             
            statisticService.updateRewardStat(blockNumber);
        }catch (Exception e) {
            logger.error("BlockLockReleaseDataJob error,"+e.getMessage(),e);
        }
        long endtime = System.currentTimeMillis();
        logger.info("BlockLockReleaseDataJob at block "+blockNumber+" end ,spends total second ="+(endtime-starttime)/1000);
    }


    private void blockLockRelease() throws Exception {
        AlienSnapshot.Snapshot rewardlock = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),"rewardlock").send().getSnapshot();
        lockRewardlock(rewardlock);
    }

    private void lockRewardlock(AlienSnapshot.Snapshot rewardlock) {
    	List<TransferMiner> insertList = new ArrayList<>();
        List<TransferMiner> updateList = new ArrayList<>();
        List<TransferMiner> releaseList = new ArrayList<>();
        long rewardLockReleaseStarttime = System.currentTimeMillis();        
        if(rewardlock==null)
        	return;
        Map<String, Map<String, Map<String, Object>>> flowrevenve= rewardlock.getFlowrevenve();
        //flow lock and release
        if(null!=flowrevenve&&flowrevenve.size()>0){
            Map<String,Addresses> addressMap = new HashMap<>();
            logger.info("lockRewardlock at "+blockNumber+" rewardlock="+flowrevenve.size());
            TransferMiner tf = new TransferMiner();
            tf.setType(1);
            List<TransferMiner> tm1 = transferMinerMapper.getTransferMinerList(tf);
            Map<String,TransferMiner> tm1Map = new HashMap<>();
            for (TransferMiner vo:tm1) {
                tm1Map.put(vo.getAddress().toLowerCase()+'_'+vo.getBlockNumber()+"_"+vo.getPledgeAddress(),vo);
            }
            for(Map.Entry<String, Map<String, Map<String, Object>>> record:flowrevenve.entrySet()){
                String address = record.getKey();
                Map<String, Map<String, Object>> rMap = record.getValue();
                Map<String, Object> lockbalanceTmp = rMap.get("lockbalance");
                Map<String, Object> lockbalanceTmpV1 = rMap.get("lockbalanceV1");
                List<Map<String, Object>> lockBalanceList=new ArrayList<Map<String,Object>>();
                if(lockbalanceTmp!=null) {
                	lockBalanceList.add(lockbalanceTmp);
                }
                if(lockbalanceTmpV1!=null) {
                	lockBalanceList.add(lockbalanceTmpV1);
                }
                
                for(Map<String, Object> lockbalance:lockBalanceList) {
                if (lockbalance.size() > 0) {
                    for (Map.Entry<String, Object> lockB: lockbalance.entrySet()) {
                        Map<String, Object> lock = (Map<String, Object>)lockB.getValue();
                        Long bNumber = Long.parseLong(lockB.getKey());
                        Map<String,Map<String, Object>> lockV = (Map<String,  Map<String, Object>>)lockB.getValue();
                        Map<String, Object> blockRewardMap = lockV.get(blockRewardType);
                        List<Map<String, Object>>  blockRewardList=new ArrayList();
                        if(blockRewardMap!=null ) {
                        	if(blockRewardMap.containsKey("lockamount")) {
                        		blockRewardList.add(blockRewardMap);
                        	}else {
                        		for(Object obj:blockRewardMap.values()) {
                        			blockRewardList.add((Map<String, Object>)obj);
                        		}
                        	}
                        }
                        if(blockRewardList.size()>0) {
                        	for( Map<String, Object> blockReward:blockRewardList) {
                                BigDecimal lockamount = new BigDecimal(blockReward.get("lockamount").toString());
                                BigDecimal playment = new BigDecimal(blockReward.get("playment").toString());
    							String burntaddress = blockReward.get("burnaddress")!=null ? blockReward.get("burnaddress").toString() : null;
    							BigDecimal burntamount = blockReward.get("burnamount")!=null ? new BigDecimal(blockReward.get("burnamount").toString()) : BigDecimal.ZERO;
    							BigDecimal burntratio = blockReward.get("burnratio")!=null
    									? new BigDecimal(blockReward.get("burnratio").toString()).divide(new BigDecimal(10000)) : BigDecimal.ZERO;							
                                String sourceAddress= blockReward.containsKey("contractaddress") ? blockReward.get("contractaddress").toString() : null;
    							String key=address.toLowerCase()+"_"+bNumber+"_"+sourceAddress;
    							TransferMiner tm = tm1Map.get(key);
                                tm1Map.remove(key);
                                if(null==tm){
                                    TransferMiner transferMiner= new TransferMiner();
                                    transferMiner.setTxHash(UUID.randomUUID().toString());
                                    transferMiner.setAddress(address);
                                    transferMiner.setBlockNumber(bNumber);
                                    transferMiner.setTotalAmount(lockamount);
                                    transferMiner.setType(1);
                                    transferMiner.setCurtime(timeStamp);
                                    transferMiner.setReleaseHeigth(Long.parseLong(blockReward.get("releaseperiod").toString()));
                                    transferMiner.setReleaseInterval(Long.parseLong(blockReward.get("releaseinterval").toString()));
                                    transferMiner.setLockNumHeight(Long.parseLong(blockReward.get("lockperiod").toString()));
                                    transferMiner.setRevenueaddress(blockReward.get("revenueaddress").toString());
                                    transferMiner.setPledgeAddress(sourceAddress);
                                    transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+transferMiner.getLockNumHeight());
                                    transferMiner.setReleaseamount(playment);
                                    transferMiner.setBurntratio(burntratio);
                                    transferMiner.setBurntaddress(burntaddress);
                                    transferMiner.setBurntamount(burntamount);               
                                    insertList.add(transferMiner);
                                    if(playment.compareTo(BigDecimal.ZERO)!=0){
                                        TransferMiner transRelease= new TransferMiner();
                                        transRelease.setTxHash(UUID.randomUUID().toString());
                                        transRelease.setRevenueaddress(blockReward.get("revenueaddress").toString());
                                        transRelease.setPledgeAddress(sourceAddress);
                                        transRelease.setAddress(address);
                                        transRelease.setBlockNumber(bNumber);
                                        transRelease.setType(2);
                                        transRelease.setUnLockNumber(blockNumber);
                                        transRelease.setCurtime(timeStamp);
                                        transRelease.setReleaseamount(playment);
                                        transRelease.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
                                        transRelease.setBurntaddress(burntaddress);
                                        transRelease.setBurntamount(burntamount);
                                        releaseList.add(transRelease);
                                        if(null!=transferMiner.getRevenueaddress()){
                                            addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                                        }
                                    }
                                }else{
                                    if(tm.getReleaseamount()!=null && tm.getReleaseamount().compareTo(playment) == -1){
                                        TransferMiner transferMiner= new TransferMiner();
                                        transferMiner.setTxHash(UUID.randomUUID().toString());
                                        transferMiner.setRevenueaddress(blockReward.get("revenueaddress").toString());
                                        transferMiner.setPledgeAddress(sourceAddress);
                                        transferMiner.setAddress(address);
                                        transferMiner.setBlockNumber(bNumber);
                                        transferMiner.setType(2);
                                        transferMiner.setUnLockNumber(blockNumber);
                                        transferMiner.setCurtime(timeStamp);
                                        transferMiner.setReleaseamount(playment.subtract(tm.getReleaseamount()));
                                        transferMiner.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
                                        transferMiner.setBurntaddress(burntaddress);
                                        BigDecimal lastBurntamount = tm.getBurntamount()==null ? BigDecimal.ZERO : tm.getBurntamount();
                                        transferMiner.setBurntamount(burntamount.subtract(lastBurntamount));                                    
                                        releaseList.add(transferMiner);
                                        tm.setReleaseamount(playment);
                                        tm.setBurntratio(burntratio);
                                        tm.setBurntaddress(burntaddress);
                                        tm.setBurntamount(burntamount);
                                        updateList.add(tm);
                                        if(null!=transferMiner.getRevenueaddress()){
                                            addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                                        }
                                    }
                                }
                                if(insertList.size()>0&& insertList.size()/Constants.BATCHCOUNT>=1){
                                    transferMinerMapper.insertBatch(insertList);
                                    insertList = new ArrayList<>();
                                }
                                if(updateList.size()>0&& updateList.size()/Constants.BATCHCOUNT>=1){
                                    transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
                                    updateList = new ArrayList<>();
                                }
                                if(releaseList.size()>0&& releaseList.size()/Constants.BATCHCOUNT>=1){
                                    transferMinerMapper.insertReleaseBatch(releaseList);
                                    releaseList = new ArrayList<>();
                                }
                        	}
                        }
                    }
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
        AddressBalanceModifyJob.getInstance().putq(new Addresses(Constants.addressZero,blockNumber));
        long rewardLockReleaseEndtime = System.currentTimeMillis();        
        logger.info("lockRewardlock at "+blockNumber+" end ,spends total second ="+(rewardLockReleaseEndtime-rewardLockReleaseStarttime)/1000);
    }

}
