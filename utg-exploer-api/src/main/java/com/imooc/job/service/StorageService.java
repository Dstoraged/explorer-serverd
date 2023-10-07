package com.imooc.job.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

import com.imooc.Utils.TimeSpend;
import com.imooc.job.service.TransactionService.TranasctionType;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.GlobalConfigMapper;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.StorageConfigMapper;
import com.imooc.mapper.StoragePoolMapper;
import com.imooc.mapper.StorageRentMapper;
import com.imooc.mapper.StorageRequestMapper;
import com.imooc.mapper.StorageRevenueMapper;
import com.imooc.mapper.StorageSpaceMapper;
import com.imooc.mapper.StorageStatMapper;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.pojo.NodePledge;
import com.imooc.pojo.StoragePool;
import com.imooc.pojo.StorageRent;
import com.imooc.pojo.StorageRequest;
import com.imooc.pojo.StorageRequest.ReqStatus;
import com.imooc.pojo.StorageRevenue;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.pojo.StorageRent.RentStatus;
import com.imooc.pojo.StorageSpace.PledgeStatus;
import com.imooc.pojo.StorageSpaceStat;
import com.imooc.utils.Constants;
import com.imooc.utils.NumericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageService {
	private BigDecimal tb1b=new BigDecimal("1099511627776");
	private BigDecimal eb1b=(new BigDecimal("1099511627776")).multiply(new  BigDecimal(1048576));
	private BigDecimal storageRewardGainRatio=BigDecimal.valueOf(11);
	private BigDecimal storageRewardAdjRatio =BigDecimal.valueOf(200);
			public enum StorageOperator{
		stReq,
		stExit,
		stValid,
		chPrice,
		chbw,
		stRent,
		stRentPg,
		stReNew,
		stReNewPg,
		stRescind,
		creFile,
		delFile,
		stReValid,
		stCatchUp,
		stProof,
		stset,
		editmgaddr,
		stchpg,
		stwtreward,
		setsp,
		exitsp,
		streplace,
		stwtpg,
		wtfd,
		wtpgexit
	}
	
	@Autowired
	StorageConfigMapper storageConfigMapper;	
	@Autowired
	StorageSpaceMapper storageSpaceMapper;	
	@Autowired
	StorageRentMapper storageRentMapper;	
	@Autowired
	StorageRequestMapper storageRequestMapper;	
	@Autowired
	StorageRevenueMapper storageRevenueMapper;	
//	@Autowired
//	TransactionMapper transactionMapper;	
	@Autowired
	UtgStorageMinerMapper utgStorageMinerMapper;	
	@Autowired
	StorageStatMapper storageStatMapper;
	@Autowired
	AccountMapper addressMapper;
	@Autowired
	AddressService addressService;
	@Autowired
	private NodeExitMapper  nodePledgeDao;
	@Autowired
	private StoragePoolMapper spDao;
	@Autowired
	private GlobalConfigMapper gcfmDao;
	@Autowired
	Web3jService web3jService;
		
	@Value("${dayOneNumber}")
    private Long dayOneNumber;	//1 day block height, 10 seconds to generate a block
	
	@Value("${lockreleaseinterval:360}")
    private Long lockreleaseinterval;
	@Value("${rewardcollectdelay:0}")
	private Integer rewardcollectdelay;
	
	public boolean stroageTransaction(Transaction tx, Log txLog){
		String input = tx.getInput();
		if(input==null)
			return false;
//		String data = new String(Numeric.hexStringToByteArray(input));
//		String[] datas = data.split(":");
//		if(datas.length<3)
//			return false;
//		String ustPrefix = datas[0];
//		String ustVersion = datas[1];
//		String ustOperator = datas[2];
//		if("UTG".equals(ustPrefix)){
		TranasctionType txType = TransactionService.parseTxtype(tx);
		if(txType!=null && "ST".equals(txType.getCategory())){
			String ustOperator = txType.getOperator();
			String[] params = TransactionService.parseTxParams(tx.getInput());			
			StorageOperator operator;
			try{
				operator= StorageOperator.valueOf(ustOperator);
			}catch(Exception e){
				log.info("invaild storage ustOperator "+ustOperator);
				return false;
			}
//			tx.setUfoprefix(ustPrefix);
//			tx.setUfoversion(ustVersion);
//			tx.setUfooperator(ustOperator);
			try{
				switch (operator) {
				case stReq:
					storageReqTransaction(tx, params, txLog);
					break;
				case stExit:
					storageExitTransaction(tx, params, txLog);
					break;				
				case chPrice:
					storageChangePriceTransaction(tx, params, txLog);
					break;
				case chbw:
					storageChangeBandwidthTransaction(tx, params, txLog);
					break;
				case stRent:
					storageRentTransaction(tx, params, txLog);
					break;
				case stRentPg:
					storageRentPledgeTransaction(tx, params, txLog);
					break;
				case stReNew:
					storageRenewTransaction(tx, params, txLog);
					break;
				case stReNewPg:
					storageRenewPledgeTransaction(tx, params, txLog);
					break;
				case stRescind:
					storageRescindTransaction(tx, params, txLog);
					break;			
				case stReValid:
					storageReValidTransaction(tx, params, txLog);
					break;
				case stProof:
					storageProofTransaction(tx, params, txLog);
					break;
				case stCatchUp:
					storageCatchUpTransaction(tx, params, txLog);
					break;	
				case creFile:
					storageCreFileTransaction(tx, params, txLog);
					break;
				case delFile:
					storageDelFileTransaction(tx, params, txLog);
					break;					
				case stValid:
					storageVaildTransaction(tx, params, txLog);
					break;
				case stset:
					storageSetTransaction(tx, params, txLog);
					break;
				case editmgaddr:
					storageEditMgaddrTransaction(tx, params, txLog);
					break;
				case stchpg:
					storageStchPgTransaction(tx, params, txLog);
					break;	
				case stwtreward:
					storageStwtrewardTransaction(tx, params, txLog);
					break;
				case setsp:
					storageSetspTransaction(tx, params, txLog);
					break;
				case exitsp:
					storageExitspTransaction(tx, params, txLog);
					break;
				case streplace:
					storageStreplaceTransaction(tx, params, txLog);
					break;
				case stwtpg:
					storageStwtpgTransaction(tx, params, txLog);
					break;
				case wtfd:
					storageWtfdTransaction(tx, params, txLog);
					break;
				case wtpgexit:
					storageWtpgexitTransaction(tx, params, txLog);
					break;
					
				default:
					return false;
				}
				return true;
			}catch(Exception e){
				String data = new String(Numeric.hexStringToByteArray(input));
				log.warn("Storage "+operator+" transaction :"+tx+", parse data :"+data+" error.",e);
			//	throw e;
			}
		}
		return false;
	}
	
	/*
	private List<String> praseParameters(TransactionObject transaction,Log txLog){		
//		String data = txLog.getData();
//		if(data==null)
//			throw new RuntimeException("Invaild transaction data");
//		String[] datas = data.split(",");			
		String input = transaction.getInput();
		String data = new String(Numeric.hexStringToByteArray(input));
		log.info("transaction data is "+data);
		String[] datas = data.split(":");		
		List<String> datalist = Arrays.asList(datas);		
		return datalist.subList(3, datalist.size());
	}
	*/
	private String getTopic(Log txLog,int index){
		if(txLog==null||txLog.getTopics()==null||txLog.getTopics().size()<=index)
			throw new RuntimeException("Cannot get txLog topic data ["+index+"] :" + txLog);
		return txLog.getTopics().get(index);
	}
	
	private boolean getStatus(Transaction tx,Log txLog){
	//	if(txLog==null||txLog.getTopics()==null||txLog.getTopics().size()<=2)
	//		return "fail";
	//	return txLog.getTopics().get(2);		
		return tx.getStatus()==1 && txLog!=null && txLog.getTopics()!=null && txLog.getTopics().size()!=0;	// when txLog is not exist, status is failed
	}
	
		
	/**
	 * 
	 * UTG:1:stReq:{device_addr}:{rent_price}:{storage_space}:{pledge_number}	:{block_size}:{roothash}:{storevaild}
	 */
	private void storageReqTransaction(Transaction tx, String[] params, Log txLog) {		
		boolean success = getStatus(tx, txLog);
		Long blockNumber = tx.getBlockNumber();
		String pledge_addr = Constants.prefixAddress(tx.getFromAddr());		
		String device_addr = Constants.prefixAddress(params[0]);
		BigDecimal rent_price = new BigDecimal(params[1]);
		BigDecimal storage_space = new BigDecimal(params[2]);
	//	Long pledge_number = Long.parseLong(params[3]);
	// 	Long blocksize = Long.parseLong(params[4]);
	// 	String roothash = params[5];
	// 	String storevaild = params[6];
		BigDecimal bw_size = new BigDecimal(params[7]);		
		BigDecimal pledge_amount = null;
		BigDecimal havAmount = null;
		BigDecimal etRate=BigDecimal.ZERO; 
		if(txLog!=null){
		//	String logdata = txLog.getData();	
		//	pledge_addr = getTopic(txLog, 1);
			pledge_amount = new BigDecimal(Numeric.decodeQuantity(getTopic(txLog, 2)));	
			if(txLog.getTopics()!=null&&txLog.getTopics().size()==5) {
				havAmount = new BigDecimal(Numeric.decodeQuantity(getTopic(txLog, 3)));
				etRate = (new BigDecimal(Numeric.decodeQuantity(getTopic(txLog, 4)))).divide(BigDecimal.valueOf(100));	
			}
			
		}		
		String revenue_addr = null;
		UtgStorageMiner storageMiner = utgStorageMinerMapper.getSingleMiner(device_addr);
		if (storageMiner != null) {
			revenue_addr = storageMiner.getRevenue_address();
		}
		BigDecimal bw_ratio = null;
		BigDecimal reward_ratio = null;
		if (bw_size != null) {
//			List<StorageConfig> bwList = storageConfigMapper.getList("bandwidth");
//			for (StorageConfig bw : bwList) {
//				if ((bw.getMin() == null || bw.getMin().compareTo(bw_size) < 0) &&	(bw.getMax() == null || bw.getMax().compareTo(bw_size) >= 0)) {
//					bw_ratio = bw.getValue();
//					break;
//				}
//			}
			bw_ratio = new BigDecimal(NumericUtil.getBandwidthPledgeRatio(bw_size.intValue()));
			reward_ratio = new BigDecimal(NumericUtil.getBandwidthRewardRatio(bw_size.intValue()));
		}
				
		
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space != null && space.getPledgeStatus() == PledgeStatus.normal.value()){
			success = false;
			log.warn("Exist available storage by device_addr=" + device_addr);
		}
	//	int pledge_status = success ? PledgeStatus.normal.value() : PledgeStatus.failed.value();
	//	boolean isNew = space == null || space.getPledgeStatus() != 0;
		int pledge_status = success ? PledgeStatus.normal.value() : PledgeStatus.failed.value();
		
		if (space == null || space.getPledgeStatus() == PledgeStatus.deleted.value()) {
			space = new StorageSpace();	
			space.setDeviceAddr(device_addr);
			space.setPledgeAddr(pledge_addr);
		 	space.setRevenueAddr(revenue_addr);
			space.setPledgeStatus(pledge_status);
			space.setPledgeNumber(blockNumber);
			space.setPledgeAmount(pledge_amount);
		// 	space.setPensateAmount(pensate_amount);
			space.setDeclareSpace(storage_space);
			space.setFreeSpace(storage_space);
			space.setRentPrice(rent_price);
			space.setRentNum(0L);
			space.setTotalAmount(BigDecimal.ZERO);
			space.setStorageAmount(BigDecimal.ZERO);
			space.setRentAmount(BigDecimal.ZERO);
			space.setBwSize(bw_size);
			space.setBwRatio(bw_ratio);
			space.setRewardRatio(reward_ratio);
		 	space.setValidNumber(blockNumber);
		 	space.setSuccNumber(blockNumber);
			space.setFailCount(0);
			space.setVaildProgress(BigDecimal.ZERO);
			space.setInstime(new Date());
			space.setUpdatetime(new Date());
			space.setEntrustRate(etRate);
			space.setHavAmount(havAmount);
			space.setManagerAddr(pledge_addr);
			space.setManagerAmount(havAmount);
			space.setManagerHeight(blockNumber);
			storageSpaceMapper.insertSpace(space);
			
			addressMapper.setAsStorage(device_addr,blockNumber);
		}
		
		StorageRequest request = new StorageRequest();
		request.setReqhash(tx.getHash());
		request.setDeviceAddr(device_addr);
		request.setStorageid(space.getStorageid());
		request.setReqType(StorageOperator.stReq.name());
		request.setReqNumber(blockNumber);
		request.setReqSpace(storage_space);
		request.setRentPrice(rent_price);
		request.setReqStatus(success ? ReqStatus.success.value() : ReqStatus.failed.value());
		request.setPledgeStatus(space.getPledgeStatus());
		request.setInstime(new Date());
		storageRequestMapper.insertRequest(request);
					
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
		tx.setParam3(storage_space);
		tx.setParam4(rent_price);
		tx.setParam6(pledge_amount == null ? null : pledge_amount.toString());
	}

	/**
	 * 
	 * UTG:1:stExit:{device_addr} 
	 */
	private void storageExitTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);		
		String device_addr = Constants.prefixAddress(params[0]);

		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false;
			log.warn("Cannot find storage by device_addr = " + device_addr);
		}else if (space.getPledgeStatus() != PledgeStatus.normal.value() && space.getPledgeStatus() != PledgeStatus.exiting.value()){
	//		success = false;
			log.warn("Storage space " + device_addr + " is not available , status =" + space.getPledgeStatus());
		}
		if (success) {			
			space.setPledgeStatus(PledgeStatus.exiting.value());
			space.setUpdatetime(new Date());
			storageSpaceMapper.updatePledgeStatus(space);

			List<StorageRent> rentList = storageRentMapper.getList(device_addr, null, null);
			for (StorageRent rent : rentList) {
				if (rent.getRentStatus() == RentStatus.normal.value()) {
					rent.setRentStatus(RentStatus.expired.value());
					rent.setUpdatetime(new Date());
					storageRentMapper.updateRentStatus(rent);
				}
			}
			
			UtgStorageMiner storageMiner = utgStorageMinerMapper.getSingleMiner(device_addr);
			if (storageMiner != null) {
				UtgStorageMiner miner = new UtgStorageMiner();
                miner.setMiner_addr(device_addr);
                miner.setRevenue_address("");
                miner.setBlocknumber(tx.getBlockNumber());
                miner.setSync_time(new Date());
                utgStorageMinerMapper.updateStorageMiner(miner);
			}
log.warn("storage space device_addr="+device_addr+" id="+space.getStorageid()+" has exit");
		}
		if(space!=null){
			StorageRequest request = new StorageRequest();
			request.setReqhash(tx.getHash());
			request.setDeviceAddr(device_addr);
			request.setStorageid(space.getStorageid());
			request.setReqType(StorageOperator.stExit.name());
			request.setReqNumber(tx.getBlockNumber());
			request.setReqStatus(success ? ReqStatus.success.value() : ReqStatus.failed.value());
			request.setPledgeStatus(space.getPledgeStatus());
			request.setInstime(new Date());
			storageRequestMapper.insertRequest(request);
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
	}
	
	/**
	 * 
	 * UTG:1:chPrice:{device_addr}	:{rent_price}
	 */
	private void storageChangePriceTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);
		String device_addr = Constants.prefixAddress(params[0]);
		BigDecimal rent_price = new BigDecimal(params[1]);

		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false; 
			log.warn("Cannot find storage by device_addr=" + device_addr);
		}else if (space.getPledgeStatus() != PledgeStatus.normal.value()){
	//		success = false; 
			log.warn("Storage space " + device_addr + " is not available , status =" + space.getPledgeStatus());
		}		
		if (success) {						
			space.setRentPrice(rent_price);
			space.setUpdatetime(new Date());
			storageSpaceMapper.updateRentPrice(space);
		}
		if(space!=null){
			StorageRequest request = new StorageRequest();
			request.setReqhash(tx.getHash());
			request.setDeviceAddr(device_addr);
			request.setStorageid(space.getStorageid());
			request.setReqType(StorageOperator.chPrice.name());
			request.setReqNumber(tx.getBlockNumber());
			request.setRentPrice(rent_price);
			request.setReqStatus(success ? ReqStatus.success.value() : ReqStatus.failed.value());
			request.setPledgeStatus(space.getPledgeStatus());
			request.setInstime(new Date());
			storageRequestMapper.insertRequest(request);
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
		tx.setParam4(rent_price);
	}

	/**
	 * 
	 * UTG:1:chbw:{device_addr}	:{bw_size}
	 */
	private void storageChangeBandwidthTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);				
		String device_addr = Constants.prefixAddress(params[0]);
		BigDecimal bw_size = new BigDecimal(params[1]);
		
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false; 
			log.warn("Cannot find storage by device_addr=" + device_addr);
		}else if (space.getPledgeStatus() != PledgeStatus.normal.value()){
	//		success = false; 
			log.warn("Storage space " + device_addr + " is not available , status =" + space.getPledgeStatus());
		}
		if (success) {
//			BigDecimal bw_ratio = null;			
//			if (bw_size != null) {
//				List<StorageConfig> bwList = storageConfigMapper.getList("bandwidth");
//				for (StorageConfig bw : bwList) {
//					if ((bw.getMin() == null || bw.getMin().compareTo(bw_size) < 0) &&	(bw.getMax() == null || bw.getMax().compareTo(bw_size) >= 0)) {
//						bw_ratio = bw.getValue();
//						break;
//					}
//				}			
//			}
			BigDecimal pledge_amount = NumericUtil.convertToBigDecimal(txLog.getData());
			BigDecimal bw_ratio = new BigDecimal(NumericUtil.getBandwidthPledgeRatio(bw_size.intValue()));
			BigDecimal reward_ratio = new BigDecimal(NumericUtil.getBandwidthRewardRatio(bw_size.intValue()));
			int bwChanged = space.getBwChanged() == null ? 0 : space.getBwChanged();
			bwChanged ++;
			space.setBwSize(bw_size);
			space.setBwRatio(bw_ratio);
			space.setRewardRatio(reward_ratio);			
			space.setPledgeAmount(pledge_amount);
			space.setPrepledgeAmount(null);			
			space.setBwChanged(bwChanged);			
			space.setUpdatetime(new Date());
			storageSpaceMapper.updateBandwidth(space);
		}
		if(space!=null){
			StorageRequest request = new StorageRequest();
			request.setReqhash(tx.getHash());
			request.setDeviceAddr(device_addr);
			request.setStorageid(space.getStorageid());
			request.setReqType(StorageOperator.chbw.name());
			request.setReqNumber(tx.getBlockNumber());
		//	request.setRentPrice(rent_price);
			request.setReqStatus(success ? ReqStatus.success.value() : ReqStatus.failed.value());
			request.setPledgeStatus(space.getPledgeStatus());
			request.setInstime(new Date());
			storageRequestMapper.insertRequest(request);
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
	//	tx.setParam4(rent_price);
	}
	
	
	/**
	 * 
	 * UTG:1:stRent:{device_addr}:{rent_space}:{rent_time}:{rent_price}
	 */
	private void storageRentTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);				
		String rent_hash = tx.getHash();		
		String rent_addr = Constants.prefixAddress(tx.getFromAddr());		
		String device_addr = Constants.prefixAddress(params[0]);
		BigDecimal rent_space = new BigDecimal(params[1]);
		Integer rent_time = Integer.parseInt(params[2]);
		BigDecimal rent_price = new BigDecimal(params[3]);
		BigDecimal rent_amount = rent_space.multiply(rent_price.multiply(new BigDecimal(rent_time))).divide(new BigDecimal(1024L * 1024 * 1024), BigDecimal.ROUND_HALF_UP);

		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false;
			log.warn("Cannot find storage by device_addr=" + device_addr);
		}else if (space.getPledgeStatus() != PledgeStatus.normal.value()){
	//		success = false;
			log.warn("Storage space " + device_addr + " is not available , status =" + space.getPledgeStatus());
		}
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if (rent != null && rent.getRentStatus() == RentStatus.normal.value()){
			success = false;
			log.warn("Exist storage available rent " + rent_hash);
		}		
		int rent_status = success ? RentStatus.applying.value() : RentStatus.failed.value();
		boolean isNew = rent == null || rent.getRentStatus() != RentStatus.applying.value();
		if (isNew)
			rent = new StorageRent();
		rent.setDeviceAddr(device_addr);
		rent.setRentHash(rent_hash);
		rent.setRentAddr(rent_addr);
		rent.setRentSpace(rent_space);
		rent.setRentPrice(rent_price);
		rent.setRentTime(rent_time);
		rent.setRentNumber(tx.getBlockNumber());
		rent.setRentAmount(rent_amount);
		rent.setRentStatus(rent_status);
	// 	rent.setRecevAmount(recev_amount);
	 	rent.setValidNumber(0L);
	 	rent.setSuccNumber(0L);
		rent.setFailCount(0);
		rent.setInstime(new Date());
		rent.setUpdatetime(new Date());
		if (isNew)
			storageRentMapper.insertRent(rent);
		else if (success)
			storageRentMapper.updateRent(rent);		
		if(space!=null){
			StorageRequest request = new StorageRequest();
			request.setReqhash(tx.getHash());
			request.setDeviceAddr(device_addr);
			request.setRentHash(rent_hash);
			request.setStorageid(space.getStorageid());
			request.setRentid(rent.getRentid());
		//	request.setRentAddr(rent_addr);
			request.setReqType(StorageOperator.stRent.name());
			request.setReqNumber(tx.getBlockNumber());
			request.setReqSpace(rent_space);
			request.setRentPrice(rent_price);
			request.setRentTime(rent_time);
			request.setRentAmount(rent_amount);
			request.setReqStatus(success ? ReqStatus.pending.value() : ReqStatus.failed.value());
			request.setPledgeStatus(rent.getRentStatus());
			request.setInstime(new Date());
			storageRequestMapper.insertRequest(request);
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
		tx.setParam3(rent_space);
		tx.setParam4(rent_price);
		tx.setParam5(rent_hash);
		tx.setParam6(rent_time.toString());
	}

	/**
	 * 
	 * UTG:1:stRentPg:{device_addr}:{rent_hash}:{rent_space}:	{rent_roothash}:{rent_storevaild}
	 */
	private void storageRentPledgeTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);		
		Long blockNumber = tx.getBlockNumber();
		String pledge_addr = Constants.prefixAddress(tx.getFromAddr());				
		String device_addr = Constants.prefixAddress(params[0]);
		String rent_hash = Constants.prefixAddress(params[1]);
		BigDecimal rent_space = new BigDecimal(params[2]);
	//	String rent_roothash = params[3];
	//	String rent_storevaild = params[4];
		
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false;
			log.warn("Cannot find storage by device_addr=" + device_addr);
		}else if (space.getPledgeStatus() != PledgeStatus.normal.value()){
	//		success = false;
			log.warn("Storage space " + device_addr + " is not available , status =" + space.getPledgeStatus());
		}
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if (rent == null){
			success = false;
			log.warn("Cannot find storage rent by hash=" + rent_hash);
		}else if (rent.getRentStatus() != RentStatus.applying.value()){
	//		success = false;
			log.warn("Storage rent " + rent_hash + " is not rent applying , status =" + rent.getRentStatus());
		}

		StorageRequest request = storageRequestMapper.getRequestInfo(device_addr, rent_hash, StorageOperator.stRent.name());
		if (request != null && request.getReqStatus()!=null&& request.getReqStatus() == ReqStatus.pending.value()) {
			request.setReqStatus(success ? ReqStatus.success.value() : ReqStatus.failed.value());
			request.setPledgeStatus(success ? RentStatus.normal.value() : RentStatus.failed.value());
			request.setUpdatetime(new Date());
			storageRequestMapper.updateRequest(request);
		} else {
			success = false;
			log.warn("Storage rent " + rent_hash + " request is not exist or error status");
		}
		
		if (success) {
		//	rent.setRentSpace(rent_space);
			rent.setPledgeAddr(pledge_addr);
			rent.setRentNumber(blockNumber);
			rent.setValidNumber(blockNumber);
			rent.setSuccNumber(blockNumber);
			rent.setFailCount(0);
			rent.setRentStatus(RentStatus.normal.value());
			rent.setUpdatetime(new Date());
			storageRentMapper.updateRentStatus(rent);

			BigDecimal free_space = space.getFreeSpace().subtract(rent_space);
			space.setFreeSpace(free_space);
			space.setRentNum(space.getRentNum() + 1);
			storageSpaceMapper.updateRentSpace(space);
			
			addressService.updateAddressSrtBalance(rent.getRentAddr(), blockNumber);
		} else if (rent != null && rent.getRentStatus() == RentStatus.applying.value()) {
			rent.setRentStatus(RentStatus.failed.value());
			rent.setUpdatetime(new Date());
			storageRentMapper.updateRentStatus(rent);
		}
		
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
		tx.setParam3(rent_space);
	//	tx.setParam4(new BigDecimal(rent_price));
		tx.setParam5(rent_hash);
	//	tx.setParam6(rent_time.toString());
	}

	/**
	 * 
	 * UTG:1:stReNew:{device_addr}:{rent_hash}:{renew_time}
	 */
	private void storageRenewTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);		
		String device_addr = Constants.prefixAddress(params[0]);
		String rent_hash = Constants.prefixAddress(params[1]);
		Integer renew_time = Integer.parseInt(params[2]);
		
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false;
			log.warn("Cannot find storage by device_addr = " + device_addr);
		}
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if(rent==null){
			success = false;
			log.warn("Cannot find storage rent by hash=" + rent_hash);
		}else if (rent.getRentStatus() != RentStatus.normal.value() && rent.getRentStatus() != RentStatus.breach.value() ){
			success = false;
			log.warn("Storage rent " + rent_hash + " is not available or breach , status =" + rent.getRentStatus());
		}
		if(space!=null && rent!=null){
			StorageRequest request = new StorageRequest();
			request.setReqhash(tx.getHash());
			request.setDeviceAddr(device_addr);
			request.setRentHash(rent_hash);
			request.setStorageid(space.getStorageid());
			request.setRentid(rent.getRentid());
		//	request.setRentAddr(rent.getRentAddr());
			request.setReqType(StorageOperator.stReNew.name());
			request.setReqNumber(tx.getBlockNumber());
			request.setReqSpace(rent.getRentSpace());
			request.setRentPrice(rent.getRentPrice());
			request.setRentTime(renew_time);
			request.setRentAmount(rent.getRentAmount());
			request.setReqStatus(success ? ReqStatus.pending.value() : ReqStatus.failed.value());
			request.setPledgeStatus(success ? RentStatus.applying.value() : RentStatus.failed.value());
			request.setInstime(new Date());
			storageRequestMapper.insertRequest(request);	
		}
		if (success) {
			rent.setRenewStatus(1);
			rent.setRenewReqhash(tx.getHash());
			storageRentMapper.updateRenewRequest(rent);
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);		
	//	tx.setParam3(rent_space);
	//	tx.setParam4(new BigDecimal(rent_price));
		tx.setParam5(rent_hash);
		tx.setParam6(renew_time.toString());
	}

	/**
	 * 
	 * UTG:1:stReNewPg:{device_addr}:{rent_hash}:{rent_space}:		{rent_roothash}:{rent_storevaild}
	 */
	private void storageRenewPledgeTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);
		String device_addr = Constants.prefixAddress(params[0]);
		String rent_hash = Constants.prefixAddress(params[1]);
	//	BigDecimal rent_space = new BigDecimal(params[2]);
	//	String rent_roothash = params[3];
	//	Stirng rent_storevaild = params[4];
		
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if(rent==null){
			success = false;
			log.warn("Cannot find storage rent by hash=" + rent_hash);
		}else if (rent.getRentStatus() != RentStatus.normal.value() && rent.getRentStatus() != RentStatus.breach.value() ){
			success = false;
			log.warn("Storage rent " + rent_hash + " is available or breach, status =" + rent.getRentStatus());
		}
	//	String device_addr = rent.getDeviceAddr();
		StorageRequest request = storageRequestMapper.getRequestInfo(device_addr,rent_hash,StorageOperator.stReNew.name());
		if(request==null || request.getPledgeStatus()!=0){
			success = false;
			log.warn("Storage rent " + rent_hash + " renew request is not exist or error status");
		}
		Integer renew_time = null;	
		if(success){
			renew_time = request.getRentTime();	
			Integer rent_time = rent.getRentTime() + renew_time;
			Long renew_number = rent.getRentNumber() + rent_time * dayOneNumber + 1;	
			BigDecimal rent_space = rent.getRentSpace();
			BigDecimal rent_price = rent.getRentPrice();
			BigDecimal rent_amount = rent_space.divide(new BigDecimal(1024L * 1024 * 1024), BigDecimal.ROUND_HALF_UP).multiply(rent_price.multiply(new BigDecimal(rent_time)));			
			rent.setRentTime(rent_time);
			rent.setRentAmount(rent_amount);
			rent.setRenewStatus(null);
			rent.setRenewNumber(renew_number);
			rent.setRenewTime(renew_time);
			rent.setRenewReqhash(null);
			rent.setUpdatetime(new Date());
			storageRentMapper.updateRentRenew(rent);
			
			request.setReqStatus(ReqStatus.success.value());
			request.setPledgeStatus(RentStatus.normal.value());
			request.setUpdatetime(new Date());
			storageRequestMapper.updateRequest(request);
			
			addressService.updateAddressSrtBalance(rent.getRentAddr(), tx.getBlockNumber());
		}else if(request!=null){
			success = false;
			log.warn("Storage rent " + rent_hash + " request is not exist or error status");
			request.setReqStatus(ReqStatus.failed.value());
			request.setPledgeStatus(RentStatus.failed.value());
			request.setUpdatetime(new Date());
			storageRequestMapper.updateRequest(request);			
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);		
	//	tx.setParam3(rent_space);
	//	tx.setParam4(new BigDecimal(rent_price));
		tx.setParam5(rent_hash);
		tx.setParam6(renew_time==null?null:renew_time.toString());
	}

	/**
	 * 
	 * UTG:1:stRescind:{device_addr}:{rent_hash} 
	 */
	private void storageRescindTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);
		String device_addr = Constants.prefixAddress(params[0]);
		String rent_hash = Constants.prefixAddress(params[1]);
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false;
			log.warn("Cannot find storage by device_addr = " + device_addr);
		}
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if(rent==null){
			success = false;
			log.warn("Cannot find storage rent by hash=" + rent_hash);
		}else if (rent.getRentStatus() != RentStatus.breach.value()){
	//		success = false;
			log.warn("Storage rent " + rent_hash + " is not breach , status =" + rent.getRentStatus());
		}
		if(success){
			rent.setRentStatus(RentStatus.rescinding.value());			
			rent.setUpdatetime(new Date());
			storageRentMapper.updateRentStatus(rent);
		}
		if(space!=null && rent!=null){
			StorageRequest request = new StorageRequest();
			request.setReqhash(tx.getHash());
			request.setDeviceAddr(device_addr);
			request.setRentHash(rent_hash);
			request.setStorageid(space.getStorageid());
			request.setRentid(rent.getRentid());
		//	request.setRentAddr(rent.getRentAddr());
			request.setReqType(StorageOperator.stRescind.name());
			request.setReqNumber(tx.getBlockNumber());
			request.setReqSpace(rent.getRentSpace());
			request.setRentPrice(rent.getRentPrice());
			request.setRentTime(rent.getRentTime());
			request.setRentAmount(rent.getRentAmount());
			request.setReqStatus(success ? ReqStatus.success.value() : ReqStatus.failed.value());
			request.setPledgeStatus(success ? RentStatus.rescinding.value() : RentStatus.failed.value());
			request.setInstime(new Date());
			storageRequestMapper.insertRequest(request);
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);		
	//	tx.setParam3(rent_space);
	//	tx.setParam4(new BigDecimal(rent_price));
		tx.setParam5(rent_hash);
	//	tx.setParam6(rent_time.toString());
	}
	
	
	/**
	 * 
	 * UTG:1:stReValid:{device_addr}:{rent_hashs}:{free_space}:{file_roothash}:{file_vaildstore}
	 */
	private void storageReValidTransaction(Transaction tx, String[] params, Log txLog) {		
		boolean success = getStatus(tx, txLog);		
		byte[] bytes = Numeric.hexStringToByteArray(tx.getInput());
		String device_addr = Constants.prefixAddress(params[0]);
		String[] rent_hashs = params[1].split(",");
	//	String free_space = params[2];
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);		
		if (space == null){
			success = false;
			log.warn("Cannot find storage by device_addr=" + device_addr);
		}
		String hashs = "";
		for (int i = 0; i < rent_hashs.length; i++) {
			String rent_hash = Constants.prefixAddress(rent_hashs[i]);
			if (!"".equals(hashs))
				hashs += ",";
			hashs += rent_hash;
			StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
			if (rent == null){
				success = false;
				log.warn("Cannot find storage rent by hash=" + rent_hash);
			}
			if (success) {
			//	int new_rent_status = rent.getRentStatus() == RentStatus.rescinding.value() ? RentStatus.recycling.value() : RentStatus.breach.value();
			//	rent.setRentStatus(new_rent_status);
				rent.setRentStatus(RentStatus.expired.value());
				rent.setUpdatetime(new Date());
				storageRentMapper.updateRentStatus(rent);

			//	BigDecimal release_space = rent.getRentSpace();
			//	BigDecimal free_space = space.getFreeSpace();
			//	space.setFreeSpace(free_space.add(release_space));
				space.setRentNum(space.getRentNum()-1);
				space.setUpdatetime(new Date());
				storageSpaceMapper.updateRentSpace(space);
			}
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam3(new BigDecimal(bytes.length));
		tx.setParam2(device_addr);
		tx.setParam5(hashs);
	}
	
	/**
	 * 
	 * UTG:1:stProof:{device_addr}:{rent_hash}:{free_space}:{file_roothash}:{file_vaildstore}
	 */
	private void storageProofTransaction(Transaction tx, String[] params, Log txLog) {		
		boolean success = getStatus(tx, txLog);		
		byte[] bytes = Numeric.hexStringToByteArray(tx.getInput());		
		String device_addr = Constants.prefixAddress(params[0]);
	//	String rent_hash = Constants.prefixAddress(params[1]);
	//	String free_space = params[2];
		String logdata = txLog == null ? null : txLog.getData();
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);		
		if (space == null){
			success = false;
			log.warn("Cannot find storage by device_addr=" + device_addr);			
		}
		if(logdata==null || "".equals(logdata) || "0x".equals(logdata)){	
			if(success){
				space.setVaildTime(new Date());
				space.setVaildStatus(0);
				storageSpaceMapper.updateVaildStatus(space);
			}
		}else{
			String reqdata = params.length > 3 ? params[3] : "";
			String[] reqArray = reqdata.split("\\|");
			boolean allLeased = space==null ? false : space.getFreeSpace().compareTo(BigDecimal.ZERO)==0;
			boolean proofStorage = allLeased ||(reqArray.length>0 && reqArray[0].length() > 0) ;
			List<String> requestRentList = new ArrayList<String>();	
			if(reqArray.length>1){
				for(int i=1;i<reqArray.length;i++){
					String[] rentProofArray = reqArray[i].split(",");				
					if(rentProofArray.length>0){				
						requestRentList.add(rentProofArray[0]);
					}
				}
			}
			Map<String, String> verifyMap = new HashMap<String, String>();
			if(success){
				String proofStr = null;
				try {
					proofStr = new String(Numeric.hexStringToByteArray(logdata));
					String[] proofArray = proofStr.split(",");
					for (String proofItem : proofArray) {
						String[] itemArray = proofItem.split(":");
						verifyMap.put(itemArray[0].toLowerCase(), itemArray[1]);
					}
				} catch (Exception e) {
					success = false;
					log.warn("Parse proof data " + logdata + " proofStr "+ proofStr+" error:" ,e);
				}
			}		
			
			String verifiedRents = "";					
			if (success){
				device_addr = device_addr.toLowerCase();
			//	if(verifyMap.containsKey(device_addr) && "1".equals(verifyMap.get(device_addr))){
				if(proofStorage){				
					space.setVaildTime(new Date());
					space.setVaildStatus(0);
					storageSpaceMapper.updateVaildStatus(space);
				}
				for(String rent_hash : verifyMap.keySet()){					
					if(rent_hash.equalsIgnoreCase(device_addr) || !"1".equals(verifyMap.get(rent_hash)))
						continue;
					if (!"".equals(verifiedRents))
						verifiedRents += ",";
					verifiedRents += rent_hash;				
					StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
					if(rent==null){
						success = false;
						log.warn("Cannot find storage rent by hash=" + rent_hash);
					}else{
						rent.setVaildTime(new Date());
						rent.setVaildStatus(0);
						storageRentMapper.updateVaildStatus(rent);
					}
				}
			}
			tx.setParam4(proofStorage ? BigDecimal.ONE : BigDecimal.ZERO);
			tx.setParam5(verifiedRents);		
			tx.setParam6(String.join(",",requestRentList));
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
		tx.setParam3(new BigDecimal(bytes.length));
	}

	
	@Deprecated
	//UTG:1:stValid:{device_addr}	:{storevaild}
	private void storageVaildTransaction(Transaction tx, String[] params, Log txLog) {
	//	boolean success = getStatus(tx, txLog);
		boolean success = false;
		String device_addr = Constants.prefixAddress(params[0]);
		tx.setStatus(success ? 1: 0);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);		
	}
	/**
	 * 
	 * UTG:1:stCatchUp:{device_addr}
	 */
	private void storageCatchUpTransaction(Transaction tx, String[] params, Log txLog) {
		boolean success = getStatus(tx, txLog);
		String device_addr = Constants.prefixAddress(params[0]);
		BigDecimal prepledge_amount = null;
		if(log==null)
			success = false;
		else{
			prepledge_amount = new BigDecimal(Numeric.toBigInt(txLog.getData()));				
		}
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){
			success = false; 
			log.warn("Cannot find storage by device_addr=" + device_addr);
		}		
		if (success) {
			space.setPledgeAmount(prepledge_amount);
			space.setUpdatetime(new Date());
			storageSpaceMapper.updateBandwidthMakeup(space);
		}
		tx.setStatus(success ? 1: 0);
	//	tx.setError(error);
		tx.setParam1(success ? "succ" : "fail");
		tx.setParam2(device_addr);
		tx.setParam4(prepledge_amount);
	}
	/**
	 * 
	 */
	@Deprecated
	private void storageSetTransaction(Transaction tx, String[] params, Log txLog) {
	//	boolean success = getStatus(tx, txLog);
		boolean success = false;
		String type = params[0];
		String value = params[1];
		tx.setStatus(success ? 1: 0);
		tx.setParam1(type);
		tx.setParam2(value);		
	}
	
	//UTG:1:creFile:{device_addr}:{rent_hashs}:{free_space}:{free_roothash}:{free_storevaild}
	private void storageCreFileTransaction(Transaction tx, String[] params, Log txLog) {
		//TODO
	}

	//UTG:1:delFile:{device_addr}:{rent_hashs}:{free_space}:{free_roothash}:{free_storevaild}
	private void storageDelFileTransaction(Transaction tx, String[] params, Log txLog) {
		//TODO
	}
	private void storageEditMgaddrTransaction(Transaction tx, String[] params, Log txLog) {
		String deviceAddr=Constants.prefixAddress(params[0]);
		String managerAddr=Constants.prefixAddress(params[1]);
		StorageSpace space=new StorageSpace();
		space.setDeviceAddr(deviceAddr);
		space.setPledgeStatus(1);
		space.setManagerAddr(managerAddr);
		tx.setParam1(deviceAddr);
		tx.setParam2(managerAddr);
		storageSpaceMapper.updateSpaceNew(space);
	}
	
	private void storageStchPgTransaction(Transaction tx, String[] params, Log txLog) {
		String deviceAddr=Constants.prefixAddress(params[0]);
		BigDecimal amount=new BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(2)));
		StorageSpace dbSpace=storageSpaceMapper.getSpaceInfo(deviceAddr);
		tx.setParam1(deviceAddr);
		tx.setParam2(params[1]);
		if(dbSpace!=null) {
			StorageSpace space=new StorageSpace();
			space.setStorageid(dbSpace.getStorageid());
			BigDecimal managerAmount=dbSpace.getManagerAmount()==null?amount:amount.add(dbSpace.getManagerAmount());
			space.setManagerAmount(managerAmount);
			space.setManagerHeight(tx.getBlockNumber());
			BigDecimal havAmount=dbSpace.getHavAmount()==null?amount:amount.add(dbSpace.getHavAmount());
			space.setHavAmount(havAmount);
			storageSpaceMapper.updateSpaceNew(space);
			NodePledge pledge=new NodePledge();
			pledge.setBurntamount(BigDecimal.ZERO);
			pledge.setNode_address(deviceAddr);
			pledge.setPledge_address(Constants.prefixAddress(tx.getFromAddr()));
			pledge.setPledge_hash(Constants.prefixAddress(tx.getHash()));
			pledge.setPledge_number(tx.getBlockNumber());
			pledge.setPledge_amount(amount);
			pledge.setPledge_time(tx.getTimeStamp());
			pledge.setPledge_status(1);
			pledge.setNode_type("SN");
			nodePledgeDao.saveOrUpdateNodePledge(pledge);
		}
	}
	
	private void storageStwtrewardTransaction(Transaction tx, String[] params, Log txLog) {
		String deviceAddr=Constants.prefixAddress(params[0]);
		BigDecimal  etRate=new BigDecimal(params[1]);
		StorageSpace dbSpace=storageSpaceMapper.getSpaceInfo(deviceAddr);
		tx.setParam1(deviceAddr);
		tx.setParam2(params[1]);
		if(dbSpace!=null) {
			StorageSpace space=new StorageSpace();
			space.setStorageid(dbSpace.getStorageid());
			space.setEntrustRate(etRate.divide(BigDecimal.valueOf(100)));
			storageSpaceMapper.updateSpaceNew(space);
		}
	}
	private void storageSetspTransaction(Transaction tx, String[] params, Log txLog) {
		String deviceAddr=Constants.prefixAddress(params[0]);
		String hash=Constants.prefixAddress(params[1]);
		StorageSpace dbSpace=storageSpaceMapper.getSpaceInfo(deviceAddr);
		tx.setParam1(deviceAddr);
		tx.setParam2(params[1]);
		if(dbSpace!=null) {
			StorageSpace space=new StorageSpace();
			space.setStorageid(dbSpace.getStorageid());
			space.setSpHash(hash);
			space.setSpHeight(tx.getBlockNumber());
			space.setSpJointime(tx.getTimeStamp()==null?new Date():tx.getTimeStamp());
			storageSpaceMapper.updateSpaceNew(space);
			StoragePool dbSp=spDao.getPoolInfo(hash);
			if (dbSp!=null){
				StoragePool pool=new StoragePool();
				pool.setHash(hash);
				pool.setSnNum(dbSp.getSnNum()==null?1:dbSp.getSnNum()+1);
				BigDecimal  snCapacity=dbSpace.getDeclareSpace()==null?BigDecimal.ZERO:dbSpace.getDeclareSpace();
				BigDecimal useCapacity=dbSp.getUsedCapacity()==null?dbSpace.getCapacity():snCapacity.add(dbSp.getUsedCapacity());
				pool.setUsedCapacity(useCapacity);
				spDao.updatePool(pool);
			}
			if(dbSpace.getSpHash()!=null&&dbSpace.getSpHash().length()>0) {
				StoragePool pool=new StoragePool();
				pool.setHash(dbSpace.getSpHash());
				pool.setSnNum(dbSp.getSnNum()-1>=0?dbSp.getSnNum()-1:0);
				if(dbSp.getUsedCapacity()!=null) {
					BigDecimal  snCapacity=dbSpace.getDeclareSpace()==null?BigDecimal.ZERO:dbSpace.getDeclareSpace();
					if(dbSp.getUsedCapacity().compareTo(snCapacity)>=0) {
						pool.setUsedCapacity(dbSp.getUsedCapacity().subtract(snCapacity));
				   }else {
					   pool.setUsedCapacity(BigDecimal.ZERO);
				   }
				}
				spDao.updatePool(pool);
				
			}
		}
	}
	private void storageExitspTransaction(Transaction tx, String[] params, Log txLog) {
		String deviceAddr=Constants.prefixAddress(params[0]);
		StorageSpace dbSpace=storageSpaceMapper.getSpaceInfo(deviceAddr);
		tx.setParam1(deviceAddr);
		if(dbSpace!=null) {
			StorageSpace space=new StorageSpace();
			space.setStorageid(dbSpace.getStorageid());
			storageSpaceMapper.updateSpaceSpStatus(space);
			if(dbSpace.getSpHash()!=null) {
				StoragePool dbSp=spDao.getPoolInfo(dbSpace.getSpHash());
				if (dbSp!=null){
					StoragePool pool=new StoragePool();
					pool.setHash(dbSpace.getSpHash());
					pool.setSnNum((dbSp.getSnNum()-1)>=0?dbSp.getSnNum()-1:0);
					if(dbSp.getUsedCapacity()!=null &&dbSp.getUsedCapacity().compareTo(dbSpace.getCapacity())>=0) {
						BigDecimal useCapacity=dbSpace.getCapacity().add(dbSp.getUsedCapacity());
						pool.setUsedCapacity(useCapacity);
					}
					spDao.updatePool(pool);
				}
			}
		}
	}
	private void storageStreplaceTransaction(Transaction tx, String[] params, Log txLog) {
		
	}
    private void storageStwtpgTransaction(Transaction tx, String[] params, Log txLog) {
    	String deviceAddr=Constants.prefixAddress(params[0]);
    	BigDecimal pledgeAmount=new BigDecimal(params[1]);
    	tx.setParam1(deviceAddr);
    	tx.setParam2(params[1]);
    	StorageSpace dbSpace=storageSpaceMapper.getSpaceInfo(deviceAddr);
		if(dbSpace!=null) {
			StorageSpace space=new StorageSpace();
			space.setStorageid(dbSpace.getStorageid());
			BigDecimal havAmount=dbSpace.getHavAmount()==null?pledgeAmount:pledgeAmount.add(dbSpace.getHavAmount());
			space.setHavAmount(havAmount);
			storageSpaceMapper.updateSpaceNew(space);
			NodePledge pledge=new NodePledge();
			pledge.setBurntamount(BigDecimal.ZERO);
			pledge.setNode_address(deviceAddr);
			pledge.setPledge_address(Constants.prefixAddress(tx.getFromAddr()));
			pledge.setPledge_hash(Constants.prefixAddress(tx.getHash()));
			pledge.setPledge_number(tx.getBlockNumber());
			pledge.setPledge_amount(pledgeAmount);
			pledge.setPledge_time(tx.getTimeStamp());
			pledge.setPledge_status(1);
			pledge.setNode_type("SN");
			nodePledgeDao.saveOrUpdateNodePledge(pledge);
		}
	}
    private void storageWtfdTransaction(Transaction tx, String[] params, Log txLog) {
    	String deviceAddr=Constants.prefixAddress(params[0]);
    	String transferType=params[1];
    	String target=Constants.prefixAddress(params[2]);
    	tx.setParam1(deviceAddr);
    	tx.setParam2(transferType);
    	tx.setParam5(target);
    	BigDecimal lockAmount =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(1)));
    	BigDecimal pledgeAmount =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(2)));
    	BigDecimal fdAmount=lockAmount.add(pledgeAmount);
    	StorageSpace dbSpace=storageSpaceMapper.getSpaceInfo(deviceAddr);
		if(dbSpace!=null) {
			StorageSpace space=new StorageSpace();
			space.setStorageid(dbSpace.getStorageid());
			if (dbSpace.getHavAmount()!=null &&dbSpace.getHavAmount().compareTo(fdAmount)>0) {
				BigDecimal havAmount=dbSpace.getHavAmount()==null?BigDecimal.ZERO:dbSpace.getHavAmount().subtract(fdAmount);
				space.setHavAmount(havAmount);
			}
			storageSpaceMapper.updateSpaceNew(space);
			updateUNodepledge(tx);
			NodePledge pledge=new NodePledge();
			pledge.setNode_address(target);
			if("PoS".equals(transferType)) {
				UtgNodeMinerQueryForm param=new UtgNodeMinerQueryForm();
				param.setNode_address(target);
				UtgNodeMiner miner= nodePledgeDao.getNode(param);
				UtgNodeMiner upMiner=new UtgNodeMiner();
				upMiner.setNode_address(target);
				BigDecimal totalAmount=miner.getTotalamount()==null?pledgeAmount:miner.getTotalamount().add(pledgeAmount);
				upMiner.setTotal_amount(totalAmount);
				nodePledgeDao.updateNodeMiner(upMiner);
			}else if("SN".equals(transferType)) {
				pledge.setNode_address(target);
				StorageSpace targetSpace=storageSpaceMapper.getSpaceInfo(target);
				if (targetSpace!=null) {
					StorageSpace update=new StorageSpace();
					BigDecimal havAmount=targetSpace.getHavAmount()==null?pledgeAmount:targetSpace.getHavAmount().add(pledgeAmount);
					update.setHavAmount(havAmount);
					update.setStorageid(targetSpace.getStorageid());
					storageSpaceMapper.updateSpToSnTransfer(update);
				}
			}else if("SP".equals(transferType)) {
				StoragePool targeteDbSp=spDao.getPoolInfo(target);
				pledge.setSp_hash(target);
				if(targeteDbSp!=null) {
					BigDecimal afterAmount = targeteDbSp.getPledgeAmount() == null ?pledgeAmount
							: targeteDbSp.getPledgeAmount().add(pledgeAmount);
					 BigDecimal capacity=targeteDbSp.getTotalCapacity()==null?SpPoolService.getCapacity(pledgeAmount):targeteDbSp.getTotalCapacity().add(SpPoolService.getCapacity(pledgeAmount));
						
					StoragePool upsp = new StoragePool();
					upsp.setHash(target);
					upsp.setPledgeAmount(afterAmount);
					upsp.setHavAmount(afterAmount);
					upsp.setTotalCapacity(capacity);
					spDao.updatePool(upsp);
				}
			}
			pledge.setPledge_address(Constants.prefixAddress(tx.getFromAddr()));
			pledge.setBurntamount(BigDecimal.ZERO);
			pledge.setPledge_hash(Constants.prefixAddress(tx.getHash()));
			pledge.setPledge_number(tx.getBlockNumber());
			pledge.setPledge_amount(pledgeAmount);
			pledge.setPledge_time(tx.getTimeStamp());
			pledge.setPledge_status(1);
			pledge.setNode_type(transferType);
			nodePledgeDao.saveOrUpdateNodePledge(pledge);
		}
	}
    private void storageWtpgexitTransaction(Transaction tx, String[] params, Log txLog) {
    	String deviceAddr=Constants.prefixAddress(params[0]);
    	String pledgeHash=Constants.prefixAddress(params[1]);
    	BigDecimal exitAmount =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(2)));
    	tx.setParam1(deviceAddr);
    	tx.setParam2(pledgeHash);
    	StorageSpace dbSpace=storageSpaceMapper.getSpaceInfo(deviceAddr);
		if(dbSpace!=null) {
			StorageSpace space=new StorageSpace();
			space.setStorageid(dbSpace.getStorageid());
			if (dbSpace.getHavAmount()!=null &&dbSpace.getHavAmount().compareTo(exitAmount)>0) {
				BigDecimal havAmount=dbSpace.getHavAmount()==null?BigDecimal.ZERO:dbSpace.getHavAmount().subtract(exitAmount);
				space.setHavAmount(havAmount);
			}
			storageSpaceMapper.updateSpaceNew(space);
			NodePledge upledge=new NodePledge();
			upledge.setPledge_hash(pledgeHash);
			upledge.setPledge_address(Constants.prefixAddress(tx.getFromAddr()));
			upledge.setPledge_status(0);
			upledge.setUnpledge_type(0);
			upledge.setUnpledge_hash(Constants.prefixAddress(tx.getHash()));
			upledge.setUnpledge_number(tx.getBlockNumber());
			upledge.setUnpledge_time(tx.getTimeStamp());
			nodePledgeDao.updateNodePledgeExit(upledge);
		}
    }
    
    
    public  void updateUNodepledge(Transaction tx) {
		NodePledge upledge=new NodePledge();
		upledge.setPledge_address(Constants.prefixAddress(tx.getFromAddr()));
		upledge.setPledge_status(0);
		upledge.setUnpledge_type(0);
		upledge.setUnpledge_hash(Constants.prefixAddress(tx.getHash()));
		upledge.setUnpledge_number(tx.getBlockNumber());
		upledge.setUnpledge_time(tx.getTimeStamp());
		nodePledgeDao.updateNodePledgeTransfer(upledge);
	}
	/**
	 * 
	 * @param blockNumber
	 * @param dayOneNumber
	 * @param web3j
	 */
	@SuppressWarnings("unchecked")
