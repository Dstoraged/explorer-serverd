package com.imooc.controller.job;

import com.imooc.Utils.TimeSpend;
import com.imooc.job.service.AddressService;
import com.imooc.job.service.BlockService;
import com.imooc.job.service.ContractService;
import com.imooc.job.service.RewardService;
import com.imooc.job.service.StorageService;
import com.imooc.job.service.TokenService;
import com.imooc.job.service.TransactionService;
import com.imooc.job.service.Web3jService;
import com.imooc.pojo.Contract;
import com.imooc.pojo.Tokens;
import com.imooc.pojo.Transaction;
import com.imooc.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RestController
public class CollectionJob {
    private static Logger logger = LoggerFactory.getLogger(CollectionJob.class);

    @Value("${ipaddress}")
    private String ipaddress;   //RPC URL
    @Value("${dayOneNumber:8640}")
    private Long dayOneNumber;	
    @Value("${taskblock:20}")
    private int taskblock;	
    @Value("${verifyblock:20}")
    private int verifyblock;
    @Value("${lockreleaseinterval:360}")
    private Long lockreleaseinterval;    
    @Value("${rewardcollectdelay:10}")
    private Integer rewardcollectdelay;
    
//    @Value("${releaseCompareEnable:true}")
//    private Boolean releaseCompareEnable;

//    @Value("${epoch}")
//    private long epoch;
//    @Value("${nodeblock}")
//    private long nodeblock;
//    private String decimals = "1000000000000000000";
    
//    @Value("${address1}")
//    private String address1;
//    @Value("${address2}")
//    private String address2;
//    @Value("${address3}")
//    private String address3;
    
    private static final String pledgeFilter= Constants.PRE+"9489b96ebcb056332b79de467a2645c56a999089b730c99fead37b20420d58e7";
//    private static final String LogFilter = Constants.PRE+"ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
      
    @Autowired
    private Web3jService web3jService;
    @Autowired
    private BlockService blockService;    
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AddressService addressService;
    @Autowired 
    private TokenService tokenService;    
    @Autowired
    private ContractService contractService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private RewardService rewardService;
    
    private static Long lastBlockReleaseNumber = null;
    private static Long lastRewardReleaseNumber = null;
    

 //   @Scheduled(cron = "0 * * * * ?")     // ${explorerJobCron}   0 * * * * ?    
	@Transactional
    public synchronized void run() throws Exception {
		//	Long latestBlockNumber = 1L;
		//	int blockTotal =1;    	
		logger.info("Collection job start ...");
		TimeSpend timeSpend = new TimeSpend();	
		
		long latestBlockNumber = blockService.getLatestBlockNumber();
		long currentBlockNumber = web3jService.getBlockNumber();
		if (latestBlockNumber == currentBlockNumber) {
			logger.warn("The lasest block number " + latestBlockNumber + " does not go forward");
			return;
		} else if (latestBlockNumber > currentBlockNumber) {
			logger.warn("The lasest block number in database " + latestBlockNumber + " has exceeded chain " + currentBlockNumber);
			return;
		}		
		Long problemBlock = blockService.verifyPreviousBlock(latestBlockNumber, verifyblock);
		if (problemBlock != null) {
			latestBlockNumber = problemBlock - 1;
			logger.warn("Collection job will be re execute on block " + latestBlockNumber + " after verify, spend " + timeSpend.getSpendTime());
		}
		int blockTotal = latestBlockNumber + taskblock < currentBlockNumber ? taskblock : (int) (currentBlockNumber - latestBlockNumber);
		logger.info("Collection job will execute block from " + (latestBlockNumber + 1) + " to " + (latestBlockNumber + blockTotal));
		
		Map<String, Long> addressMap = new HashMap<>();		
		Long blockNumber = null;
		Map<Long,BigInteger> blokMap=new  HashMap<Long,BigInteger>();
		for (int i = 0; i < blockTotal; i++) {
			blockNumber = latestBlockNumber + i + 1;
			collectBlock(blockNumber, addressMap,blokMap);
		}
		
		//	new StoragePledgeStatusJob(web3j, blockNumber, dayOneNumber, storageService).start();
		storageService.updateStoragePledgeStatus(blockNumber,blokMap);
		
		storageService.updateStorageVaildProgress(blockNumber);
		//	storageService.updateStorageAmount(blockNumber);
		
		if (addressMap.size() > 0 && blockNumber != null) {
			addressService.syncAddressesBalance(addressMap);
			addressService.syncAddressesSrtBalance(blockNumber);
		}
		logger.info("Collection job finished spend " + timeSpend.getSpendTime() + "\n");
		return;
    }
    
