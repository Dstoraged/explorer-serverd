package com.imooc.job.service;

import com.imooc.pojo.Tokens;
import com.imooc.utils.NumericUtil;
import com.imooc.utils.SampleKeys;
import com.imooc.utils.Web3jUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.AlienSnapshot;
import org.web3j.protocol.core.methods.response.AlienSnapshot.Snapshot;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetBlockTransactionCountByNumber;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthGetStorageAt;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Web3jService {
//	@Value("${ipaddress}")
//	public String ipaddress;

	@Autowired
	private Web3j web3j;
	
	private Logger logger = LoggerFactory.getLogger(Web3jService.class);

	public Web3j getWeb3j(){
		return web3j;
	}

	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_chainId","params": [],"id":1}
	 * @return
	 */
	public BigInteger getChainId() {
		try {
			EthChainId response = web3j.ethChainId().send();
			if (response.hasError())
				logger.info("ethChainId error:" + response.getError().getMessage());
			return response.getChainId();
		} catch (Exception e) {
			logger.warn("ethChainId error", e);
			throw new RuntimeException(e);
		}
	}
	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_gasPrice","params": [],"id":1}
	 * @return
	 */
	public BigInteger getGasPrice() {
		try {
			EthGasPrice response = web3j.ethGasPrice().send();
			if (response.hasError())
				logger.info("ethGasPrice error:" + response.getError().getMessage());
			return response.getGasPrice();
		} catch (Exception e) {
			logger.warn("ethGasPrice error", e);
			throw new RuntimeException(e);
		}
	}
	
	//====================Block====================
	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_blockNumber","params": [],"id":1}
	 * @return
	 */
	public Long getBlockNumber() {
		try {
			EthBlockNumber response = web3j.ethBlockNumber().send();
			if (response.hasError())
				throw new RuntimeException("ethBlockNumber error:" + response.getError().getMessage());
			return response.getBlockNumber().longValue();
		} catch (Exception e) {
			logger.warn("ethBlockNumber error", e);
			throw new RuntimeException(e);
		}
	}
	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_getBlockByNumber","params": ["0x1",true],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public Block getBlockByNumber(Long blockNumber) {
		try {
			EthBlock response = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), true).send();
			if (response.hasError())
				throw new RuntimeException("ethGetBlockByNumber " + blockNumber + " error:" + response.getError().getMessage());
			return response.getResult();
		} catch (Exception e) {
			logger.warn("ethGetBlockByNumber " + blockNumber + " error", e);
			throw new RuntimeException(e);
		}
	}
	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_getBlockByNumber2","params": ["0x1000",true],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public Block getBlockByNumber2(Long blockNumber) {
		try {
			EthBlock response = web3j.ethGetBlockByNumber2(new DefaultBlockParameterNumber(blockNumber), true).send();
			if (response.hasError()) 	
				throw new RuntimeException("ethGetBlockByNumber2 " + blockNumber + " error:" + response.getError().getMessage());		
			return response.getResult();
		} catch (Exception e) {
			logger.warn("ethGetBlockByNumber2 " + blockNumber + " error", e);
			throw new RuntimeException(e);
		}
	}
	/**
	 *
	 * @param blockNumber
	 * @return
	 */
	public Long getBlockTimestamp(Block block){
		BigInteger timestamp = block.getTimestamp();
		return timestamp.longValue();	
	}
	public Long getBlockTimestamp(Long blockNumber){
		BigInteger timestamp = getBlockByNumber(blockNumber).getTimestamp();
		return timestamp.longValue();	
	}
	/**
	 *
	 * @param blockNumber
	 * @return
	 */
	public Date getBlockDate(Block block){
		Long timestamp = getBlockTimestamp(block);
		return new Date(timestamp * 1000);
	}
	public Date getBlockDate(Long blockNumber){
		Long timestamp = getBlockTimestamp(blockNumber);
		return new Date(timestamp * 1000);
	}	
	
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"eth_getUncleByBlockNumberAndIndex","params": ["0x1000","0x0"],"id":1}
	 * @param blockNumber
	 * @param index
	 * @return
	 */
	public Block getUncleByBlockNumber(Long blockNumber, Long index) {
		try{
			DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
			EthBlock response = web3j.ethGetUncleByBlockNumberAndIndex(defaultBlockParameter, BigInteger.valueOf(index)).send();
			if (response.hasError()) 	
				throw new RuntimeException("ethGetUncleByBlockNumberAndIndex " + blockNumber + " of "+index+" error:" + response.getError().getMessage());	
			return response.getResult();
		} catch (Exception e) {
			logger.warn("ethGetUncleByBlockNumberAndIndex " + blockNumber + " of "+index+" error",e);			
			throw new RuntimeException(e);
		}
	}
	
	
	//====================Address====================

	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_getBalance","params": ["...","latest"],"id":1}
	 * @param address
	 * @return
	 */
	public BigInteger getAddressBalance(String address) {
		try {
			EthGetBalance response = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();			
			if (response.hasError()) 
				logger.info("ethGetBalance " + address + " error:" + response.getError().getMessage());			
			return response.getBalance();
		} catch (Exception e) {
			logger.warn("ethGetBalance " + address + " error", e);
			throw new RuntimeException(e);
		}
	}
	public BigInteger getAddressBalance(String address, Long blockNumber) {
		try {
			EthGetBalance response = web3j.ethGetBalance(address, new DefaultBlockParameterNumber(blockNumber)).send();
			if (response.hasError()) 
				logger.info("ethGetBalance " + address + " at " + blockNumber + " error:" + response.getError().getMessage());				
			return response.getBalance();
		} catch (Exception e) {
			logger.warn("ethGetBalance " + address + " at " + blockNumber + " error", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_getTransactionCount","params": ["...","latest"],"id":1}
	 * @param address
	 * @return
	 */
	public BigInteger getAddressNonce(String address) {
		try {
			EthGetTransactionCount response = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
			if (response.hasError())
				logger.info("ethGetTransactionCount " + address + " error:" + response.getError().getMessage());
			return response.getTransactionCount();
		} catch (Exception e) {
			logger.warn("ethGetTransactionCount " + address + " error", e);
			throw new RuntimeException(e);
		}
	}
	public BigInteger getAddressNonce(String address, Long blockNumber) {		
		try {
			EthGetTransactionCount  response = web3j.ethGetTransactionCount(address, new DefaultBlockParameterNumber(blockNumber)).send();
			if (response.hasError())
				logger.info("ethGetTransactionCount " + address + " at " + blockNumber + " error:" + response.getError().getMessage());				
			return response.getTransactionCount();
		} catch (Exception e) {
			logger.warn("ethGetTransactionCount " + address + " at " + blockNumber + " error", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * {"jsonrpc":"2.0","method":"eth_getCode","params": ["...","latest"],"id":1}
	 * @param address
	 * @return
	 */
	public String getAddressCode(String address) {
		try {
			EthGetCode response = web3j.ethGetCode(address, DefaultBlockParameterName.LATEST).send();
			if (response.hasError())
				logger.info("ethGetCode " + address + " error:" + response.getError().getMessage());				
			return response.getCode();
		} catch (Exception e) {
			logger.warn("ethGetCode " + address + " error", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * {"jsonrpc":"2.0","method":"eth_getStorageAt","params": ["...","0","latest"],"id":1}
	 * @param address
	 * @param position
	 * @return
	 */
	public String getStorageAt(String address, BigInteger position){
		try{
			EthGetStorageAt response = web3j.ethGetStorageAt(address, position, DefaultBlockParameterName.LATEST).send();
			if (response.hasError()) 
				logger.info("ethGetStorageAt " + address + " error:" + response.getError().getMessage());				
			return response.getData();
		} catch (Exception e) {
			logger.warn("ethGetStorageAt " + address + " error",e);			
			throw new RuntimeException(e);
		}
	}
	
	
	
	//=========================Transaction============================		
	
	//@ChainProxyController.getTransactionReceipt
	//@ExplorerJob.getTransactionReceipt
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"eth_getTransactionReceipt","params": ["..."],"id":1}
	 * @param transactionHash
	 * @return
	 */
	public TransactionReceipt getTransactionReceipt(String transactionHash){
		try {
			EthGetTransactionReceipt response = web3j.ethGetTransactionReceipt(transactionHash).send();
			if (response.hasError()) 	
				logger.info("ethGetTransactionReceipt " + transactionHash + " error:" + response.getError().getMessage());			
			return response.getResult();
		} catch (Exception e) {
			logger.warn("ethGetTransactionReceipt " + transactionHash + " error", e);
			throw new RuntimeException(e);
		}
	}
	//@TransactionController.checkTransactionStatus
	public String checkTransactionStatus(String transactionHash){
		TransactionReceipt receipt = getTransactionReceipt(transactionHash);
		return receipt.getStatus();		
	}
	
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"eth_getTransactionByHash","params": ["..."],"id":1}
	 * @param transactionHash
	 * @return
	 */
	public org.web3j.protocol.core.methods.response.Transaction getTransactionByHash(String transactionHash){
		try {
			EthTransaction response = web3j.ethGetTransactionByHash(transactionHash).send();
			if (response.hasError()) 	
				logger.info("ethGetTransactionByHash "+transactionHash+" error:"+response.getError().getMessage());			
			return response.getResult();
		} catch (Exception e) {
			logger.warn("ethGetTransactionByHash "+transactionHash+" error", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"eth_getBlockTransactionCountByNumber","params": ["0x1000"],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public BigInteger getBlockTransactionCountByNumber(Long blockNumber){
		try{
			EthGetBlockTransactionCountByNumber response = web3j.ethGetBlockTransactionCountByNumber(new DefaultBlockParameterNumber(blockNumber)).send();
			if (response.hasError()) 	
				logger.info("ethGetBlockTransactionCountByNumber "+blockNumber+" error:"+response.getError().getMessage());			
			return response.getTransactionCount();
		} catch (Exception e) {
			logger.warn("ethGetBlockTransactionCountByNumber "+blockNumber+" error",e);			
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * {"jsonrpc":"2.0","method":"eth_getTransactionByBlockNumberAndIndex","params": ["0x1000","0x0"],"id":1}
	 * @param blockNumber
	 * @param index
	 * @return
	 */
	public org.web3j.protocol.core.methods.response.Transaction getTransactionByBlockNumberAndIndex(Long blockNumber, Long index){
		try{	
			EthTransaction response = web3j.ethGetTransactionByBlockNumberAndIndex(new DefaultBlockParameterNumber(blockNumber), BigInteger.valueOf(index)).send();			
			if (response.hasError()) 	
				logger.info("ethGetTransactionByBlockNumberAndIndex "+blockNumber+" of "+index+" error:"+response.getError().getMessage());			
			return response.getResult();
		} catch (Exception e) {
			logger.warn("ethGetTransactionByBlockNumberAndIndex "+blockNumber+" of "+index+" error",e);			
			throw new RuntimeException(e);
		}
	}


	public String sendRawTransaction(String signedTransactionData){
		try{
			EthSendTransaction response = web3j.ethSendRawTransaction(signedTransactionData).send();
			if (response.hasError()) 	
				logger.info("ethSendRawTransaction error:"+response.getError().getMessage());			
			return response.getResult();
		} catch (Exception e) {
			logger.warn("ethSendRawTransaction error", e);
			throw new RuntimeException(e);
		}
	}
	
	
	//=========================Snapshot============================	

	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getSnapshotSignerAtNumber","params": [1314350],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public Snapshot getSnapshotSigner(Long blockNumber){
		try {
			AlienSnapshot response = web3j.alienSnapshotSignerAtNumber(blockNumber.intValue()).send();
			if (response.hasError() || response.getSnapshot() == null){
				logger.info("alienSnapshotSignerAtNumber "+blockNumber+" error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot();			
		} catch (Exception e) {
			logger.warn("getSnapshotSigners error",e);			
			throw new RuntimeException(e);
		}
	}
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getSRTBalance","params": ["..."],"id":1}
	 * @param address
	 * @return
	 */
	public BigInteger getAddrSrtBalance(String address){		
		try {
			AlienSnapshot response = web3j.alienSRTBalance(address).send();
			if (response.hasError() || response.getSnapshot() == null){
				logger.info("alienSRTBalance "+address+" error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getAddrsrtbal();			
		} catch (Exception e) {
			logger.warn("alienSRTbalAtNumber error",e);	
			throw new RuntimeException(e);
		}
	}
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getSRTbalAtNumber","params": [1000000],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public Map<String, Object> getAllSrtBalance(Long blockNumber){		
		try {
			AlienSnapshot response = web3j.alienSRTbalAtNumber(blockNumber.intValue()).send();
			if (response.hasError() || response.getSnapshot() == null){
				logger.info("alienSRTbalAtNumber "+blockNumber+" error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getSrtbal();			
		} catch (Exception e) {
			logger.warn("alienSRTbalAtNumber error",e);	
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getSnapshotReleaseAtNumber","params": [10000,"rewardlock"],"id":1}
	 * @param blockNumber
	 * @param type   rewardlock/candidatepledge/flowminerpledge/
	 * @return
	 */
	public Snapshot getSnapshotRelease(Long blockNumber,String type){
		try {
			AlienSnapshot response = web3j.alienSnapshotReleaseAtNumber(blockNumber.intValue(),type).send();
			if (response.hasError() || response.getSnapshot() == null){
				logger.info("alienSnapshotReleaseAtNumber "+blockNumber+" error:"+response.getError().getMessage());
				return null;
			}
			Snapshot snapshot = response.getSnapshot();
		//	snapshot.getFlowrevenve();			
		//	snapshot.getCandidatepledge();
		//	snapshot.getFlowminerpledge();
			return snapshot;
		} catch (Exception e) {
			logger.warn("alienSnapshotReleaseAtNumber error",e);			
			throw new RuntimeException(e);
		}
	}
	
	public Map<String, Map<String, Map<String, Object>>> getRevenveRelease(Long blockNumber, String type) {
		try {
			int step = 200000;
			Integer startblock = 0;
			Integer endblock = 0;
			Map<String, Map<String, Map<String, Object>>> revenveRelease = new HashMap<>();
			for (int i = 0; i <= (blockNumber / step ); i++) {
				startblock = endblock + 1;
				endblock = (i + 1) * step > blockNumber ? blockNumber.intValue() : ((i + 1) * step);
				AlienSnapshot response = web3j.alienSnapshotReleaseAtNumber2(blockNumber.intValue(), type, startblock, endblock).send();
				if (response.hasError() || response.getSnapshot() == null) {
					logger.info("alienSnapshotReleaseAtNumber2 " + blockNumber + " error:" + response.getError().getMessage());
					return null;
				}
				Map<String, Map<String, Map<String, Object>>> map = response.getSnapshot().getFlowrevenve();
				for (String address : map.keySet()) {
					Map<String, Map<String, Object>> increaseData = map.get(address);
					Map<String, Object> increaseLockbalance = increaseData.get("lockbalance");
					Map<String, Map<String, Object>> fullyData = revenveRelease.get(address);
					if (fullyData == null) {
						fullyData = new HashMap<>();
						revenveRelease.put(address, fullyData);
					}
					Map<String, Object> fullLockbalance = fullyData.get("lockbalance");
					if (fullLockbalance == null) {
						fullLockbalance = new HashMap<>();
						fullyData.put("lockbalance", fullLockbalance);
					}		
					fullLockbalance.putAll(increaseLockbalance);
				}
			}
			return revenveRelease;
		} catch (Exception e) {
			logger.warn("getRevenveRelease error", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getStorageRewardAtNumber","params": [9000,"rewardlock"],"id":1}
	 * @param blockNumber
	 * @param type  spaceLock/leaseLock/blockLock/revertLock
	 * @return
	 */
	public Map<String, Object> getStorageReward(Long blockNumber,String type){	
		try {
			AlienSnapshot response = web3j.alienStorageRewardAtNumber(blockNumber.intValue(), type).send();
			if (response.hasError() || response.getSnapshot()==null){
				logger.info("alienStorageRewardAtNumber "+blockNumber+" error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getStoragereward();			
		} catch (Exception e) {
			logger.warn("alienStorageRewardAtNumber error",e);			
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getSPledgeAtNumber","params": [1000000],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public Map<String, Map<String, Object>> getSPledge(Long blockNumber){
		try {
			AlienSnapshot response = web3j.alienSPledgeAtNumber(blockNumber.intValue()).send();
			if (response.hasError() || response.getSnapshot() == null) {
				logger.info("alienSPledgeAtNumber " + blockNumber + " error:" + response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getSpledge();
		} catch (Exception e) {
			logger.warn("alienSPledgeAtNumber error", e);
			throw new RuntimeException(e);
		}
	}
		
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getStorageRatiosAtNumber","params": [1086],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public Map<String, Object> getStorageRatios(Long blockNumber){		
		try {
			AlienSnapshot response = web3j.alienStorageRatiosAtNumber(blockNumber.intValue()).send();
			if (response.hasError() || response.getSnapshot() == null){
		//		logger.info("alienStorageRatiosAtNumber "+blockNumber+" error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getRatios();			
		} catch (Exception e) {
			logger.warn("alienStorageRatiosAtNumber error",e);	
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *
	 * {"jsonrpc":"2.0","method":"alien_getSPledgeCapVerAtNumber","params": [8640],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public Map<String, Object> getSPledgeCapVer(Long blockNumber){		
		try {
			AlienSnapshot response = web3j.alienSPledgeCapVerAtNumber(blockNumber.intValue()).send();
			if (response.hasError() || response.getSnapshot() == null){
				logger.info("alienSPledgeCapVerAtNumber "+blockNumber+" error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getSpledgecapver();			
		} catch (Exception e) {
			logger.warn("alienSPledgeCapVerAtNumber error",e);	
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * {"jsonrpc":"2.0","method":"alien_getPaysAtNumber","params": [8640],"id":1}
	 * @param blockNumber
	 * @return
	 */
	public List<Map<String, Object>> getPays(Long blockNumber){		
		try {
			AlienSnapshot response = web3j.alienPaysAtNumber(blockNumber.intValue()).send();
			if (response.hasError() || response.getSnapshot() == null){
				logger.info("alienPaysAtNumber "+blockNumber+" error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getPays();			
		} catch (Exception e) {
			logger.warn("alienPaysAtNumber error",e);	
			throw new RuntimeException(e);
		}
	}
	
	
	public Map<String, Object> getStgbandwidthmakeup(){
		try {
			AlienSnapshot response = web3j.alienSTGBandwidthMakeup().send();
			if (response.hasError() || response.getSnapshot() == null){
				logger.info("alienSTGBandwidthMakeup error:"+response.getError().getMessage());
				return null;
			}
			return response.getSnapshot().getStgbandwidthmakeup();			
		} catch (Exception e) {
			logger.warn("alienSTGBandwidthMakeup error",e);	
			throw new RuntimeException(e);
		}
	}
	
	public Long getSnapshotPeriod(){
        try {
        	int blockNumber = getBlockNumber().intValue();
        	Snapshot snap = web3j.alientSnapshotByNumber(blockNumber).send().getSnapshot();        	
        	return snap.getPeriod().longValue();        	
        } catch (Exception e) {
			logger.warn("alientSnapshotByNumber error",e);			
			throw new RuntimeException(e);
		}
    }
	
	
	
	
	
	//==========================Contract===========================
	//@ExplorerJob.getAddressTokenBalance
	public BigInteger getAddressTokenBalance(String contract, String address) {		
        try {
        	Function function = new Function("balanceOf", Arrays.asList(new Address(address)), Arrays.asList(new TypeReference<Address>() {}));
            String encode = FunctionEncoder.encode(function);
            Transaction ethCallTransaction = Transaction.createEthCallTransaction(address, contract, encode);
        	EthCall ethResult = web3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST).send();
        	String value = ethResult.getValue();        	
        	return NumericUtil.convertToBigInteger(value);            
        } catch (IOException e) {
        	logger.warn("ethCall error", e);
			throw new RuntimeException(e);
        }
    }	

	/**
	 * 
	 * @param contract
	 * @return
	 */
	public Tokens getERC20Properties(String contract){
		Tokens properties = new Tokens();
		try{			
			ERC20 metadata = ERC20.load(contract, web3j, SampleKeys.CREDENTIALS, null);
	    	String name = metadata.name().send();
	    	properties.setName(name);
	    	String symbol = metadata.symbol().send();
	    	properties.setSymbol(symbol);
	    	BigInteger decimals = metadata.decimals().send();
	    	properties.setDecimals(decimals.intValue());
	    	BigInteger totalSupply = metadata.totalSupply().send();	    	
	    	properties.setTotalSupply(new BigDecimal(totalSupply));	    	
		}catch(Exception e) {
			logger.info("Contract "+contract+" is not like erc20 :"+e.getMessage());			
		}
		return properties;
	}
	
	@SuppressWarnings("rawtypes")
	public String getTokenIdOwner(String contract, BigInteger tokenId){
		Function function = new Function("ownerOf", Arrays.<Type>asList(new Uint256(tokenId)), Collections.<TypeReference<?>>emptyList());
		String data = FunctionEncoder.encode(function);
		String result = getContractParam(contract, data);
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	public List<Type> getTransferBatchDatas(String data){
		Event event = new Event("TransferBatch", Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, 
				new TypeReference<Address>(true) {}, new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
		List<Type> nonIndexedValues = FunctionReturnDecoder.decode(data, event.getNonIndexedParameters());
		return nonIndexedValues;
	}
	
	@SuppressWarnings("rawtypes")
	public String getContractTeamType(String contract,String paramKey){
		try{			
			Function function = new Function("teamtype", Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
			String value = getContractParam(contract,paramKey);
			List<Type> types = FunctionReturnDecoder.decode(value, function.getOutputParameters());
			if (types == null || types.size() == 0)
				throw new RuntimeException("Parse contract teamtype types value failed");
			return types.get(0).getValue().toString();
		}catch(Exception e){
			throw new RuntimeException("Parse contract teamtype error:"+e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<Type> getContractTeamItems(String contract,Long blockNumber){
		try {
			Function function = new Function("lockUpItem", Arrays.<Type>asList(new Uint256(blockNumber)), Arrays.<TypeReference<?>>asList(
					new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, 
					new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
			String data = FunctionEncoder.encode(function);
			String value = getContractParam(contract,data);
			List<Type> results = FunctionReturnDecoder.decode(value, function.getOutputParameters());
			return results;
		} catch (Exception e) {
			logger.warn("getContractTeamItems", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param contract
	 * @param paramKey
	 * @return
	 */
	public String getContractParam(String contract, String paramKey){
		try{
			Transaction tx = new Transaction(null, null, null, null, contract, null, paramKey);
			EthCall send = web3j.ethCall(tx, DefaultBlockParameterName.LATEST).send();
			return send.getValue();
		}catch(Exception e){
			logger.info("ethCall error", e);
			throw new RuntimeException(e);
		}
	}
	
	
	
	//@ChainProxyController.ethCall
	public String ethTransaction(String from, String to, BigInteger value){
		try{
			BigInteger nonce = getAddressNonce(from);
			BigInteger gasPrice = getGasPrice();
			BigInteger gasLimit = null;
			BigInteger valueWei = Convert.toWei(new BigDecimal(value), Convert.Unit.ETHER).toBigInteger();			
			Transaction transaction = Transaction.createEtherTransaction(from, nonce, gasPrice, gasLimit, to, valueWei);
			EthCall response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
			if(response.hasError())
				throw new RuntimeException(response.getError().getMessage());
			return response.getResult();			
		} catch (IOException e) {
			logger.warn("ethCall error", e);
			throw new RuntimeException(e);
		}
	}	
			

	//@ChainProxyController.estimateGas
	public EthEstimateGas estimateGas(String fromHash, String toHash, BigInteger value){
		try{
			BigInteger nonce = getAddressNonce(fromHash);
			BigInteger gasPrice = getGasPrice();
			BigInteger values = Convert.toWei(new BigDecimal(value), Convert.Unit.ETHER).toBigInteger();
			Transaction transaction = Transaction.createEtherTransaction(fromHash, nonce, gasPrice, null, toHash, values);
			EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
			if (ethEstimateGas.hasError()) {
				throw new RuntimeException(ethEstimateGas.getError().getMessage());
			}
			return ethEstimateGas;
		} catch (IOException e) {
			logger.warn("ethEstimateGas error", e);
			throw new RuntimeException(e);
		}
	}
	
	
	
	public static void main(String[] args) throws IOException{
	//	Web3jService web3jService = new Web3jService();
	//	web3jService.ipaddress = "http://192.168.9.203:8545";
		/*Block block = web3j.getBlockByNumber(5636L);
		List<TransactionResult> transactions = block.getTransactions();
		for(TransactionResult transactionResult : transactions){
			TransactionObject transaction = (EthBlock.TransactionObject) transactionResult;
			TransactionReceipt receipt = web3j.getTransactionReceipt(transaction.getHash());			
			List<Log> logs = receipt.getLogs();
			for(Log log : logs){
				List<String> topics = log.getTopics();
				for(String topic : topics){
					System.out.println(Numeric.decodeQuantity(topic));
				}
			}
		}*/
		
		Web3j web3j = Web3jUtils.getWeb3j("http://192.168.9.102:8545");
		Snapshot snapshot = web3j.alienSPledgeAtNumber(8990).send().getSnapshot();
	//	Snapshot snapshot = web3j.alienStorageRatiosAtNumber(8980).send().getSnapshot();
	//	System.out.print(snapshot);
		Map<String, Map<String, Object>> spledge = snapshot.getSpledge();
		System.out.print(spledge);
	}
	
}
