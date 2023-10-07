package com.imooc.job.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.ContractLockupMapper;
import com.imooc.mapper.ContractMapper;
import com.imooc.pojo.Contract;
import com.imooc.pojo.ContractLockup;
import com.imooc.pojo.Tokens;
import com.imooc.pojo.Transaction;
import com.imooc.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractService {
		
	@Autowired
	private ContractMapper contractMapper;
	@Autowired
	private ContractLockupMapper contractLockupMapper;
	@Autowired
    AddressService addressService;
	@Autowired
    AccountMapper addressMapper;
	@Autowired
	private Web3jService web3jService;
	
	//Team Lockup Contract
	private static String transferAdmin1Topic = Constants.PRE+"858ac4d8dc6c854f604e18771d27d0066054fe88f5d6721149df79353081ee2c";
	private static String transferAdmin2Topic = Constants.PRE+"2dedd5a3b828d6e458eb1eb5a6a1bae4b64d76722b28af86c387f0b692cab3fb";

	private static String approveTopic = Constants.PRE+"77b92c0722e5bbe8d1413b7fbec6093bc4dc966a65832498dc8c2c67d9a937cc";
	private static String provideTopic = Constants.PRE+"76ac9883977ff904765119c5f2cf43d304e1929d6d8399fb697024e8df545e0e";
	private static String pickupV1Topic = Constants.PRE+"dd6eb07a5b1cff6970b7abdf62d8f670d00202f7404afd55741dfd27ea7a2d9b";
	private static String pickupV2Topic = Constants.PRE+"4a142a723704af60f8fd272a17a3f4b4c29ac84fb512c7038ccbfe91f9e1feaf";
	private static String pickupV3Topic = Constants.PRE + "6db7c49fcc0ced8e600b0dd3847e5a6119341f80837f723ca1c28363d3236ad7";
	private static String lockupCancelTopic = Constants.PRE+"e94fb30214f4aa2d2dff6df243aebd3a3c0e8d86b1aea2ffb6b88fdc0129b0c4";	
	
	//v1
	private static String admin1V1Key = "0x3c477f91";
	private static String admin2V1Key = "0x68a9f31c";	
	private static String lockupperiodKey = "0xddfeb214";
	//v2
	private static String admin1V2Key = "0x8da5cb5b";
	private static String admin2V2Key = "0xf851a440";
	private static String lockstartKey = "0xcf993172";
	//v3
	private static String teamtypeKey = "0xbfdc9224";
	
	//common	
	private static String releaseperiodKey = "0xb200ef93";
	private static String releaseintervalKey = "0x3d09e94d";
	
    
    public Contract getContract(String contractAddr){
    	return contractMapper.getContract(contractAddr);
    }

	public Contract buildContract(TransactionReceipt receipt, Transaction tx) {
		String address = receipt.getContractAddress();
		if(address==null)
			return null;
		Long blockNumber = receipt.getBlockNumber().longValue();
		Contract contract = new Contract();				
		contract.setContract(address);
		contract.setTxhash(receipt.getTransactionHash());
		contract.setAuthor(receipt.getFrom());
		contract.setBlocknumber(blockNumber);
		contract.setIstoken(0);
		contract.setCreatetime(new Date());
		
		Tokens properties = web3jService.getERC20Properties(address);
		contract.setName(properties.getName());
		contract.setSymbol(properties.getSymbol());
				
		boolean isLockup = parseLockupContractV3(contract, address);
		if(!isLockup)
			isLockup = parseLockupContractV2(contract, address);
		if(!isLockup)
			isLockup = parseLockupContractV1(contract, address);
	//	if(!isLockup)
	//		log.info("Contract "+address+" is not a common contract.");
		contractMapper.saveOrUpdate(contract);
		addressService.saveOrUpdateAddress(address, blockNumber);
		addressMapper.setAsContract(address, blockNumber);
		
		tx.setToAddr(receipt.getContractAddress());
		tx.setUfoprefix("CT");
		tx.setUfoversion("1");
		tx.setUfooperator("ContractNew");
		return contract;		
	}
	

	public boolean parseTransaction(Contract contract, Transaction tx, Log txLog) {
	//	if (transactionReceipt == null || transactionReceipt.getLogs().size() == 0)
	//		return false;
		boolean isLockupContract = false;
		String contractAddr = contract.getContract();
		String txhash = tx.getHash();
	//	for(Log txLog : transactionReceipt.getLogs()){
		//	Log log = transactionReceipt.getLogs().get(0);
			if (txLog == null || txLog.getTopics() == null || txLog.getTopics().size() == 0)
				return false;
			String topic = txLog.getTopics().get(0);
		//	log.info("topic:" + topic);
	    	if(transferAdmin1Topic.equals(topic)){
	    		isLockupContract = true;
	    		String newAdmin1 = Constants.PRE + txLog.getTopics().get(1).substring(26);
	    		contract.setAdmin1(newAdmin1);
	    		contract.setUpdatetime(new Date());
	    		contractMapper.saveOrUpdate(contract);
	    		tx.setUfooperator("TlcChAdmin1");
	    		tx.setParam1(newAdmin1);	    		
	    	}else if(transferAdmin2Topic.equals(topic)){
	    		isLockupContract = true;
	    		String newAdmin2 = Constants.PRE + txLog.getTopics().get(1).substring(26);
	    		contract.setAdmin2(newAdmin2);
	    		contract.setUpdatetime(new Date());
	    		contractMapper.saveOrUpdate(contract);
	    		tx.setUfooperator("TlcChAdmin2");
	    		tx.setParam1(newAdmin2);
	    	}else if(approveTopic.equals(topic)){
	    		isLockupContract = true;
	//    		String data = txLog.getData();
	//    		String data1 = data.substring(0,66);
	//    		BigInteger approveAmount = new BigInteger(data1.substring(2),16);	
	    	}else if(provideTopic.equals(topic)){
	    		isLockupContract = true;
	    		String topic1 = txLog.getTopics().get(1);
	    		String topic2 = txLog.getTopics().get(2);
	    		String data = txLog.getData();
	    		String data1 = data.substring(0,66);
	    		String address = Constants.PRE + topic1.substring(26);
	    		BigInteger lockstart = new BigInteger(topic2.substring(2),16); 	
	    		BigInteger lockupAmount = new BigInteger(data1.substring(2),16);
	    		Long lockupperiod = contract.getLockupperiod();	
				Long releaseperiod = contract.getReleaseperiod();
				Long releaseinterval = contract.getReleaseinterval();
	    		ContractLockup contractLockup = new ContractLockup();
				contractLockup.setContract(contractAddr);
				contractLockup.setAddress(address);
				contractLockup.setTxhash(txhash);
				contractLockup.setLogindex(txLog.getLogIndex().intValue());
				contractLockup.setType(1);
				contractLockup.setLockupnumber(lockstart.longValue());
				contractLockup.setLockupamount(new BigDecimal(lockupAmount));
				contractLockup.setLockupperiod(lockupperiod);
				contractLockup.setReleaseperiod(releaseperiod);
				contractLockup.setReleaseinterval(releaseinterval);
				contractLockup.setCreatetime(new Date());
				contractLockupMapper.saveOrUpdate(contractLockup);
				tx.setUfooperator("TlcProvide");
	    		tx.setParam1(address);
	    		tx.setParam3(new BigDecimal(lockstart));
	    		tx.setParam4(new BigDecimal(lockupAmount));
	    	}else if(pickupV1Topic.equals(topic)){
	    		isLockupContract = true;
	    		String topic1= txLog.getTopics().get(1);
	    		String topic2 = txLog.getTopics().get(2); 
	    		String data = txLog.getData();
	            String data1 = data.substring(0,66);  
				String data2 = data.substring(66, 66 + 64);
				String data3 = data.substring(66 + 64, 66 + 64 + 64);
	    		String address = Constants.PRE + topic1.substring(26);
	    		BigInteger lockstart = new BigInteger(topic2.substring(2),16);		          
	            BigInteger pickupAmount = new BigInteger(data1.substring(2),16);       
	            BigInteger lockupAmount = new BigInteger(data2.substring(2),16);          
	            BigInteger remainAmount = new BigInteger(data3.substring(2),16);            
	            Long lockupperiod = contract.getLockupperiod();
				Long releaseperiod = contract.getReleaseperiod();
				Long releaseinterval = contract.getReleaseinterval();		
				ContractLockup contractLockup = new ContractLockup();
				contractLockup.setContract(contractAddr);
				contractLockup.setAddress(address);
				contractLockup.setTxhash(txhash);
				contractLockup.setLogindex(txLog.getLogIndex().intValue());
				contractLockup.setType(2);
				contractLockup.setLockupnumber(lockstart.longValue());
				contractLockup.setPickupamount(new BigDecimal(pickupAmount));
				contractLockup.setLockupamount(new BigDecimal(lockupAmount));
				contractLockup.setRemainamount(new BigDecimal(remainAmount));
				contractLockup.setLockupperiod(lockupperiod);
				contractLockup.setReleaseperiod(releaseperiod);
				contractLockup.setReleaseinterval(releaseinterval);
				contractLockup.setCreatetime(new Date());				
				contractLockupMapper.saveOrUpdate(contractLockup);
				tx.setUfooperator("TlcPickup");
	    		tx.setParam1(address);
	    		tx.setParam2(lockstart.toString());
	    		tx.setParam3(new BigDecimal(pickupAmount));
	    		tx.setParam4(new BigDecimal(lockupAmount));	    		
	    	}else if(lockupCancelTopic.equals(topic)){
				isLockupContract = true;
				String data = txLog.getData();
				String data1 = data.substring(0, 66);
				String data2 = data.substring(66, 66 + 64);
				String data3 = data.substring(66 + 64, 66 + 64 + 64);
				String data4 = data.substring(66 + 64 + 64, 66 + 64 + 64 + 64);
	    		String address = Constants.PRE + data1.substring(26);
	            BigInteger lockstart = new BigInteger(data2.substring(2),16);
	            BigInteger lockupcancel = new BigInteger(data3.substring(2),16);
	            BigInteger cancelAmount = new BigInteger(data4.substring(2),16);           
	            ContractLockup contractLockup = new ContractLockup();
	            contractLockup.setContract(contractAddr);
				contractLockup.setAddress(address);
				contractLockup.setTxhash(txhash);
				contractLockup.setLogindex(txLog.getLogIndex().intValue());
				contractLockup.setType(1);
				contractLockup.setLockupnumber(lockstart.longValue());				
				contractLockup.setCancelnumber(lockupcancel.longValue());
	            contractLockup.setCancelamount(new BigDecimal(cancelAmount));
	            contractLockupMapper.updateCancel(contractLockup);	            
	            tx.setUfooperator("TlcRecycle");
	    		tx.setParam1(address);
	    		tx.setParam2(lockstart.toString());
	    		tx.setParam3(new BigDecimal(lockupcancel));
	    		tx.setParam4(new BigDecimal(cancelAmount));
	          //  	log.warn("can not find contract lockup data for contract:"+contract+",address:"+address.toLowerCase()+",blocknumber:"+lockstart);
	    	}else if(pickupV2Topic.equals(topic)){
	    		isLockupContract = true;	    		
	    		String topic1= txLog.getTopics().get(1);
	    	//	String topic2 = txLog.getTopics().get(2);  
	    		String data = txLog.getData();
	            String data1 = data.substring(0,66); 
	            String data2=data.substring(66,66+64);
	        //  String data3= data.substring(66+64,66+64+64);    
	    		String address = Constants.PRE + topic1.substring(26);
	    	//	BigInteger lockstart = new BigInteger(topic2.substring(2),16); 		           
	            BigInteger pickupAmount = new BigInteger(data1.substring(2),16);          
	            BigInteger lockupAmount = new BigInteger(data2.substring(2),16);
	        //  BigInteger remainAmount = new BigInteger(data3.substring(2),16);
	            Long lockupstart = contract.getLockupstart();
	            Long lockupperiod = contract.getLockupperiod();
				Long releaseperiod = contract.getReleaseperiod();
				Long releaseinterval = contract.getReleaseinterval();		
				ContractLockup contractLockup = new ContractLockup();
				contractLockup.setContract(contractAddr);
				contractLockup.setAddress(address);
				contractLockup.setTxhash(txhash);
				contractLockup.setLogindex(txLog.getLogIndex().intValue());			
				contractLockup.setType(2);
				contractLockup.setLockupnumber(lockupstart);				
				contractLockup.setPickupamount(new BigDecimal(pickupAmount));
				contractLockup.setLockupamount(new BigDecimal(lockupAmount));
			//	contractLockup.setRemainamount(new BigDecimal(remainAmount));
				contractLockup.setLockupperiod(lockupperiod);
				contractLockup.setReleaseperiod(releaseperiod);
				contractLockup.setReleaseinterval(releaseinterval);
				contractLockup.setCreatetime(new Date());				
				contractLockupMapper.saveOrUpdate(contractLockup);
				
				tx.setUfooperator("TlcPickup");
	    		tx.setParam1(address);
	    		tx.setParam3(new BigDecimal(pickupAmount));
	    		tx.setParam4(new BigDecimal(lockupAmount));
	    	//	tx.setParam5(pickupAmount.toString());
	    	}else if(pickupV3Topic.equals(topic)){
	    		isLockupContract = true;
				String address = Constants.PRE + txLog.getTopics().get(1).substring(26);
				String data1 = txLog.getData().substring(0, 66);
				String data2 = txLog.getData().substring(66, 66 + 64);
				String data3 = txLog.getData().substring(66 + 64, 66 + 64 + 64);
				BigInteger pickupAmount = new BigInteger(data1.substring(2), 16);
				Long lockupstart = new BigInteger(data2.substring(2), 16).longValue();				
				BigInteger lockupAmount = new BigInteger(data3.substring(2), 16);
				
				ContractLockup contractLockup = new ContractLockup();
				contractLockup.setContract(contractAddr);
				contractLockup.setAddress(address);
				contractLockup.setTxhash(txhash);
				contractLockup.setLogindex(txLog.getLogIndex().intValue());
				contractLockup.setType(2);
				contractLockup.setLockupnumber(lockupstart);				
				contractLockup.setPickupamount(new BigDecimal(pickupAmount));
				contractLockup.setLockupamount(new BigDecimal(lockupAmount));			
			//	contractLockup.setLockupperiod(lockupperiod);
			//	contractLockup.setReleaseperiod(releaseperiod);
			//	contractLockup.setReleaseinterval(releaseinterval);
				contractLockup.setCreatetime(new Date());				
				contractLockupMapper.saveOrUpdate(contractLockup);
				
				tx.setUfooperator("TlcPickup");
	    		tx.setParam1(address);
	    		tx.setParam3(new BigDecimal(pickupAmount));
	    		tx.setParam4(new BigDecimal(lockupAmount));
	    		tx.setParam5(lockupstart.toString());
	    	}
	//	}		
		if(tx.getUfooperator()!=null && !"".equals(tx.getUfooperator())){
			tx.setUfoprefix("CT");
			tx.setUfoversion("1");
		}
		tx.setContract(contractAddr);
		return isLockupContract;
    }
	
	@SuppressWarnings("rawtypes")
	public ContractLockup parseContractLockup(Contract contract,Transaction tx) {
		if ( contract.getType()!=null && !contract.getType().equals("0") && !contract.getType().equals("1") && !contract.getType().equals("2")) {
			String contractAddr = contract.getContract();
			Long blockNumber = tx.getBlockNumber();
			try{
				List<Type> results = web3jService.getContractTeamItems(contractAddr, blockNumber);
				Long lockupstart = Long.parseLong(results.get(0).getValue().toString());
				Long lockupend = Long.parseLong(results.get(1).getValue().toString());
				Long releaseperiod = Long.parseLong(results.get(2).getValue().toString());
				Long releaseinterval = Long.parseLong(results.get(3).getValue().toString());
				Long releasetimes = Long.parseLong(results.get(4).getValue().toString());
				BigDecimal lockupAmount = new BigDecimal(results.get(5).getValue().toString());
			//	BigDecimal pickupAmount = new BigDecimal(results.get(6).getValue().toString());				
				List<ContractLockup> lockupList = contractLockupMapper.getList(contractAddr,1,lockupstart);
				if(lockupList.size()>0 && !tx.getHash().equals(lockupList.get(0).getTxhash())){					
					ContractLockup contractLockup = lockupList.get(0);					
					log.info("The lockup data "+contractLockup+" has exist");
					contractLockup.setLockupamount(lockupAmount);	
					contractLockupMapper.saveOrUpdate(contractLockup);					
					return contractLockup;
				}
				ContractLockup contractLockup = new ContractLockup();
				contractLockup.setContract(contractAddr);
				contractLockup.setAddress(tx.getFromAddr());
				contractLockup.setTxhash(tx.getHash());
				contractLockup.setLogindex(-1);
				contractLockup.setType(1);				
				contractLockup.setLockupnumber(lockupstart);
				contractLockup.setCancelnumber(lockupend);		
				contractLockup.setLockupamount(lockupAmount);
				contractLockup.setLockupperiod(releasetimes);
				contractLockup.setReleaseperiod(releaseperiod);
				contractLockup.setReleaseinterval(releaseinterval);
			//	contractLockup.setPickupamount(pickupAmount);
				contractLockup.setCreatetime(new Date());
				contractLockupMapper.saveOrUpdate(contractLockup);
				
			//	tx.setUfooperator("TlcLockup");
				tx.setContract(contractAddr);
	    		tx.setParam1(contractLockup.getAddress());
	    		tx.setParam3(new BigDecimal(contractLockup.getLockupnumber()));
	    		tx.setParam4(contractLockup.getLockupamount());
	    		
				return contractLockup;
			} catch (Exception e) {
				log.info("Parse team contract v3 Contract "+contractAddr+" lockup item error :"+e.getMessage());
			}
		}
		return null;
	}

	private boolean parseLockupContractV1(Contract contract, String address){
		try{
			String admin1 = getAddressValue(address, admin1V1Key);
			String admin2 = getAddressValue(address, admin2V1Key);
			BigInteger lockupperiod = getNumericValue(address, lockupperiodKey);
			BigInteger releaseperiod = getNumericValue(address, releaseperiodKey);
			BigInteger releaseinterval = getNumericValue(address,releaseintervalKey);
			
			contract.setType("1");			
			contract.setAdmin1(admin1);
			contract.setAdmin2(admin2);
			contract.setLockupperiod(lockupperiod.longValue());
			contract.setReleaseinterval(releaseinterval.longValue());
			contract.setReleaseperiod(releaseperiod.longValue());			
			return true;
		} catch (Exception e) {
			log.info("Contract "+address+" is not a team lockup contract v1 for :"+e.getMessage());
			return false;
		}
	}
		
	private boolean parseLockupContractV2(Contract contract, String address){
		try{
			String admin1 = getAddressValue(address, admin1V2Key);		
			String admin2 = null;
			try{
				admin2 = getAddressValue(address, admin2V2Key);
			}catch(Exception e){				
			}
			BigInteger lockupstart = getNumericValue(address, lockstartKey);
			BigInteger releaseperiod = getNumericValue(address, releaseperiodKey);
			BigInteger releaseinterval = getNumericValue(address,releaseintervalKey);
			contract.setType("2");
			contract.setAdmin1(admin1);
			contract.setAdmin2(admin2);
		//	contract.setLockupperiod(lockupperiod.longValue());
			contract.setLockupstart(lockupstart.longValue());
			contract.setReleaseinterval(releaseinterval.longValue());
			contract.setReleaseperiod(releaseperiod.longValue());
			return true;
		} catch (Exception e) {
			log.info("Contract "+address+" is not a team lockup contract v2 for :"+e.getMessage());
			return false;
		}
	}
	
	private boolean parseLockupContractV3(Contract contract, String address){
		try{
			String admin1 = getAddressValue(address, admin1V2Key);		
			String type = web3jService.getContractTeamType(address,teamtypeKey);
			String admin2 = null;
			Long lockupstart = null;
			Long releaseperiod = null;
			Long releaseinterval = null;
			try{
				admin2 = getAddressValue(address, admin2V2Key);
				lockupstart = getNumericValue(address, lockstartKey).longValue();
				releaseperiod = getNumericValue(address, releaseperiodKey).longValue();
				releaseinterval = getNumericValue(address,releaseintervalKey).longValue();
			}catch(Exception e){
				log.info("Parse team contract not required param "+e.getMessage());
			}
			contract.setType(type);
			contract.setAdmin1(admin1);
			//not required
			contract.setAdmin2(admin2);
			contract.setLockupstart(lockupstart);
			contract.setReleaseinterval(releaseinterval);
			contract.setReleaseperiod(releaseperiod);			
			return true;
		} catch (Exception e) {
			log.info("Contract "+address+" is not a team lockup contract v3 for :"+e.getMessage());
			return false;
		}
	}

	private String getAddressValue(String contract, String key) {
		String value = web3jService.getContractParam(contract, key);
		if(value==null||"0x".equals(value))
			throw new RuntimeException("Invaild contract "+contract+" param "+key+" address value:"+value);
		return Constants.PRE +  value.substring(26);
	}

	private BigInteger getNumericValue(String contract, String key) {
		String value = web3jService.getContractParam(contract, key);
		if(value==null||"0x".equals(value))
			throw new RuntimeException("Invaild contract "+contract+" param "+key+" numeric value:"+value);
		return new BigInteger(value.substring(2), 16);
	}
        

	protected ContractLockup createContractLockup(String contract, String address, Long lockupnumber, BigDecimal lockupamount, BigDecimal pickupamount, BigDecimal remainamount, 
			Long lockupperiod, Long releaseperiod, Long releaseinterval) {
		ContractLockup contractLockup = new ContractLockup();
		contractLockup.setContract(contract);
		contractLockup.setAddress(address);
		contractLockup.setLockupnumber(lockupnumber);
		contractLockup.setPickupamount(pickupamount);
		contractLockup.setLockupamount(lockupamount);
		contractLockup.setRemainamount(remainamount);
		contractLockup.setLockupperiod(lockupperiod);
		contractLockup.setReleaseperiod(releaseperiod);
		contractLockup.setReleaseinterval(releaseinterval);
		contractLockup.setCreatetime(new Date());		
        return contractLockup;        
	}
	
}
