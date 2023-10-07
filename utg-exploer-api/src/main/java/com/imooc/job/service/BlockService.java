package com.imooc.job.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.AlienSnapshot;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

import com.imooc.enums.NodeTypeEnum;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.BlockForkMapper;
import com.imooc.mapper.BlockMapper;
import com.imooc.mapper.BlockRewardsMapper;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.PunishedMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.pojo.BlockFork;
import com.imooc.pojo.BlockRewards;
import com.imooc.pojo.Blocks;
import com.imooc.pojo.Punished;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.pojo.vo.BlocksVo;
import com.imooc.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BlockService {
	
	@Autowired
    BlockMapper blockMapper;	
	@Autowired
	BlockForkMapper blockForkMapper;	 
	@Autowired
	BlockRewardsMapper blockRewardsMapper;
	@Autowired
    NodeExitMapper nodeExitMapper;	
	@Autowired
	PunishedMapper punishedMapper;
	@Autowired
    TransactionMapper transactionMapper;
	@Autowired
    AccountMapper addressMapper;
	@Autowired
	Web3jService web3jService;
	
	@Value("${nodeblock}")
    private long nodeblock;	
	@Value("${epoch}")
    private long round;
	private String decimals = "1000000000000000000";

	public Long getLatestBlockNumber(){
		Long latestBlockNumber = blockMapper.getLeatestBlockNumber();
		return latestBlockNumber;
	}

	public Long verifyPreviousBlock(Long latestBlockNumber,int taskblock){
		long previousSize = latestBlockNumber <= taskblock ? latestBlockNumber : taskblock;
	//	long previousBlock = latestBlockNumber <= taskblock ? 1 : latestBlockNumber - taskblock;
	//	List<Blocks> blockList = blockMapper.getBlocksAfter(previousBlock);
	//	Map<Long,Blocks> blockMap = new java.util.LinkedHashMap<>();
	//	for(Blocks block : blockList){
	//		blockMap.put(block.getBlockNumber(), block);
	//	}		
		Long problemBlock  = null;
		for(int i = 0; i < previousSize; i++){
			Long blockNumber = latestBlockNumber - i;			
		//	Block blockInfo = getBlockInfo(blockNumber,web3j);
			Block blockInfo = web3jService.getBlockByNumber2(blockNumber);
			if(blockInfo==null){
				log.warn("Get chain block "+blockNumber+" error, data is null");
				return null;
			}			
			List<BlocksVo> blockList = blockMapper.getBlockInfoByNumber(blockNumber);
			if(blockList.size()!=1){
				log.warn("Get data block "+blockNumber+" error, size="+blockList.size());
				return null;
			}
			BlocksVo blockVo = blockList.get(0);
			if (!blockVo.getHash().equalsIgnoreCase(blockInfo.getHash()) || !blockVo.getMinerAddress().equalsIgnoreCase(blockInfo.getMiner())
					|| !blockVo.getTxsCount().equals(blockInfo.getTransactions().size()) || !blockVo.getGasLimit().equals(blockInfo.getGasLimit().longValue())
					|| !blockVo.getGasUsed().equals(blockInfo.getGasUsed().longValue())) {
				problemBlock = blockNumber;
				String logs = "Block "+blockNumber+" has problem ";
				if(!blockVo.getHash().equalsIgnoreCase(blockInfo.getHash()))
					logs += ", chain hash = "+blockInfo.getHash()+" and db hash = "+blockVo.getHash();
				if(!blockVo.getMinerAddress().equalsIgnoreCase(blockInfo.getMiner()))
					logs += ",chain miner = "+blockInfo.getMiner()+" and db miner = "+blockVo.getMinerAddress();
				if(!blockVo.getTxsCount().equals(blockInfo.getTransactions().size()))
					logs += ",chain txcount = "+blockInfo.getTransactions().size()+" and db txcount = "+blockVo.getTxsCount();
				if(!blockVo.getGasLimit().equals(blockInfo.getGasLimit().longValue()))
					logs += ",chain gaslimit = "+blockInfo.getGasLimit()+" and db gaslimit = "+blockVo.getGasLimit();
				if(!blockVo.getGasUsed().equals(blockInfo.getGasUsed().longValue()))
					logs += ",chain gasused = "+blockInfo.getGasUsed()+" and db gasused = "+blockVo.getGasUsed();
				log.warn(logs);
			}			
		}		
		if(problemBlock!=null){			
			blockMapper.truncBlock(problemBlock);
			blockMapper.truncBlockFork(problemBlock);
			blockMapper.truncBlockRewards(problemBlock);
			blockMapper.truncPunished(problemBlock);

			blockMapper.truncTransaction(problemBlock);
		//	blockMapper.truncContract(problemBlock);			
		//	blockMapper.truncStorageSpace(problemBlock);
		//	blockMapper.truncStorageRent(problemBlock);
		//	blockMapper.truncStorageRequest(problemBlock);
		}
		return problemBlock;
	}
	

	public void handleBlockDposAndPunished(Block block) {
		long blockNumber = block.getNumber().longValue();
		Date blockDate = new Date(block.getTimestamp().longValue() * 1000);
		String pledge = getPledgeDeposit();
		AlienSnapshot.Snapshot snapSign = web3jService.getSnapshotSigner(blockNumber);
		AlienSnapshot.Snapshot snapSignPrevious = web3jService.getSnapshotSigner(blockNumber - 1);
		if (snapSign != null && blockNumber % nodeblock == 0) {
			List<String> signers = snapSign.getSigners(); 		// 21 lists of all witness nodes
			signers = signers.stream().map(String::toLowerCase).collect(Collectors.toList());
			saveDposNodeInfo(blockNumber, signers, pledge);		// Get node data information
			log.info("Save dpos node data at " + blockNumber);
		}
		
		if (snapSign != null && snapSignPrevious != null) {
			Map<String, BigInteger> thisPunished = snapSign.getPunished();
			Map<String, BigInteger> previousPunished = snapSignPrevious.getPunished();
			updateNodeFractions(thisPunished);
			if (thisPunished.size() > 0) { 		// TODO
				savePunishedInfo(pledge, blockNumber, blockDate, thisPunished, previousPunished, block.getMiner());
			}
		}
	}
	

	public BigInteger handleBlockRewardAndFork(Block block){
		long blockNumber = block.getNumber().longValue();
		Date blockDate = new Date(block.getTimestamp().longValue() * 1000);
		BigInteger blockReward = saveBlockReward(block.getHash(), blockNumber, block.getMiner(), blockDate);
        List<String> uncles = block.getUncles();
        if (null != uncles && !uncles.isEmpty()) {
			int forkCount = saveBlockFork(uncles, block.getHash(), blockNumber);			
			BigInteger unclesReward = saveUnclesReward(block.getHash(), blockNumber, block.getMiner());
			if(forkCount>0)
				log.info("Save block fork at " + blockNumber + " count " + forkCount);
            blockReward = blockReward.add(unclesReward);
        }
        return blockReward;
	}
	

	public void saveBlockAndReward(Block block, BigInteger blockReward, BigInteger feeTotal, int txscount) {
		saveFeeReward(block.getHash(), block.getMiner(), feeTotal, block.getNumber());		
		blockReward = blockReward.add(feeTotal);
		saveBlockInfo(block, blockReward, txscount);
	}
	

	private void saveBlockInfo(Block block, BigInteger reward, int txCount) {
		Long blockNumber = block.getNumber().longValue();
	//	BigDecimal rewardNumber = getRewardNumber(blockNumber);
		Integer round = getRound(blockNumber.intValue());
		Blocks item = new Blocks();
		item.setHash(block.getHash());
		item.setBlockNumber(block.getNumber().longValue());
		item.setIsTrunk(1);
		item.setTimeStamp(new Date(block.getTimestamp().longValue() * 1000));
		item.setMinerAddress(block.getMiner());
		item.setBlockSize(block.getSize().intValue());
		item.setGasLimit(block.getGasLimit().longValue());
		item.setGasUsed(block.getGasUsed().longValue());
		item.setReward(new BigDecimal(reward));
		item.setTxsCount(txCount);
		item.setNonce(block.getNonceRaw());
		item.setDifficulty(block.getDifficulty().longValue());
		item.setTotalDifficulty(block.getTotalDifficulty().longValue());
		item.setParentHash(block.getParentHash());
		item.setRound(round);
		blockMapper.insertOrUpdate(item);
	}
	

	private String getPledgeDeposit() {
		List<Transaction> deposit = transactionMapper.getTransactionByTxType("Deposit");
		String pledge = null;
		if (deposit.size() > 0) {
			pledge = deposit.get(0).getParam1();
		} else {
			throw new RuntimeException("no Deposit config set");
		}
		return pledge;
	}
	

    private void saveDposNodeInfo(Long blockNumber, List<String> signers, String pledge){
		Set<String> witNodes = new HashSet<>();
		if (blockNumber % nodeblock == 0) {
			if (signers != null && signers.size() > 0) {
				for (int k = 0; k < signers.size(); k++) {
					witNodes.add(signers.get(k).toLowerCase());
				}
			}
		}
        //Synchronize node table status
        if (witNodes.size()>0) {
            List<UtgNodeMiner> list =  nodeExitMapper.getNodeListNotExit();
            for (UtgNodeMiner node: list) {
                if(witNodes.contains(node.getNode_address().toLowerCase())){
                    if(node.getNode_type()==NodeTypeEnum.wait.getCode()){
                        UtgNodeMiner UtgNodeMiner = new UtgNodeMiner();
                        UtgNodeMiner.setNode_address(node.getNode_address());
                        UtgNodeMiner.setNode_type(NodeTypeEnum.witness.getCode());
                        UtgNodeMiner.setSync_time(new Date());
                        nodeExitMapper.updateNodeStatus(UtgNodeMiner);
                    }
                }else {
                    if(node.getNode_type()==NodeTypeEnum.witness.getCode()) {
                        UtgNodeMiner UtgNodeMiner = new UtgNodeMiner();
                        UtgNodeMiner.setNode_address(node.getNode_address());
                        UtgNodeMiner.setNode_type(NodeTypeEnum.wait.getCode());
                        UtgNodeMiner.setSync_time(new Date());
                        nodeExitMapper.updateNodeStatus(UtgNodeMiner);
                    }
                }
            }
           /* List<Transaction> deposit = transactionMapper.getTransactionByTxType(SscOperatorEnum.Deposit.getCode());
            String pledge = null;
            if(deposit.size()>0){
                pledge = deposit.get(0).getParam1();
            }else{
                throw  new RuntimeException("no Deposit config set");
            }*/            
            for (String witNode:witNodes) {
                UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
                queryForm.setNode_address(witNode);
                if(nodeExitMapper.getNode(queryForm) == null){
                	String address = Constants.PRE+witNode.substring(2);
                    UtgNodeMiner node = new UtgNodeMiner() ;
                    node.setNode_address(address);
                    node.setNode_type(NodeTypeEnum.witness.getCode());
                    node.setBlocknumber(blockNumber);
                    node.setPledge_amount(new BigDecimal(pledge));
                    node.setSync_time(new Date());
                    nodeExitMapper.saveOrUpdateNodeMiner(node);
                    addressMapper.setAsMiner(address,blockNumber);
                }
            }
        }
    }
        

	private void updateNodeFractions(Map<String, BigInteger> punished) {
		List<String> addresses = new ArrayList<>();
		addresses.add("0");
		for (Map.Entry<String, BigInteger> entry : punished.entrySet()) {
			String address = entry.getKey().toLowerCase();
			int fractions = entry.getValue().intValue();
			addresses.add(address);
			UtgNodeMiner node = new UtgNodeMiner();
			node.setNode_address(address);
			node.setFractions(fractions);
			node.setSync_time(new Date());
			nodeExitMapper.updateNodeMiner(node);
		}
		if (addresses.size() > 0) {
			nodeExitMapper.updateNodeBatch(addresses);
		}
	}
    

	private void savePunishedInfo(String pledge, Long blockNumber, Date blockDate, Map<String, BigInteger> thisPunished, Map<String, BigInteger> previousPunished,String minerHash) {
        minerHash=minerHash.toLowerCase();
        Long feeMiss=30L;			//Absent block deducts 30
        for(Map.Entry<String,BigInteger> entry:thisPunished.entrySet()){  //Traverse the map of the current block height                    
            String address = entry.getKey();
            if(previousPunished.containsKey(address)){
            	BigInteger valueThis = entry.getValue();
            	BigInteger valuePrevious = previousPunished.get(address);
            	if(valueThis.compareTo(valuePrevious)<=0)
            		continue;
            }
            BigInteger pledgeAmoun = new BigInteger(pledge).divide(new BigInteger(Constants.TOTALMBPOINT+"")).multiply(new BigInteger(feeMiss.toString()));
            Punished punished = new Punished();
            punished.setAddress(address);
            punished.setBlockNumber(blockNumber);
            punished.setRound(getRound(blockNumber.intValue()));
            punished.setFractions(feeMiss);
            punished.setType(1);	//1 Absent block
            punished.setTimeStamp(blockDate);            
            punished.setPledgeAmount(new BigDecimal(pledgeAmoun));
            punishedMapper.insert(punished);
            log.info("Save punlished data for address="+address+" at block "+blockNumber);
        }
    }
    

	private int saveBlockFork(List<String> uncles, String nephewHash, Long nephewNumber) {
		int forkCount = 0;
		for (String uncleHash : uncles) {
			if (uncleHash==null||"".equals(uncleHash))
				continue;
			BlockFork item = new BlockFork();
			item.setNepHewHash(nephewHash);
			item.setNepHewNumber(nephewNumber);
			item.setIsTrunk(1);
			item.setUncleHash(uncleHash);
			item.setUncleHandled(0);
			blockForkMapper.saveOrUpdate(item);
			forkCount++;
		}
		return forkCount;
	}

	private BigInteger saveBlockReward(String blockHash, Long blockNumber, String minerAddress, Date blockDate) {
        BigDecimal rewardNumber = getRewardNumber(blockNumber);
		BlockRewards reward = new BlockRewards();
		reward.setBlockHash(blockHash);
		reward.setAddress(minerAddress);
		reward.setReward(rewardNumber);
		reward.setRewardType("BlockReward");
		reward.setRewardHash(blockHash);
		reward.setBlockNumber(blockNumber);
		reward.setIsTrunk(1);
		reward.setTimeStamp(blockDate);
		blockRewardsMapper.insertOrUpdate(reward);
		long blockReward = rewardNumber.longValue();
		return BigInteger.valueOf(blockReward);
    }
	
    private BigInteger saveUnclesReward(String blockHash, Long blockNumber, String minerAddress) {
    	BigDecimal rewardNumber = getRewardNumber(blockNumber);    	
        BlockRewards reward = new BlockRewards();
        reward.setBlockHash(blockHash);
        reward.setAddress(minerAddress);
        reward.setReward(rewardNumber);
        reward.setRewardType("UncleReward");
        reward.setRewardHash(blockHash);
        reward.setBlockNumber(blockNumber);
        reward.setIsTrunk(1);
        blockRewardsMapper.insertOrUpdate(reward);       
        long blockReward=rewardNumber.longValue();
        return BigInteger.valueOf(blockReward);
    }
    
	private void saveFeeReward(String blockHash, String minerAddress, BigInteger fee, BigInteger number) {
		BlockRewards reward = new BlockRewards();
		reward.setBlockHash(blockHash);
		reward.setAddress(minerAddress);
		reward.setReward(new BigDecimal(fee));
		reward.setRewardType("FeeReward");
		reward.setRewardHash(blockHash);
		reward.setBlockNumber(number.longValue());
		reward.setIsTrunk(1);
		blockRewardsMapper.insertOrUpdate(reward);
	}
	
        

    //Get node exit data
    /*private void saveNodeExit(String rs){
        BigDecimal pledgeAmount=convertToBigDecimal("0x"+rs.substring(2, 66));
        BigDecimal deductionAmount=convertToBigDecimal("0x"+rs.substring(67,130));
        BigDecimal tractAmount=convertToBigDecimal("0x"+rs.substring(131,194));
        BigDecimal lockStartNumber=convertToBigDecimal("0x"+rs.substring(195,258));
        BigDecimal lockNumber=convertToBigDecimal("0x"+rs.substring(259,322));
        BigDecimal releaseNumber=convertToBigDecimal("0x"+rs.substring(323,386));
        BigDecimal releaseInterval=convertToBigDecimal("0x"+rs.substring(387,450));
        NodeExit nodeExit = new NodeExit();
        nodeExit.setAddressName("");
        nodeExit.setTimeStamp(new Date());
        nodeExit.setPledgeAmount(pledgeAmount);
        nodeExit.setDeductionAmount(deductionAmount);
        nodeExit.setTractAmount(tractAmount);
        nodeExit.setLockStartNumber(lockStartNumber.longValue());
        nodeExit.setLockNumber(lockNumber.longValue());
        nodeExit.setReleaseNumber(releaseNumber.longValue());
        nodeExit.setReleaseInterval(releaseInterval.longValue());
        nodeExitMapper.insert(nodeExit);
    }*/
    
    /*private BigDecimal convertToBigDecimal(String value) {
        if (null != value)
            return new BigDecimal(Numeric.decodeQuantity(value));
        return null;
    }*/
    
	private BigDecimal getRewardNumber(Long blockNumber){        
		BigDecimal rewardNumber = new BigDecimal("0.0");        
        /*if(blockNumber<=Long.valueOf(blocktotal)){
            rewardNumber=new BigDecimal("3.40740710650912607813");
        }else{
            rewardNumber= getMinerNumber(blockNumber);
        }*/
		rewardNumber = getMinerNumber(blockNumber);
		BigDecimal fee = new BigDecimal(decimals);	//"1000000000000000000"
		rewardNumber = rewardNumber.multiply(fee);
		return rewardNumber;
	}
	
	private BigDecimal getMinerNumber(Long nowNumber) {
		try {
			double x = 210000d / 420000;
			BigDecimal total = new BigDecimal(x * Math.pow(1 - 0.04, nowNumber / 420000));
			return total;
		} catch (Exception e) {
			log.warn("getMinerNumber " + nowNumber + " error:" + e.getMessage());
			return new BigDecimal("0");
		}
	}
    
	//Get the round of block height
	private Integer getRound(int blockNumber) {
		int rouds = 0;
		if (blockNumber <= round) {
			rouds = 1;
			return rouds;
		} else {
		//  float cell=blockNumber/round;
			double nYear = (double) blockNumber / round;
			double nFee = Math.ceil(nYear);
			int round = new Double(nFee).intValue();
			return round;
		}
	}
}
