package com.imooc.controller.job;

import com.imooc.Utils.MapCache;
import com.imooc.enums.MinerStatusEnum;
import com.imooc.enums.NodeTypeEnum;
import com.imooc.job.service.AddrTransferService;
import com.imooc.job.service.AddressService;
import com.imooc.job.service.BlockService;
import com.imooc.job.service.ContractService;
import com.imooc.job.service.PosService;
import com.imooc.job.service.SpPoolService;
import com.imooc.job.service.StatisticService;
import com.imooc.job.service.StorageService;
import com.imooc.job.service.TokenService;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.vo.TransactionVo;
import com.imooc.service.NodeLjRewardService;
import com.imooc.service.util.TxDataParse;
import com.imooc.utils.*;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ConditionalOnProperty("explorerJobCron")
@RestController
@Api(value = "Sync block transaction split token information", tags = "Sync block transaction split token information")
public class ExplorerJob {
    private static Logger logger = LoggerFactory.getLogger(ExplorerJob.class);

    @Value("${ipaddress}")
    private String ipaddress;   //url

    @Value("${dayOneNumber}")
    private   Long dayOneNumber;//1 day block height, 10 seconds to generate a block


    @Value("${addresspool}")
    private String addresspool;//Interest pool address

    @Value("${yeartotal}")
    private   Long yeartotal;//The total number of seconds in a year

    @Value("${perblocksecond}")
    private   int perblocksecond;//How many seconds is a block

    @Value("${year}")
    private  String year;//Life cycle is calculated as 43 years

    @Value("${allrewards}")
    private  String allrewards;  //Total block reward

    @Value("${blocktotal}")
    private   String blocktotal;//Estimated total number of blocks in a year

    @Value("${teamlock}")
    private String teamlock;

    @Value("${nodepledge}")
    private String nodepledge;


    @Value("${mineraddress1}")
    private String mineraddress1;

    @Value("${mineraddress2}")
    private String mineraddress2;

    @Value("${mineraddress3}")
    private String mineraddress3;

    @Value("${signaddress}")
    private  String signaddress;

    @Value("${teammanager}")
    private String teammanager;

    @Value("${feemanager}")
    private  String feemanager;

    @Value("${epoch}")
    private long round;//Number of blocks in a cycle

    @Value("${epoch}")
    private long epoch;//Number of blocks between each round of election

    @Value("${nodeblock}")
    private long nodeblock;//Number of synchronization witness node blocks

    @Value("${sendData}")
    private String dendData;//

    @Value("${contractMethod}")
    private String contractMethod;//

    @Value("${rnMinValue}")
    private String rnMinValue;

    @Value("${cnMinValue}")
    private String cnMinValue;

    @Value("${enMinValue}")
    private String enMinValue;

    @Value("${voterInput}")
    private  String voterInput;

    @Value("${exploerUrl}")
    private  String exploerUrl;

    @Value("${profitUrl}")
    private  String profitUrl;

    @Value("${pledgeAddress}")
    private  String pledgeAddress;

    @Value("${address1}")
    private  String address1;

    @Value("${address2}")
    private  String address2;

    @Value("${address3}")
    private  String address3;

    @Value("${taskblock}")
    private  int taskblock;				//each task execute block number	
    @Value("${lockreleaseinterval}")
    private   Long lockreleaseinterval;//lockreleaseinterval
        
    @Value("${storageDailyInterval:}")
    private  Long storageDailyInterval;
    
    @Value("${storageDelayBlock:10}")
    private  Long storageDelayBlock;

    @Value("${releaseCompareEnable:true}")
    private Boolean releaseCompareEnable;
    
    @Value("${blocklock}")
    private   Long blocklock;//blocklock
    
    @Value("${rewardStatBlock:360}")
    private int rewardStatBlock;
    @Value("${accumulateRewardBlock:730}")
    private long accumulateRewardBlock;
//    private String erc20Start="0x70a08231";//

//    private String erc1155Start="0x00fdd58e";//

//    private String erc1155Transaction="0xf242432a";//

    private static final String pledgeFilter=Constants.PRE+"9489b96ebcb056332b79de467a2645c56a999089b730c99fead37b20420d58e7";

//    private static final String LogFilter = Constants.PRE+"ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
        
    private String decimals = "1000000000000000000";
    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private BlockForkMapper blockForkMapper;

    @Autowired
    private BlockRewardsMapper blockRewardsMapper;

    @Autowired
    private TransactionMapper transactionMapper;

//    @Autowired
//    private TransForkMapper transForkMapper;

//    @Autowired
//    private ContractsMapper contractsMapper;

//    @Autowired
//    private LogMapper logMapper;

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private TransferTokenMapper transferTokenMapper;

//    @Autowired
//    private TokenInstancesMapper tokenInstancesMapper;

    @Autowired
    private AddrBalancesMapper addrBalancesMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TransferMinerMapper transferMinerMapper;

//    @Autowired
//    private DposVotesMapper dposVotesMapper;

//    @Autowired
//    private DposNodeMapper dposNodeMapper;

    @Autowired
    private NodeExitMapper nodeExitMapper;

    @Autowired
    private PunishedMapper punishedMapper;

//    @Autowired
//    private DposVoterWalletMapper dposVoterWalletMapper;

//    @Autowired
//    private ContractTokenMapper contractTokenMapper;

//    @Autowired
//    private PledgeDataMapper pledgeDataMapper;

//    @Autowired
//    private PledgeTotalDataMapper pledgeTotalDataMapper;

    @Autowired
    private UtgStorageMinerMapper utgStorageMinerMapper;
    
    @Autowired
    private StorageSpaceMapper storageSpaceMapper;    
//    @Autowired
//    private StorageReleaseMapper storageReleaseMapper;
    @Autowired
    private GlobalConfigMapper globalConfigMapper;
    
    
    @Autowired
    private BlockService blockService;
    
    @Autowired
    private StorageService storageService;
    
    @Autowired 
    private TokenService tokenService;
    
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private AddressService addressService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private SpPoolService spService;
    @Autowired
    private PosService poSService;
    @Autowired
    private NodeLjRewardService ljRewardService;
//    @Autowired
//    private AddrTransferService addrTransferService;
    
    private static Long lastRewardReleaseNumber = null;
    private static Long lastBlockReleaseNumber = null;
    
