package com.imooc.job.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.imooc.enums.MinerStatusEnum;
import com.imooc.enums.NodeTypeEnum;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.GlobalConfigMapper;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.StorageSpaceMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.pojo.GlobalConfig;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.utils.Constants;
import com.imooc.utils.NumericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionService {
	
	public enum TranasctionType{
		
		ExchRate("ExchRate", "1", "SSC", "SSC"),
		Deposit("Deposit", "1", "SSC", "SSC"),
		CndLock("CndLock", "1", "SSC", "SSC"),
		RwdLock("RwdLock", "1", "SSC", "SSC"), 
		FlwLock("FlwLock", "1", "SSC", "SSC"),
		WdthPnsh("WdthPnsh", "1", "SSC", "SSC"),
		PoSwtfd("PoSwtfd", "1", "SSC", "SSC"),
		Exch("Exch", "1", "UTG", "UTG"),
		Bind("Bind", "1", "UTG", "UTG"),
		Unbind("Unbind", "1", "UTG", "UTG"),
		Rebind("Rebind", "1", "UTG", "UTG"), 
		CandReq("CandReq", "1", "UTG", "UTG"),
		CandExit("CandExit", "1", "UTG", "UTG"),
		CandPnsh("CandPnsh", "1", "UTG", "UTG"), 
		FlwReq("FlwReq", "1", "UTG", "UTG"),
		FlwExit("FlwExit", "1", "UTG", "UTG"),
	   
		stReq("stReq", "1", "UTG", "ST"),
		stExit("stExit", "1", "UTG", "ST"),
		stValid("stValid", "1", "UTG", "ST"),
		chPrice("chPrice", "1", "UTG", "ST"),
		chbw("chbw", "1", "UTG", "ST"),	
		stRent("stRent", "1", "UTG", "ST"),	
		stRentPg("stRentPg", "1", "UTG", "ST"),		
		stReNew("stReNew", "1", "UTG", "ST"),
		stReNewPg("stReNewPg", "1", "UTG", "ST"),
		stRescind("stRescind", "1", "UTG", "ST"),
		stReValid("stReValid", "1", "UTG", "ST"),
		stProof("stProof", "1", "UTG", "ST"),
		stCatchUp("stCatchUp", "1", "UTG", "ST"),
		creFile("creFile", "1", "UTG", "ST"),
		delFile("delFile", "1", "UTG", "ST"),
		stset("stset", "1", "UTG", "ST"),
		editmgaddr("editmgaddr", "1", "UTG", "ST"),
		stchpg("stchpg", "1", "UTG", "ST"),
		stwtreward("stwtreward", "1", "UTG", "ST"),
		setsp("setsp", "1", "UTG", "ST"),
		exitsp("exitsp", "1", "UTG", "ST"),
		streplace("streplace", "1", "UTG", "ST"),
		stwtpg("stwtpg", "1", "UTG", "ST"),
		wtfd("wtfd", "1", "UTG", "ST"),
		wtpgexit("wtpgexit", "1", "UTG", "ST"),
		
		
		addsp("addsp","1","UTG","SP"),
		spchpg("spchpg","1","UTG","SP"),
		spremovesn("spremovesn","1","UTG","SP"),
		spwtpg("spwtpg","1","UTG","SP"),
		spwtfd("spwtfd","1","UTG","SP"),
		spwtexit("spwtexit","1","UTG","SP"),
		spexit("spexit","1","UTG","SP"),
		spfee("spfee","1","UTG","SP"),
		spetrate("spetrate","1","UTG","SP"),
		sprvebind("sprvebind","1","UTG","SP")
		;	
		TranasctionType(String operator,String version,String prefix,String category){
			this.operator = operator;
			this.version = version;
			this.prefix = prefix;
			this.category = category;
		}
		
		private String operator;
		private String version;
		private String prefix;
		private String category;
		
		public static TranasctionType parseOperator(String operator){
			for(TranasctionType txTypeEnum: TranasctionType.values()){
				if(txTypeEnum.getOperator().equals(operator))
					return txTypeEnum;
			}
			return null;
		}
		
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getPrefix() {
			return prefix;
		}
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}		
	}
	
	private static final String zeroA = "0000000000000000000000000000000000000000";	
	private static final String candReqTopic 	= Constants.PRE+"61edf63329be99ab5b931ab93890ea08164175f1bce7446645ba4c1c7bdae3a8"; 	//Node pledge
	private static final String flwReqTopic 	= Constants.PRE+"041e56787332f2495a47171278fa0f1ddb21961f702d0ba53c2bb2c079ccd418";		//Data mining pledge, claimed bandwidth
	private static final String pledgeExitTopic	= Constants.PRE+"9489b96ebcb056332b79de467a2645c56a999089b730c99fead37b20420d58e7"; 	//Node exit, traffic mining exit
	private static final String bindTopic 		= Constants.PRE+"f061654231b0035280bd8dd06084a38aa871445d0b7311be8cc2605c5672a6e3"; 	//Binding, unbinding, replacement