	@Transactional
//	@RequestMapping("/syncblock")
	public synchronized void syncBlock(@RequestParam(value = "blockNumber", required = true) Long blockNumber) {
		Map<String, Long> addressMap = new HashMap<>();
		Map<Long,BigInteger> blokMap=new  HashMap<Long,BigInteger>();
		collectBlock(blockNumber, addressMap,blokMap);

		storageService.updateStoragePledgeStatus(blockNumber,blokMap);

		storageService.updateStorageVaildProgress(blockNumber);

		if (addressMap.size() > 0 && blockNumber != null) {
			addressService.syncAddressesBalance(addressMap);
			addressService.syncAddressesSrtBalance(blockNumber);
		}
	}
	
	@SuppressWarnings("rawtypes")	
    private void collectBlock(Long blockNumber,Map<String, Long> addressMap,Map<Long,BigInteger> blokMap){
		TimeSpend blockSpend = new TimeSpend();
		logger.info("Block " + blockNumber + " start ...");
		Block block = web3jService.getBlockByNumber2(blockNumber);
		Date blockDate = new Date(block.getTimestamp().longValue() * 1000);
		blokMap.put(blockNumber.longValue(), block.getTimestamp());
		blockService.handleBlockDposAndPunished(block);
		
		BigInteger blockReward = blockService.handleBlockRewardAndFork(block);
        
		if (blockNumber % dayOneNumber == 0) {		
			storageService.saveSpaceStatistic(blockNumber, blockDate);
		}
		
		if (blockNumber % dayOneNumber == 1) {
			storageService.cleanVaildStatus(blockNumber);				
		}

	//	new StorageRevenueJob(web3j, blockNumber, storageService).start();
	//	storageService.updateStorageRevenue(blockNumber);

        //RewardLockReleaseDataJob and BlockLockReleaseDataJob
		if ((blockNumber - rewardcollectdelay) % dayOneNumber == 0) {
			Long rewardBloclk = blockNumber - rewardcollectdelay;
			Date rewardDate = web3jService.getBlockDate(rewardBloclk);
			if (lastBlockReleaseNumber != null && rewardBloclk <= lastBlockReleaseNumber) {
				logger.warn("BlockLockReleaseDataJob at block " + rewardBloclk + " has executed at " + lastBlockReleaseNumber);
			} else {
      		//	Date yesterday = DateUtil.getBeginDayOfYesterday();	
        	//	new BlockLockReleaseDataJob(yesterday,blockDate,web3j,blockNumber,utgStorageMinerMapper,transferMinerMapper).start();
				rewardService.blockReleaseReward(rewardBloclk, rewardDate);
				lastBlockReleaseNumber = rewardBloclk;
        	}
        }
		if ((blockNumber - lockreleaseinterval - rewardcollectdelay) % dayOneNumber == 0) {
			Long rewardBloclk = blockNumber - rewardcollectdelay;
			Date rewardDate = web3jService.getBlockDate(rewardBloclk);
        	if (lastRewardReleaseNumber!=null && rewardBloclk<=lastRewardReleaseNumber){
        		logger.warn("RewardLockReleaseDataJob at block "+rewardBloclk +" has executed at "+lastRewardReleaseNumber);
        	}else{            		
        	//	new RewardLockReleaseDataJob(yesterday,blockDate,web3j,blockNumber,dayOneNumber,utgStorageMinerMapper,transferMinerMapper,storageService).start();
				rewardService.lockReleaseReward(rewardBloclk, rewardDate);
				lastRewardReleaseNumber = rewardBloclk;
        	}
        }            
        /*					
		if (blockNumber > rewardcollectdelay) {			
			new StorageBlockJob(web3j, blockNumber - rewardcollectdelay, transferMinerMapper).start();								
		}
		if((blockNumber - lockreleaseinterval - rewardcollectdelay) % dayOneNumber == 0) {
			new StorageDailyJob(web3j, blockNumber- lockreleaseinterval - rewardcollectdelay, storageService, transferMinerMapper).start();
			new StorageReleaseJob(web3j,blockNumber,dayOneNumber,releaseCompareEnable,transferMinerMapper,storageReleaseMapper).start();
		}			
    	*/
        
		addressMap.put(block.getMiner(), blockNumber);
		BigInteger feeTotal = BigInteger.ZERO;
        List<TransactionResult> resultList = block.getTransactions();
        if (null != resultList && !resultList.isEmpty()) {
            List<Transaction> txList = new ArrayList<>();
            for (TransactionResult resultItem : resultList) {
                if (null == resultItem)
                    continue;
                TransactionObject transaction = (TransactionObject) resultItem;
				TransactionReceipt receipt = web3jService.getTransactionReceipt(transaction.getHash());
                if (null == receipt)
                    continue;
                
				Transaction tx = transactionService.parseTransaction(transaction, receipt, blockDate);             
                List<Log> logList = receipt.getLogs();
                Log logItem = (logList == null || logList.size() == 0) ? null : logList.get(0);
				String topTopic = (logItem == null || logItem.getTopics() == null || logItem.getTopics().size() == 0) ? null : logItem.getTopics().get(0);
				boolean hasParsed = false;

				hasParsed = transactionService.parseTxData(tx, logList);
                if(receipt.getContractAddress()!=null){
                	hasParsed = tokenService.buildToken(receipt, tx) !=null;
					if (!hasParsed) {
						contractService.buildContract(receipt, tx);
					}
					addressMap.put(receipt.getContractAddress(), blockNumber);
                }
                
                if(pledgeFilter.equalsIgnoreCase(topTopic) && logItem!=null){
                	rewardService.plexitLockTransaction(tx, logItem);
                }
                if(!hasParsed){
                	hasParsed = storageService.stroageTransaction(tx, logItem);
                }
                
            	if(!hasParsed && tx.getToAddr()!=null){            	
            		String contractAddr = tx.getToAddr();
            		Tokens token = tokenService.getToken(contractAddr);
            		if(token!=null){
            			hasParsed = tokenService.parseTransaction(token, tx, logItem);                        		
            		}
            		if(!hasParsed){                			
                		Contract contract = contractService.getContract(tx.getToAddr());
                		if(contract!=null){
                			hasParsed = contractService.parseTransaction(contract, tx, logItem);
                			if(hasParsed){
                				tx.setContract(tx.getToAddr());
                			}
                		}
            		}
            	}
				logger.info("Get transaction data, hash=" + tx.getHash() + ",blocknumber=" + tx.getBlockNumber() + ",form=" + tx.getFromAddr() + ",to="
						+ tx.getToAddr() + ",contract=" + tx.getContract() + ",ufooperator=" + tx.getUfooperator() + ",status=" + tx.getStatus()
						+ ",input size=" + (tx.getInput() == null ? 0 : tx.getInput().length()-2));
            	txList.add(tx);
				feeTotal = feeTotal.add(transaction.getGasPrice().multiply(receipt.getGasUsed()));
				addressMap.put(transaction.getFrom(), blockNumber);
				if (transaction.getTo() != null) 
					addressMap.put(transaction.getTo(), blockNumber);
			}
            transactionService.batchSave(txList);				
		}
		blockService.saveBlockAndReward(block, blockReward, feeTotal, resultList != null ? resultList.size() : 0);
		logger.info("Block " + blockNumber + " end , spend " + blockSpend.getSpendTime() + "\n");
    }

}