    @Value("${StBandwidthMakeup:}")
    private Long StBandwidthMakeup;
    @Value("${StBandwidthMakeupDays:7}")
    private int StBandwidthMakeupDays;
    @Value("${nodePledgeInterval:30}")
    private int nodePledgeInterval;
//    @Scheduled(cron = "0 * * * * ?") 
//    public void run()  throws Exception {
//    	Long blockNumber=14225l;
//    	 Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
//    	Date timeStamp= new Date();
//    	 Date yesterday = DateUtil.getBeginDayOfYesterday();
//    	 EthBlock ethBlock = null;
//    	 BigInteger blockNumberPara = BigInteger.valueOf(blockNumber);
//         
//         try {
//             ethBlock = web3j.ethGetBlockByNumber2(DefaultBlockParameter.valueOf(blockNumberPara), true).send();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         EthBlock.Block block = ethBlock.getBlock();
//    	 new NodeRewardCltJob(ipaddress,blockNumber,dayOneNumber,block.getTimestamp(),ljRewardService).start();
// 		 
//    }
    @Scheduled(cron = "${explorerJobCron}")     // 0/5 * * * * ?   0 * * * * ?
    @Transactional
    public synchronized void run() throws Exception {
		logger.info("ExplorerJob start...");
		List<Transaction> deposit = transactionMapper.getTransactionByTxType(SscOperatorEnum.Deposit.getCode());
		String pledge = null;
		if (deposit.size() > 0) {
			pledge = deposit.get(0).getParam1();
		} else {
			throw new Exception("no Deposit config set");
		}
        Date yesterday = DateUtil.getBeginDayOfYesterday();
        
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        Long lastBlockNumber = blockMapper.getLeatestBlockNumber();   //block number in database
        if (lastBlockNumber == null) 
            lastBlockNumber = 0L;        
        long currentBlockNumber = getCurrentBlockNumber(web3j);		//block number in chain
        if (lastBlockNumber >= currentBlockNumber) {
        	logger.warn("The leaset Block number in database "+lastBlockNumber+" has exceeded chain "+currentBlockNumber);
            return;
        }
        
        //verify previous block
        Long problemBlock = blockService.verifyPreviousBlock(lastBlockNumber, 20);
        if(problemBlock!=null){
        	lastBlockNumber = problemBlock -1;
        }
        
        int blockTotal = lastBlockNumber + taskblock< currentBlockNumber ? taskblock : (int) (currentBlockNumber - lastBlockNumber);        
        int txCount = 0;
        int logCount = 0;
        int logTotal = 0;
        int uncleCount = 0;
        int uncleTotal = 0;
        int blockCount = 0;
        int tokenCount = 0;
        int tokenTotal = 0;
        int balanceCount = 0;
        int balanceTotal = 0;
        int contractCount = 0;
        int contractTotal = 0;
        int tokenTransCount = 0;
        int tokenTransTotal = 0;
        long allst = System.currentTimeMillis();        
        logger.info("ExplorerJob collect block from " + (lastBlockNumber + 1) + " to " + (lastBlockNumber + blockTotal));
        Long blockNumber = null;
        Map<Long,BigInteger> blockTimeMap=new HashMap<>();
        for (int i = 0; i < blockTotal; i++) {
            long blockst = System.currentTimeMillis();            
            blockNumber = lastBlockNumber + i + 1;
            logger.info("Block "+blockNumber+" start .....");
            BigInteger blockNumberPara = BigInteger.valueOf(blockNumber);
            EthBlock ethBlock;
            try {
                ethBlock = web3j.ethGetBlockByNumber2(DefaultBlockParameter.valueOf(blockNumberPara), true).send();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (null == ethBlock) {
                break;
            }
            EthBlock.Block block = ethBlock.getBlock();
            if (null == block) {
                break;
            }
            blockTimeMap.put(blockNumber.longValue(), block.getTimestamp());
            //Snapshot and save punished
            long snapst = System.currentTimeMillis();
          //  logger.info("snap start .....");
            AlienSnapshot.Snapshot snapSign=web3j.alienSnapshotSignerAtNumber(new Long(blockNumber).intValue()).send().getSnapshot();
            Date timeStamp= new Date();
          //  logger.info("blocknumber="+blockNumber+" getSnapshot time costs="+(System.currentTimeMillis()-snapst));
            long lastst = System.currentTimeMillis();
            AlienSnapshot.Snapshot snapSignLatest=web3j.alienSnapshotSignerAtNumber(new Long(blockNumber-1).intValue()).send().getSnapshot();
         //   logger.info("blocknumber="+(blockNumber-1)+" getSnapLatest time costs="+(System.currentTimeMillis()-lastst));
            timeStamp = new Date(block.getTimestamp().longValue() * 1000);
            if(snapSign!=null && snapSignLatest!=null){
	            long time = snapSign.getLoopStartTime().longValue() * 1000;
	            Date date = new Date(time);
	            List<String> signers = snapSign.getSigners();//21 lists of all witness nodes
	            signers = signers.stream().map(String::toLowerCase).collect(Collectors.toList());
	            saveDposNodeInfo(blockNumber, nodeblock, signers);//Get node data information
	            Map<String, BigInteger> thisBlock = snapSign.getPunished();	            
	            Map<String, BigInteger> latestBlock = snapSignLatest.getPunished();
	            updateNodeFractions(thisBlock);
	            Map<String, Object> thisPledge = snapSign.getSignpledge();
				if (blockNumber % nodePledgeInterval == 0)
	            	updateNodePledge(thisPledge);
	            if (thisBlock.size() == 0 || thisBlock.isEmpty()) {
	                if (latestBlock.size() == 0 || latestBlock.isEmpty()) {
	       //             logger.info("The comparison data are all empty");
	                }
	            } else {
	                savePunishedInfo(pledge,blockNumber, timeStamp, thisBlock, latestBlock, block.getMiner());
	            }
            }
            //Enable scheduled task
			//execute every day end
			if (blockNumber % dayOneNumber == 0) {
				storageService.saveSpaceStatistic(blockNumber, timeStamp);
			}
			//execute every day start
			if (blockNumber % dayOneNumber == 1) {
				storageService.cleanVaildStatus(blockNumber);
			}
			//TODO Only specific blocks have data. In order not to miss, each block will collect
		//	new StorageRevenueJob(web3j, blockNumber, storageService).start();
			storageService.updateStorageRevenue(blockNumber);
			
            //RewardLockReleaseDataJob and BlockLockReleaseDataJob
            if(blockNumber!=lockreleaseinterval && (blockNumber-lockreleaseinterval)%dayOneNumber==0){
            	if (lastRewardReleaseNumber!=null && blockNumber<=lastRewardReleaseNumber){
            		logger.warn("RewardLockReleaseDataJob at block "+blockNumber +" has executed at "+lastRewardReleaseNumber);
            	}else{            		
            		new RewardLockReleaseDataJob(yesterday,timeStamp,web3j,blockNumber,dayOneNumber,utgStorageMinerMapper,transferMinerMapper,storageService,statisticService,spService).start();
            		lastRewardReleaseNumber = blockNumber;            		
            	}
            }
          //  if(blockNumber%blocklock==0){
            if(blockNumber!=blocklock && (blockNumber-blocklock)%dayOneNumber==0){
            	if(lastBlockReleaseNumber!=null && blockNumber<=lastBlockReleaseNumber){
            		logger.warn("BlockLockReleaseDataJob at block "+blockNumber +" has executed at "+lastBlockReleaseNumber);
            	}else{
            		new BlockLockReleaseDataJob(yesterday,timeStamp,web3j,blockNumber,dayOneNumber,utgStorageMinerMapper,transferMinerMapper,statisticService).start();
            		lastBlockReleaseNumber = blockNumber;
            	}
            }
            /*					
			if (blockNumber > storageDelayBlock) {
				// Storage Block job
				new StorageBlockJob(web3j, blockNumber-storageDelayBlock, transferMinerMapper).start();
				// Storage Daily Job
				if(storageDailyInterval==null){			//execute on every block when debug... 			
					new StorageDailyJob(web3j, blockNumber-storageDelayBlock, storageService, transferMinerMapper).start();
				}else if((blockNumber - storageDelayBlock) % dayOneNumber == storageDailyInterval) {	//The storageDailyInterval block of each execute
					new StorageDailyJob(web3j, blockNumber-storageDelayBlock, storageService, transferMinerMapper).start();
				}				
			}
			*/
            /*if (blockNumber != lockreleaseinterval && (blockNumber - lockreleaseinterval) % dayOneNumber == 0) {
            	new StorageReleaseJob(web3j,blockNumber,dayOneNumber,releaseCompareEnable,transferMinerMapper,storageReleaseMapper).start();
        	}*/
            			
						
        //    long snapEd = System.currentTimeMillis();
        //    logger.info("Block "+blockNumber+" snap end ,spends "+(snapEd-snapst)+" ms");

            //Save block reward and fork 
            Date dates=new Date(block.getTimestamp().longValue() * 1000);//Block timestamp
            BigInteger blockReward = saveBlockReward(block.getHash(), blockNumber.longValue(), block.getMiner(),dates);
            List<String> uncles = block.getUncles();
            if (null != uncles && !uncles.isEmpty()) {
                uncleTotal += uncles.size();
                uncleCount += saveBlockFork(uncles, block.getHash(), blockNumber.longValue());
                blockReward = blockReward.add(saveUnclesReward(block.getHash(), blockNumber.longValue(), uncles.size(), block.getMiner()));
            }
            
            //Iterate block transactions 
            Map<String, Boolean> ethAddress = new HashMap<>();
            ethAddress.put(block.getMiner(),false);//miner
            Map<String, Map<String, Boolean>> erc20Address = new HashMap<>();
            BigInteger feeTotal = BigInteger.ZERO;
            List<EthBlock.TransactionResult> resultList = block.getTransactions();
            if (null != resultList && !resultList.isEmpty()) {
            	logger.info("Block " + blockNumber + " have " + resultList.size() + " transactions");
                List<Transaction> trasList = new ArrayList<>();
                Date timestamp = new Date(block.getTimestamp().longValue() * 1000);
                for (EthBlock.TransactionResult resultItem : resultList) {
                    if (null == resultItem)
                        continue;
                    EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) resultItem;
                    TransactionReceipt receipt = getTransactionReceipt(web3j, transaction.getHash());
                    String fromHashs="";
                    if(transaction.getFrom() !=null){
                        fromHashs =transaction.getFrom();
                    }
                    if (null == receipt)
                        continue;
                    /*if(receipt.getLogs().size()==0){
                        txCount += saveTransaction(trasList,timestamp, transaction, receipt,null) ? 1 : 0;
                    }
                    if(receipt.getLogs().size()!=0){
                        txCount += saveTransaction(trasList,timestamp, transaction, receipt,receipt.getLogs()) ? 1 : 0;
                    }*/
                    //Return Transaction data object
                    Transaction tx = saveTransaction(trasList,timestamp, transaction, receipt,receipt.getLogs());
                    if(tx!=null)
                    	txCount ++;
                    //Move to iterate bottom to save data
                    /*if(trasList.size()>0&&trasList.size()/Constants.BATCHCOUNT>=1){
                        transactionMapper.batchTransaction(trasList);
                        trasList = new ArrayList<>();
                    }*/
                    feeTotal = feeTotal.add(transaction.getGasPrice().multiply(receipt.getGasUsed()));
                    ethAddress.put(transaction.getFrom(), false);
                    if (transaction.getTo() != null) {
                        ethAddress.put(transaction.getTo(), false);
                    }
                    //New contract                    
                    if(receipt.getContractAddress()!=null){
                    //	boolean isMatched = handleERC20Token(web3j, receipt.getContractAddress()); // ERC20 Token
                    	boolean isMatched = tokenService.buildToken(receipt, tx) !=null; 	// Token Contract
						if (!isMatched) {
							contractService.buildContract(receipt, tx); 		// Other Contract							
						}
						ethAddress.put(receipt.getContractAddress(), true);
                    }
	                
                    boolean isMatch = false;
                    List<Log> logList = receipt.getLogs();
                    if (null != logList && logList.size()>0) {
                        logTotal += logList.size();
                        for (Log logItem : logList) {
                            if (null == logItem)
                                continue;
                            List<String> topic = logItem.getTopics();
                           
                            if (null != topic && topic.size()>0) {
                                String topFilter=logItem.getTopics().get(0);
                             //   logger.info("topFilter="+topFilter);                                                               
                                //pledge
                                if(topFilter.equalsIgnoreCase(pledgeFilter)){
                                	String addres=logItem.getAddress();                               
                                	validateAddress(topFilter,blockNumber,addres,logItem,fromHashs,transaction.getGasPrice(),receipt.getGasUsed(),block.getGasLimit(),transaction,timeStamp,receipt,dates);
                                }
                                
                                //erc20
                                /*else if(topFilter.equalsIgnoreCase (LogFilter)){
                                    Date dateTranToken =new Date(block.getTimestamp().longValue() * 1000);
                                    TransToken token = null;
                                    if(transaction.getTo()!=null ){
                                        token = saveTransTokenFromLog(logItem, transaction.getGasPrice(), receipt.getGasUsed(),transaction.getNonce(),block.getGasLimit(),dateTranToken);
                                       // saveTransactionErc20(timestamp, transaction, receipt,logItem) ;
                                        tx.setContract(token.getContract());
                                        tx.setToAddr(token.getToAddr());
                                        tx.setUfoprefix("CT");
                                		tx.setUfoversion("1");
                                        tx.setUfooperator("TokenTransfer");
                                        tx.setParam1(token.getAmount().toString());                                        
                                        Map<String, Boolean> targetMap = erc20Address.get(logItem.getAddress());
                                        if(null ==targetMap){
                                            targetMap = new HashMap<String, Boolean> ();
                                            erc20Address.put(logItem.getAddress(),targetMap);
                                        }
                                        if (null != token) {
                                            erc20Address.put(logItem.getAddress(), targetMap);
                                        }
                                        if(token.getCoinType()==0){
                                            handleERC20Token(web3j, logItem.getAddress());
                                        }
                                        if(null !=targetMap){
                                            targetMap.put (token.getFromAddr (), false);
                                            targetMap.put (token.getToAddr(), false);
                                        }
                                    }
                                }*/
                                
                                else{
                                	isMatch = storageService.stroageTransaction(tx,logItem);		//storage transaction
                                	if (!isMatch){
                                		isMatch=spService.stroagePoolTransaction(tx, logItem);
                                	}
                                	if (!isMatch){
                                		isMatch=poSService.poSTransaction(tx, logItem);
                                	}
                                	if(!isMatch && tx.getToAddr()!=null){                                		
                                		String contractAddr = tx.getContract();
                                		if(contractAddr==null)
											contractAddr = tx.getToAddr();
                                		Tokens token = tokenService.getToken(contractAddr);
                                		if(token!=null){
                                			isMatch = tokenService.parseTransaction(token, tx, logItem);    	//Token trasaction                            		
                                		}
                                		if(!isMatch){ 
                                			Contract contract = contractService.getContract(contractAddr);
	                                		if(contract!=null){
	                                			isMatch = contractService.parseTransaction(contract, tx, logItem);		//Contract transaction	                                			
	                                		}
                                		}
                                	}                               	
                                }
                            }                            
                        }                   
                    }
					if (!isMatch && tx.getToAddr() != null && tx.getStatus() == 1 && (tx.getInput() == null || tx.getInput().equals("0x"))
							&& tx.getValue().compareTo(BigDecimal.ZERO) > 0) {     
                    	Contract contract = contractService.getContract(tx.getToAddr());
                    	if(contract!=null)
                    		isMatch = contractService.parseContractLockup(contract, tx)!=null;
                    }
					logger.info("Transction collected :" + tx);
              //    addrTransferService.putTransaction(tx);	
                    AddrTransferService.getInstance().putTransaction(tx);
                    //At iterate bottom to batch save transaction data
                    if(trasList.size()>0&&trasList.size()/Constants.BATCHCOUNT>=1){
                        transactionMapper.batchTransaction(trasList);
                        trasList = new ArrayList<>();
                    }                    
                }
                //After iterate to batch save rest of transaction data
                if(trasList.size()>0){
                    transactionMapper.batchTransaction(trasList);
                }
            }
            
            //Handle addresses balance
            Set<String> addressess = ethAddress.keySet();
            balanceTotal = addressess.size();
            Date insert = new Date(block.getTimestamp().longValue() * 1000);
            handleEthBalance(web3j, addressess, blockNumber,insert);
            Set<String> contracts = erc20Address.keySet();
            for (String contract : contracts) {
                Map<String, Boolean> contractMap = erc20Address.get(contract);
                if (null != contractMap && contractMap.size()>0){
                    addressess = contractMap.keySet();
                    handleTokenBalance(web3j, contract, addressess, blockNumber,insert);
                }
            }
            saveFeeReward(block.getHash(), block.getMiner(), feeTotal,block.getNumber());
            blockReward = blockReward.add(feeTotal);
            blockCount += saveBlockFromEthBlock(block,blockNumber, blockReward, null != resultList ? resultList.size() : 0) ? 1 : 0;
            
            saveCandidateAutoExit(web3j,blockNumber,insert);
            
            long blocked = System.currentTimeMillis();
            logger.info("Block "+blockNumber+" end ,spends "+(blocked-blockst)+ " ms \n");
            
            if(StBandwidthMakeup!=null){
            	if(blockNumber.equals(StBandwidthMakeup)){
            		storageService.startBandwidthMakeup(blockNumber);
            	}
            	if(blockNumber.equals(StBandwidthMakeup + StBandwidthMakeupDays * dayOneNumber)){
            		storageService.finishBandwidthMakeup(blockNumber);
            	}
            }
            if(blockNumber%rewardStatBlock==0){
//            	statisticService.updateRewardStat(blockNumber);	//TODO
			}
            if(blockNumber%dayOneNumber==accumulateRewardBlock) {
    			new NodeRewardCltJob(ipaddress,blockNumber,dayOneNumber,block.getTimestamp(),ljRewardService).start();
    		}
        }
      //execute every minute
	//	if (i == blockTotal - 1) {
		if (blockNumber != null) {
			statisticService.saveGlobalStat(blockNumber);
			addressService.saveOrUpdateAddress(Constants.addressZero, blockNumber);
			new AddressSrtBalanceModifyJob(web3j, blockNumber, accountMapper).start();
			new StoragePledgeStatusJob(web3j, blockNumber, dayOneNumber, storageService,blockTimeMap).start();			
		    new SpStoragePoolDataJob(ipaddress,blockNumber,spService,blockTimeMap).start();
		} 
		
		
       long alled = System.currentTimeMillis();
       logger.info("ExplorerJob collect end, spend " + (alled-allst) + " ms \n");
        return;
    }
   
