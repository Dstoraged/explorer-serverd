package com.imooc.job.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.AlienSnapshot;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.AlienSnapshot.Snapshot;

import com.imooc.Utils.TimeSpend;
import com.imooc.enums.MinerStatusEnum;
import com.imooc.enums.NodeTypeEnum;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.StorageReleaseMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.pojo.StorageReleaseCompare;
import com.imooc.pojo.StorageReleaseDetail;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.TransferMiner;
import com.imooc.pojo.UtgCltStoragedata;
import com.imooc.pojo.UtgNetStatics;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.utils.Constants;
import com.imooc.utils.DateUtil;
import com.imooc.utils.NumericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RewardService {

	public enum RewardType {		
		block		(1,  2,  "rewardlock",	 "3", 11, "blockLock"), 
		space		(9,  10, "bandwidthlock","5", 12, "spaceLock"), 
		lease		(5,  6,  "flowlock", 	 "4", 13, "leaseLock"),
		posplexit	(17, 18, "posplexit", 	 "6", 14, "revertLock"),
		nodeexit	(3,  4,  "candidatepledge", null, null, null),
		minerexit	(7,  8,  "flowminerpledge", null, null, null),
	//	pays 		(null,null,null,15, null),
		;
					
		Integer lockupType;
		Integer releaseType;
		String  snapshotType;
		String 	snapshotKey;
		
		Integer lockupV2;
		String  snapshotV2;	

		RewardType(Integer lockupType,Integer releaseType,String snapshotType,String snapshotKey,Integer lockupV2,String snapshotV2) {
			this.lockupType = lockupType;
			this.releaseType = releaseType;
			this.snapshotType = snapshotType;
			this.snapshotKey = snapshotKey;
			this.lockupV2 = lockupV2;
			this.snapshotV2 = snapshotV2;
		}
		public Integer getLockupType() {
			return lockupType;
		}
		public Integer getReleaseType() {
			return releaseType;
		}
		public String getSnapshotType() {
			return snapshotType;
		}
		public String getSnapshotKey() {
			return snapshotKey;
		}		
	}
	private static Integer paysType = 15;
	
	@Autowired
	TransferMinerMapper transferMinerMapper;
	@Autowired
	StorageReleaseMapper storageReleaseMapper;
	@Autowired
	UtgStorageMinerMapper utgStorageMinerMapper;
	@Autowired
    NodeExitMapper nodeExitMapper;
	@Autowired
    TransactionMapper transactionMapper;
	@Autowired
	Web3jService web3jService;
	@Autowired
	AddressService addressService;	
	@Autowired
	StorageService storageService;
	
	@Value("${dayOneNumber}")
    private Long dayOneNumber;	//1 day block height, 10 seconds to generate a block
	
	@Value("${releaseCompareEnable:true}")
    private Boolean releaseCompareEnable;

	@Async
	public void blockReleaseReward(Long blockNumber, Date blockDate) {
		TimeSpend timeSpend = new TimeSpend();
		log.info("BlockReleaseReward at " + blockNumber + " start ...");

		Snapshot snapshopt = web3jService.getSnapshotRelease(blockNumber, RewardType.block.snapshotType);
		if (snapshopt != null && snapshopt.getFlowrevenve() !=null && snapshopt.getFlowrevenve().size() > 0) {			
			saveRewardData(snapshopt.getFlowrevenve(), RewardType.block, blockNumber, blockDate);
			transferAdd(blockNumber, 1);
		}
		log.info("BlockReleaseReward at " + blockNumber + " end , spends " + timeSpend.getSpendTime());
	} 

	@Async
	public void lockReleaseReward(Long blockNumber, Date blockDate) {
		TimeSpend timeSpend = new TimeSpend();
		log.info("LockReleaseReward at " + blockNumber + " start ...");		
		Date yesterday = DateUtil.getBeginDayOfYesterday();
	
		Snapshot bandwidthlock = web3jService.getSnapshotRelease(blockNumber, RewardType.space.snapshotType);
		if (bandwidthlock != null && bandwidthlock.getFlowrevenve() != null && bandwidthlock.getFlowrevenve().size() > 0) {		
		//	saveRewardData(bandwidthlock.getFlowrevenve(), RewardType.space, blockNumber, blockDate);
		//	saveUtgCltStoragedata(bandwidthlock.getFlowrevenve(), RewardType.space.rpcType, blockNumber, yesterday);
			saveStorageReward(bandwidthlock.getFlowrevenve(), RewardType.space, blockNumber, blockDate, yesterday);
		}

		Snapshot leaselock = web3jService.getSnapshotRelease(blockNumber, RewardType.lease.snapshotType);
		if (leaselock != null && leaselock.getFlowrevenve() != null && leaselock.getFlowrevenve().size() > 0)
			saveRewardData(leaselock.getFlowrevenve(), RewardType.lease, blockNumber, blockDate);

		AlienSnapshot.Snapshot posplexit = web3jService.getSnapshotRelease(blockNumber, RewardType.posplexit.snapshotType);
		if (posplexit != null && posplexit.getFlowrevenve() != null && posplexit.getFlowrevenve().size() > 0)
			saveRewardData(posplexit.getFlowrevenve(), RewardType.posplexit, blockNumber, blockDate);


		Snapshot candidatepledge = web3jService.getSnapshotRelease(blockNumber, RewardType.nodeexit.snapshotType);
		if (candidatepledge != null && candidatepledge.getCandidatepledge() != null && candidatepledge.getCandidatepledge().size() > 0)
			savePlexitData(candidatepledge.getCandidatepledge(), RewardType.nodeexit, blockNumber, blockDate);

		Snapshot flowminerpledge = web3jService.getSnapshotRelease(blockNumber, RewardType.minerexit.snapshotType);
		if (flowminerpledge != null && flowminerpledge.getFlowminerpledge() != null && flowminerpledge.getFlowminerpledge().size() > 0)
			savePlexitData(flowminerpledge.getFlowminerpledge(), RewardType.minerexit, blockNumber, blockDate);

		addressService.saveOrUpdateAddress(Constants.addressZero, blockNumber);
//        new TransferAddJob(yesterday,blockNumber,utgStorageMinerMapper,transferMinerMapper).start();
		transferAdd(blockNumber, 0);
//        new NetStaticsJob(utgStorageMinerMapper,transferMinerMapper,yesterday).start();
		saveNetStaticsData(yesterday);
        

		long dayBlocknumber = (blockNumber / dayOneNumber) * dayOneNumber;
		Date dayBlocktime = web3jService.getBlockDate(dayBlocknumber);

		storageService.updateStorageRevenue(blockNumber);

		storageService.updateStorageAmount(blockNumber);

		storageService.updateSpaceRewardStat(dayBlocknumber);

		storageService.saveRevenueStat(dayBlocknumber, dayBlocktime);

		storageService.saveGlobalStat(dayBlocknumber, dayBlocktime);
		log.info("LockReleaseReward at " + blockNumber + " end , spends " + timeSpend.getSpendTime());		
	}

	
	public void plexitLockTransaction(Transaction tx, Log logItem) {
	//	long logLength=receipt.getLogs().size();
	//	BigInteger value= item.getValue();        
    //    if(value==null){
    //        value=BigInteger.ZERO;
    //    }
    //    BigDecimal valSend=new BigDecimal(value.toString());
		int type = NumericUtil.convertToInteger(logItem.getTopics().get(2));
        TransferMiner transferMiner= new TransferMiner();
        List<String> topics=logItem.getTopics();
        String address="";
        if(topics.size()>=1){
            address=Constants.PRE+topics.get(1).substring(26);
        }
   //     String fromHashs = transaction.getFrom();
   //     BigInteger gasPrice = transaction.getGasPrice();
   //     BigInteger gasUsed = receipt.getGasUsed();
   //     BigInteger gasLimit = block.getGasLimit();
   //     long logLength = receipt.getLogs().size();        
       if(type==0){
           // BigDecimal values=convertToBigDecimal ("0x"+logItem.getData().substring(2,66));
            UtgNodeMinerQueryForm queryForm = new UtgNodeMinerQueryForm();
            queryForm.setNode_address(address);
            UtgNodeMiner preNode = nodeExitMapper.getNode(queryForm);
            if(preNode==null)
            	return;
            String revenueAddress = StringUtils.isEmpty(preNode.getRevenue_address()) ? preNode.getNode_address() : preNode.getRevenue_address();
            transferMiner.setTxHash(logItem.getTransactionHash());
            transferMiner.setType(RewardType.nodeexit.lockupType);	//Node lock
            transferMiner.setAddress(address);                
            transferMiner.setRevenueaddress(revenueAddress);
            transferMiner.setTotalAmount(preNode.getPledge_amount());
            transferMiner.setReleaseamount(BigDecimal.ZERO);
            transferMiner.setBlockNumber(logItem.getBlockNumber().longValue()-1);
            transferMiner.setBlockHash(logItem.getBlockHash());
            transferMiner.setLogIndex(logItem.getLogIndex().intValue());
      //      transferMiner.setLogLength(logLength);
            transferMiner.setValue(new BigDecimal(0));
            transferMiner.setGasLimit(tx.getGasLimit());
            transferMiner.setGasPrice(tx.getGasPrice());
            transferMiner.setGasUsed(tx.getGasUsed());
            transferMiner.setPresentAmount(null);
            transferMiner.setNodeNumber(null);
            transferMiner.setCurtime(new Date());                
            transferMiner.setStartTime(tx.getTimeStamp());    
            List<Transaction> listNode = transactionMapper.getTransactionByTxType("CndLock");       
            if(listNode.size()>0){
                Transaction t = listNode.get(0);                    
                transferMiner.setReleaseHeigth(Long.parseLong(t.getParam2()));
                transferMiner.setReleaseInterval(t.getParam3().longValue());
                transferMiner.setLockNumHeight(Long.parseLong(t.getParam1()));
                transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+Long.parseLong(t.getParam1()));
            }else{
			//	transferMiner.setUnLockNumber(logItem.getBlockNumber().longValue());
			//	transferMiner.setReleaseHeigth(0L);
			//	transferMiner.setReleaseInterval(0L);
			//	transferMiner.setLockNumHeight(0L);
                throw new RuntimeException("no node lock config");
            }
            
            //Node exit
            UtgNodeMiner node = new UtgNodeMiner();
            node.setNode_address(address);
            node.setNode_type(NodeTypeEnum.exit.getCode());
            node.setPledge_amount(new BigDecimal(0));
            node.setSync_time(new Date());
            node.setExit_type(0);
            nodeExitMapper.updateNodeMiner(node);
       }else if(type==1){
            UtgStorageMiner pre_miner = utgStorageMinerMapper.getSingleMiner(address);
            if(pre_miner==null)	
            	return;
            String revenueaddress =StringUtils.isEmpty(pre_miner.getRevenue_address())?pre_miner.getMiner_addr():pre_miner.getRevenue_address();
            transferMiner.setTxHash(logItem.getTransactionHash());
            transferMiner.setType(RewardType.minerexit.lockupType);
            transferMiner.setAddress(address);
            transferMiner.setRevenueaddress(revenueaddress);
            transferMiner.setTotalAmount(pre_miner.getPledge_amount());
            transferMiner.setReleaseamount(BigDecimal.ZERO);
            transferMiner.setBlockNumber(logItem.getBlockNumber().longValue()-1);
            transferMiner.setBlockHash(logItem.getBlockHash());
            transferMiner.setLogIndex(logItem.getLogIndex().intValue());
       //     transferMiner.setLogLength(logLength);                
            transferMiner.setValue(new BigDecimal(0));                
            transferMiner.setGasLimit(tx.getGasLimit());
            transferMiner.setGasPrice(tx.getGasPrice());
            transferMiner.setGasUsed(tx.getGasUsed());                
            transferMiner.setPresentAmount(null);
            transferMiner.setNodeNumber(null);
            transferMiner.setCurtime(new Date());
            transferMiner.setStartTime(tx.getTimeStamp());
            
            List<Transaction> listMiner = transactionMapper.getTransactionByTxType("FlwLock");
            if(listMiner.size()>0){
                Transaction t = listMiner.get(0);
                transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+Long.parseLong(t.getParam1()));
                transferMiner.setReleaseHeigth(Long.parseLong(t.getParam2()));
                transferMiner.setReleaseInterval(t.getParam3().longValue());
                transferMiner.setLockNumHeight(Long.parseLong(t.getParam1()));
            }else{
			//	transferMiner.setUnLockNumber(logItem.getBlockNumber().longValue());
			//	transferMiner.setReleaseHeigth(0L);
			//	transferMiner.setReleaseInterval(0L);
			//	transferMiner.setLockNumHeight(0L);
                throw new RuntimeException("no miner lock config");
            }
            
            //storage mining miners exit
            UtgStorageMiner miner = new UtgStorageMiner();
            miner.setMiner_addr(address);
            miner.setSync_time(new Date());
            miner.setMiner_status(MinerStatusEnum.exit.getCode());
            miner.setPledge_amount(new BigDecimal(0));
            miner.setBandwidth(new BigDecimal(0));
            miner.setBlocknumber(tx.getBlockNumber());
            utgStorageMinerMapper.updateStorageMiner(miner);
		} else {
			throw new RuntimeException("Invaild plexitLock type "+type);
		}
		transferMinerMapper.insertOrUpdateMiner(transferMiner);
		// saveOrUpdateAddresses(transferMiner);
		addressService.setAddressLocked(address, transferMiner.getBlockNumber());	
	}
	
	
	@SuppressWarnings("unchecked")
	private void saveRewardData(Map<String, Map<String, Map<String, Object>>> rewardMap, RewardType rewardType, Long blockNumber, Date blockDate) {
		TimeSpend timeSpend = new TimeSpend();
		List<TransferMiner> insertList = new ArrayList<>();
		List<TransferMiner> updateList = new ArrayList<>();
		Map<String, Long> addressMap = new HashMap<>();

		Map<String, TransferMiner> lockRewardMap = new HashMap<>(); 
		TransferMiner tf = new TransferMiner();
		tf.setType(rewardType.lockupType);
		List<TransferMiner> lockRewardList = transferMinerMapper.getTransferMinerList(tf);
		for (TransferMiner vo : lockRewardList) {
			lockRewardMap.put(vo.getAddress().toLowerCase() + '_' + vo.getBlockNumber(), vo);
		}
		for (Entry<String, Map<String, Map<String, Object>>> record : rewardMap.entrySet()) {
			String address = record.getKey().toLowerCase();
			Map<String, Map<String, Object>> data = record.getValue();
			Map<String, Object> lockbalance = data.get("lockbalance");
			if (null != lockbalance && lockbalance.size() > 0) {
				for (Entry<String, Object> lockMap : lockbalance.entrySet()) {
					Long locknumber = Long.parseLong(lockMap.getKey()); 
					Map<String, Map<String, Object>> lockData = (Map<String, Map<String, Object>>) lockMap.getValue();
					Map<String, Object> rewardData = lockData.get(rewardType.snapshotKey);
					if (rewardData != null) {
						String revenueaddress = rewardData.get("revenueaddress").toString();
						BigDecimal lockamount = new BigDecimal(rewardData.get("lockamount").toString());
						BigDecimal playment = new BigDecimal(rewardData.get("playment").toString());					
						Long releaseperiod = Long.parseLong(rewardData.get("releaseperiod").toString());
						Long releaseinterval = Long.parseLong(rewardData.get("releaseinterval").toString());
						Long lockperiod = Long.parseLong(rewardData.get("lockperiod").toString());					
						String burntaddress = rewardData.containsKey("burnaddress") ? rewardData.get("burnaddress").toString() : null;
						BigDecimal burntamount = rewardData.containsKey("burnamount") ? new BigDecimal(rewardData.get("burnamount").toString()) : BigDecimal.ZERO;
						BigDecimal burntratio = rewardData.get("burnratio")!=null
								? new BigDecimal(rewardData.get("burnratio").toString()).divide(new BigDecimal(10000)) : BigDecimal.ZERO;
						TransferMiner lockReward = lockRewardMap.get(address + "_" + locknumber);
						lockRewardMap.remove(address + "_" + locknumber);
						if (null == lockReward) {
							lockReward = new TransferMiner();
							lockReward.setTxHash(UUID.randomUUID().toString());
							lockReward.setType(rewardType.lockupType);
							lockReward.setAddress(address);
							lockReward.setBlockNumber(locknumber);
							lockReward.setTotalAmount(lockamount);
							lockReward.setReleaseHeigth(releaseperiod);
							lockReward.setReleaseInterval(releaseinterval);
							lockReward.setLockNumHeight(lockperiod);								
							lockReward.setUnLockNumber(lockReward.getBlockNumber() + lockReward.getLockNumHeight());
							lockReward.setRevenueaddress(revenueaddress);
							lockReward.setReleaseamount(playment);
							lockReward.setCurtime(blockDate);
							lockReward.setBurntratio(burntratio);
							lockReward.setBurntaddress(burntaddress);
							lockReward.setBurntamount(burntamount);
							insertList.add(lockReward);
							if (playment.compareTo(BigDecimal.ZERO) != 0) { 
								TransferMiner releaseReward = new TransferMiner();
								releaseReward.setTxHash(UUID.randomUUID().toString());
								releaseReward.setType(rewardType.releaseType);
								releaseReward.setAddress(address);
								releaseReward.setRevenueaddress(revenueaddress);
								releaseReward.setBlockNumber(locknumber);
								releaseReward.setReleaseamount(playment);
								releaseReward.setUnLockNumber(blockNumber);
								releaseReward.setCurtime(blockDate);
								releaseReward.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
								releaseReward.setBurntaddress(burntaddress);
								releaseReward.setBurntamount(burntamount);
								insertList.add(releaseReward);								
							}
						} else {
							if (lockReward.getReleaseamount() != null && lockReward.getReleaseamount().compareTo(playment) == -1) {
								TransferMiner releaseReward = new TransferMiner();
								releaseReward.setTxHash(UUID.randomUUID().toString());
								releaseReward.setRevenueaddress(revenueaddress);
								releaseReward.setAddress(address);
								releaseReward.setBlockNumber(locknumber);
								releaseReward.setType(rewardType.releaseType);
								releaseReward.setUnLockNumber(blockNumber);
								releaseReward.setCurtime(blockDate);
								releaseReward.setReleaseamount(playment.subtract(lockReward.getReleaseamount()));
								releaseReward.setBurntratio(burntamount.compareTo(BigDecimal.ZERO)>0 ? burntratio : BigDecimal.ZERO);
								releaseReward.setBurntaddress(burntaddress);
								BigDecimal lastBurntamount = lockReward.getBurntamount()==null ? BigDecimal.ZERO : lockReward.getBurntamount();
								lockReward.setBurntamount(burntamount.subtract(lastBurntamount));								
								insertList.add(releaseReward);
								lockReward.setReleaseamount(playment);
								lockReward.setBurntratio(burntratio);
								lockReward.setBurntaddress(burntaddress);
								lockReward.setBurntamount(burntamount);
								updateList.add(lockReward);								
							}
						}
						if (insertList.size() > 0 && insertList.size() / Constants.BATCHCOUNT >= 1) {
							transferMinerMapper.insertBatch(insertList);
							insertList = new ArrayList<>();
						}
						if (updateList.size() > 0 && updateList.size() / Constants.BATCHCOUNT >= 1) {
							transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
							updateList = new ArrayList<>();
						}
						if (revenueaddress != null) {
							addressMap.put(revenueaddress, blockNumber);
						}
					}
				}
			}
		}
		if (insertList.size() > 0) {
			transferMinerMapper.insertBatch(insertList);
		}
		if (updateList.size() > 0) {
			transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
		}
		addressService.syncAddressesBalance(addressMap);
		log.info("Save reward type of " + rewardType.name() + " at " + blockNumber + " insert " + insertList.size() + " and update " + updateList.size()
				+ " data ,spend " + timeSpend.getSpendTime());		
	}

	
	private void savePlexitData(Map<String, Map<String, Object>> plexitMap, RewardType rewardType, Long blockNumber, Date blockDate) {
		TimeSpend timeSpend = new TimeSpend();
		List<TransferMiner> insertList = new ArrayList<>();
		List<TransferMiner> updateList = new ArrayList<>();
		Map<String, Long> addressMap = new HashMap<>();

		Map<String, TransferMiner> existMap = new HashMap<>();
		TransferMiner tf = new TransferMiner();
		tf.setType(rewardType.lockupType);
		List<TransferMiner> lockList = transferMinerMapper.getLockByType(tf);
		for (TransferMiner lock : lockList) {
			existMap.put(lock.getAddress().toLowerCase(), lock);
		}

		for (Entry<String, Map<String, Object>> record : plexitMap.entrySet()) {
			String address = record.getKey().toLowerCase();
			Map<String, Object> data = record.getValue();
			Long startblocknumber = Long.parseLong(data.get("startblocknumber").toString());				
			String revenueaddress = data.get("revenueaddress").toString();
			BigDecimal playment = new BigDecimal(data.get("playment").toString());
			TransferMiner lockReward = existMap.get(address);
			if (lockReward != null && startblocknumber != 0) {
				if (lockReward.getReleaseamount() != null && lockReward.getReleaseamount().compareTo(playment) == -1) {
					TransferMiner releaseReward = new TransferMiner();
					releaseReward.setTxHash(UUID.randomUUID().toString());
					releaseReward.setType(rewardType.releaseType);
					releaseReward.setAddress(address);
					releaseReward.setBlockNumber(lockReward.getBlockNumber());						
					releaseReward.setRevenueaddress(revenueaddress);						
					releaseReward.setReleaseamount(playment.subtract(lockReward.getReleaseamount()));
					releaseReward.setCurtime(blockDate);
					insertList.add(releaseReward);				
					lockReward.setReleaseamount(playment);
					updateList.add(lockReward);						
				}
			}
			if (insertList.size() > 0 && insertList.size() / Constants.BATCHCOUNT >= 1) {
				transferMinerMapper.insertBatch(insertList);
				insertList = new ArrayList<>();
			}
			if (updateList.size() > 0 && updateList.size() / Constants.BATCHCOUNT >= 1) {
				transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
				updateList = new ArrayList<>();
			}				
			if (revenueaddress != null) {
				addressMap.put(revenueaddress, blockNumber);
			}
		}			
		if (insertList.size() > 0) {
			transferMinerMapper.insertBatch(insertList);
		}
		if (updateList.size() > 0) {
			transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
		}
		addressService.syncAddressesBalance(addressMap);
		log.info("Exist pledge release of " + rewardType.name() + " at " + blockNumber + " save " + insertList.size() + " and update " + updateList.size()
				+ " reward data ,spend " + timeSpend.getSpendTime());				
	}

	
    private void transferAdd(Long blockNumber, int type) {
		List<TransferMiner> list = transferMinerMapper.getTransferAdd(blockNumber, type);
		List<TransferMiner> insertList = new ArrayList<>();
		List<TransferMiner> updateList = new ArrayList<>();		
		Map<String, Long> addressMap = new HashMap<>();
		for (TransferMiner trans : list) {
			if (trans.getReleaseHeigth() == 0 || trans.getReleaseInterval() == 0) {	
				TransferMiner vo = new TransferMiner();
				vo.setId(trans.getId());
				vo.setReleaseamount(trans.getTotalAmount());
				updateList.add(vo);
				// release record
				TransferMiner transferMiner = new TransferMiner();
				transferMiner.setTxHash(UUID.randomUUID().toString());
				transferMiner.setType(trans.getType() + 1);
				transferMiner.setBlockNumber(trans.getBlockNumber());
				transferMiner.setAddress(trans.getAddress());				
				transferMiner.setRevenueaddress(trans.getRevenueaddress());
				transferMiner.setReleaseamount(trans.getTotalAmount());
				transferMiner.setLogIndex(1);
				transferMiner.setCurtime(new Date());
				insertList.add(transferMiner);	
				if (null != transferMiner.getRevenueaddress()) {
					addressMap.put(transferMiner.getRevenueaddress(), blockNumber);
				}
			} else {
				// Long times = trans.getReleaseHeigth()/trans.getReleaseInterval();
				TransferMiner vo = new TransferMiner();
				vo.setId(trans.getId());
				vo.setReleaseamount(trans.getTotalAmount());
				updateList.add(vo);
				// release record
				TransferMiner transferMiner = new TransferMiner();
				transferMiner.setTxHash(UUID.randomUUID().toString());
				transferMiner.setType(trans.getType() + 1);
				transferMiner.setBlockNumber(trans.getBlockNumber());
				transferMiner.setAddress(trans.getAddress());
				transferMiner.setRevenueaddress(trans.getRevenueaddress());
				transferMiner.setReleaseamount(trans.getTotalAmount().subtract(trans.getReleaseamount()));
				transferMiner.setLogIndex(1);				
				transferMiner.setCurtime(new Date());
				insertList.add(transferMiner);
				if (null != transferMiner.getRevenueaddress()) {
					addressMap.put(transferMiner.getRevenueaddress(), blockNumber);
				}
			}			
			if (insertList.size() > 0 && insertList.size() / Constants.BATCHCOUNT >= 1) {
				transferMinerMapper.insertBatch(insertList);
				insertList = new ArrayList<>();
			}
			if (updateList.size() > 0 && updateList.size() / Constants.BATCHCOUNT >= 1) {
				transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
				updateList = new ArrayList<>();
			}
		}
		if (insertList.size() > 0) {
			transferMinerMapper.insertBatch(insertList);
		}
		if (updateList.size() > 0) {
			transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
		}		
		addressService.syncAddressesBalance(addressMap);        
    }
	
    

    @SuppressWarnings("unchecked")
	protected void saveStorageReward(Map<String, Map<String, Map<String, Object>>> rewardMap, RewardType rewardType, Long blockNumber, Date blockDate,	Date yesterday) {
    	TimeSpend timeSpend = new TimeSpend();
		List<TransferMiner> insertList = new ArrayList<>();
		List<TransferMiner> updateList = new ArrayList<>();
		Map<String, Long> addressMap = new HashMap<>();
		
		Map<String, TransferMiner> lockRewardMap = new HashMap<>(); 
		TransferMiner tf = new TransferMiner();
		tf.setType(rewardType.lockupType);
		List<TransferMiner> lockRewardList = transferMinerMapper.getTransferMinerList(tf);
		for (TransferMiner vo : lockRewardList) {
			lockRewardMap.put(vo.getAddress().toLowerCase() + '_' + vo.getBlockNumber(), vo);
		}
		//===========UtgCltStoragedata==============
    	List<UtgCltStoragedata> cltList = new ArrayList<>();
        Map<String,Long> maxBlockMap = new HashMap<>();
        List<TransferMiner> maxBlockNumberList = transferMinerMapper.getMaxBlockNumberTransferGroupByAddress();
        for (TransferMiner vo:maxBlockNumberList) {
            maxBlockMap.put(vo.getAddress().toLowerCase(),vo.getBlockNumber());
        }
        //===========UtgCltStoragedata==============        
        for (Entry<String, Map<String, Map<String, Object>>> record : rewardMap.entrySet()) {
			String address = record.getKey().toLowerCase(); 
			Map<String, Map<String, Object>> data = record.getValue();
			Map<String, Object> lockbalance = data.get("lockbalance");
			//===========UtgCltStoragedata==============
            //flowLock and bandwidthrewardlock maxblockNumber
            Long maxBlockNumber = maxBlockMap.get(address.toLowerCase())==null?0L:maxBlockMap.get(address.toLowerCase());
            maxBlockMap.remove(address.toLowerCase());
            //day  bandwidthreward
            UtgCltStoragedata clt = new UtgCltStoragedata(address);
            clt.setBlocknumber(blockNumber);
            clt.setBandwidthreward(BigDecimal.ZERO);
            clt.setReport_time(yesterday);
            clt.setInstime(new Date());
            //===========UtgCltStoragedata==============
            if (null != lockbalance && lockbalance.size() > 0) {
				for (Entry<String, Object> lockMap : lockbalance.entrySet()) {
					Long locknumber = Long.parseLong(lockMap.getKey());
					Map<String, Map<String, Object>> lockData = (Map<String, Map<String, Object>>) lockMap.getValue();
					Map<String, Object> rewardData = lockData.get(rewardType.snapshotKey);
					if (rewardData != null) {
						String revenueaddress = rewardData.get("revenueaddress").toString(); 
						BigDecimal lockamount = new BigDecimal(rewardData.get("lockamount").toString());
						BigDecimal playment = new BigDecimal(rewardData.get("playment").toString());					
						Long releaseperiod = Long.parseLong(rewardData.get("releaseperiod").toString()); 
						Long releaseinterval = Long.parseLong(rewardData.get("releaseinterval").toString());
						Long lockperiod = Long.parseLong(rewardData.get("lockperiod").toString());
						TransferMiner lockReward = lockRewardMap.get(address + "_" + locknumber);
						lockRewardMap.remove(address + "_" + locknumber);
						//===========UtgCltStoragedata==============	
                        if(locknumber>maxBlockNumber){
                             clt.setBandwidthreward(NumericUtil.add(clt.getBandwidthreward(),lockamount));
                        }
                       //===========UtgCltStoragedata==============
                        if (null == lockReward) {
							lockReward = new TransferMiner();
							lockReward.setTxHash(UUID.randomUUID().toString());
							lockReward.setType(rewardType.lockupType);
							lockReward.setAddress(address);
							lockReward.setBlockNumber(locknumber);
							lockReward.setTotalAmount(lockamount);
							lockReward.setReleaseHeigth(releaseperiod);
							lockReward.setReleaseInterval(releaseinterval);
							lockReward.setLockNumHeight(lockperiod);								
							lockReward.setUnLockNumber(lockReward.getBlockNumber() + lockReward.getLockNumHeight());
							lockReward.setRevenueaddress(revenueaddress);
							lockReward.setReleaseamount(playment);
							lockReward.setCurtime(blockDate);
							insertList.add(lockReward);
							if (playment.compareTo(BigDecimal.ZERO) != 0) {
								TransferMiner releaseReward = new TransferMiner();
								releaseReward.setTxHash(UUID.randomUUID().toString());
								releaseReward.setType(rewardType.releaseType);
								releaseReward.setAddress(address);
								releaseReward.setRevenueaddress(revenueaddress);
								releaseReward.setBlockNumber(locknumber);
								releaseReward.setReleaseamount(playment);
								releaseReward.setUnLockNumber(blockNumber);
								releaseReward.setCurtime(blockDate);
								insertList.add(releaseReward);								
							}
						} else {
							if (lockReward.getReleaseamount() != null && lockReward.getReleaseamount().compareTo(playment) == -1) {
								TransferMiner releaseReward = new TransferMiner();
								releaseReward.setTxHash(UUID.randomUUID().toString());
								releaseReward.setRevenueaddress(revenueaddress);
								releaseReward.setAddress(address);
								releaseReward.setBlockNumber(locknumber);
								releaseReward.setType(rewardType.releaseType);
								releaseReward.setUnLockNumber(blockNumber);
								releaseReward.setCurtime(blockDate);
								releaseReward.setReleaseamount(playment.subtract(lockReward.getReleaseamount()));
								insertList.add(releaseReward);		
								lockReward.setReleaseamount(playment);
								updateList.add(lockReward);							
							}
						}
						if (insertList.size() > 0 && insertList.size() / Constants.BATCHCOUNT >= 1) {
							transferMinerMapper.insertBatch(insertList);
							insertList = new ArrayList<>();
						}
						if (updateList.size() > 0 && updateList.size() / Constants.BATCHCOUNT >= 1) {
							transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
							updateList = new ArrayList<>();
						}
						if (revenueaddress != null) {
							addressMap.put(revenueaddress, blockNumber);
						}
					}
				}
			}
            //===========UtgCltStoragedata==============
			// day bandwidthreward
			if (clt.getBandwidthreward().compareTo(BigDecimal.ZERO) != 0) {
				clt.setProfitamount(clt.getBandwidthreward());
				clt.setFwflag(1);
				cltList.add(clt);
			}
			if (cltList.size() > 0 && cltList.size() / Constants.BATCHCOUNT >= 1) {
				utgStorageMinerMapper.batchSaveFlwDataDay(cltList);
				cltList = new ArrayList<>();
			}
			//===========UtgCltStoragedata==============
		}
        //===========UtgCltStoragedata==============
		if (cltList.size() > 0) {
			utgStorageMinerMapper.batchSaveFlwDataDay(cltList);
		}
		//===========UtgCltStoragedata==============
		if (insertList.size() > 0) {
			transferMinerMapper.insertBatch(insertList);
		}
		if (updateList.size() > 0) {
			transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
		}
		addressService.syncAddressesBalance(addressMap);
		log.info("Lock release of " + rewardType.name() + " at " + blockNumber + " save " + insertList.size() + " and update " + updateList.size()
				+ " reward data ,spend " + timeSpend.getSpendTime());

	}					

    
	@SuppressWarnings("unchecked")
	protected void saveUtgCltStoragedata(Map<String, Map<String, Map<String, Object>>> flowrevenve,String chainType,Long blockNumber,Date yesterday){
   //     Map<String, Map<String, Map<String, Object>>> flowrevenve= bandwidthlock.getFlowrevenve();
		List<UtgCltStoragedata> cltList = new ArrayList<>();
        if(null!=flowrevenve&&flowrevenve.size()>0) {
            Map<String,Long> maxBlockMap = new HashMap<>();
            List<TransferMiner> maxBlockNumberList = transferMinerMapper.getMaxBlockNumberTransferGroupByAddress();
            for (TransferMiner vo:maxBlockNumberList) {
                maxBlockMap.put(vo.getAddress().toLowerCase(),vo.getBlockNumber());
            }            
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
                    //    Map<String, Object> lock = (Map<String, Object>)lockB.getValue();
                        Long bNumber = Long.parseLong(lockB.getKey());
                        Map<String,Map<String, Object>> lockV = (Map<String,  Map<String, Object>>)lockB.getValue();
                        Map<String, Object> bandwidthReward = lockV.get(chainType);
                        if(bandwidthReward!=null){
                            BigDecimal lockamount = new BigDecimal(bandwidthReward.get("lockamount").toString());                            
                            if(bNumber>maxBlockNumber){		//?
                                clt.setBandwidthreward(NumericUtil.add(clt.getBandwidthreward(),lockamount));
                            }                            
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
        }
	}
	

    private void saveNetStaticsData(Date ctime) {
        long starttime = System.currentTimeMillis();
        log.info("NetStaticsJob start .....");
        try {
            //NetStatics
			UtgNetStatics nns = new UtgNetStatics();
			nns.setCtime(ctime);
			nns.setTotal_bw(BigDecimal.ZERO);
			UtgCltStoragedata flw = utgStorageMinerMapper.queryUtgCltStoragedataDayByTime(DateUtil.getFormatDateTime(ctime, "yyyy-MM-dd"));
			if (flw != null) {
				nns.setTotal_utg(flw.getProfitamount());
			}
			UtgStorageMiner miner = utgStorageMinerMapper.getMinerSum();
			if (miner != null && miner.getBandwidth() != null) {
				nns.setTotal_bw(miner.getBandwidth());
			}
			UtgNetStatics prenns = utgStorageMinerMapper.queryUtgNetStaticsByCtime(DateUtil.getFormatDateTime(DateUtil.addDaysToDate(ctime, -1), "yyyy-MM-dd"));
			if (prenns != null) {
				nns.setIncre_bw(nns.getTotal_bw().subtract(prenns.getTotal_bw()));
			} else {
				nns.setIncre_bw(nns.getTotal_bw());
			}
			if (nns.getTotal_bw().compareTo(BigDecimal.ZERO) == 0) {
				nns.setUtg_gbrate(BigDecimal.ZERO);
			} else {
				nns.setUtg_gbrate(nns.getTotal_utg().divide(new BigDecimal("1000000000000000000"), 20, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(1024))
						.divide(nns.getTotal_bw(), 4, BigDecimal.ROUND_HALF_UP));
			}
            utgStorageMinerMapper.insertUtgNetStatics(nns);
        }catch (Exception e) {
            log.error("NetStaticsJob error,"+e.getMessage(),e);
        }
        minerModify();
        utgDayLockRealseModify(ctime);
        long endtime = System.currentTimeMillis();
        log.info("NetStaticsJob end ,spends total second ="+(endtime-starttime)/1000);
    }

    private void minerModify() {
        try {
            //miner statis
            List<UtgStorageMiner> minerList = utgStorageMinerMapper.getAllMinerAddress();
            if(minerList!=null&&minerList.size()>0){
                List<UtgCltStoragedata>  flwList = utgStorageMinerMapper.queryUtgCltStoragedataDayGroupBYAddress();
                Map<String, UtgCltStoragedata> cltAddressMap = new HashMap<>();
                for (UtgCltStoragedata data: flwList) {
                    cltAddressMap.put(data.getEn_address().toLowerCase(),data);
                }
                List<TransferMiner> transfList = transferMinerMapper.getReleaseamountGroupByAddress();
                Map<String,TransferMiner> transfAddressMap = new HashMap<>();
                for (TransferMiner data: transfList) {
                    transfAddressMap.put(data.getAddress().toLowerCase(),data);
                }
                List<UtgStorageMiner> list = new ArrayList<>();
                for (UtgStorageMiner minner: minerList) {
                    String mAddress = minner.getMiner_addr().toLowerCase();
                    UtgCltStoragedata mflw = cltAddressMap.get(mAddress);
                    if (mflw != null) {
                        minner.setMiner_storage(mflw.getStorage_value());
                        minner.setPaysrt(mflw.getSrtnum());
                        minner.setRevenue_amount(mflw.getProfitamount());
                        cltAddressMap.remove(mAddress);
                    }
                    TransferMiner tm = transfAddressMap.get(mAddress);
                    if (tm != null) {
                        minner.setRelease_amount(tm.getReleaseamount());
                        minner.setLock_amount(tm.getTotalAmount());
                        transfAddressMap.remove(mAddress);
                    }
                    minner.setSync_time(new Date());
                    list.add(minner);
                    if(list.size()>0&&list.size()/Constants.BATCHCOUNT>=1){
                    	utgStorageMinerMapper.updateStorageMinerBatch(list);
                        list = new ArrayList<>();
                    }
                }
                if(list.size()>0){
                	utgStorageMinerMapper.updateStorageMinerBatch(list);
                }
            }
        }catch (Exception e) {
            log.error("MinerModifyJob error,"+e.getMessage(),e);
        }
    }

    private void utgDayLockRealseModify(Date ctime) {
        try {
			List<UtgCltStoragedata> utgCltList = utgStorageMinerMapper.getFlwDataDayListByReportTime(DateUtil.getFormatDateTime(ctime, "yyyy-MM-dd"));
			if (utgCltList != null && utgCltList.size() > 0) {
				List<TransferMiner> bandwidthList = transferMinerMapper.getReleaseamountGroupByAddressAndType(9);
				Map<String, TransferMiner> bandwidthMap = new HashMap<>();
				for (TransferMiner data : bandwidthList) {
					bandwidthMap.put(data.getAddress().toLowerCase(), data);
				}
				List<UtgCltStoragedata> list = new ArrayList<>();
				for (UtgCltStoragedata clt : utgCltList) {
					String mAddress = clt.getEn_address().toLowerCase();
					if (clt.getFwflag() == 1) {
						TransferMiner mbandwidth = bandwidthMap.get(mAddress);
						if (mbandwidth != null) {
							clt.setLockamount(mbandwidth.getTotalAmount());
							clt.setReleaseamount(mbandwidth.getReleaseamount());
							list.add(clt);
						}
					}
					if (list.size() > 0 && list.size() / Constants.BATCHCOUNT >= 1) {
						utgStorageMinerMapper.batchUpdateFlwDataDay(list);
						list = new ArrayList<>();
					}
				}
				if (list.size() > 0) {
					utgStorageMinerMapper.batchUpdateFlwDataDay(list);
				}
			}
        }catch (Exception e) {
            log.error("utgDayLockRealseModify error,"+e.getMessage(),e);
        }
    }
    
    
    
    //=================================V2=========================================
    //@Async
    @SuppressWarnings("unused")
	private void storageBlockReward(Long blockNumber,Date blockDate){
    	TimeSpend timeSpend = new TimeSpend();
		log.info("StorageBlockReward at " + blockNumber + " start ...");
		
		Map<String, Object> rewardData = web3jService.getStorageReward(blockNumber, RewardType.block.snapshotV2);
		if (rewardData != null && rewardData.size() > 0)
			saveStorageReward(rewardData, RewardType.block, blockNumber, blockDate);
		
		List<Map<String, Object>> rewardList = web3jService.getPays(blockNumber);
		savePaysReward(rewardList, paysType, blockNumber,blockDate);		
		log.info("StorageBlockReward at " + blockNumber + " end , spends " + timeSpend.getSpendTime());
    }
    

    //@Async
    @SuppressWarnings("unused")
    private void storageLockReward(Long blockNumber,Date blockDate){
    	TimeSpend timeSpend = new TimeSpend();
		log.info("StorageReleaseReward at " + blockNumber + " start ...");
		

		Snapshot candidatepledge = web3jService.getSnapshotRelease(blockNumber, RewardType.nodeexit.snapshotType);
		if (candidatepledge != null && candidatepledge.getCandidatepledge() != null && candidatepledge.getCandidatepledge().size() > 0)
			savePlexitData(candidatepledge.getCandidatepledge(), RewardType.nodeexit, blockNumber, blockDate);

		Snapshot flowminerpledge = web3jService.getSnapshotRelease(blockNumber, RewardType.minerexit.snapshotType);
		if (flowminerpledge != null && flowminerpledge.getFlowminerpledge()!=null && flowminerpledge.getFlowminerpledge().size()>0)
			savePlexitData(flowminerpledge.getFlowminerpledge(), RewardType.minerexit, blockNumber, blockDate);
				

		Map<String, Object> spaceData = web3jService.getStorageReward(blockNumber, RewardType.space.snapshotV2);
		if (spaceData != null && spaceData.size() > 0)
			saveStorageReward(spaceData, RewardType.space, blockNumber, blockDate);

		Map<String, Object> leaseData = web3jService.getStorageReward(blockNumber, RewardType.lease.snapshotV2);
		if (leaseData != null && leaseData.size() > 0)
			saveStorageReward(leaseData, RewardType.lease, blockNumber, blockDate);

		Map<String, Object> posplexitData = web3jService.getStorageReward(blockNumber, RewardType.posplexit.snapshotV2);
		if (posplexitData != null && posplexitData.size() > 0)
			saveStorageReward(posplexitData, RewardType.posplexit, blockNumber, blockDate);
		

		addressService.saveOrUpdateAddress(Constants.addressZero, blockNumber);
//        new TransferAddJob(yesterday,blockNumber,utgStorageMinerMapper,transferMinerMapper).start();
		transferAdd(blockNumber, 0);
		Date yesterday = DateUtil.getBeginDayOfYesterday();
//        new NetStaticsJob(utgStorageMinerMapper,transferMinerMapper,yesterday).start();
		saveNetStaticsData(yesterday);
        

		storageService.updateStorageAmount(blockNumber);
		long dayBlocknumber = (blockNumber / dayOneNumber) * dayOneNumber;
		Date dayBlocktime = web3jService.getBlockDate(dayBlocknumber);

		storageService.updateSpaceRewardStat(dayBlocknumber);

		storageService.saveRevenueStat(dayBlocknumber, dayBlocktime);

		storageService.saveGlobalStat(dayBlocknumber, dayBlocktime);
		log.info("StorageReleaseReward at " + blockNumber + " end , spends " + timeSpend.getSpendTime());
    }    
	
    //@Async
    @SuppressWarnings("unused")
    private void storageReleaseReward(Long blockNumber,Date blockDate){
    	updateReleaseAmount(RewardType.block, blockNumber,blockDate);
		updateReleaseAmount(RewardType.space, blockNumber,blockDate);
		updateReleaseAmount(RewardType.lease, blockNumber,blockDate);
		updateReleaseAmount(RewardType.posplexit, blockNumber,blockDate);
		if(releaseCompareEnable!=null && releaseCompareEnable==true)
			compareReleaseData(blockNumber);
    }
    
        

    @SuppressWarnings("unchecked")
	private boolean saveStorageReward(Map<String, Object> rewardData, RewardType rewardType, Long blockNumber,Date blockDate) {
		List<?> rewardList = (List<?>) rewardData.get("reward");
		if (rewardList == null || rewardList.size() == 0)
			return false;
		TimeSpend timeSpend = new TimeSpend();
		List<TransferMiner> insertList = new ArrayList<>();
		Map<String, Long> addressMap = new HashMap<>();

		TransferMiner query = new TransferMiner();
		query.setType(rewardType.lockupV2);
		List<TransferMiner> existList = transferMinerMapper.getTransferMinerList(query);
		Map<String, TransferMiner> existMap = new HashMap<>();
		for (TransferMiner vo : existList) {
			existMap.put(rewardType.lockupV2 + "_" + vo.getAddress().toLowerCase() + "_" + vo.getBlockNumber(), vo);
		}

		Long lockPeriod =  Long.parseLong(rewardData.get("LockPeriod").toString());
		Long releasePeriod = Long.parseLong(rewardData.get("ReleasePeriod").toString());
		Long releaseInterval = Long.parseLong(rewardData.get("ReleaseInterval").toString());
		for (Object rewardObj : rewardList) {
			Map<String, Object> reward = (Map<String, Object>) rewardObj;
			String target = (String) reward.get("target");
			BigDecimal amount = new BigDecimal(reward.get("amount").toString());
			String revenue = (String) reward.get("revenue");

			TransferMiner transferMiner = new TransferMiner();
			transferMiner.setTxHash(UUID.randomUUID().toString());
			transferMiner.setType(rewardType.lockupV2);
			transferMiner.setAddress(target);
			transferMiner.setBlockNumber(blockNumber);
			transferMiner.setTotalAmount(amount);
        //    transferMiner.setLogIndex(1);                    
        //    transferMiner.setValue(new BigDecimal(0));            
        //    transferMiner.setBlockHash();
        //    transferMiner.setGasLimit();
        //    transferMiner.setGasUsed();
        //    transferMiner.setGasPrice();
        //    transferMiner.setLogLength(logLength);	
        //    transferMiner.setStartTime(null);
        //    transferMiner.setNodeNumber(null);
        //    transferMiner.setPresentAmount(null);
            transferMiner.setReleaseHeigth(releasePeriod);
            transferMiner.setReleaseInterval(releaseInterval);
            transferMiner.setLockNumHeight(lockPeriod);
            transferMiner.setUnLockNumber(blockNumber+lockPeriod);
            transferMiner.setCurtime(blockDate);
            transferMiner.setRevenueaddress(revenue);
       //      transferMiner.setReleaseamount(playment);            
            
			boolean exist = existMap.containsKey(rewardType.lockupV2 + "_" + target.toLowerCase() + "_" + blockNumber);
			if (exist) {
				log.warn("Exist same transfer miner rewardType="+rewardType.name()+"["+rewardType.lockupV2+"] address="+target+" blockNumber="+blockNumber);
			} else {
				insertList.add(transferMiner);
			}

			if (insertList.size() > 0 && insertList.size() / Constants.BATCHCOUNT >= 1) {
				transferMinerMapper.insertBatch(insertList);
				insertList = new ArrayList<>();
			}
			if (null != transferMiner.getRevenueaddress()) {
				addressMap.put(transferMiner.getRevenueaddress(), blockNumber);
			}
		}
		if (insertList.size() > 0) {
			transferMinerMapper.insertBatch(insertList);
		}
	//	addressService.syncAddressesBalance(addressMap);		
		log.info("StorageReward " + rewardType.name() + " at blocknumber=" + blockNumber + " end ,spends "+timeSpend.getSpendTime());
		return true;
	}
	

    private void savePaysReward(List<Map<String, Object>> rewardList, Integer paysType, Long blockNumber,Date blockDate){
    	TimeSpend timeSpend = new TimeSpend();
    	List<TransferMiner> insertList = new ArrayList<>();
		Map<String, Long> addressMap = new HashMap<>();

		TransferMiner query = new TransferMiner();
		query.setType(paysType);
		List<TransferMiner> existList = transferMinerMapper.getTransferMinerList(query);
		Map<String, TransferMiner> existMap = new HashMap<>();
		for (TransferMiner vo : existList) {
			existMap.put(paysType + "_" + vo.getBlockNumber(), vo);
		}
//		Long lockPeriod =  Long.parseLong(rewardData.get("LockPeriod").toString());	
//		Long releasePeriod = Long.parseLong(rewardData.get("ReleasePeriod").toString());
//		Long releaseInterval = Long.parseLong(rewardData.get("ReleaseInterval").toString());

		for (Map<String, Object> reward : rewardList) {	
			String address = (String) reward.get("address");
			BigDecimal amount = new BigDecimal(reward.get("amount").toString());
//			String revenue = (String) reward.get("revenue");

			TransferMiner transferMiner = new TransferMiner();
			transferMiner.setTxHash(UUID.randomUUID().toString());
			transferMiner.setType(paysType);
//          transferMiner.setAddress(target);
			transferMiner.setBlockNumber(blockNumber);
            transferMiner.setTotalAmount(amount);
        //    transferMiner.setLogIndex(1);                    
        //    transferMiner.setValue(new BigDecimal(0));
        //    transferMiner.setBlockHash();
        //    transferMiner.setGasLimit();
        //    transferMiner.setGasUsed();
        //    transferMiner.setGasPrice();
        //    transferMiner.setLogLength(logLength);	
        //    transferMiner.setStartTime(null);
        //    transferMiner.setNodeNumber(null);
        //    transferMiner.setPresentAmount(null);
//            transferMiner.setReleaseHeigth(releasePeriod);
//            transferMiner.setReleaseInterval(releaseInterval);
//            transferMiner.setLockNumHeight(lockPeriod);
//            transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+transferMiner.getLockNumHeight());
            transferMiner.setCurtime(blockDate);
            transferMiner.setReleaseamount(amount);
            transferMiner.setRevenueaddress(address);
            
			boolean exist = existMap.containsKey(paysType  + "_" + blockNumber);
			if (exist) {
				log.warn("Exist same transfer miner rewardType="+paysType+" blockNumber="+blockNumber);
			} else {
				insertList.add(transferMiner);
			}

			if (insertList.size() > 0 && insertList.size() / Constants.BATCHCOUNT >= 1) {
				transferMinerMapper.insertBatch(insertList);
				insertList = new ArrayList<>();
			}
			if (null != transferMiner.getRevenueaddress()) {
				addressMap.put(transferMiner.getRevenueaddress(), blockNumber);
			}
		}
		if (insertList.size() > 0) {
			transferMinerMapper.insertBatch(insertList);
		}
		addressService.syncAddressesBalance(addressMap);
		log.info("ReleasePaysReward at blocknumber=" + blockNumber + " end ,spends "+timeSpend.getSpendTime());
    }
		
	

	private void updateReleaseAmount(RewardType rewardType, Long blockNumber,Date blockDate){
    	TransferMiner query = new TransferMiner();
		query.setType(rewardType.lockupV2);
		List<TransferMiner> list = transferMinerMapper.getTransferMinerList(query);
		List<TransferMiner> updateList = new ArrayList<>();
		List<StorageReleaseDetail> detailList = new ArrayList<>();
		Map<String, Long> addressMap = new HashMap<>();
		for(TransferMiner transferMiner : list){
			BigDecimal totalAmount = transferMiner.getTotalAmount();
			BigDecimal lastReleaseAmount = transferMiner.getReleaseamount();
			Long lockNumHeight = transferMiner.getBlockNumber();
			Long lockPeriod = transferMiner.getLockNumHeight();
			Long releasePeriod = transferMiner.getReleaseHeigth();
			Long releaseInterval = transferMiner.getReleaseInterval();		
			if (totalAmount == null || lockNumHeight == null|| lockPeriod == null || releasePeriod == null || releaseInterval == null )
				continue;
			if (lastReleaseAmount != null && lastReleaseAmount.compareTo(totalAmount) == 0){
				continue;		 // release completed
			}
			lockNumHeight = ((lockNumHeight-1) / dayOneNumber) * dayOneNumber;
			
			StorageReleaseDetail detail = new StorageReleaseDetail();
			detail.setRewardid(transferMiner.getId());
			detail.setAddress(transferMiner.getAddress());
			detail.setRevenueaddress(transferMiner.getRevenueaddress());
			detail.setType(transferMiner.getType());
			detail.setBlocknumber(blockNumber);
			detail.setTotalamount(transferMiner.getTotalAmount());
			detail.setUpdatetime(new Date());
			if (lastReleaseAmount != null && lastReleaseAmount.compareTo(totalAmount) > 0){	
				log.warn("Release amount error id["+transferMiner.getId()+"]: releaseAmount ["+lastReleaseAmount+"] > totalAmount ["+totalAmount+"] at blocknumber "+blockNumber);
				transferMiner.setReleaseamount(totalAmount);	// Forced release = total
				detail.setReleaseonce(totalAmount.subtract(lastReleaseAmount));
				detail.setReleasetype(0);
			}else if (blockNumber < lockNumHeight + lockPeriod){
				continue;										// Release not started
			}else if (blockNumber > lockNumHeight + lockPeriod + releasePeriod ){			
				log.info("Release calculate error id["+transferMiner.getId()+"]: blockNumber["+blockNumber+"] > "
						+ "lockNumHeight ["+lockNumHeight+"]+lockPeriod["+lockPeriod+"]+releasePeriod["+releasePeriod+"]="+(lockNumHeight+lockPeriod+releasePeriod)
						+ ", releaseAmount["+lastReleaseAmount+"] < totalAmount["+totalAmount+"]");
				transferMiner.setReleaseamount(totalAmount);	// release completed
				if(lastReleaseAmount!=null)
					detail.setReleaseonce(totalAmount.subtract(lastReleaseAmount));
				else
					detail.setReleaseonce(totalAmount);
				detail.setReleasetype(2);
			}else{
				
				Long releaseTimes = (blockNumber - lockNumHeight - lockPeriod) / releaseInterval;										//Release times
				if(releaseTimes<=0)		
					continue;			//Not reach release time	
				
				BigDecimal releasePertime = totalAmount.divide(new BigDecimal(releasePeriod / releaseInterval), BigDecimal.ROUND_DOWN);	//Release per time
				BigDecimal releaseAmount = releasePertime.multiply(new BigDecimal(releaseTimes)).setScale(0, BigDecimal.ROUND_DOWN);	//calculate release amount				
				transferMiner.setReleaseamount(releaseAmount);
				if(lastReleaseAmount!=null)
					detail.setReleaseonce(releaseAmount.subtract(lastReleaseAmount));
				else
					detail.setReleaseonce(releaseAmount);
				detail.setReleasetype(1);
			}
			updateList.add(transferMiner);
			if (updateList.size() > 0 && updateList.size() / Constants.BATCHCOUNT >= 1) {
				transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
				updateList = new ArrayList<>();
			}
			detailList.add(detail);
			if (releaseCompareEnable!=null && releaseCompareEnable==true && detailList.size() > 0 && detailList.size() / Constants.BATCHCOUNT >= 1) {
				storageReleaseMapper.insertDetailBatch(detailList);
				detailList = new ArrayList<>();
			}
			if (null != transferMiner.getRevenueaddress()) {
				addressMap.put(transferMiner.getRevenueaddress(), blockNumber);
			}
		}
		if (updateList.size() > 0) {
			transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
		}
		if (releaseCompareEnable!=null && releaseCompareEnable==true && detailList.size() > 0) {
			storageReleaseMapper.insertDetailBatch(detailList);
		}
		addressService.syncAddressesBalance(addressMap);
    }
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void compareReleaseData(Long blockNumber){
		if(blockNumber<dayOneNumber)
			return;
		int blockdays = (int) (blockNumber / dayOneNumber) -1;
		long startblock = blockdays * dayOneNumber +1;
		long endblock = (blockdays+1) * dayOneNumber;
		List<StorageReleaseCompare> compareList = new ArrayList<StorageReleaseCompare>();
		List<Map> releasePays = storageReleaseMapper.getRangeReleasePays(startblock, endblock);
		List<Map> releaseStat = storageReleaseMapper.getRangeReleaseStat(startblock, endblock);
		Map<String,Map<String, Object>> paysMap = new HashMap<String,Map<String, Object>>();		
		for (Map<String, Object> pays : releasePays) {
			String revenueaddress = (String) pays.get("revenueaddress");
			if(paysMap.containsKey(revenueaddress))
				log.warn("Exist same revenueaddress "+revenueaddress+" in release pays block from "+startblock+" to "+endblock);
			else
				paysMap.put(revenueaddress, pays);
		}
		Map<String,Map<String, Object>> statMap = new HashMap<String,Map<String, Object>>();		
		for (Map<String, Object> stat : releaseStat) {
			String revenueaddress = (String) stat.get("revenueaddress");
			if(statMap.containsKey(revenueaddress))
				log.warn("Exist same revenueaddress "+revenueaddress+" in release stat block from "+startblock+" to "+endblock);
			else
				statMap.put(revenueaddress, stat);
		}
		for(String revenueaddress : paysMap.keySet()){
			Map<String, Object> pays = paysMap.get(revenueaddress);
			BigDecimal release_pay = pays.get("releaseamount")!=null ? new BigDecimal(pays.get("releaseamount").toString()) : null;
			Map<String, Object> stat = statMap.get(revenueaddress);
			BigDecimal release_stat = stat!=null && stat.get("releaseamount")!=null ? new BigDecimal(stat.get("releaseamount").toString()) : null;
			if(stat!=null)
				stat.put("exist", true);
			BigDecimal offset = null;
			if(release_pay!=null && release_stat!=null)
				offset = release_pay.subtract(release_stat);
			else if(release_pay!=null && release_stat==null)
				offset = release_pay.negate();
			else if(release_pay==null && release_stat!=null)
				offset = release_stat.negate();
					
			StorageReleaseCompare compare = new StorageReleaseCompare();
			compare.setBlockdays(blockdays);
			compare.setStartblock(startblock);
			compare.setEndblock(endblock);
			compare.setRevenueaddress(revenueaddress);
			compare.setRelease_pay(release_pay);
			compare.setRelease_stat(release_stat);
			compare.setOffset(offset);
			compare.setUpdatetime(new Date());
			compareList.add(compare);
		}
		
		for(String revenueaddress : statMap.keySet()){
			Map<String, Object> stat = statMap.get(revenueaddress);
			if(stat.containsKey("exist"))
				continue;
			BigDecimal release_stat = stat!=null && stat.get("releaseamount")!=null ? new BigDecimal(stat.get("releaseamount").toString()) : null;
			BigDecimal offset = release_stat!=null ? release_stat.negate() : null;			
			StorageReleaseCompare compare = new StorageReleaseCompare();
			compare.setBlockdays(blockdays);
			compare.setStartblock(startblock);
			compare.setEndblock(endblock);
			compare.setRevenueaddress(revenueaddress);
			compare.setRelease_pay(null);
			compare.setRelease_stat(release_stat);
			compare.setOffset(offset);
			compare.setUpdatetime(new Date());
			compareList.add(compare);
		}
		if(compareList.size()>0)
			storageReleaseMapper.insertCompareBatch(compareList);
	}
	
	
}
