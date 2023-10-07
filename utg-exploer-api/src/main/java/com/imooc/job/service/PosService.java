package com.imooc.job.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

import com.imooc.job.service.TransactionService.TranasctionType;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.StoragePoolMapper;
import com.imooc.mapper.StorageSpaceMapper;
import com.imooc.pojo.NodePledge;
import com.imooc.pojo.StoragePool;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PosService {
	public enum SpOperator{
		PoSwtfd
	}
	@Autowired
	private NodeExitMapper  nodePledgeDao;
	
	@Autowired
	private StorageSpaceMapper  spaceDao;
	@Autowired
	private StoragePoolMapper spDao;
	public boolean poSTransaction(Transaction tx, Log txLog){
		String input = tx.getInput();
		if(input==null)
			return false;
		if (!getStatus(tx,txLog)) {
			return false;
		}
		TranasctionType txType = TransactionService.parseTxtype(tx);
		 if(txType!=null && "SSC".equals(txType.getCategory())){
			String ustOperator = txType.getOperator();
			String[] params = TransactionService.parseTxParams(tx.getInput());			
			SpOperator operator;
			try{
				operator= SpOperator.valueOf(ustOperator);
			}catch(Exception e){
				log.info("invaild PoS ustOperator "+ustOperator);
				return false;
			}
			try{
				switch (operator) {
				case PoSwtfd:
					spPoSwtfdTransaction(tx, params, txLog);
					break;
					
					
				default:
					return false;
				}
				return true;
			}catch(Exception e){
				String data = new String(Numeric.hexStringToByteArray(input));
				log.warn("PoS "+operator+" transaction :"+tx+", parse data :"+data+" error.",e);
			//	throw e;
			}
		}
		return false;
		
	}
	private void spPoSwtfdTransaction(Transaction tx, String[] params, Log txLog) {
		String posAddr=Constants.prefixAddress(params[0]);
		String transferType=params[1];
		String target=Constants.prefixAddress(params[2]);
		BigDecimal lockAmount   =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(1)));
		BigDecimal pledgeAmount =new  BigDecimal(Numeric.decodeQuantity(txLog.getTopics().get(2)));
		BigDecimal totalExitAmount=lockAmount.add(pledgeAmount);
		UtgNodeMinerQueryForm sourceParam=new UtgNodeMinerQueryForm();
		sourceParam.setNode_address(posAddr);
		UtgNodeMiner dbMiner= nodePledgeDao.getNode(sourceParam);
		if (dbMiner!=null) {
			if (dbMiner.getTotal_amount().compareTo(totalExitAmount)>0) {
				UtgNodeMiner upMiner=new UtgNodeMiner();
				upMiner.setNode_address(dbMiner.getNode_address());
				BigDecimal totalAmount=dbMiner.getTotal_amount()==null?BigDecimal.ZERO:dbMiner.getTotal_amount().subtract(totalExitAmount);
				upMiner.setTotal_amount(totalAmount);
				nodePledgeDao.updateNodeMiner(upMiner);
				
			}
			updateUNodepledge(tx);
			NodePledge pledge=new NodePledge();
			if ("PoS".equals(transferType)){
				UtgNodeMinerQueryForm param=new UtgNodeMinerQueryForm();
				param.setNode_address(target);
				UtgNodeMiner miner= nodePledgeDao.getNode(param);
				
				UtgNodeMiner upMiner=new UtgNodeMiner();
				upMiner.setNode_address(target);
				 BigDecimal totalAmount=miner.getTotalamount()==null?pledgeAmount:miner.getTotalamount().add(pledgeAmount);
				upMiner.setTotal_amount(totalAmount);
				nodePledgeDao.updateNodeMiner(upMiner);
				pledge.setNode_address(target);
			}else if ("SP".equals(transferType)){
				StoragePool targeteDbSp=spDao.getPoolInfo(target);
				pledge.setNode_address(target);
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
			pledge.setPledge_address(Constants.prefixAddress(tx.getFromAddr()));
			pledge.setBurntamount(BigDecimal.ZERO);
			pledge.setPledge_hash(Constants.prefixAddress(tx.getHash()));
			pledge.setPledge_number(tx.getBlockNumber());
			pledge.setPledge_amount(pledgeAmount);
			pledge.setPledge_time(tx.getTimeStamp());
			pledge.setPledge_status(1);
			pledge.setNode_type(transferType);
			nodePledgeDao.saveOrUpdateNodePledge(pledge);
		}else {
			log.info("not find PoS node "+posAddr);
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
		log.info("posTransfer pledge_addr="+upledge.getPledge_address());
		nodePledgeDao.updateNodePledgeTransfer(upledge);
	}
	private boolean getStatus(Transaction tx,Log txLog){	
			return tx.getStatus()==1 && txLog!=null && txLog.getTopics()!=null && txLog.getTopics().size()!=0;	// when txLog is not exist, status is failed
		}
}