    private void saveCandidateAutoExit(Web3j web3j,Long blockNumber,Date blockDate){
    	try {
    		AlienSnapshot response = web3j.alienCandidateAutoExitAtNumber(blockNumber.intValue()).send();
    		if (response.hasError() || response.getSnapshot() == null) {
			//	logger.info("alienCandidateAutoExitAtNumber " + blockNumber + " error:" + response.getError().getMessage());
				return;
			}
    		List<String> nodeList = response.getSnapshot().getCandidateautoexit();
    		if(nodeList!=null && nodeList.size()>0){
    			for(String node_address : nodeList){
    				UtgNodeMinerQueryForm queryForm = new UtgNodeMinerQueryForm();
    	            queryForm.setNode_address(node_address);
    				UtgNodeMiner node = nodeExitMapper.getNode(queryForm);			
    				if(node!=null){
    				//	String manage_address = node.getManage_address();
    					node.setNode_type(NodeTypeEnum.exit.getCode());    					
    	                node.setPledge_amount(new BigDecimal(0));
    	                node.setTotal_amount(new BigDecimal(0));
    	                node.setSync_time(new Date());
    	                node.setExit_type(1);
    	                nodeExitMapper.updateNodeMiner(node);
    	                
    	             //   UtgNodeMinerQueryForm queryForm = new UtgNodeMinerQueryForm();
        				queryForm.setNode_address(node_address);
        				queryForm.setPledge_status(1);
        				List<NodePledge> pledgeList = nodeExitMapper.getNodePledgeList(queryForm);
        				for(NodePledge nodePledge : pledgeList){
        					nodePledge.setPledge_status(0); 
        			//		int unpledge_type = nodePledge.getPledge_address().equals(manage_address) ? 1 : 0;
        					nodePledge.setUnpledge_type(1);
        					nodePledge.setUnpledge_number(blockNumber);
        		            nodePledge.setUnpledge_time(blockDate);    		            
        		            nodeExitMapper.saveOrUpdateNodePledge(nodePledge);
        				}
        				logger.info("CandidateAutoExit :node "+node_address+" has exit at block "+blockNumber);
    				}    				
    			}
    		}
    		
		} catch (IOException e) {
			logger.warn("alienCandidateAutoExitAtNumber error", e);
			return;
		}
    }
    