//  private static final String exchTopic 		= Constants.PRE+"dd6398517e51250c7ea4c550bdbec4246ce3cd80eac986e8ebbbb0eda27dcf4c"; 		
	private static final String exchTopic 		= Constants.PRE+"1ebef91bab080007829976060bb3c203fd4d5b8395c552e10f5134e188428147";		//ful exchange
	private static final String candPnshTopic 	= Constants.PRE+"d67fe14bb06aa8656e0e7c3230831d68e8ce49bb4a4f71448f98a998d2674621"; 	//Penalty make-up
		
	@Value("${address1}")
    private String address1;
    @Value("${address2}")
    private String address2;
    @Value("${address3}")
    private String address3;
    
    @Autowired
    TransactionMapper transactionMapper;
    @Autowired
    UtgStorageMinerMapper utgStorageMinerMapper;
    @Autowired
    NodeExitMapper nodeExitMapper;
    @Autowired
    AccountMapper addressMapper;
    @Autowired
    StorageSpaceMapper storageSpaceMapper;
    @Autowired
    GlobalConfigMapper globalConfigMapper;
    

	public Transaction parseTransaction(TransactionObject item, TransactionReceipt receipt, Date blockDate) {
		Transaction transaction = new Transaction();
		int status = "0x1".equals(receipt.getStatus()) ? 1 : 0;
		int type = 0;
		transaction.setHash(item.getHash());
		transaction.setIsTrunk(1);
		transaction.setTimeStamp(blockDate);
		transaction.setFromAddr(item.getFrom());
		transaction.setToAddr(item.getTo());
		transaction.setValue(new BigDecimal(item.getValue()));
		transaction.setNonce(item.getNonce().longValue());
		transaction.setGasLimit(item.getGas().longValue());
		transaction.setGasPrice(item.getGasPrice().longValue());
		transaction.setStatus(status);
		transaction.setCumulative(receipt.getCumulativeGasUsed().longValue());
		transaction.setBlockHash(item.getBlockHash());
		transaction.setBlockNumber(item.getBlockNumber().longValue());
		transaction.setBlockIndex(item.getTransactionIndex().intValue());
		transaction.setInternal(0);
		transaction.setInput(item.getInput());
		transaction.setContract(receipt.getContractAddress());
		transaction.setGasUsed(receipt.getGasUsed().longValue());
		transaction.setType(type);
	//	parseTxData(item.getInput(), transaction, receipt.getLogs());
		return transaction;
	}

	public boolean parseTxData(Transaction tx, List<Log> logList) {
		TranasctionType txType = parseTxtype(tx);
		if (txType == null || tx.getStatus() != 1)
			return true;
		String[] params = parseTxParams(tx.getInput());
		if ("SSC".equalsIgnoreCase(txType.getCategory())) {
			return sscTransaction(tx, txType, params);			
		} else if ("UTG".equalsIgnoreCase(txType.getCategory())) {
			return utgTransaction(tx, txType, params, logList);
		}
		return false;
	}

	public static TranasctionType parseTxtype(Transaction tx) {
		String input = tx.getInput();
		if (input != null) {
			String inputString = NumericUtil.convertToString(input);
			String[] datas = inputString.split(":");
			if (datas.length < 3 || (!"UTG".equals(datas[0]) && !"SSC".equals(datas[0])))
				return null;
			if (datas[0] != null && datas[0].length() < 10)
				tx.setUfoprefix(datas[0]);
			if (datas[1] != null && datas[1].length() < 10)
				tx.setUfoversion(datas[1]);
			if (datas[2] != null && datas[2].length() < 20) {
				tx.setUfooperator(datas[2]);
				TranasctionType txType = TranasctionType.parseOperator(tx.getUfooperator());
				return txType;
			}
		}
		return null;
	}	

	public static String[] parseTxParams(String input) {
		String inputData = NumericUtil.convertToString(input);
		List<String> datalist = Arrays.asList(inputData.split(":"));
		if (datalist.size() < 3)
			throw new RuntimeException("Invaild input " + input + " as :" + inputData + " to split params");
		String[] paramDatas = datalist.subList(3, datalist.size()).toArray(new String[datalist.size() - 3]);
		return paramDatas;
	}	
	

	public void batchSave(List<Transaction> txList) {
		if (txList != null && txList.size() > 0) {
			transactionMapper.batchTransaction(txList);
		}
	}


	private boolean sscTransaction(Transaction tx, TranasctionType txType, String[] params) {
		switch (txType) {
		case ExchRate:
			txExchRate(tx, params);
			break;
		case Deposit:
			txDeposit(tx, params);
			break;
		case CndLock:
			txCndLock(tx, params);
			break;
		case FlwLock:
			txFlwLock(tx, params);
			break;
		case RwdLock: 
			txRwdLock(tx, params);
			break;
		case WdthPnsh:
			txWdthPnsh(tx, params);
			break;
		default:
			return false;
		}
		return true;
    }    

	private boolean utgTransaction(Transaction tx, TranasctionType txType, String[] params, List<Log> logs){
		if (logs == null) {
			tx.setStatus(0);
			return true;
		}
		List<String> topics = logs == null || logs.size() == 0 ? null : logs.get(0).getTopics();
		String logData = logs == null || logs.size() == 0 ? null : logs.get(0).getData();
		try {
			switch (txType) {
			case Exch: 
				txExch(tx, params, topics, logData);
				break;
			case Bind:
				txBind(tx, params, topics, logData);
				break;
			case Unbind:
				txUnbind(tx, params, topics, logData);
				break;
			case Rebind:
				txRebind(tx, params, topics, logData);
				break;
			case CandReq:
				txCandReq(tx, params, topics, logData);
				break;
			case CandExit:
				txCandExit(tx, params, topics, logData);
				break;
			case CandPnsh:
				txCandPnsh(tx, params, topics, logData);
				break;
			case FlwReq:
				txFlwReq(tx, params, topics, logData, logs);
				break;
			case FlwExit:
				txFlwExit(tx, params, topics, logData);
				break;
			default:
				return false;
			}
		}catch(Exception e){
			log.warn("Parses UTG transaction error:",e);
			tx.setStatus(0);
		}
		return true;
	}
	
	/**
	 * 
	 * @param tx
	 * @param params
	 */
	private void txExchRate(Transaction tx, String[] params){
     //   srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.ExchRate.getEncode().length())));
		if(!address1.equalsIgnoreCase(tx.getFromAddr())){
            tx.setStatus(0);
            return;
        }
		GlobalConfig config = new GlobalConfig();
		config.setType("ExchRate");
		config.setValue(params[0]);
		config.setTxhash(tx.getHash());
		config.setBlocknumber(tx.getBlockNumber());
		config.setUpdatetime(tx.getTimeStamp());
		globalConfigMapper.saveOrUpdate(config);
		tx.dataBuild(params[0], null, null, null, null, null);
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 */
	private void txDeposit(Transaction tx, String[] params){
    //    srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.Deposit.getEncode().length())));
		if(!address2.equalsIgnoreCase(tx.getFromAddr())){
            tx.setStatus(0);
            return;
        }
		String type = params[1];      
		if (!"0".equals(type)) {
			String value = bigNumberConvert(params[0]).toString();
			tx.setUfooperator("stset");
			tx.setParam1(type);
			tx.setParam2(value);
			GlobalConfig config = new GlobalConfig();
			config.setType(type);
			config.setValue(value);
			config.setTxhash(tx.getHash());
			config.setBlocknumber(tx.getBlockNumber());
			config.setUpdatetime(tx.getTimeStamp());
			globalConfigMapper.saveOrUpdate(config);
		}else{
			tx.dataBuild(bigNumberConvert(params[0]).toString(),params[1],null,null,null,null);
		}
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 */
	private void txCndLock(Transaction tx, String[] params){
    //    srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.CndLock.getEncode().length())));
		if(!address2.equalsIgnoreCase(tx.getFromAddr())){
            tx.setStatus(0);
            return;
        }
		tx.dataBuild(bigNumberConvert(params[0]).toString(), bigNumberConvert(params[1]).toString(), bigNumberConvert(params[2]).toString(), null, null, null);
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 */
	private void txRwdLock(Transaction tx, String[] params){
     //   srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.RwdLock.getEncode().length())));
		if(!address2.equalsIgnoreCase(tx.getFromAddr())){
            tx.setStatus(0);
            return;
        }
		tx.dataBuild(bigNumberConvert(params[0]).toString(), bigNumberConvert(params[1]).toString(), bigNumberConvert(params[2]).toString(), null, null, null);
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 */
	private void txFlwLock(Transaction tx, String[] params){
    //    srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.FlwLock.getEncode().length())));
		if(!address2.equalsIgnoreCase(tx.getFromAddr())){
            tx.setStatus(0);
            return;
        }
		tx.dataBuild(bigNumberConvert(params[0]).toString(), bigNumberConvert(params[1]).toString(), bigNumberConvert(params[2]).toString(), null, null, null);
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 */
	private void txWdthPnsh(Transaction tx, String[] params){
    //    srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.WdthPnsh.getEncode().length())));
		if(!address3.equalsIgnoreCase(tx.getFromAddr())){
            tx.setStatus(0);
            return;
        }
		tx.dataBuild(prefixAddress(params[0]), null, bigNumberConvert(params[1]).toString(), null, null, null);
	}
	
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 */
	private void txExch(Transaction tx, String[] params, List<String> topics, String logData){
    //    srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Exch.getEncode().length())));
		if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(exchTopic)) {
			tx.setStatus(0);
            return;
		}
		BigInteger utg = bigNumberConvert(logData.substring(2, 66));
		BigInteger srt = NumericUtil.convertToBigInteger("0x" + logData.substring(67, 130));
		tx.dataBuild(prefixAddress(params[0]), null, utg.toString(), srt == null ? null : srt.toString(), null, null);
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 */
	private void txBind(Transaction tx, String[] params, List<String> topics, String logData){
     //    srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Bind.getEncode().length())));
		if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(bindTopic)) {
			tx.setStatus(0);
			return;
		}
		Integer type = NumericUtil.convertToInteger(topics.get(2));
		String miner_addr = prefixAddress(params[0]);
		String revenue_address = tx.getFromAddr();
		tx.dataBuild(miner_addr, revenue_address, type.toString(), null, prefixAddress(params[2]), prefixAddress(params[3]));
		
		// Income address binding income type 0 node reward 1 traffic mining
		if (type == 0) { // Node binding
			UtgNodeMiner node = new UtgNodeMiner();
			node.setNode_address(miner_addr);
			node.setRevenue_address(revenue_address);
			node.setSync_time(new Date());
			nodeExitMapper.updateNodeMiner(node);
		} else if (type == 1) {
			if (logData != null && logData.length() >= 66) {
				revenue_address = Constants.PRE + logData.substring(26, 66);
				tx.dataBuild(miner_addr, revenue_address, type.toString(), null, prefixAddress(params[2]), prefixAddress(params[3]));
			}
			UtgStorageMiner pre_miner = utgStorageMinerMapper.getSingleMiner(miner_addr);
			if (pre_miner == null) {
				UtgStorageMiner minerInsert = new UtgStorageMiner(miner_addr, revenue_address, MinerStatusEnum.toBeAddPool.getCode(), tx.getBlockNumber(),	new Date());
				utgStorageMinerMapper.saveOrUpdata(minerInsert);
			} else {
				UtgStorageMiner minerUpdate = new UtgStorageMiner(miner_addr, revenue_address, pre_miner.getMiner_status(), tx.getBlockNumber(), new Date());
				utgStorageMinerMapper.updateStorageMiner(minerUpdate);
			}
			addressMapper.setAsRevenue(revenue_address,tx.getBlockNumber());
			// Update storage space revenue address
			StorageSpace space = storageSpaceMapper.getSpaceInfo(miner_addr);
			if (space != null) {
				space.setRevenueAddr(revenue_address);
				space.setUpdatetime(new Date());
				storageSpaceMapper.updateRevenueAddr(space);
			}
		}
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 */
	private void txUnbind(Transaction tx, String[] params, List<String> topics, String logData){
   //     srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Unbind.getEncode().length())));
		if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(bindTopic)){
			tx.setStatus(0);
            return;
		}
		Integer type = NumericUtil.convertToInteger(topics.get(2));
        String miner_addr = prefixAddress(params[0]);
    //    String revenue_address = tx.getFromAddr();
        tx.dataBuild(miner_addr,null,type.toString(),null,null,null);
        if(type==0){            //Node revenue address unbinding
            UtgNodeMiner node = new UtgNodeMiner();
            node.setNode_address(miner_addr);
            node.setRevenue_address("");
            node.setSync_time(new Date());
            nodeExitMapper.updateNodeMiner(node);
        }else if(type==1){     //Unbind income address
            UtgStorageMiner miner = new UtgStorageMiner();
            miner.setMiner_addr(miner_addr);
            miner.setRevenue_address("");
            miner.setBlocknumber(tx.getBlockNumber());
            miner.setSync_time(new Date());
            utgStorageMinerMapper.updateStorageMiner(miner);            
            //Update storage space revenue address
            StorageSpace space = storageSpaceMapper.getSpaceInfo(miner_addr);
            if(space!=null){                	 
           	 space.setRevenueAddr("");
           	 space.setUpdatetime(new Date());
           	 storageSpaceMapper.updateRevenueAddr(space);
            }
        }
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 */
	private void txRebind(Transaction tx, String[] params, List<String> topics, String logData){
     //   srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Rebind.getEncode().length())));
		log.info(tx.getHash()+" topics "+topics);
		if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(bindTopic)) {
			tx.setStatus(0);
			return;
		}
		Integer type = NumericUtil.convertToInteger(topics.get(2));
		String miner_addr = prefixAddress(params[0]);
		String revenue_address = prefixAddress(params[4]);
		tx.dataBuild(miner_addr, revenue_address, type.toString(), null, prefixAddress(params[2]), prefixAddress(params[3]));
		log.info(tx.getHash()+" Rebind: type  "+type+"  miner_addr= "+miner_addr+"  revenue_address="+revenue_address);
		if (type == 0) {
			// Node revenue address replacement
			UtgNodeMiner node = new UtgNodeMiner();
			node.setNode_address(miner_addr);
			node.setRevenue_address(revenue_address);
			node.setSync_time(new Date());
			nodeExitMapper.updateNodeMiner(node);
		} else if (type == 1) {
			// Change of income address
			UtgStorageMiner miner = new UtgStorageMiner();
			miner.setMiner_addr(miner_addr);
			miner.setRevenue_address(revenue_address);
			miner.setBlocknumber(tx.getBlockNumber());
			miner.setSync_time(new Date());
			if (!zeroA.equalsIgnoreCase(params[3])) {
				miner.setAddpool(1);
			}
			utgStorageMinerMapper.updateStorageMiner(miner);
			addressMapper.setAsRevenue(revenue_address,tx.getBlockNumber());
			// Update storage space revenue address
			StorageSpace space = storageSpaceMapper.getSpaceInfo(miner_addr);
			if (space != null) {
				space.setRevenueAddr(revenue_address);
				space.setUpdatetime(new Date());
				storageSpaceMapper.updateRevenueAddr(space);
			}
		}
	}
	
	private void txCandReq(Transaction tx, String[] params, List<String> topics, String logData){
	//	srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandReq.getEncode().length())));    
        if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(candReqTopic)){
        	tx.setStatus(0);
            return;
        }
		String address = prefixAddress(params[0]);
		Integer type = NumericUtil.convertToInteger(topics.get(2));
		BigDecimal pledgeAmout = NumericUtil.convertToBigDecimal("0x" + logData.substring(2, 66));
		tx.dataBuild(address, null, type.toString(), pledgeAmout.toString(), null, null);
        
        UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
        queryForm.setNode_address(address);
        UtgNodeMiner pre_node = nodeExitMapper.getNode(queryForm);
        UtgNodeMiner node = new UtgNodeMiner() ;
        node.setNode_address(address);
        node.setNode_type(NodeTypeEnum.wait.getCode());
        node.setBlocknumber(tx.getBlockNumber());
        if(pre_node!=null){
            node.setPledge_amount(NumericUtil.add(pre_node.getPledge_amount(),pledgeAmout));
        }else{
            node.setPledge_amount(pledgeAmout);
        }
        node.setJoin_time(tx.getTimeStamp());
        node.setSync_time(new Date());
        nodeExitMapper.saveOrUpdateNodeMiner(node);
        addressMapper.setAsMiner(address,tx.getBlockNumber());
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 */
	private void txCandExit(Transaction tx, String[] params, List<String> topics, String logData){
	//	srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandExit.getEncode().length())));     
        if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(pledgeExitTopic)){
        	tx.setStatus(0);
            return;
        }
        String address = prefixAddress(params[0]);
        Integer type = NumericUtil.convertToInteger(topics.get(2));        
        tx.dataBuild(address,null,type.toString(),null,null,null);
        
        
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 */
	private void txCandPnsh(Transaction tx, String[] params, List<String> topics, String logData){
	//	srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandPnsh.getEncode().length())));       
        if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(candPnshTopic)){
        	tx.setStatus(0);
            return;
        }
		String address = prefixAddress(params[0]);
		Integer mbpoint = NumericUtil.convertToInteger("0x" + logData.substring(2, 66));
		BigInteger fee = NumericUtil.convertToBigInteger("0x" + logData.substring(67, 130));
		tx.dataBuild(address, null, mbpoint == null ? null : mbpoint.toString(), fee == null ? null : fee.toString(), null, null);
        //Block out penalties to make up
        UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
        queryForm.setNode_address(address);
        UtgNodeMiner pre_node = nodeExitMapper.getNode(queryForm);
        if(pre_node!=null){
            UtgNodeMiner node = new UtgNodeMiner();
            node.setNode_address(address);
            node.setFractions(0);
            node.setPledge_amount(NumericUtil.add(pre_node.getPledge_amount(),new BigDecimal(fee)));
            node.setSync_time(new Date());
            nodeExitMapper.updateNodeMiner(node);
        }
	}
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 * @param logs
	 */
	private void txFlwReq(Transaction tx, String[] params, List<String> topics, String logData, List<Log> logs){
	//	srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.FlwReq.getEncode().length())));
		if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(flwReqTopic)){
        	tx.setStatus(0);
            return;
        }
		String miner_address = prefixAddress(params[0]);
		String line_type = bigNumberConvert(params[1]).toString();
		String bandwidth = bigNumberConvert(params[2]).toString();
		Integer type = NumericUtil.convertToInteger(topics.get(2));
		BigDecimal pledgeAmout = NumericUtil.convertToBigDecimal("0x" + logs.get(1).getData().substring(67, 130));
		tx.dataBuild(miner_address, null, bandwidth, pledgeAmout == null ? null : pledgeAmout.toString(), line_type, type.toString());
		// Data mining pledge
		UtgStorageMiner pre_miner = utgStorageMinerMapper.getSingleMiner(miner_address);
		if (pre_miner != null) {
			UtgStorageMiner miner = new UtgStorageMiner();
			miner.setMiner_addr(miner_address);
			miner.setSync_time(new Date());
			miner.setMiner_status(MinerStatusEnum.mining.getCode());
			miner.setLine_type(line_type);
			miner.setBandwidth(new BigDecimal(bandwidth));
			miner.setPledge_amount(NumericUtil.add(pre_miner.getPledge_amount(), pledgeAmout));
			miner.setBlocknumber(tx.getBlockNumber());
			miner.setJoin_time(tx.getTimeStamp());
			utgStorageMinerMapper.updateStorageMiner(miner);
		}
	}	
	/**
	 * 
	 * @param tx
	 * @param params
	 * @param topics
	 * @param logData
	 */
	private void txFlwExit(Transaction tx, String[] params, List<String> topics, String logData){
	//	srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.FlwExit.getEncode().length())));	
		if (topics == null || topics.size() == 0 || !topics.get(0).equalsIgnoreCase(pledgeExitTopic)) {
			tx.setStatus(0);
			return;
		}
		String miner_address = prefixAddress(params[0]);
		Integer type = NumericUtil.convertToInteger(topics.get(2));		
		tx.dataBuild(miner_address, null, type.toString(), null, null, null);
	}
    
	
    /**
     * 40-byte address plus prefix
     */
	private static String prefixAddress(String address) {
		return Constants.PRE + address;
	}

    /**
     * 16 to 10
     * @param num
     * @return
     */
	private static BigInteger bigNumberConvert(String num) {
		return new BigInteger(num, 16);
	}


	
}