//	@Async
//	@Transactional
	public void updateStoragePledgeStatus(Long blockNumber,Map<Long,BigInteger> blockTimeMap) {
		TimeSpend timeSpend = new TimeSpend();		
		Map<String, Map<String, Object>> spledge = web3jService.getSPledge(blockNumber);
		if(spledge==null)
			return;
		Map<String, Map<String,Object>> spaceMap = new HashMap<String, Map<String,Object>>();
		Map<String, Map<String,Object>> rentMap = new HashMap<String, Map<String,Object>>();
		Map<String, Map<String,Object>> entrustMap = new HashMap<String, Map<String,Object>>();
		Map<String,StoragePool>  spNumMap=new HashMap<>();
		List<StorageSpace> ratioList=new ArrayList<>();
		String utgv2number=gcfmDao.getTypeValue("utgv2number");
		long effectNumber=utgv2number==null?0:Long.parseLong(utgv2number); 
		for (String device_addr : spledge.keySet()) {
			Map<String, Object> pledgeData = spledge.get(device_addr);						
			Map<String, Object> spaceData = new HashMap<String,Object>();
			Integer pledgeStatus = (Integer) pledgeData.get("pledgeStatus");
			spaceData.put("pledge_status", pledgeStatus);
			Long vaildNumber = null;
			Long succNumber = null;
			if(pledgeData.containsKey("lastverificationtime")){
				vaildNumber = Long.parseLong(pledgeData.get("lastverificationtime").toString());
				spaceData.put("valid_number", vaildNumber);
			}
			if(pledgeData.containsKey("lastverificationsuccesstime")){
				succNumber = Long.parseLong(pledgeData.get("lastverificationsuccesstime").toString());
				spaceData.put("succ_number", succNumber);
			}
			if(pledgeData.containsKey("validationfailuretotaltime")){
				Integer validationfailuretotaltime = (Integer) pledgeData.get("validationfailuretotaltime");
				spaceData.put("fail_count", validationfailuretotaltime);
			}
			if(pledgeData.containsKey("storagecapacity")){		
				BigDecimal storagecapacity = new BigDecimal(pledgeData.get("storagecapacity").toString());
				spaceData.put("free_space", storagecapacity);
			}
			if(pledgeData.containsKey("manager")){	
				String manager=pledgeData.get("manager")==null?null:pledgeData.get("manager").toString();
				if(manager!=null && !manager.contains("00000000000000000000000")) {
					spaceData.put("manager", pledgeData.get("manager").toString());
				}
			}
			if(pledgeData.containsKey("sphash")){		
				String sphash=pledgeData.get("sphash")==null?null:pledgeData.get("sphash").toString();
				if(sphash.contains("00000000000000000000000000000000000000000000000000000000000")) {
					sphash=null;
				}
				spaceData.put("sphash", sphash);
				if(sphash!=null && pledgeStatus!=null &&pledgeStatus.intValue()==0) {
					StoragePool spNum= spNumMap.get(sphash);
					if(spNum==null) {
						spNum=new StoragePool();
						spNum.setSnNum(1);
						spNum.setHash(sphash);
						spNumMap.put(sphash,spNum);
					}else {
						spNum.setSnNum(spNum.getSnNum()==null?1:spNum.getSnNum()+1);
					}
					
				}
			}	
			if(pledgeData.containsKey("spheight")){		
				spaceData.put("spheight", pledgeData.get("spheight")==null?null:pledgeData.get("spheight").toString());
			}
			if(pledgeData.containsKey("entrustRate")){	
				BigDecimal entrustRate=pledgeData.get("entrustRate")==null?BigDecimal.ZERO:new  BigDecimal(pledgeData.get("entrustRate").toString());
				spaceData.put("entrustRate",entrustRate.divide(BigDecimal.valueOf(100)));
			}
			if(pledgeData.containsKey("managerAmount")){		
				spaceData.put("managerAmount",pledgeData.get("managerAmount")==null?BigDecimal.ZERO:new BigDecimal( pledgeData.get("managerAmount").toString()));
			}
			if(pledgeData.containsKey("managerheight")){		
				spaceData.put("managerheight",pledgeData.get("managerheight")==null?BigDecimal.ZERO: new BigDecimal( pledgeData.get("managerheight").toString()));
			}
			if(pledgeData.containsKey("havAmount")){		
				spaceData.put("havAmount",pledgeData.get("havAmount")==null?BigDecimal.ZERO:new BigDecimal( pledgeData.get("havAmount").toString()));
			}
			
		//	int vaildStatus = (vaildNumber!=null && succNumber!=null && succNumber>=vaildNumber) ? 0 : 1;						
		//	spaceData.put("vaild_status", vaildStatus);			
			if(succNumber!=null && succNumber!=0){
				int failDays = (int) ((blockNumber - succNumber) / dayOneNumber) -1;
				if(failDays<0)
					failDays = 0;
				spaceData.put("fail_days", failDays);
			}
			spaceMap.put(device_addr, spaceData);
			
			long rentNum = 0;
			Map<String, Object> leaseMap = (Map<String, Object>) pledgeData.get("lease");			
			if (leaseMap != null) {				
				for (String rent_hash : leaseMap.keySet()) {					
					Map<String, Object> leaseData = (Map<String, Object>) leaseMap.get(rent_hash);	
					Map<String, Object> rentData = new HashMap<String,Object>();
					Integer status = (Integer) leaseData.get("status");
					rentData.put("rent_status", status);
					vaildNumber = null;
					succNumber = null;
					Integer renewStatus = null;	
					String renewReqhash = null;
					if(leaseData.containsKey("lastverificationtime")){
						vaildNumber = Long.parseLong(leaseData.get("lastverificationtime").toString());
						rentData.put("valid_number", vaildNumber);
					}
					if(leaseData.containsKey("lastverificationsuccesstime")){
						succNumber = Long.parseLong(leaseData.get("lastverificationsuccesstime").toString());
						rentData.put("succ_number", succNumber);
					}
					if(leaseData.containsKey("validationfailuretotaltime")){
						Integer validationfailuretotaltime = (Integer) leaseData.get("validationfailuretotaltime");
						rentData.put("fail_count", validationfailuretotaltime);
					}					
					if(leaseData.containsKey("leaselist")){
						Map<String,Object> leaselist = (Map<String,Object>) leaseData.get("leaselist");
						List<String> renewList = new ArrayList<String>();
						for(String hash : leaselist.keySet()){
							Map<String,Object> hashdata = (Map<String, Object>) leaselist.get(hash);
							if(hashdata.containsKey("deposit")){								
								BigDecimal deposit = new BigDecimal(hashdata.get("deposit").toString());
								if(hash.equals(rent_hash)){
									rentData.put("pledge_amount", deposit);
									continue;
								}
								if(deposit.compareTo(BigDecimal.ZERO)==0){
									renewStatus = 1;
									renewReqhash = hash;
									renewList.add(hash);
								}								
							}							
						}
						rentData.put("renewList", renewList);
					}
					if (status == RentStatus.expired.value() || status == RentStatus.unaccepted.value() || status == RentStatus.failed.value()) {
						rentData.put("renew_status", null);
						rentData.put("renew_reqhash", null);
						rentData.put("renewList", new ArrayList<String>());
					}else if(status != RentStatus.applying.value()){
						rentData.put("renew_status", renewStatus);
						rentData.put("renew_reqhash", renewReqhash);
						rentNum ++;
					}
				//	vaildStatus = (vaildNumber!=null && succNumber!=null && succNumber>=vaildNumber) ? 0 : 1;					
				//	rentData.put("vaild_status", vaildStatus);					
					if(succNumber!=null && succNumber!=0){
						int failDays = (int) ((blockNumber - succNumber) / dayOneNumber) -1;
						if(failDays<0)
							failDays = 0;
						rentData.put("fail_days", failDays);
					}
					rentMap.put(rent_hash, rentData);
				}
				spaceData.put("rent_num", rentNum);
			}
			Map<String, Map<String, Object>>  tmEntrustMap = (Map<String, Map<String, Object>>) pledgeData.get("entrustDetail");
			if(tmEntrustMap!=null&&tmEntrustMap.size()>0) {
				for(String ethash:tmEntrustMap.keySet()) {
					Map<String, Object>  etMap= tmEntrustMap.get(ethash);
					etMap.put("snAddr", device_addr);
					entrustMap.put(ethash, etMap);
				}
				
			}
		}
		
	//	long currentNumber = blockNumber - lockreleaseinterval - rewardcollectdelay;
		long currentDays = blockNumber / dayOneNumber;
		List<StorageSpace> spaceList = storageSpaceMapper.getList(null,null,null);
		List<StorageSpace> updateSpaces = new ArrayList<>();
		Set<String> existAddrs = new HashSet<String>();
		for (StorageSpace space : spaceList) {
			String device_addr = space.getDeviceAddr();
			if(existAddrs.contains(device_addr)){
				if(space.getPledgeStatus()!=PledgeStatus.deleted.value() && space.getPledgeStatus()!=PledgeStatus.failed.value()){
					log.warn("Update exist storage space device_addr="+device_addr+" id="+space.getStorageid()+" from "+space.getPledgeStatus()+" to delete status ");
					space.setPledgeStatus(PledgeStatus.deleted.value());
					//deleted space
					space.setUpdatetime(new Date());
				//	updateSpaces.add(space);
					storageSpaceMapper.updatePledgeStatus(space);
				}
				continue;
			}				
			existAddrs.add(device_addr);
			Map<String, Object> spaceData = spaceMap.get(device_addr);
			if(spaceData==null){					
				if(space.getPledgeStatus()==PledgeStatus.deleted.value() || space.getPledgeStatus()==PledgeStatus.failed.value())
					continue;
				space.setPledgeStatus(PledgeStatus.deleted.value());		//deleted space
				space.setUpdatetime(new Date());
				storageSpaceMapper.updatePledgeStatus(space);
				log.info("Update storage space "+device_addr+" to delete status");
			}else{
				if(effectNumber<blockNumber &&(space.getSpHash()==null ||space.getSpHash().length()==0)) {
					StorageSpace ratioSpace=new StorageSpace();
					ratioSpace.setStorageid(space.getStorageid());
					ratioSpace.setDeclareSpace(space.getDeclareSpace());
					ratioSpace.setRatio(space.getRatio());
					ratioList.add(ratioSpace);
			    }
				Long valid_number = (Long) spaceData.get("valid_number");
				Long succ_number = (Long) spaceData.get("succ_number");				
				Integer pledge_status = (Integer) spaceData.get("pledge_status");
				BigDecimal free_space = (BigDecimal) spaceData.get("free_space");
				Integer fail_count = (Integer) spaceData.get("fail_count");
				Integer fail_days = (Integer) spaceData.get("fail_days");
				Long rent_num = (Long) spaceData.get("rent_num");
				String manager= spaceData.get("manager")==null?null:spaceData.get("manager").toString();
				String sphash= spaceData.get("sphash")==null?null:spaceData.get("sphash").toString();
				Long spheight= spaceData.get("sphash")==null?null:Long.valueOf(spaceData.get("spheight").toString());
				BigDecimal entrustRate =spaceData.get("entrustRate")==null?null: (BigDecimal) spaceData.get("entrustRate");
				BigDecimal managerAmount =spaceData.get("managerAmount")==null?null: (BigDecimal) spaceData.get("managerAmount");
				Long managerheight =spaceData.get("managerheight")==null?null: Long.valueOf(spaceData.get("managerheight").toString());
				BigDecimal havAmount =spaceData.get("havAmount")==null?null: (BigDecimal) spaceData.get("havAmount");
				
				Integer vaild24Status = null;
				if(pledge_status!=null && pledge_status==0){				
					if (valid_number != null && valid_number != 0 && succ_number != null && succ_number != 0) {
						long vaildDays = valid_number / dayOneNumber;				
						Long pledgeNumber = space.getPledgeNumber();	
						if (currentDays - vaildDays <= 1 && pledgeNumber != null && !pledgeNumber.equals(valid_number)) {
							vaild24Status = valid_number.equals(succ_number) ? 1 : 0;			
						}
					}
			//		space.setVaild24Status(vaild24Status);
				}				
				if (!(isEquals(space.getPledgeStatus(), pledge_status) && isEquals(space.getFreeSpace(), free_space) && isEquals(space.getValidNumber(), valid_number) 
						&& isEquals(space.getSuccNumber(), succ_number) && isEquals(space.getFailCount(), fail_count) && isEquals(space.getRentNum(), rent_num)
						&& isEquals(space.getVaild24Status(), vaild24Status))){					
					log.info("Update storage space id="+space.getStorageid()+", device_addr="+space.getDeviceAddr()+",pledge_status="+space.getPledgeStatus()+"->"+pledge_status
							+",free_space="+space.getFreeSpace()+"->"+free_space+",valid_number="+space.getValidNumber()+"->"+valid_number+",succ_number="+space.getSuccNumber()+"->"+succ_number
							+",fail_count="+space.getFailCount()+"->"+fail_count+",rent_num="+space.getRentNum()+"->"+rent_num+",vaild24Status="+space.getVaild24Status()+"->"+vaild24Status);				
					space.setPledgeStatus(pledge_status);
				//	if(spaceData.containsKey("free_space"))
					space.setFreeSpace(free_space);
					space.setValidNumber(valid_number);
					space.setSuccNumber(succ_number);
					space.setFailCount(fail_count);
					space.setFailDays(fail_days);
					space.setRentNum(rent_num);
					space.setVaild24Status(vaild24Status);
					space.setManagerAddr(manager);
					space.setEntrustRate(entrustRate);
					space.setSpHash(sphash);
					space.setSpHeight(spheight);
					space.setManagerAmount(managerAmount);
					space.setManagerHeight(managerheight);
					space.setHavAmount(havAmount);
				//	space.setVaildStatus((Integer) spaceData.get("vaild_status"));				
				//	space.setUpdatetime(new Date());
				//	storageSpaceMapper.updateVaildNumber(space);
					updateSpaces.add(space);
				}
			}
		}
		if (updateSpaces.size() > 0){			
			storageSpaceMapper.batchUpdatePledge(updateSpaces);
		//	log.info("Update storage space list:"+updateSpaces);
		}

		List<StorageRent> rentList = storageRentMapper.getList(null, null, null);
		List<StorageRent> updateRents = new ArrayList<>();
		for (StorageRent rent : rentList) {
			String rent_hash = rent.getRentHash();
		//	int rent_status = rent.getRentStatus();
			Map<String, Object> rentData = rentMap.get(rent_hash);
			if(rentData==null){	
				if(rent.getRentNumber()!=null && blockNumber - rent.getRentNumber() <lockreleaseinterval)	
					continue;
				if(rent.getRentStatus()==RentStatus.expired.value() || rent.getRentStatus()==RentStatus.unaccepted.value() || rent.getRentStatus()==RentStatus.failed.value())
					continue;
				if(rent.getRentStatus()==RentStatus.applying.value()){
					rent.setRentStatus(RentStatus.unaccepted.value());
					rent.setUpdatetime(new Date());
					updateRents.add(rent);
					storageRequestMapper.cleanRentRequest(rent_hash);
					log.info("Rent "+rent_hash+" is applying ,rent_number="+rent.getRentNumber()+",blockNumber="+blockNumber);
				}else{
					rent.setRentStatus(RentStatus.expired.value());
					rent.setRenewStatus(null);
					rent.setRenewReqhash(null);
					rent.setUpdatetime(new Date());
					updateRents.add(rent);
					storageRequestMapper.cleanRentRequest(rent_hash);
					log.info("Update missing storage rent "+rent_hash+" to delete status");
				}
			//	rent.setUpdatetime(new Date());				
			}else{		
				Long valid_number = (Long) rentData.get("valid_number");
				Long succ_number = (Long) rentData.get("succ_number");				
				Integer rent_status = (Integer) rentData.get("rent_status");
				BigDecimal pledge_amount = (BigDecimal) rentData.get("pledge_amount");
				Integer fail_count = (Integer) rentData.get("fail_count");
				Integer fail_days = (Integer) rentData.get("fail_days");
				Integer renew_status = (Integer) rentData.get("renew_status");
				String renew_reqhash = (String) rentData.get("renew_reqhash");
				List<String> renewList = (List<String>) rentData.get("renewList");
				Integer vaild24Status = null;
				if(rent_status!=null && rent_status==1){					
					if (valid_number != null && valid_number != 0 && succ_number != null && succ_number != 0) {
						long vaildDays = valid_number / dayOneNumber;					
						Long pledgeNumber = rent.getRentNumber();	
						if (currentDays - vaildDays <= 1 && pledgeNumber != null && !pledgeNumber.equals(valid_number)) {
							vaild24Status = valid_number.equals(succ_number) ? 1 : 0;				
						}
					}							
				}				
				
				if (!(isEquals(rent.getRentStatus(), rent_status) && isEquals(rent.getPledgeAmount(), pledge_amount) && isEquals(rent.getValidNumber(), valid_number) 
						&& isEquals(rent.getSuccNumber(), succ_number) && isEquals(rent.getFailCount(), fail_count) && isEquals(rent.getRenewStatus(), renew_status) 
						&& isEquals(rent.getRenewReqhash(), renew_reqhash) && isEquals(rent.getVaild24Status(), vaild24Status))){
					log.info("Update storage rent id="+rent.getRentid()+", rent_hash="+rent.getRentHash()+",rent_status="+rent.getRentStatus()+"->"+rent_status
							+",pledge_amount="+rent.getPledgeAmount()+"->"+pledge_amount+",valid_number="+rent.getValidNumber()+"->"+valid_number
							+",succ_number="+rent.getSuccNumber()+"->"+succ_number+",fail_count="+rent.getFailCount()+"->"+fail_count+",renew_status="+rent.getRenewStatus()
							+"->"+renew_status+",renew_reqhash="+rent.getRenewReqhash()+"->"+renew_reqhash+",vaild24Status="+rent.getVaild24Status()+"->"+vaild24Status);
					rent.setRentStatus(rent_status);
					rent.setPledgeAmount(pledge_amount);
					rent.setValidNumber(valid_number);
					rent.setSuccNumber(succ_number);
					rent.setFailCount(fail_count);			
					rent.setFailDays(fail_days);				
					rent.setRenewStatus(renew_status);
					rent.setRenewReqhash(renew_reqhash);
					rent.setVaild24Status(vaild24Status);	
				//	rent.setVaildStatus((Integer) rentData.get("vaild_status"));	
				//	rent.setUpdatetime(new Date());					
				//	storageRentMapper.updateVaildNumber(rent);
					updateRents.add(rent);	
				}	
						
			//	if(rentData.containsKey("renewList")){
				if(renewList!=null && renewList.size()>0){
					log.info("Update storage renew rentid="+rent.getRentid()+", rent_hash="+rent.getRentHash()+",renewList="+renewList);
					storageRequestMapper.updateRenewStatus(rent_hash,renewList);
//					List<StorageRequest> expiredList = storageRequestMapper.getExpiredRenewRequest(rent_hash, renewList);
//					for (StorageRequest renew : expiredList) {						
//						renew.setReqStatus(ReqStatus.expired.value());
//						renew.setPledgeStatus(RentStatus.unaccepted.value());
//						storageRequestMapper.updateRequest(renew);
//						log.info("Update storage renew expired reqid="+renew.getReqid()+",reqhash="+renew.getReqhash()+",rentid="+renew.getRentid()+", rent_hash="+renew.getRentHash());						
//					}
				}
			}
		}
		if (updateRents.size() > 0){
			storageRentMapper.batchUpdatePledge(updateRents);
		//	log.info("Update storage rent list:"+updateRents);
		}	
		
		if(entrustMap.size()>0) {
			UtgNodeMinerQueryForm param=new UtgNodeMinerQueryForm();
			param.setEtType("SN");
			param.setPledge_status(1);
			List<NodePledge>  nodePledgeList=nodePledgeDao.getNodePledgeListCache(param);
			Map<String,NodePledge>  nodePledgeMap=new HashMap<>();
			if(nodePledgeList!=null) {
				for(NodePledge pledge:nodePledgeList) {
					nodePledgeMap.put(pledge.getPledge_hash(),pledge);
				}
			}
			for(String pledgeHash:entrustMap.keySet()) {
				Map<String,Object> itemMap=entrustMap.get(pledgeHash);
				Long height=itemMap.get("height")==null?null: Long.valueOf(itemMap.get("height").toString());
				BigDecimal amount=itemMap.get("amount")==null?null:new BigDecimal( itemMap.get("amount").toString());
				NodePledge pledge= nodePledgeMap.remove(pledgeHash);
				if(pledge==null) {
					pledge=new NodePledge();
					pledge.setBurntamount(BigDecimal.ZERO);
					pledge.setNode_address(itemMap.get("snAddr").toString());
					pledge.setPledge_address(itemMap.get("address").toString());
					pledge.setPledge_hash(pledgeHash);
					pledge.setPledge_number(height);
					pledge.setPledge_amount(amount==null?BigDecimal.ZERO:amount);
					BigInteger pledgeTime= blockTimeMap.get(pledge.getPledge_number().longValue());
					if(pledgeTime!=null) {
						pledge.setPledge_time(new Date(pledgeTime.longValue()*1000));
					}
					pledge.setPledge_status(1);
					pledge.setNode_type("SN");
					nodePledgeDao.saveOrUpdateNodePledge(pledge);
				}else {
					if(pledge.getPledge_status().intValue()!=1 || (amount!=null&&amount.compareTo(pledge.getPledge_amount())!=0)) {
						pledge.setPledge_status(1);
						pledge.setPledge_amount(amount);
						nodePledgeDao.updateNodePledgeBySpace(pledge);
					}
				}
			}
			if(nodePledgeMap.size()>0) {
				BigInteger pledgeTime= blockTimeMap.get(blockNumber.longValue());
				Date time=null;
				if(pledgeTime!=null) {
					time=new Date(pledgeTime.longValue()*1000);
				}
				for(NodePledge delPledge: nodePledgeMap.values()) {
					NodePledge upledge=new NodePledge();
					upledge.setPledge_address(delPledge.getPledge_address());
					upledge.setId(delPledge.getId());
					upledge.setPledge_status(0);
					upledge.setUnpledge_type(0);
					upledge.setUnpledge_hash("");
					upledge.setUnpledge_number(blockNumber);
					
					upledge.setUnpledge_time(time);
					nodePledgeDao.updateNodePledgeTransfer(upledge);
				}
				
			}
			
		}
		if(spNumMap.size()>0) {
			spDao. batchUpdateSpSnNum(spNumMap.values());
		}
		log.info(effectNumber+" storage space ratioList size"+ratioList.size());
		if(ratioList.size()>0) {
			List<StorageSpace> updateRatioList=new ArrayList<>();
			for(StorageSpace ratioSpace:ratioList) {
				BigDecimal  ratio=calStorageNewRatio(ratioSpace.getDeclareSpace());
				if(ratioSpace.getRatio()==null ||(ratio!=null&&ratio.compareTo(ratioSpace.getRatio())!=0)) {
					ratioSpace.setRatio(ratio);
					updateRatioList.add(ratioSpace);
				}
				if(updateRatioList.size()>500) {
					storageSpaceMapper.batchUpdateSpaceRatio(updateRatioList);
					updateRatioList.clear();
				}
			}
			if(updateRatioList.size()>0) {
				storageSpaceMapper.batchUpdateSpaceRatio(updateRatioList);
			}
		}
		log.info("Update storage pledge status at "+blockNumber+" update "+updateSpaces.size()+" space and "+updateRents.size()+" rent, spend " + timeSpend.getSpendTime());
	}
	private BigDecimal calStorageNewRatio(BigDecimal calCapacity ) {
		if(calCapacity==null) {
			return BigDecimal.ZERO;
		}
	    if (calCapacity.compareTo(eb1b) >0) {
	        calCapacity=eb1b;
	    }
	    BigDecimal log2Value =new BigDecimal("0.3010299956639812");
	    BigDecimal storageRatio =BigDecimal.ZERO;
	    if (calCapacity.compareTo(tb1b) >0) {
	    	BigDecimal tbcapity = calCapacity.divide(tb1b,10,RoundingMode.HALF_UP);
	    	BigDecimal logval=BigDecimal.valueOf(Math.log10(tbcapity.doubleValue()));
	        storageRatio =logval.divide(log2Value,10,RoundingMode.HALF_UP);
	    }
	    storageRatio = storageRatio.divide(storageRewardGainRatio,10,RoundingMode.HALF_UP).add(storageRewardAdjRatio.divide(BigDecimal.valueOf((10000)),10,RoundingMode.HALF_UP));
	    return  storageRatio.setScale(6, RoundingMode.HALF_UP);
	
	}
	private static boolean isEquals(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null)
			return true;
		else if ((obj1 == null && obj2 != null) || (obj1 != null && obj2 == null) )
			return false;
		return obj1.equals(obj2);
	}
	

	public void updateStorageVaildProgress(Long blockNumber) {
		TimeSpend timeSpend = new TimeSpend();
		Map<String, Object> data = web3jService.getSPledgeCapVer(blockNumber);
		if(data==null)
			return;
		List<StorageSpace> spaceList = storageSpaceMapper.getList(null,null,null);
		Set<String> existAddrs = new HashSet<String>();
		int count = 0;
		for (StorageSpace space : spaceList) {
			String device_addr = space.getDeviceAddr();
			Object value = data.get(device_addr);
			if(existAddrs.contains(device_addr) || value ==null)				
				continue;				
			existAddrs.add(device_addr);			
			BigDecimal progress = new BigDecimal(value.toString());
			if(isEquals(space.getVaildProgress(), progress))
				continue;
			space.setVaildProgress(progress);				
			storageSpaceMapper.updateVaildProgress(space);
			count ++;
			log.info("Update storage space "+space.getDeviceAddr()+" vaild progress to "+progress);					
		}		
		log.info("Update storage vaild progress at "+blockNumber+" data size " + count +",spend "+timeSpend.getSpendTime());
	}
		

	@SuppressWarnings("unchecked")