    //Determine whether the transaction type is locked or pledged
    private void validateAddress(String topFilter, Long blockNumber, String addres, Log logItem,String fromHashs,BigInteger gasPrice,BigInteger gasUsed,BigInteger gasLimit,EthBlock.TransactionObject item,Date timeStamp,TransactionReceipt receipt,Date date)throws Exception  {
        BigInteger value= item.getValue();
        long logLength=receipt.getLogs().size();
        if(value==null){
            value=BigInteger.ZERO;
        }
        BigDecimal valSend=new BigDecimal(value.toString());
        if(pledgeFilter.equalsIgnoreCase(topFilter)){
            //0: Block production pledge and lock-up, 1: storage pledge and lock-up, 2: storage reward
            int type = Integer.parseInt(convertToBigDecimal(logItem.getTopics().get(2)).toString());
            if(type == 0){
                saveTransMinerFromLogV2(logItem,3,blockNumber,fromHashs,gasPrice,gasUsed,gasLimit,receipt,date,logLength,valSend) ;//
            }else if(type == 1){
                saveTransMinerFromLogV2(logItem,7,blockNumber,fromHashs,gasPrice,gasUsed,gasLimit,receipt,date,logLength,valSend) ;//
            }
        }
    }
	
	private void updateNodeFractions(Map<String, BigInteger> fractionMap) {
		List<String> nodes = new ArrayList<>();
		nodes.add("0");
		for (Map.Entry<String, BigInteger> entryNow : fractionMap.entrySet()) {
			String node_address = entryNow.getKey();
			nodes.add(node_address);
			UtgNodeMiner newNode = new UtgNodeMiner();
			newNode.setNode_address(node_address);
			newNode.setSync_time(new Date());
			newNode.setFractions(entryNow.getValue().intValue());			
			nodeExitMapper.updateNodeMiner(newNode);
		}
		if (nodes.size() > 0) {
			nodeExitMapper.updateNodeBatch(nodes);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateNodePledge(Map<String, Object> pledgeMap){		
		if(pledgeMap!=null){
			for (Map.Entry<String,Object> entry : pledgeMap.entrySet()) {
				String node_address = (String) entry.getKey();
				Map<String, Object> pledgeData = (Map<String, Object>) entry.getValue();				
				BigDecimal total_amount = new BigDecimal(pledgeData.get("totalamount").toString());
				Long punish_block = Long.parseLong(pledgeData.get("lastpunish").toString());
				Integer rate = Integer.parseInt(pledgeData.get("distributerate").toString());
				UtgNodeMiner newNode = new UtgNodeMiner();
				newNode.setNode_address(node_address);
				newNode.setSync_time(new Date());
				newNode.setTotal_amount(total_amount);
				newNode.setPunish_block(punish_block);
				newNode.setRate(rate);				
				nodeExitMapper.updateNodeMiner(newNode);
				logger.info("Update node pledge data: node_address="+node_address+",total_amount="+total_amount+",punish_block="+punish_block+",rate="+rate);
			}
		}	
	}
    //Get the score information of the penalty node
    private void savePunishedInfo(String pledge,Long blockNumber,Date timeStamp ,Map<String, BigInteger> thisBlock, Map<String, BigInteger> latestBlock,String minerHash){
        int num =blockNumber.intValue();
        Integer round=getRound(num); //Current round of punishment
     //   logger.info("Start comparing data");
        minerHash=minerHash.toLowerCase();
        Long feeMiss=30L;//Absent block deducts 30
        for(Map.Entry<String,BigInteger> entryNow:thisBlock.entrySet()){  //Traverse the map of the current block height
            /*if(latestBlock.size()==0){
                Punished  punished = new Punished();
                punished.setAddress(entryNow.getKey());
                punished.setBlockNumber(blockNumber);
                punished.setRound(getRound(num));
                punished.setFractions(feeMiss);
                punished.setType(VideoStatusEnum.SUCCESS.value);//1 Absent block
                punished.setTimeStamp(timeStamp);
                BigInteger pledgeAmoun = new BigInteger(pledge).divide(new BigInteger(Constants.TOTALMBPOINT+"")).multiply(new BigInteger(feeMiss.toString()));
                punished.setPledgeAmount(new BigDecimal(pledgeAmoun));
                punishedMapper.insert(punished);
            }else{
                for(Map.Entry<String,BigInteger>entryLatest:latestBlock.entrySet()){
                    Punished  punished = new Punished();
                    String keyNow=entryNow.getKey();
                    String keyLatest=entryLatest.getKey();
                    BigInteger valueNow =entryNow.getValue();
                    BigInteger valueLatest =entryLatest.getValue();
                    if(keyNow.equalsIgnoreCase(keyLatest)){  //Analyze the scores of the same address in adjacent blocks
                        BigInteger subFee=valueNow.subtract(valueLatest);
                        if(subFee.longValue()>0){  //Absent block
                            punished.setAddress(keyNow);
                            punished.setBlockNumber(blockNumber);
                            punished.setRound(getRound(num));
                            punished.setFractions(feeMiss);
                            punished.setType(VideoStatusEnum.SUCCESS.value);//1 Absent block
                            punished.setTimeStamp(timeStamp);
                            BigInteger pledgeAmoun = new BigInteger(pledge).divide(new BigInteger(Constants.TOTALMBPOINT+"")).multiply(new BigInteger(feeMiss.toString()));
                            punished.setPledgeAmount(new BigDecimal(pledgeAmoun));
                            punishedMapper.insert(punished);
                        }
                    }
                }
            }
            */            
            String address = entryNow.getKey();
            if(latestBlock.containsKey(address)){
            	BigInteger valueNow = entryNow.getValue();
            	BigInteger valueLatest = latestBlock.get(address);
            	if(valueNow.compareTo(valueLatest)<=0)
            		continue;
            }
            Punished  punished = new Punished();
            punished.setAddress(address);
            punished.setBlockNumber(blockNumber);
            punished.setRound(getRound(num));
            punished.setFractions(feeMiss);
            punished.setType(1);//1 Absent block
            punished.setTimeStamp(timeStamp);
            BigInteger pledgeAmoun = new BigInteger(pledge).divide(new BigInteger(Constants.TOTALMBPOINT+"")).multiply(new BigInteger(feeMiss.toString()));
            punished.setPledgeAmount(new BigDecimal(pledgeAmoun));
            punishedMapper.insert(punished);
        }
    }



    private Date getDateByNumber(Web3j web3j,Long blockNumber)throws Exception{
        BigInteger blockNumberParam= BigInteger.valueOf(blockNumber);
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumberParam), true).send();
        Date date = new Date(ethBlock.getBlock().getTimestamp().longValue()*1000);
        return date;
    }

