package com.imooc.job.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSONObject;
import com.imooc.Utils.TimeSpend;
import com.imooc.job.service.TransactionService.TranasctionType;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.StoragePoolMapper;
import com.imooc.mapper.StorageSpaceMapper;
import com.imooc.pojo.NodePledge;
import com.imooc.pojo.StoragePool;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.Form.StoragePoolForm;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.utils.Constants;
import com.imooc.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpPoolService {
	public static BigDecimal defaultMinPledge=BigDecimal.valueOf(10).pow(18).multiply(BigDecimal.valueOf(625));
	public static BigDecimal spacePrice=BigDecimal.valueOf(10).pow(18).multiply(BigDecimal.valueOf(1.25));
	public static BigDecimal  OneTb=new BigDecimal("1099511627776");
	public enum SpOperator{
		addsp,
		spchpg,
		spremovesn,
		spwtpg,
		spwtfd,
		spwtexit,
		spexit,
		spfee,
		spetrate,
		sprvebind
	}
	@Autowired
	private StoragePoolMapper spDao;
	
	@Autowired
	private NodeExitMapper  nodePledgeDao;
	
	@Autowired
	private StorageSpaceMapper  spaceDao;
	public boolean stroagePoolTransaction(Transaction tx, Log txLog){
		String input = tx.getInput();
		if(input==null)
			return false;
		if (!getStatus(tx,txLog)) {
			return false;
		}
		TranasctionType txType = TransactionService.parseTxtype(tx);
		 if(txType!=null && "SP".equals(txType.getCategory())){
			String ustOperator = txType.getOperator();
			String[] params = TransactionService.parseTxParams(tx.getInput());			
			SpOperator operator;
			try{
				operator= SpOperator.valueOf(ustOperator);
			}catch(Exception e){
				log.info("invaild SP ustOperator "+ustOperator);
				return false;
			}
			try{
				switch (operator) {
				case addsp:
					spAddTransaction(tx, params, txLog);
					break;
				case spchpg:
					spChpgTransaction(tx, params, txLog);
					break;
				case spremovesn:
					spRemoveSnTransaction(tx, params, txLog);
					break;
				case spwtpg:
					spEntrustPledgeTransaction(tx, params, txLog);
					break;	
				case spwtfd:
					spEntrustTransferTransaction(tx, params, txLog);
					break;
				case spwtexit:
					spEntrustExitTransaction(tx, params, txLog);
					break;	
				case spexit:
					spExitTransaction(tx, params, txLog);
					break;
				case spfee:
					spFeeTransaction(tx, params, txLog);
					break;	
				case spetrate:
					spEntrustRateTransaction(tx, params, txLog);
					break;
				case sprvebind:
					spRevenueAddrTransaction(tx, params, txLog);
					break;	
					
				default:
					return false;
				}
				return true;
			}catch(Exception e){
				String data = new String(Numeric.hexStringToByteArray(input));
				log.warn("SP "+operator+" transaction :"+tx+", parse data :"+data+" error.",e);
			//	throw e;
			}
		}
		return false;
		
	}
	private void spAddTransaction(Transaction tx, String[] params, Log txLog) {	
		BigInteger capacity =Numeric.decodeQuantity(txLog.getTopics().get(1) );
		BigInteger pledgeAmount =Numeric.decodeQuantity(txLog.getTopics().get(2) );
		tx.setParam1(pledgeAmount.toString());
		tx.setParam3(new BigDecimal(params[1]));
		tx.setParam4(new BigDecimal(params[2]));
		if(params.length==4) {
			tx.setParam2(params[3]);
		}
		StoragePool sp=new StoragePool();
		sp.setHash( Constants.prefixAddress(tx.getHash()));
		sp.setActiveHeight(tx.getBlockNumber());
		sp.setActiveStatus(1);
		sp.setCreatetime(tx.getTimeStamp());
		sp.setPledgeAmount(new BigDecimal(pledgeAmount));
		sp.setFeeRate(Integer.parseInt(params[1]));
		sp.setEntrustRate(Integer.parseInt(params[2]));
		sp.setHavAmount(sp.getPledgeAmount());
		sp.setInstime(new Date());
		sp.setManagerAddr(Constants.prefixAddress(tx.getFromAddr()));
		sp.setPledgeAddr(Constants.prefixAddress(tx.getFromAddr()));
		sp.setManagerAmount(sp.getPledgeAmount());
		sp.setSpAddr(Constants.prefixAddress(sp.getHash().substring(26)));//ux570a67ff34326d94c7ea0f
//		if (params.length==4 ){
//			sp.setRevenueAddr(Constants.prefixAddress(params[3]));
//		}
		sp.setSpAmount(BigDecimal.ZERO);
		sp.setSpRelease(BigDecimal.ZERO);
		sp.setSpBurnt(BigDecimal.ZERO);
		sp.setSnNum(0);
		sp.setTotalCapacity(new  BigDecimal(capacity));
		sp.setUsedCapacity(BigDecimal.ZERO);
		spDao.insertPool(sp);
		addNodePledge(tx, sp.getHash(),sp.getPledgeAmount());
	}
	
	
	private void spChpgTransaction(Transaction tx, String[] params, Log txLog) {	
		String hash=Constants.prefixAddress(params[0]);
		BigDecimal pledgeAmount =new BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(2) ));
		tx.setParam1(hash);
		tx.setParam3(pledgeAmount);
		StoragePool dbSp=spDao.getPoolInfo(hash);
		StoragePool sp=new StoragePool();
		if (dbSp!=null) {
			
			sp.setHash( hash);
			BigDecimal totalAmount=dbSp.getPledgeAmount()==null?pledgeAmount: pledgeAmount.add(dbSp.getPledgeAmount());
			sp.setPledgeAmount(totalAmount);
			sp.setHavAmount(totalAmount);
			BigDecimal managerAmount=dbSp.getManagerAmount()==null?pledgeAmount: pledgeAmount.add(dbSp.getManagerAmount());
			sp.setManagerAmount(managerAmount);
			BigDecimal dbPledgeAmount= dbSp.getPledgeAmount()==null?BigDecimal.ZERO:dbSp.getPledgeAmount();
			if (dbPledgeAmount.compareTo(BigDecimal.ZERO)==0 && sp.getActiveStatus().intValue()==0
					&& sp.getPledgeAmount().compareTo(defaultMinPledge)>=0) {
				sp.setActiveStatus(1);
			}
			spDao.updatePool(sp);
			log.info("manager add sp["+sp.getHash()+"] pledgeAmount="+sp.getPledgeAmount());
			addNodePledge(tx, hash,pledgeAmount);
		}else {
			log.info("manager pledgeAmount  not find  sp["+sp.getHash()+"] pledgeAmount="+sp.getPledgeAmount());
			return;
		}
		
		
		
		
		
	}
	
	private void spRemoveSnTransaction(Transaction tx, String[] params, Log txLog) {
		String sphash=Constants.prefixAddress(params[0]);
		String snAddress=Constants.prefixAddress(params[1]);
		tx.setParam1(sphash);
		tx.setParam2(snAddress);
		BigInteger totalSpace=Numeric.decodeQuantity(txLog.getTopics().get(1));
		StorageSpace space=new StorageSpace();
		space.setDeviceAddr(snAddress);
		space.setPledgeStatus(0);
		spaceDao.updateSpaceSpStatus(space);
		StoragePool dbSp=spDao.getPoolInfo(sphash);
		if(dbSp!=null) {
			StoragePool sp=new StoragePool();
			sp.setHash(sphash);
			if(dbSp.getSnNum()!=null && dbSp.getSnNum()>1) {
				sp.setSnNum(dbSp.getSnNum()-1);
			}
			sp.setSnSpaceCapacity(new BigDecimal(totalSpace));
			spDao.updatePool(sp);
		}
		
		log.info("manager["+tx.getFromAddr()+"] remove SN["+snAddress+"] from sp["+sphash+"],free space="+totalSpace);
	}
	private void spEntrustPledgeTransaction(Transaction tx, String[] params, Log txLog) {
		String hash=Constants.prefixAddress(params[0]);
		BigDecimal amount=new BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(2)));
		tx.setParam1(hash);
		tx.setParam3(amount);
		StoragePool dbSp=spDao.getPoolInfo(hash);
		if (dbSp!=null) {
			StoragePool sp=new StoragePool();
			sp.setHash(hash);
			BigDecimal afterAmount=dbSp.getPledgeAmount()==null?amount: dbSp.getPledgeAmount().add(amount);
		    sp.setPledgeAmount(afterAmount); 
		    sp.setHavAmount(afterAmount);
		    BigDecimal capacity=dbSp.getTotalCapacity()==null?getCapacity(amount):dbSp.getTotalCapacity().add(getCapacity(amount));
			sp.setTotalCapacity(capacity);
			spDao.updatePool(sp);
			addNodePledge(tx, sp.getHash(),amount);
		}
		
		log.info("add sp["+hash+"] entrust["+tx.getHash()+"] pledgeAmount="+amount);
		
		
	}
	private void spEntrustTransferTransaction(Transaction tx, String[] params, Log txLog) {
		String hash=Constants.prefixAddress(params[0]);
		String transferType=params[1];
		String target=Constants.prefixAddress(params[2]);
		BigDecimal lockAmount   =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(1)));
		BigDecimal pledgeAmount =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(2)));
		BigDecimal totalExitAmount=lockAmount.add(pledgeAmount);
		updateUNodepledge(tx);
		StoragePool dbSp=spDao.getPoolInfo(hash);
		if (dbSp != null) {
			StoragePool sp = new StoragePool();
			sp.setHash(hash);
			BigDecimal afterAmount = dbSp.getPledgeAmount() == null ?totalExitAmount
					: dbSp.getPledgeAmount().subtract(totalExitAmount);
			sp.setPledgeAmount(afterAmount);
			sp.setHavAmount(afterAmount);
			spDao.updatePool(sp);
		}
		NodePledge pledge=new NodePledge();
		if ("PoS".equals(transferType)){
			UtgNodeMinerQueryForm param=new UtgNodeMinerQueryForm();
			param.setNode_address(target);
			UtgNodeMiner miner= nodePledgeDao.getNode(param);
			if(miner!=null) {
				UtgNodeMiner upMiner=new UtgNodeMiner();
				upMiner.setNode_address(target);
				BigDecimal totalAmount=miner.getTotalamount()==null?pledgeAmount:miner.getTotalamount().add(pledgeAmount);
				upMiner.setTotal_amount(totalAmount);
				nodePledgeDao.updateNodeMiner(upMiner);
				pledge.setNode_address(target);
			}
			
		}else if ("SP".equals(transferType)){
			StoragePool targeteDbSp=spDao.getPoolInfo(target);
			pledge.setNode_address(target);
			pledge.setSp_hash(target);
			if(targeteDbSp!=null) {
				BigDecimal afterAmount = targeteDbSp.getPledgeAmount() == null ?pledgeAmount
						: targeteDbSp.getPledgeAmount().add(pledgeAmount);
				 BigDecimal capacity=targeteDbSp.getTotalCapacity()==null?getCapacity(pledgeAmount):targeteDbSp.getTotalCapacity().add(getCapacity(pledgeAmount));
					
				StoragePool upsp = new StoragePool();
				upsp.setHash(target);
				upsp.setPledgeAmount(afterAmount);
				upsp.setHavAmount(afterAmount);
				upsp.setTotalCapacity(capacity);
				spDao.updatePool(upsp);
			}
			
		}else if ("SN".equals(transferType)){
			pledge.setNode_address(target);
			StorageSpace space=spaceDao.getSpaceInfo(target);
			if (space!=null) {
				StorageSpace update=new StorageSpace();
				BigDecimal havAmount=space.getHavAmount()==null?pledgeAmount:space.getHavAmount().add(pledgeAmount);
				update.setHavAmount(havAmount);
				update.setStorageid(space.getStorageid());
				spaceDao.updateSpToSnTransfer(update);
			}
		}
		pledge.setPledge_address(tx.getFromAddr());
		pledge.setBurntamount(BigDecimal.ZERO);
		pledge.setPledge_hash(tx.getHash());
		pledge.setPledge_number(tx.getBlockNumber());
		pledge.setPledge_amount(pledgeAmount);
		pledge.setPledge_time(tx.getTimeStamp());
		pledge.setPledge_status(1);
		pledge.setNode_type(transferType);
		nodePledgeDao.saveOrUpdateNodePledge(pledge);
		tx.setParam1(hash);
		tx.setParam2(target);
		tx.setParam3(pledgeAmount);
		tx.setParam4(lockAmount);
		log.info("transfer sp["+hash+"] entrust["+tx.getFromAddr()+"] target ["+target+"] pledgeAmount="+pledgeAmount);
	}
	
	
	private void spEntrustExitTransaction(Transaction tx, String[] params, Log txLog) {
		String hash=Constants.prefixAddress(params[0]);
		String pledgeHash=Constants.prefixAddress(params[1]);
		BigDecimal pledgeAmount =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(1)));
		NodePledge upledge=new NodePledge();
		upledge.setPledge_hash(pledgeHash);
		upledge.setPledge_address(tx.getFromAddr());
		upledge.setPledge_status(0);
		upledge.setUnpledge_type(0);
		upledge.setUnpledge_hash(tx.getHash());
		upledge.setUnpledge_number(tx.getBlockNumber());
		upledge.setUnpledge_time(tx.getTimeStamp());
		nodePledgeDao.updateNodePledgeExit(upledge);
		StoragePool dbSp=spDao.getPoolInfo(hash);
		if (dbSp!=null) {
			StoragePool upsp = new StoragePool();
			upsp.setHash(hash);
			BigDecimal afterAmount = dbSp.getPledgeAmount() == null ?pledgeAmount
					: dbSp.getPledgeAmount().subtract(pledgeAmount);
			upsp.setPledgeAmount(afterAmount);
			upsp.setHavAmount(afterAmount);
			spDao.updatePool(upsp);
		}
		tx.setParam1(hash);
		tx.setParam2(pledgeHash);
		tx.setParam3(pledgeAmount);
	}
	
	
	private void spExitTransaction(Transaction tx, String[] params, Log txLog) {
		String hash=Constants.prefixAddress(params[0]);
		StoragePool upsp = new StoragePool();
		upsp.setHash(hash);
		upsp.setActiveStatus(2);
		upsp.setStorageRatio(0.0);
		spDao.updatePool(upsp);
		NodePledge upledge=new NodePledge();
		upledge.setNode_address(hash);
		upledge.setPledge_status(0);
		upledge.setUnpledge_type(0);
		upledge.setUnpledge_hash(tx.getHash());
		upledge.setUnpledge_number(tx.getBlockNumber());
		upledge.setUnpledge_time(tx.getTimeStamp());
		nodePledgeDao.updateNodePledgeBySpExit(upledge);
		StorageSpace space=new  StorageSpace();
		space.setSpHash(hash);
//		space.setSpHeight(null);
		spaceDao.updateSpaceToSp(space);
		tx.setParam1(hash);
	}
	private void spFeeTransaction(Transaction tx, String[] params, Log txLog) {
		String hash=Constants.prefixAddress(params[0]);
		Integer fee=Integer.valueOf(params[1]);
		StoragePool upsp = new StoragePool();
		upsp.setHash(hash);
		upsp.setFeeRate(fee);
		spDao.updatePool(upsp);
		tx.setParam1(hash);
		tx.setParam2(fee.toString());
	}
	private void spEntrustRateTransaction(Transaction tx, String[] params, Log txLog) {
		String hash=Constants.prefixAddress(params[0]);
		Integer entrustRate=Integer.valueOf(params[1]);
		StoragePool upsp = new StoragePool();
		upsp.setHash(hash);
		upsp.setEntrustRate(entrustRate);
		spDao.updatePool(upsp);
		tx.setParam1(hash);
		tx.setParam2(entrustRate.toString());
	}
	private void spRevenueAddrTransaction(Transaction tx, String[] params, Log txLog) {
		String hash=Constants.prefixAddress(params[0]);
		String bindType=params[1];
		String revenueAddr=params[2];
		StoragePool upsp = new StoragePool();
		upsp.setHash(hash);
		if (!"unbind".equals(bindType)) {
			upsp.setRevenueAddr(revenueAddr);
		}else {
			upsp.setUnbind("unbind");
		}
		spDao.updatePool(upsp);
		tx.setParam1(hash);
		tx.setParam2(bindType);
		tx.setParam5(revenueAddr);
	}
	
	public void getAllSpByMainClt(String url,Long blockNumber,Map<Long,BigInteger> blockTimeMap) {
		String param="{\"jsonrpc\":\"2.0\",\"method\":\"alien_getSPoolAtNumber\",\"params\":["+blockNumber+"],\"id\":0}";
		String spData=HttpUtils.HttpPostWithJson(url,param);
		List<StorageSpace> ratioList=new ArrayList<>();
		if (spData != null && !spData.equals("0.0")) {
			JSONObject spJson = JSONObject.parseObject(spData);
			JSONObject result = spJson.getJSONObject("result");
			if (result != null) {
				JSONObject	poolpledge=result.getJSONObject("poolpledge");
				if(poolpledge!=null) {
					Map<String,StoragePool>  poolMap=getAllActivePool();
					Map<String,NodePledge>  nodePledgeMap=getNodePledgeMap();
					List<StoragePool> butchUpdateList=new ArrayList<StoragePool>();
					for(String hash:poolpledge.keySet()) {
						String spHash=Constants.prefixAddress(hash);
						String spAddr=Constants.prefixAddress(spHash.substring(26));
						JSONObject chainSp = poolpledge.getJSONObject(hash);
						StoragePool dbPool=poolMap.remove(spHash);
						int status=chainSp.getInteger("status");
						double snRatio=chainSp.getDouble("snRatio")/1000000.0;
						int unpledge_type=0;
						if(status==1) {
							StorageSpace  space=new StorageSpace();
							space.setSpHash(spHash);
							space.setRatio(BigDecimal.valueOf(snRatio));
							ratioList.add(space);
						}else if(status==3){
							unpledge_type=1;
						}
						if(dbPool==null && (status==1 ||status==0)) {//add
							StoragePool sp = createSpInfo(blockTimeMap, spHash,spAddr, chainSp);
							spDao.insertPool(sp);
						}else if(dbPool!=null) {
							StoragePool sp = updateSpInfo(blockTimeMap, spHash,spAddr, chainSp);
							butchUpdateList.add(sp);
						}
						JSONObject entrustDetail = chainSp.getJSONObject("entrustDetail");
						if (entrustDetail != null&&entrustDetail.size()>0) {
							
							for(String pledgeHash:entrustDetail.keySet()) {
								 JSONObject  detail= entrustDetail.getJSONObject(pledgeHash);
//								 log.info(msg);
								 NodePledge dbPledge=nodePledgeMap.remove(Constants.prefixAddress(pledgeHash));
								boolean isrunDetail=true;
								if(dbPledge!=null) {
								  if(dbPledge.getPledge_status().intValue()==1 &&dbPledge.getPledge_amount().compareTo(detail.getBigDecimal("amount"))==0) {
									  isrunDetail=false;
								  }
								}
								if(isrunDetail) {
									NodePledge pledge=new NodePledge();
									pledge.setBurntamount(BigDecimal.ZERO);
									pledge.setNode_address(spHash);
									pledge.setPledge_address(Constants.prefixAddress(detail.getString("address")));
									pledge.setPledge_hash(Constants.prefixAddress(pledgeHash));
									pledge.setPledge_number(detail.getLong("height"));
									pledge.setPledge_amount(detail.getBigDecimal("amount"));
									BigInteger pledgeTime = blockTimeMap.get(pledge.getPledge_number().longValue());
									if (pledgeTime != null) {
										pledge.setPledge_time(new Date(pledgeTime.longValue()*1000));
									}
									pledge.setPledge_status(1);
									pledge.setSp_hash(spHash);
									pledge.setNode_type("SP");
									nodePledgeDao.saveOrUpdateNodePledge(pledge);
								}
							}
						}
					}
					if(poolMap.size()>0) {
//						log.info("need set pool outtime :"+poolMap.size());
						for(StoragePool upPool:poolMap.values()) {
							upPool.setActiveStatus(3);
							upPool.setSnNum(0);
							butchUpdateList.add(upPool);
						}
					}
					if(nodePledgeMap!=null&& nodePledgeMap.size()>0) {
						for(NodePledge dbPledge:nodePledgeMap.values()) {
							NodePledge upledge=new NodePledge();
							upledge.setPledge_address(dbPledge.getPledge_address());
							upledge.setId(dbPledge.getId());
							upledge.setPledge_status(0);
							upledge.setUnpledge_type(0);
							if(dbPledge.getUnpledge_hash()==null) {
								upledge.setUnpledge_hash("");
							}
							if(dbPledge.getUnpledge_number()==null) {
								upledge.setUnpledge_number(blockNumber);
							}
							if(dbPledge.getUnpledge_time()==null) {
								BigInteger pledgeTime = blockTimeMap.get(blockNumber.longValue());
								if (pledgeTime != null) {
									upledge.setPledge_time(new Date(pledgeTime.longValue()*1000));
								}
							}
							nodePledgeDao.updateNodePledgeTransfer(upledge);
						}
					}
					if(butchUpdateList.size()>0) {
						spDao.batchUpdateSp(butchUpdateList);
					}
				}
			}
		}
		log.info("sp ratioList size"+ratioList.size());
		if(ratioList.size()>0) {
			spaceDao.batchUpdateSpaceRatioBySpHash(ratioList);
		}
	}
	private StoragePool createSpInfo(Map<Long, BigInteger> blockTimeMap, String hash,String spAddr, JSONObject chainSp) {
		StoragePool sp = new StoragePool();
		sp.setHash(hash);
		sp.setActiveHeight(chainSp.getLong("number"));
		sp.setActiveStatus(chainSp.getInteger("status"));
		BigInteger pledgeTime = blockTimeMap.get(sp.getActiveHeight().longValue());
		if (pledgeTime != null) {
			sp.setCreatetime(new Date(pledgeTime.longValue()*1000));
		}
		sp.setPledgeAmount(chainSp.getBigDecimal("totalAmount"));
		sp.setFeeRate(chainSp.getInteger("fee"));
		sp.setEntrustRate(chainSp.getInteger("entrustRate"));
		sp.setHavAmount(sp.getPledgeAmount());
		sp.setInstime(new Date());
		sp.setManagerAddr(Constants.prefixAddress(chainSp.getString("manager")));
		sp.setPledgeAddr(Constants.prefixAddress(chainSp.getString("address")));
		sp.setManagerAmount(chainSp.getBigDecimal("managerAmount"));
		sp.setSpAddr(spAddr);// ux570a67ff34326d94c7ea0f
		String revenueAddr = chainSp.getString("revenueAddress");
		if (revenueAddr != null&&!revenueAddr.contains("0000000000000000000000000000")) {
			sp.setRevenueAddr(revenueAddr);
		}
		sp.setSpAmount(BigDecimal.ZERO);
		sp.setSpRelease(BigDecimal.ZERO);
		sp.setSpBurnt(BigDecimal.ZERO);
		sp.setTotalCapacity(chainSp.getBigDecimal("totalcapacity"));
		sp.setStorageRatio(chainSp.getDouble("snRatio") / 1000000.0);
		sp.setUsedCapacity(chainSp.getBigDecimal("usedcapacity"));
		return sp;
	}
	private StoragePool updateSpInfo(Map<Long, BigInteger> blockTimeMap, String hash,String spAddr, JSONObject chainSp) {
		StoragePool sp = new StoragePool();
		sp.setHash(hash);
		sp.setActiveStatus(chainSp.getInteger("status"));
		if(sp.getActiveStatus()!=null && sp.getActiveStatus().intValue()==3) {
			sp.setActiveStatus(2);
		}
		if(sp.getActiveStatus()!=null&&sp.getActiveStatus().intValue()>=2) {
			sp.setUsedCapacity(BigDecimal.ZERO);
		}
		sp.setPledgeAmount(chainSp.getBigDecimal("totalAmount"));
		sp.setFeeRate(chainSp.getInteger("fee"));
		sp.setEntrustRate(chainSp.getInteger("entrustRate"));
		sp.setHavAmount(sp.getPledgeAmount());
		sp.setInstime(new Date());
		sp.setManagerAddr(Constants.prefixAddress(chainSp.getString("manager")));
		sp.setPledgeAddr(Constants.prefixAddress(chainSp.getString("address")));
		sp.setManagerAmount(chainSp.getBigDecimal("managerAmount"));
		String revenueAddr = Constants.prefixAddress(chainSp.getString("revenueAddress"));
        if (revenueAddr != null&&!revenueAddr.contains("0000000000000000000000000000")) {
			sp.setRevenueAddr(revenueAddr);
		}
		sp.setTotalCapacity(chainSp.getBigDecimal("totalcapacity"));
		sp.setStorageRatio(chainSp.getDouble("snRatio") / 1000000.0);
		sp.setUsedCapacity(chainSp.getBigDecimal("usedcapacity"));
		if(sp.getUsedCapacity()!=null &&sp.getUsedCapacity().compareTo(BigDecimal.ZERO)<=0) {
			sp.setSnNum(0);
		}
		
		
		return sp;
	}
	
	public Map<String,StoragePool> getAllActivePool(){
		Map<String,StoragePool>  poolMap=new HashMap<String, StoragePool>();
		StoragePoolForm storagePoolForm=new StoragePoolForm();
		storagePoolForm.setStatusList(new Integer[] {0,1});
		List<StoragePool> list=spDao.getStoragePoolList(storagePoolForm);
		if(list!=null) {
			for(StoragePool pool:list) {
				poolMap.put(pool.getHash(), pool);
			}
		}
		return poolMap;
		
	}
	public Map<String,NodePledge> getNodePledgeMap(){
		UtgNodeMinerQueryForm param=new UtgNodeMinerQueryForm();
		param.setEtType("SP");
		param.setPledge_status(1);
		List<NodePledge>  nodePledgeList=nodePledgeDao.getNodePledgeListCache(param);
		Map<String,NodePledge>  nodePledgeMap=new HashMap<>();
		if(nodePledgeList!=null) {
			
			for(NodePledge pledge:nodePledgeList) {
				nodePledgeMap.put(pledge.getPledge_hash(),pledge);
			}
		}
		return nodePledgeMap;
	}
	
	private void addNodePledge(Transaction tx,String hash,BigDecimal pledgeAmount) {
		NodePledge pledge=new NodePledge();
		pledge.setBurntamount(BigDecimal.ZERO);
		pledge.setNode_address(hash);
		pledge.setPledge_address(Constants.prefixAddress(tx.getFromAddr()));
		pledge.setPledge_hash(Constants.prefixAddress(tx.getHash()));
		pledge.setPledge_number(tx.getBlockNumber());
		pledge.setPledge_amount(pledgeAmount);
		pledge.setPledge_time(tx.getTimeStamp());
		pledge.setPledge_status(1);
		pledge.setSp_hash(hash);
		pledge.setNode_type("SP");
		nodePledgeDao.saveOrUpdateNodePledge(pledge);
		log.info("add sp["+hash+"] entrust["+tx.getHash()+"] pledgeAmount="+pledgeAmount);
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
	public static BigDecimal getCapacity(BigDecimal amount) {
		return amount.divide(spacePrice).multiply(OneTb);
	}
	private boolean getStatus(Transaction tx,Log txLog){		
			return tx.getStatus()==1 && txLog!=null && txLog.getTopics()!=null && txLog.getTopics().size()!=0;	// when txLog is not exist, status is failed
		}
	
    public void updateStoragePoolAmount(Long blockNumber) {
    	try{
			TimeSpend timeSpend = new TimeSpend();
			spDao.updateStoragePoolAmount();
			log.info("Update spool reward stat at "+blockNumber+" spend "+timeSpend.getSpendTime());
		}catch(Exception e){
			log.warn("Update spool reward stat at "+blockNumber+" error:",e);
		}
    }
    public void updateStoragePoolSnNum(Long blockNumber) {
    	try{
			TimeSpend timeSpend = new TimeSpend();
			spDao.updateStoragePoolSn();
			spaceDao.updateSpaceDelSpStatus();
			log.info("Update spool sn_num stat at "+blockNumber+" spend "+timeSpend.getSpendTime());
		}catch(Exception e){
			log.warn("Update spool sn_num stat at "+blockNumber+" error:",e);
		}
    }
}