//	@Async
	public void updateStorageRevenue(Long blockNumber){
		TimeSpend timeSpend = new TimeSpend();
		Map<String, Object> data = web3jService.getStorageRatios(blockNumber);
		if(data==null)
			return;
		List<StorageRevenue> revenueList = storageRevenueMapper.getAllList();
		Map<String,StorageRevenue> revenueMap = new HashMap<String,StorageRevenue>();
		for(StorageRevenue revenue : revenueList){
			String revenue_addr = revenue.getRevenueAddr().toLowerCase();
			revenueMap.put(revenue_addr, revenue);
		}			
		for(String revenue_address : data.keySet()){
			Map<String,Object> revenue_value = (Map<String, Object>) data.get(revenue_address);
			BigDecimal ratio = revenue_value.containsKey("ratio") ? new BigDecimal(revenue_value.get("ratio").toString()) : null;
			BigDecimal capacity = revenue_value.containsKey("capacity") ? new BigDecimal(revenue_value.get("capacity").toString()) : null;
			StorageRevenue revenue = revenueMap.get(revenue_address.toLowerCase());
			if(revenue==null)
				revenue = new StorageRevenue();			
			revenue.setRevenueAddr(revenue_address.toLowerCase());
			revenue.setCapacity(capacity);
			revenue.setRatio(ratio);
			revenue.setUpdatetime(new Date());
		//	updateOrSaveStorageRevenue(revenue);
			if(revenue.getRevenueid()==null)
				storageRevenueMapper.insertSelective(revenue);
			else
				storageRevenueMapper.updateByPrimaryKeySelective(revenue);
		}
		log.info("Update storage revenue at "+blockNumber+" data size " + data.size()+",spend "+timeSpend.getSpendTime());
	}
	
	public void updateRevenueAmount(Long blocknumber){
		TimeSpend timeSpend = new TimeSpend();
		storageRevenueMapper.updateRevenueAmount();
    	log.info("Update revenue reward amount at "+blocknumber+" spend "+timeSpend.getSpendTime());
	}
	
	public void cleanVaildStatus(Long blockNumber){
		storageSpaceMapper.cleanVaildStatus();
		storageRentMapper.cleanVaildStatus();
		log.info("Clean storage vaild status at "+blockNumber);
	}
		

	public void saveSpaceStatistic(Long blocknumber,Date blocktime){
		Date sttime = new Date();
		try{
			TimeSpend timeSpend = new TimeSpend();
		//	storageStatMapper.saveSpaceStat(blocknumber, blocktime, sttime);		
			List<StorageSpace> list = storageSpaceMapper.getList(null, null, null);
			Map<String,StorageSpace> map = new HashMap<>();
			List<StorageSpaceStat> statList = new ArrayList<>();		
			for (StorageSpace space : list) {
				String deviceAddr  = space.getDeviceAddr();
				if(map.containsKey(deviceAddr))
					continue;
				map.put(deviceAddr, space);			
				StorageSpaceStat item = new StorageSpaceStat();
				item.setStorageid(space.getStorageid());
				item.setBlocknumber(blocknumber);
				item.setBlocktime(blocktime);
				item.setSttime(sttime);
				item.setDevice_addr(space.getDeviceAddr());
				item.setRevenue_addr(space.getRevenueAddr());
				item.setPledge_status(space.getPledgeStatus());			
				item.setRent_space(space.getDeclareSpace().subtract(space.getFreeSpace()));
				item.setRent_price(space.getRentPrice());
				item.setRent_num(space.getRentNum());
				item.setBw_size(space.getBwSize());
				item.setBw_ratio(space.getBwRatio());
				item.setVaild_status(space.getVaildStatus());
				item.setVaild_progress(space.getVaildProgress());
				statList.add(item);			
			}
			if(statList.size()>0)
				storageStatMapper.insertSpaceStatBatch(statList);
			log.info("Save space statistic at "+blocknumber+" spend "+timeSpend.getSpendTime());
		}catch(Exception e){
			log.warn("Save space statistic at "+blocknumber+" error:",e);
		}		
	}
	
	
	public void updateStorageAmount(Long blocknumber){
		TimeSpend timeSpend = new TimeSpend();
    	/*List<Map<String,Object>> list = storageSpaceMapper.getAmountStat();
    	log.info("get storage amount stat at "+blocknumber+" size "+list.size()+" spend "+timeSpend.getSpendTime());
    	Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
    	for(Map<String,Object> record : list){
    		Long storageid = (Long) record.get("storageid");
    		String device_addr = (String) record.get("device_addr");
    		if(map.containsKey(device_addr))
    			continue;
    		else
    			map.put(device_addr, record);    		
    		BigDecimal storageamount = record.get("storageamount")!=null ? new BigDecimal(record.get("storageamount").toString()) : BigDecimal.ZERO;
    		BigDecimal rentamount = record.get("rentamount")!=null ? new BigDecimal(record.get("rentamount").toString()) : BigDecimal.ZERO;    		
    		StorageSpace space = new StorageSpace();
    		space.setStorageid(storageid);
    		space.setStorageAmount(storageamount);
    		
    		space.setRentAmount(rentamount);
    		
    		space.setTotalAmount(storageamount.add(rentamount));
    		storageSpaceMapper.updateAmount(space);
    	}*/
		storageSpaceMapper.updateStorageAmount();
    	log.info("Update storage reward amount at "+blocknumber+" spend "+timeSpend.getSpendTime());
    }
	
	public void updateSpaceRewardStat(Long blocknumber){
		try{
			TimeSpend timeSpend = new TimeSpend();
			storageStatMapper.updateSpaceRewardStat(blocknumber);
			log.info("Update space reward stat at "+blocknumber+" spend "+timeSpend.getSpendTime());
		}catch(Exception e){
			log.warn("Update space reward stat at "+blocknumber+" error:",e);
		}
	}
	

	public void saveRevenueStat(long blocknumber,Date blocktime){
		Date sttime = new Date();
		try{
			TimeSpend timeSpend = new TimeSpend();
			Map<String,Object> params = new HashMap<>();
			params.put("blocknumber", blocknumber);
			params.put("blocktime", blocktime);
			params.put("sttime", sttime);			
		//	storageStatMapper.saveRevenueStat(params);
			storageStatMapper.saveRevenueStat(blocknumber,blocktime,sttime);
			log.info("Save revenue stat at "+blocknumber+" spend "+timeSpend.getSpendTime());
		}catch(Exception e){
			log.warn("Save revenue stat at "+blocknumber+" error:",e);
		}
	}
	

	public void saveGlobalStat(long blocknumber,Date blocktime){
		Date sttime = new Date();
		try{
			TimeSpend timeSpend = new TimeSpend();
			Map<String,Object> params = new HashMap<>();
			params.put("blocknumber", blocknumber);
			params.put("blocktime", blocktime);
			params.put("sttime", sttime);			
		//	storageStatMapper.saveGlobalStat(params);
			storageStatMapper.saveGlobalStat(blocknumber,blocktime,sttime);
			log.info("Save global stat at "+blocknumber+" spend "+timeSpend.getSpendTime());
		}catch(Exception e){
			log.warn("Save global stat at "+blocknumber+" error:",e);
		}
	}
	
	

	@SuppressWarnings("unchecked")
	@Transactional
	public void startBandwidthMakeup(Long blockNumber){
		Map<String, Object> data = web3jService.getStgbandwidthmakeup();
		if (data == null || data.size() == 0) {
			log.warn("Start handle space bandwidth makeup total 0 at "+blockNumber);
		//	throw new RuntimeException("getStgbandwidthmakeup data error");
			return;
		}
		int count = 0;
		for (String address : data.keySet()) {
			Map<String, Object> item = (Map<String, Object>) data.get(address);
		//	StorageSpace space = storageSpaceMapper.getSpaceInfo(address);	
			BigDecimal depositMakeup = new BigDecimal(item.get("depositmakeup").toString());
			Integer bandwidth = Integer.parseInt(item.get("oldbandwidth").toString());
		//	BigDecimal burnRatio = new BigDecimal(item.get("burnratio").toString()).divide(new BigDecimal(100000),BigDecimal.ROUND_HALF_UP);						
			double bw_ratio = NumericUtil.getBandwidthPledgeRatio(bandwidth);
			double reward_ratio = NumericUtil.getBandwidthRewardRatio(bandwidth);
			count += storageSpaceMapper.startBandwidthMakeup(address, depositMakeup, bandwidth, bw_ratio, reward_ratio);	 
		}
		log.warn("Start handle space bandwidth makeup total "+count+" at "+blockNumber);
	}
	

	@Transactional
	public void finishBandwidthMakeup(Long blockNumber) {
		List<StorageSpace> spaceList = storageSpaceMapper.getList(null, null, "0");
		int count = 0;
		for (StorageSpace space : spaceList) {
			
			if (space.getBwSize() != null && space.getBwSize().compareTo(new BigDecimal(20)) > 0) {
				BigDecimal prepledgeAmount = space.getPrepledgeAmount();
				BigDecimal pledgeAmount = space.getPledgeAmount();
				if (prepledgeAmount != null && prepledgeAmount.compareTo(pledgeAmount) > 0) {
					int bw_size = 20;
					double bw_ratio = NumericUtil.getBandwidthPledgeRatio(bw_size);
					double reward_ratio = NumericUtil.getBandwidthRewardRatio(bw_size);					
					space.setBwSize(new BigDecimal(bw_size));
					space.setBwRatio(new BigDecimal(bw_ratio));
					space.setRewardRatio(new BigDecimal(reward_ratio));
					space.setPrepledgeAmount(null);
					count += storageSpaceMapper.finishBandwidthMakeup(space);
				}
			}
		}
		log.warn("Finish handle space bandwidth makeup total " + count+" at "+blockNumber);
	}	
}