    private void saveTransMinerFromLogV2(Log logItem,Integer type,Long blockNumber,String fromHash,BigInteger gasPrice,BigInteger gasUsed,BigInteger gasLimit,TransactionReceipt receipt,Date date,Long logLength,BigDecimal transactionValue) throws Exception {
        TransferMiner transferMiner= new TransferMiner();
        List<String> topics=logItem.getTopics();
        String address="";
        if(topics.size()>=1){
            address=Constants.PRE+topics.get(1).substring(26);
        }
        switch (type){
            case 3:
               // BigDecimal values=convertToBigDecimal ("0x"+logItem.getData().substring(2,66));
                UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
                queryForm.setNode_address(address);
                UtgNodeMiner preNode = nodeExitMapper.getNode(queryForm);
                if(preNode==null)
                	return;
                transferMiner.setTxHash(logItem.getTransactionHash());
                transferMiner.setLogIndex(logItem.getLogIndex().intValue());
                transferMiner.setAddress(address);
                transferMiner.setBlockHash(logItem.getBlockHash());
                transferMiner.setBlockNumber(logItem.getBlockNumber().longValue()-1);
                transferMiner.setTotalAmount(preNode.getPledge_amount());
                transferMiner.setRevenueaddress(StringUtils.isEmpty(preNode.getRevenue_address())?preNode.getNode_address():preNode.getRevenue_address());
                transferMiner.setType(3);//Node lock
                transferMiner.setCurtime(new Date());
                transferMiner.setValue(new BigDecimal(0));
                transferMiner.setStartTime(date);
                transferMiner.setGasLimit(gasLimit.longValue());
                transferMiner.setGasPrice(gasPrice.longValue());
                transferMiner.setGasUsed(gasUsed.longValue());
                transferMiner.setLogLength(logLength);
                transferMiner.setPresentAmount(null);
                List<Transaction> listNode = transactionMapper.getTransactionByTxType(SscOperatorEnum.CndLock.getCode());
                if(listNode.size()>0){
                    Transaction t = listNode.get(0);
                    transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+Long.parseLong(t.getParam1()));
                    transferMiner.setReleaseHeigth(Long.parseLong(t.getParam2()));
                    transferMiner.setReleaseInterval(t.getParam3().longValue());
                    transferMiner.setLockNumHeight(Long.parseLong(t.getParam1()));
                }else{
//                    transferMiner.setUnLockNumber(logItem.getBlockNumber().longValue());
//                    transferMiner.setReleaseHeigth(0L);
//                    transferMiner.setReleaseInterval(0L);
//                    transferMiner.setLockNumHeight(0L);
                    throw  new Exception("no node lock config");
                }
                transferMiner.setNodeNumber(null);
                transferMiner.setReleaseamount(BigDecimal.ZERO);

                //Node exit
                UtgNodeMiner node = new UtgNodeMiner();
                node.setNode_address(address);
                node.setNode_type(NodeTypeEnum.exit.getCode());
                node.setPledge_amount(new BigDecimal(0));
                node.setTotal_amount(new BigDecimal(0));
                node.setSync_time(new Date());
                node.setExit_type(0);
                nodeExitMapper.updateNodeMiner(node);
                
                UtgNodeMinerQueryForm pledgeQueryForm = new  UtgNodeMinerQueryForm();
                pledgeQueryForm.setNode_address(address);
                pledgeQueryForm.setPledge_status(1);
                List<NodePledge> pledgeList = nodeExitMapper.getNodePledgeList(pledgeQueryForm);
                for (NodePledge nodePledge : pledgeList) {
                	nodePledge.setPledge_status(0);
                	nodePledge.setUnpledge_type(1);
                	nodePledge.setUnpledge_hash(receipt.getTransactionHash());
                	nodePledge.setUnpledge_number(blockNumber);
                	nodePledge.setUnpledge_time(date);
                	nodeExitMapper.saveOrUpdateNodePledge(nodePledge);
                }
                break;

            case  7:
                UtgStorageMiner pre_miner = utgStorageMinerMapper.getSingleMiner(address);
                if(pre_miner==null)			//TODO
                	return;
                transferMiner.setTxHash(logItem.getTransactionHash());
                transferMiner.setLogIndex(logItem.getLogIndex().intValue());
                transferMiner.setAddress(address);
                transferMiner.setBlockHash(logItem.getBlockHash());
                transferMiner.setBlockNumber(logItem.getBlockNumber().longValue()-1);
                transferMiner.setTotalAmount(pre_miner.getPledge_amount());
                transferMiner.setRevenueaddress(StringUtils.isEmpty(pre_miner.getRevenue_address())?pre_miner.getMiner_addr():pre_miner.getRevenue_address());
                transferMiner.setType(7);
                transferMiner.setValue(new BigDecimal(0));
                transferMiner.setStartTime(date);
                transferMiner.setGasLimit(gasLimit.longValue());
                transferMiner.setGasPrice(gasPrice.longValue());
                transferMiner.setGasUsed(gasUsed.longValue());
                transferMiner.setLogLength(logLength);
                transferMiner.setPresentAmount(null);
                List<Transaction> listMiner = transactionMapper.getTransactionByTxType(SscOperatorEnum.FlwLock.getCode());
                if(listMiner.size()>0){
                    Transaction t = listMiner.get(0);
                    transferMiner.setUnLockNumber(transferMiner.getBlockNumber()+Long.parseLong(t.getParam1()));
                    transferMiner.setReleaseHeigth(Long.parseLong(t.getParam2()));
                    transferMiner.setReleaseInterval(t.getParam3().longValue());
                    transferMiner.setLockNumHeight(Long.parseLong(t.getParam1()));
                }else{
//                    transferMiner.setUnLockNumber(logItem.getBlockNumber().longValue());
//                    transferMiner.setReleaseHeigth(0L);
//                    transferMiner.setReleaseInterval(0L);
//                    transferMiner.setLockNumHeight(0L);
                      throw  new Exception("no miner lock config");
                }
                transferMiner.setNodeNumber(null);
                transferMiner.setReleaseamount(BigDecimal.ZERO);

                //storage mining miners exit
                UtgStorageMiner miner = new UtgStorageMiner();
                miner.setMiner_addr(address);
                miner.setSync_time(new Date());
                miner.setMiner_status(MinerStatusEnum.exit.getCode());
                miner.setPledge_amount(new BigDecimal(0));                
                miner.setBandwidth(new BigDecimal(0));
                miner.setBlocknumber(blockNumber);
                utgStorageMinerMapper.updateStorageMiner(miner);
                break;

            default :
                return ;
        }
        transferMinerMapper.insertOrUpdateMiner(transferMiner);
        saveOrUpdateAddresses(transferMiner);
        return ;
    }

    /**
     * If it is a storage lock, insert the lock address into the address table
     * @param transferMiner
     */
    private void saveOrUpdateAddresses(TransferMiner transferMiner) {
        Integer type=transferMiner.getType();
        String address=transferMiner.getAddress();
        if(type==5){
            Addresses addrParam=new Addresses();
            addrParam.setAddress(transferMiner.getAddress());
            Addresses addrs=accountMapper.selectOne(addrParam);
            if(addrs==null){
                addrs=new Addresses();
                addrs.setAddress(address);
                addrs.setContract(address);
                addrs.setBlockNumber(transferMiner.getBlockNumber());
                addrs.setBalance(new BigDecimal("0"));
                addrs.setNonce(0l);
                addrs.setHaslock(1);
                addrs.setInsertedTime(new Date());
                accountMapper.saveOrUpdataHaslock(addrs);
            }else{
                if(null==addrs.getHaslock()||0==addrs.getHaslock()){
                    accountMapper.updateHaslock(address);
                }
            }
        }
    }

    /**
     * transaction data
     * @param data    input
     * @param transaction
     */
    private void txData(String data, Transaction transaction,List<Log> logs) {    	
        //ufo
        TransactionVo transactionVo = MapCache.getValue(data) ;
        if (transactionVo != null) {
            transaction.setUfoprefix(transactionVo.getUfoprefix());
            transaction.setUfoversion(transactionVo.getUfoversion());
            transaction.setUfooperator(transactionVo.getUfooperator());
            TxDataParse.setTxData(address1,address2,address3,transaction,transactionMapper,accountMapper,utgStorageMinerMapper,nodeExitMapper,storageSpaceMapper,globalConfigMapper,logs);
            if("Exch".equals(transactionVo.getUfooperator())){
            	addressService.updateAddressSrtBalance(transaction.getParam1(), transaction.getBlockNumber());
            }
        }else if(data!=null){
//        	data = new String(Numeric.hexStringToByteArray(data));
//        	String[] datas = data.split(":");
        //	logger.warn("input:"+data);
//    		if(datas.length<3 ||(!"UTG".equals(datas[0]) && !"SSC".equals(datas[0])))
//    			return;
    	//	logger.warn("datas[0]:"+datas[0]+",datas[1]:"+datas[1]+",datas[2]:"+datas[2]);
//    		if(datas[0]!=null && datas[0].length()<10)
//    			transaction.setUfoprefix(datas[0]);
//    		if(datas[1]!=null && datas[1].length()<10)
//    			transaction.setUfoversion(datas[1]);
//    		if(datas[2]!=null && datas[2].length()<10)
//    			transaction.setUfooperator(datas[2]);
        }
    }


    private Transaction saveTransaction(List<Transaction> trasList,Date timestamp, EthBlock.TransactionObject item, TransactionReceipt receipt,List<Log> logs) {
        Transaction transaction = new Transaction();
        /*if (null == transaction) {
            logger.info("" + item.toString());
            return false;
        }*/
        String status = receipt.getStatus();
        if (status.equals("0x1")) { //success
            status = "1";
        } else {
            status = "0";
        }
        transaction.setHash(item.getHash());
        transaction.setIsTrunk(1);
        transaction.setTimeStamp(timestamp);
        transaction.setFromAddr(item.getFrom());
        transaction.setToAddr(item.getTo());
        transaction.setValue(new BigDecimal(item.getValue()));
        transaction.setNonce(item.getNonce().longValue());
        transaction.setGasLimit(item.getGas().longValue());
        transaction.setGasPrice(item.getGasPrice().longValue());
        transaction.setStatus(Integer.valueOf(status));
        transaction.setCumulative(receipt.getCumulativeGasUsed().longValue());
        transaction.setBlockHash(item.getBlockHash());
        transaction.setBlockNumber(item.getBlockNumber().longValue());
        transaction.setBlockIndex(item.getTransactionIndex().intValue());
        transaction.setInternal(0);
        transaction.setInput(item.getInput());
        transaction.setContract(receipt.getContractAddress());
        transaction.setGasUsed(receipt.getGasUsed().longValue());
        transaction.setType(getType(item.getInput()));

        txData(item.getInput(), transaction,logs);
        trasList.add(transaction);
//        transactionMapper.insertorUpdate(transaction);
    //    return true;
        return transaction;
    }

    /*Get the block height on the chain*/
    private Long getCurrentBlockNumber(Web3j web3j) {
        EthBlockNumber ethBlockNumber;
        try {
            ethBlockNumber = web3j.ethBlockNumber().send();
        } catch (IOException e) {
        	throw new RuntimeException(e);            
        }        
        if (ethBlockNumber.hasError())
        	throw new RuntimeException(ethBlockNumber.getError().getMessage());
        BigInteger number = ethBlockNumber.getBlockNumber();        
        return number.longValue();
    }

    /*Save block reward   */
    private BigInteger saveBlockReward(String blockHash, Long blockNumber, String minerAddress,Date dates) {
        BigDecimal rewardNumber = new BigDecimal("0.0");
        BlockRewards reward = new BlockRewards();
        /*if(blockNumber<=Long.valueOf(blocktotal)){
            rewardNumber=new BigDecimal("3.40740710650912607813");
        }else{
            rewardNumber= getMinerNumber(blockNumber);
        }*/
        rewardNumber= getMinerNumber(blockNumber);
        BigDecimal fee= new BigDecimal(decimals);
        rewardNumber=rewardNumber.multiply(fee);
        if (null != reward) {
            reward.setBlockHash(blockHash);
            reward.setAddress(minerAddress);
            reward.setReward(rewardNumber);
            reward.setRewardType("BlockReward");
            reward.setRewardHash(blockHash);
            reward.setBlockNumber(blockNumber);
            reward.setIsTrunk(1);
            reward.setTimeStamp(dates);
            blockRewardsMapper.insertOrUpdate(reward);
        }
        long blockReward=rewardNumber.longValue();
        return BigInteger.valueOf(blockReward);
    }

    /*Save block fork information*/
    private int saveBlockFork(List<String> uncles, String nephewHash, Long nephewNumber) {
        int forkCount = 0;
        for (String uncleHash : uncles) {
            if (StringUtils.isEmpty(uncleHash))
                continue;
            BlockFork item = new BlockFork();
            if (null == item) {
                continue;
            }
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

    /*Save uncle block reward*/
    private BigInteger saveUnclesReward(String blockHash, Long blockNumber, int unclesNumber, String minerAddress) {
        BigDecimal rewardNumber = new BigDecimal("0.0");
        BlockRewards rewards = new BlockRewards();
//        if(blockNumber<=Long.valueOf(blocktotal)){
//            rewardNumber=new BigDecimal("3.40740710650912607813");
//        }else{
//            rewardNumber= getMinerNumber(blockNumber);
//        }
        rewardNumber= getMinerNumber(blockNumber);
        BigDecimal fee= new BigDecimal("1000000000000000000");
        rewardNumber=rewardNumber.multiply(fee);
        BlockRewards reward = new BlockRewards();
        if (null != reward) {
            reward.setBlockHash(blockHash);
            reward.setAddress(minerAddress);
            reward.setReward(rewardNumber);
            reward.setRewardType("UncleReward");
            reward.setRewardHash(blockHash);
            reward.setBlockNumber(blockNumber);
            reward.setIsTrunk(1);
           blockRewardsMapper.insertOrUpdate(reward);
        }
        long blockReward=rewardNumber.longValue();
        return BigInteger.valueOf(blockReward);
    }

    private TransactionReceipt getTransactionReceipt(Web3j web3j, String transHash) {
        EthGetTransactionReceipt ethReceipt;
        try {
            ethReceipt = web3j.ethGetTransactionReceipt(transHash).send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (null == ethReceipt) {
            return null;
        }
        TransactionReceipt receipt = ethReceipt.getResult();
        if (null == receipt) {
            return null;
        }
        return receipt;
    }



    private void handleEthBalance(Web3j web3j, Set<String> addresses, Long blockNumber,Date insert) {
        for (String address : addresses) {
           /* long balancestart = System.currentTimeMillis();
            BigInteger balance = getAddressBalance(web3j, address, blockNumber);
            if (null == balance)
                continue;
            BigInteger nonce = getAddressNonce(web3j, address, blockNumber);
            if (null == nonce)
                continue;
            long balancesend = System.currentTimeMillis();
            logger.info(address+ " end ,balancegetspends total second ="+(balancesend-balancestart));
            saveAddressBalance(address, null, blockNumber, balance, nonce,insert);
            saveWithDraw(address, null, blockNumber, balance, nonce);
            long balancesupdate = System.currentTimeMillis();
            logger.info(address+ " end ,balanceupdatespends total second ="+(balancesupdate-balancesend));*/
            AddressBalanceModifyJob.getInstance().putq(new Addresses( address,blockNumber));
        }
    }

    /*Save storage records*/
    private boolean saveWithDraw(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce) {
        AddrBalances item = new AddrBalances();
        if (null == item) {
            return false;
        }
        item.setAddress(address);
        if (null == contract) {
            contract = address;
        }
        item.setContract(contract);
        item.setBlockNumber(blockNumber);
        item.setBalance(new BigDecimal(balance));
        item.setNonce(nonce.longValue());
        addrBalancesMapper.saveOrUpdate(item);
        return true;
    }

    private int handleTokenBalance(Web3j web3j, String contract, Set<String> addresses, Long blockNumber,Date insert) {
        int count = 0;
        for (String address : addresses) {
            BigInteger balance = getAddressTokenBalance(web3j, contract, address, blockNumber);
            if (null == balance)
                continue;
            BigInteger nonce = getAddressNonce(web3j, address, blockNumber);
            if (null == nonce)
                continue;
            count+=saveAddressTokenBalance(address, contract, blockNumber, balance, nonce,insert) ? 1 : 0;
        }
        return count;
    }

    private void saveFeeReward(String blockHash, String minerAddress, BigInteger fee,BigInteger number) {
        BlockRewards reward = new BlockRewards();
        if (null == reward) {
            return;
        }
        reward.setBlockHash(blockHash);
        reward.setAddress(minerAddress);
        reward.setReward(new BigDecimal(fee));
        reward.setRewardType("FeeReward");
        reward.setRewardHash(blockHash);
        reward.setBlockNumber(number.longValue());
        reward.setIsTrunk(1);
        blockRewardsMapper.insertOrUpdate(reward);
    }


    /*Get address balance*/
    private BigInteger getAddressBalance(Web3j web3j, String address, Long blockNumber) {
        EthGetBalance ethResult;
        try {
            ethResult = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        if (null == ethResult) {
            return null;
        }
        return ethResult.getBalance();
    }


    /*Get address nonce*/
    private BigInteger getAddressNonce(Web3j web3j, String address, Long blockNumber) {
        EthGetTransactionCount ethResult;
        try {
            ethResult = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ethResult.getTransactionCount();
    }

    /*Save address balance*/
    private boolean saveAddressBalance(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce,Date insert) {
        Addresses item = new Addresses();
        if (null == item) {
            return false;
        }
        item.setAddress(address);
        if (null == contract) {
            contract = address;
        }
        item.setContract(contract);
        item.setBlockNumber(blockNumber);
        item.setBalance(new BigDecimal(balance));
        item.setNonce(nonce.longValue());
        item.setInsertedTime(insert);
        accountMapper.saveOrUpdata(item);
        return true;
    }

    private boolean saveAddressTokenBalance(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce,Date insert) {
        Addresses item = new Addresses();
        if (null == item) {
            return false;
        }
        item.setAddress(address);
        if (null == contract) {
            contract = address;
        }
        item.setContract(contract);
        item.setBlockNumber(blockNumber);
        item.setBalance(new BigDecimal(balance));
        item.setNonce(nonce.longValue());
        item.setInsertedTime(insert);
        accountMapper.saveOrUpdataToken(item);
        return true;
    }


    /*Get address Erc20 token balance
     * */
    private BigInteger getAddressTokenBalance(Web3j web3j, String contract, String address, Long blockNumber) {
        EthCall ethResult;
        BigInteger blockNumberPara = BigInteger.valueOf(blockNumber);
        Function function = new Function(ERC20.FUNC_BALANCEOF, Arrays.asList(new Address(address)), Arrays.asList(new TypeReference<Address>() {
        }));
        String encode = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.request.Transaction ethCallTransaction = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address, contract, encode);
        try {
           // ethResult = web3j.ethCall(ethCallTransaction, DefaultBlockParameter.valueOf(blockNumberPara)).send();
            ethResult=web3j.ethCall(ethCallTransaction,DefaultBlockParameterName.LATEST).send();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        if (null == ethResult) {
            return null;
        }
        if(ethResult.getValue()==null){
            return Numeric.decodeQuantity("0");
        }else {
            return Numeric.decodeQuantity(ethResult.getValue());
        }
    }


    /*Save block information    */
    private boolean saveBlockFromEthBlock(EthBlock.Block block,Long blockNumber, BigInteger reward, int txCount) {
        Integer round=getRound(blockNumber.intValue());
        BigDecimal fee= new BigDecimal("1000000000000000000");
        Blocks item = new Blocks();
        BigDecimal rewardNumber = new BigDecimal("0.0");
        BigInteger number =block.getNumber();
        /*if(number.longValue()<=Long.valueOf(blocktotal)){
            rewardNumber=new BigDecimal("3.40740710650912607813");
        }else{
            rewardNumber =getMinerNumber(blockNumber);
        }*/
        rewardNumber =getMinerNumber(blockNumber);
        rewardNumber=rewardNumber.multiply(fee);
        if (null == item) {
            return false;
        }
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
        return true;
    }

    public  BigDecimal getMinerNumber(Long nowNumber){
        try {
            double x = 210000d/420000;
            BigDecimal total = new BigDecimal(x* Math.pow(1-0.04,nowNumber/420000)) ;
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal("0");
        }
    }



    //Get the round of block height
    private  Integer getRound(int blockNumber){
        int rouds=0;
        if(blockNumber<=round){
            rouds=1;
            return rouds;
        }else{
            float cell=blockNumber/round;
            double nYear=(double)blockNumber/round;
            double nFee= Math.ceil(nYear);
            int round = new Double(nFee).intValue();
            return round;
        }
    }

    //Get node exit data
    private void saveNodeExit(String rs){
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
    }

    //Save node data
    private void saveDposNodeInfo(Long blockNumber, long nodeblock, List<String> signers) throws Exception{
        Integer type;
        BigInteger va;
        Set<String> witNodes = new HashSet<>();
        if(blockNumber%nodeblock==0){
            if( signers!=null&&signers.size()>0){
                for(int k=0;k<signers.size();k++){
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
            List<Transaction> deposit = transactionMapper.getTransactionByTxType(SscOperatorEnum.Deposit.getCode());
            String pledge = null;
            if(deposit.size()>0){
                pledge = deposit.get(0).getParam1();
            }else{
                throw  new Exception("no Deposit config set");
            }
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
                    accountMapper.setAsMiner(address,blockNumber);
                }
            }
        }

    }

    /*Get ERC20 parameters*/
    private String getERC20StringParameter(Web3j web3j, String contract, int property) {
        ERC20 metadata = ERC20.load(contract, web3j, SampleKeys.CREDENTIALS, null);
        if (null == metadata) {
            return null;
        }
        try {
            switch (property) {
                case 0:
                    return metadata.name().send();
                case 1:
                    return metadata.symbol().send();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*Get erc20 parameters  new DefaultGasProvider()*/
    private BigInteger getERC20IntegerParameter(Web3j web3j, String contract, int property) {
        ERC20 metadata = ERC20.load(contract, web3j, SampleKeys.CREDENTIALS, null);
        if (null == metadata) {
            return null;
        }
        try {
            switch (property) {
                case 2:
                    return metadata.decimals().send();

                case 3:
                    return metadata.totalSupply().send();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /*Get ERC20 tokens*/
    private boolean handleERC20Token(Web3j web3j, String contract) {
        Tokens token =tokenMapper.getTokens(contract);        
        if(null !=token)
            return false;        
        String name = getERC20StringParameter(web3j, contract, 0);        
        if (null == name)
            return false;
        String symbal = getERC20StringParameter(web3j, contract, 1);        
        if (null == symbal)
            return false;
        BigInteger decimal = getERC20IntegerParameter(web3j, contract, 2);        
        if (null == decimal)
            return false;
        BigInteger totalSupply = getERC20IntegerParameter(web3j, contract, 3);        
        if (null == totalSupply)
            return false;
        return saveERC20(contract, name, symbal, decimal.byteValue(), totalSupply,null,null);
    }

    private boolean saveERC20(String contract, String name, String symbal, int decimal, BigInteger totalSupply ,String description,String contractManager) {
        Tokens item = new Tokens();
        item.setContract(contract);
        item.setType(0);
        item.setName(name);
        item.setSymbol(symbal);
        item.setDecimals(decimal);
        item.setTotalSupply(new BigDecimal(totalSupply));
        item.setCataloged(0);
        item.setDescription(description);
        item.setContractManager(contractManager);
        tokenMapper.saveOrUpdate(item);
        return true;
    }

	private TransToken saveTransTokenFromLog(Log logItem,BigInteger gasPrice,BigInteger gasUsed,BigInteger nonce ,BigInteger gasLimit,Date date) {
        TransToken item = new TransToken();
        List<String> topics = logItem.getTopics();
        switch (topics.size()) {
            case 3:
                item.setCoinType(0);
                item.setFromAddr(truncateAddress(topics.get(1)));
                item.setToAddr(truncateAddress(topics.get(2)));
                item.setAmount(convertToBigDecimal(logItem.getData()));
                item.setGasPrice(gasPrice.longValue());
                item.setGasUsed(gasUsed.longValue());
                item.setNonce(nonce.longValue());
                item.setGasLimit(gasLimit.longValue());
                item.setContract(logItem.getAddress());
                item.setTimeStamp(date);
                item.setContract(logItem.getAddress());
                break;
            default:
                return null;
        }
        item.setTransHash(logItem.getTransactionHash());
        item.setLoginIndex(logItem.getLogIndex().intValue());
        item.setBlockHash(logItem.getBlockHash());
        item.setBlockNumber(logItem.getBlockNumber().longValue());
        transferTokenMapper.insertOrUptate(item);
        logger.debug("save token transaction: " + logItem.getTransactionHash() + ", " + logItem.getLogIndex());
        return item;
    }

    private boolean saveTransactionErc20(Date timestamp, EthBlock.TransactionObject item, TransactionReceipt receipt,Log logItem) {
        Transaction transaction = new Transaction();
        List<String> topics = logItem.getTopics();
        String status = receipt.getStatus();
        if (status.equals("0x1")) {
            status = "1";
        } else {
            status = "0";
        }
        transaction.setHash(item.getHash());
        transaction.setIsTrunk(1);
        transaction.setTimeStamp(timestamp);
        transaction.setFromAddr(truncateAddress(topics.get(1)));
        transaction.setToAddr(truncateAddress(topics.get(2)));
        transaction.setValue(convertToBigDecimal(logItem.getData()));
        transaction.setNonce(item.getNonce().longValue());
        transaction.setGasLimit(item.getGas().longValue());
        transaction.setGasPrice(item.getGasPrice().longValue());
        transaction.setStatus(Integer.valueOf(status));
        transaction.setCumulative(receipt.getCumulativeGasUsed().longValue());
        transaction.setBlockHash(item.getBlockHash());
        transaction.setBlockNumber(item.getBlockNumber().longValue());
        transaction.setBlockIndex(item.getTransactionIndex().intValue());
        transaction.setInternal(0);
        transaction.setInput(item.getInput());
        transaction.setContract(receipt.getContractAddress());
        transaction.setGasUsed(receipt.getGasUsed().longValue());
        transaction.setType(1);

        transactionMapper.insertorUpdate(transaction);
        return true;
    }


    private Long convertToLong(String value) {
        if (null != value)
            return Numeric.decodeQuantity(value).longValue();
        return null;
    }

    private String truncateAddress(String address) {
        return Constants.PRE + address.substring(address.length() - 40);
    }


    private BigDecimal convertToBigDecimal(String value) {
        if (null != value){
            BigDecimal decimal = new BigDecimal(Numeric.decodeQuantity(value));
            if(decimal.compareTo(new BigDecimal("1E64"))>=0){
            	logger.warn("Data "+value+" out of decimal range :"+decimal);
            	return BigDecimal.ZERO;
            }else{
            	return decimal;
            }            	
        }
        return null;
    }


    protected static String hexStr2Str(String s) {
        if(s==null || s.isEmpty()){
            return null;
        }
        s=s.replaceAll("0+$", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    private Integer getType(String inputParam) {
        return 0;
    }
}
