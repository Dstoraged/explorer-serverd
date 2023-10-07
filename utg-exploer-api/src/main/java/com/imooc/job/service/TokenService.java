package com.imooc.job.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.ContractMapper;
import com.imooc.mapper.TokenMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.mapper.TransferTokenMapper;
import com.imooc.pojo.Contract;
import com.imooc.pojo.Tokens;
import com.imooc.pojo.TransToken;
import com.imooc.pojo.Transaction;
import com.imooc.utils.Constants;
import com.imooc.utils.NumericUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenService {

	@Autowired
	TokenMapper tokenMapper;
	@Autowired
    ContractMapper contractMapper;
	@Autowired
	AddressService addressService;
	@Autowired
    AccountMapper addressMapper;
	@Autowired
	TransferTokenMapper transferTokenMapper;
	@Autowired
	TransactionMapper transactionMapper;
	@Autowired
	Web3jService web3jService;
	
	private static String erc20TransferTopic  = Constants.PRE + "ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
	private static String erc721TransferTopic = Constants.PRE + "ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
	private static String erc1155TransferTopic= Constants.PRE + "c3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62";
	private static String erc1155BatchTransferTopic = Constants.PRE + "4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb";
	
	public Tokens getToken(String contractAddr){
    	return tokenMapper.getTokens(contractAddr);
    }
	
	public Tokens buildToken(TransactionReceipt receipt, Transaction tx) {
		String address = receipt.getContractAddress();
		if (address == null)
			return null;
		Tokens token = null;
		if (token == null)
			token = parseERC1155Token(address);
		if (token == null)
			token = parseERC721Token(address);
		if (token == null)
			token= parseERC20Token(address);
		if (token == null) {
			log.info("contract " + address + " is not a valid token.");
			return null;
		}		
		setTokenContract(token,tx.getBlockNumber(),receipt);		
		tx.setToAddr(receipt.getContractAddress());
		tx.setUfoprefix("CT");
		tx.setUfoversion("1");
		tx.setUfooperator("TokenNew");
		
		return token;
    }

	
	public boolean parseTransaction(Tokens token, Transaction tx, Log logItem) {
		if (logItem == null || logItem.getTopics() == null || logItem.getTopics().size() == 0)
			return false;
		String contractAddress = tx.getToAddr();
		String txhash = tx.getHash();
		List<String> topics = logItem.getTopics();
		String topFilter = topics.get(0);
	//	List<String> tokenAddresses = new ArrayList<>();
		if (token.getType() == 0) {			//ERC20
			token = parseERC20Token(contractAddress);
			if (token == null) {
				log.warn("parse erc20 transaction failed for contract " + contractAddress + " in tx "+txhash);
				return false;
			} else if (erc20TransferTopic.equalsIgnoreCase(topFilter)) {
				transferERC20(tx, logItem, token);				
			} else{
				log.info("Unprocessed erc20 transaction for contract " + contractAddress + " in tx "+txhash +" and topic="+topFilter);
				return false;
			}
		} else if (token.getType() == 1) {	//ERC721
			token = parseERC721Token(contractAddress);
			if (token == null) {
				log.warn("parse erc721 transaction failed for " + contractAddress+ " in tx "+txhash);
				return false;
			} else if (erc721TransferTopic.equalsIgnoreCase(topFilter)) {
				transferERC721(tx, logItem, token);
			} else{
				log.info("Unprocessed erc721 transaction for contract " + contractAddress + " in tx "+txhash+" and topic="+topFilter);
				return false;
			}
		} else if (token.getType() == 2) {	//ERC1155
			token = parseERC1155Token(contractAddress);
			if (token == null) {
				log.warn("parse erc721 token transaction failed for " + contractAddress+ " in tx "+txhash);
				return false;
			} else if (erc1155TransferTopic.equalsIgnoreCase(topFilter)) {
				transferERC1155(tx, logItem, token);				
			} else if (erc1155BatchTransferTopic.equalsIgnoreCase(topFilter)) {
				batchTransferERC1155(tx, logItem, token);				
			} else{
				log.info("Unprocessed erc1155 transaction for contract " + contractAddress + " in tx "+txhash+" and topic="+topFilter);
				return false;
			}
		}	
		Contract contract = contractMapper.getContract(contractAddress);
		if(contract==null)
			setTokenContract(token,tx.getBlockNumber(),null);
		
		return true;
	}
	
	
	private void setTokenContract(Tokens token,Long blockNumber, TransactionReceipt receipt) {
		String address = token.getContract();
	//	Long blockNumber = receipt.getBlockNumber().longValue();
		Contract contract = new Contract();
		contract.setContract(address);
		if(receipt!=null){
			contract.setTxhash(receipt.getTransactionHash());
			contract.setAuthor(receipt.getFrom());
			contract.setBlocknumber(receipt.getBlockNumber().longValue());
		}
		contract.setType("0");
		contract.setCreatetime(new Date());
		contract.setName(token.getName());
		contract.setSymbol(token.getSymbol());
		contract.setIstoken(1);
		contractMapper.saveOrUpdate(contract);
		addressService.saveOrUpdateAddress(address, blockNumber);
		addressMapper.setAsContract(address,blockNumber);
	}
	
	//===========================ERC20================================
	
	private Tokens parseERC20Token(String contract){
    	try{
    		Tokens properties = web3jService.getERC20Properties(contract);
    		if(properties.getName()==null||properties.getSymbol()==null||properties.getDecimals()==null||properties.getTotalSupply()==null)
    			return null;	        
	        Tokens token = new Tokens();
	        token.setContract(contract);
	        token.setType(0);
	        token.setName(properties.getName());
	        token.setSymbol(properties.getSymbol());
	        token.setDecimals(properties.getDecimals());
	        token.setTotalSupply(properties.getTotalSupply());
	        token.setCataloged(0);
	        tokenMapper.saveOrUpdate(token);
	        return token;
    	}catch(Exception e){
    		log.info("vaild contract "+contract+" as erc20 failed:"+e.getMessage());
    		return null;
    	}       
    }
	
	private void transferERC20(Transaction tx, Log logItem, Tokens token) {
		List<String> topics = logItem.getTopics();
		String contract = logItem.getAddress();
		String from = truncateAddress(topics.get(1));
		String to = truncateAddress(topics.get(2));	
		BigDecimal amount = NumericUtil.convertToBigDecimal(logItem.getData(),BigDecimal.ZERO);
		
		TransToken item = new TransToken();
		item.setTransHash(logItem.getTransactionHash());
		item.setLoginIndex(logItem.getLogIndex().intValue());
		item.setCoinType(token.getType());		
		item.setFromAddr(from);
        item.setToAddr(to);        
        item.setAmount(amount);
        item.setContract(contract);
        item.setBlockHash(logItem.getBlockHash());
        item.setBlockNumber(logItem.getBlockNumber().longValue());
    //    item.setTokenId(tokenId);
    //    item.setValue(value);
		item.setGasUsed(tx.getGasUsed());
		item.setGasPrice(tx.getGasPrice());
		item.setGasLimit(tx.getGasLimit());
		item.setNonce(tx.getNonce());
		item.setTimeStamp(tx.getTimeStamp());
        transferTokenMapper.insertOrUptate(item);
        
        tx.setContract(contract);
    //    tx.setToAddr(to);
        tx.setUfoprefix("CT");
		tx.setUfoversion("1");
        tx.setUfooperator("TokenTransfer");
        tx.setParam1(to);
        
        List<String> tokenAddresses = new ArrayList<>();
        tokenAddresses.add(from);
        tokenAddresses.add(to);        
        addressService.handleTokenBalance(contract, tokenAddresses, tx.getBlockNumber());
	}
	
	
	//===========================ERC721================================
	
	private Tokens parseERC721Token(String contract) {
		try {
			String paramKey = "0x01ffc9a780ac58cd00000000000000000000000000000000000000000000000000000000";
			String result = web3jService.getContractParam(contract, paramKey);
			String v = result.substring(result.length() - 1);
			if (!"1".equals(v))
				throw new RuntimeException("result " + result + " is mismatch format:last character is 1");
			Tokens token = new Tokens();
			token.setContract(contract);
			token.setType(1);
			token.setCataloged(0);
			Tokens properties = web3jService.getERC20Properties(contract);			
			token.setName(properties.getName());
			token.setSymbol(properties.getSymbol());
			token.setDecimals(properties.getDecimals());
		//    token.setTotalSupply(properties.getTotalSupply());		
			BigDecimal totalSupply = getERC721TotalSupply(contract);
			token.setTotalSupply(totalSupply);
			tokenMapper.saveOrUpdate(token);
			return token;
		} catch (Exception e) {
			log.info("vaild contract " + contract + " as erc721 failed:" + e.getMessage());
			return null;
		}
	}
	
	private BigDecimal getERC721TotalSupply(String contract) {
		try {
			String paramKey = "0x18160ddd";
			String result = web3jService.getContractParam(contract, paramKey);
			if(result==null)
				return null;
			BigInteger value = new BigInteger(result.substring(2), 16);
			return new BigDecimal(value);
		} catch (Exception e) {
			log.warn("get contract " + contract + " erc1155 totalSupply failed" ,e);
			return null;
		}
	}
	
	private void transferERC721(Transaction tx, Log logItem, Tokens token) {
		List<String> topics = logItem.getTopics();
		String contract = logItem.getAddress();
		String from = truncateAddress(topics.get(1).substring(26));
		String to = truncateAddress(topics.get(2).substring(26));
		BigInteger tokenId = new BigInteger(topics.get(3).substring(2), 16);
	//	BigDecimal amount = NumericUtil.convertToBigDecimal(logItem.getData(),BigDecimal.ZERO);
		String owner = web3jService.getTokenIdOwner(contract, tokenId);
		if (owner != null)
			owner = Constants.PRE + owner.substring(26);
		TransToken item = new TransToken();
		item.setTransHash(logItem.getTransactionHash());
		item.setLoginIndex(logItem.getLogIndex().intValue());
		item.setCoinType(token.getType());		
		item.setFromAddr(from);
	    item.setToAddr(to);        
	//    item.setAmount(amount);
	    item.setContract(contract);
	    item.setBlockHash(logItem.getBlockHash());
	    item.setBlockNumber(logItem.getBlockNumber().longValue());
	    item.setTokenId(tokenId);
	 //   item.setValue(new BigDecimal(value));
	 //   item.setOperator(operator);
		item.setGasUsed(tx.getGasUsed());
		item.setGasPrice(tx.getGasPrice());
		item.setGasLimit(tx.getGasLimit());
		item.setNonce(tx.getNonce());
		item.setTimeStamp(tx.getTimeStamp());		
		item.setOwner(owner);
		transferTokenMapper.insertOrUptate(item);
		
		tx.setContract(contract);
     //   tx.setToAddr(to);
        tx.setUfoprefix("CT");
		tx.setUfoversion("1");
        tx.setUfooperator("TokenTransfer");
        tx.setParam1(to);
        
        List<String> tokenAddresses = new ArrayList<>();
        tokenAddresses.add(from);
        tokenAddresses.add(to);        
        addressService.handleTokenBalance(contract, tokenAddresses, tx.getBlockNumber());
    }
	
	
	//===========================ERC1155================================
	
	private Tokens parseERC1155Token(String contract) {
		try {
			String paramKey = "0x01ffc9a7d9b67a2600000000000000000000000000000000000000000000000000000000";
			String result = web3jService.getContractParam(contract, paramKey);
			String v = result.substring(result.length() - 1);
			if (!"1".equals(v))
				throw new RuntimeException("result " + result + " is mismatch format:last character is 1");
			Tokens token = new Tokens();
			token.setContract(contract);
			token.setType(2);
			token.setCataloged(0);
			Tokens properties = web3jService.getERC20Properties(contract);			
			token.setName(properties.getName());
	        token.setSymbol(properties.getSymbol());
	        token.setDecimals(properties.getDecimals());
	    //    token.setTotalSupply(properties.getTotalSupply());
			BigDecimal totalSupply = getERC1155TotalSupply(contract);
			token.setTotalSupply(totalSupply);
			tokenMapper.saveOrUpdate(token);
			
			return token;
		} catch (Exception e) {
			log.info("vaild contract " + contract + " as erc1155 failed:" + e.getMessage());
			return null;
		}
	}
	
	private BigDecimal getERC1155TotalSupply(String contract){
		try{
			String paramKey = "0x18160ddd";
			String result = web3jService.getContractParam(contract, paramKey);
			if(result==null)
				return null;
			BigInteger value = new BigInteger(result.substring(2), 16);
			return new BigDecimal(value);
		}catch(Exception e){
			log.warn("get contract " + contract + " erc1155 totalSupply failed" ,e);
			return null;
		}
	}
	
	private void transferERC1155(Transaction tx, Log logItem, Tokens token) {
		List<String> topics = logItem.getTopics();
		String contract = logItem.getAddress();
		String operator = truncateAddress(topics.get(1).substring(26));
		String from = truncateAddress(topics.get(2).substring(26));
		String to = truncateAddress(topics.get(3).substring(26));
		BigInteger tokenId = new BigInteger(logItem.getData().substring(2, 66), 16);
		BigInteger value = new BigInteger(logItem.getData().substring(66), 16);
	//	BigDecimal amount = NumericUtil.convertToBigDecimal(logItem.getData(),BigDecimal.ZERO);
		
		TransToken item = new TransToken();
		item.setTransHash(logItem.getTransactionHash());
		item.setLoginIndex(logItem.getLogIndex().intValue());
		item.setCoinType(token.getType());		
		item.setFromAddr(from);
	    item.setToAddr(to);        
	 //   item.setAmount(amount);
	    item.setContract(contract);
	    item.setBlockHash(logItem.getBlockHash());
	    item.setBlockNumber(logItem.getBlockNumber().longValue());
	    item.setTokenId(tokenId);
	    item.setValue(new BigDecimal(value));
	    item.setOperator(operator);
		item.setGasUsed(tx.getGasUsed());
		item.setGasPrice(tx.getGasPrice());
		item.setGasLimit(tx.getGasLimit());
		item.setNonce(tx.getNonce());
		item.setTimeStamp(tx.getTimeStamp());
		
		transferTokenMapper.insertOrUptate(item);
		
		tx.setContract(contract);
    //    tx.setToAddr(to);
        tx.setUfoprefix("CT");
		tx.setUfoversion("1");
        tx.setUfooperator("TokenTransfer");     
        tx.setParam1(to);
        
//        List<String> tokenAddresses = new ArrayList<>();
//        tokenAddresses.add(from);
//        tokenAddresses.add(to);
//        addressService.handleTokenBalance(contract, tokenAddresses, tx.getBlockNumber());
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void batchTransferERC1155(Transaction tx, Log logItem, Tokens token) {
		List<String> topics = logItem.getTopics();
		String contract = logItem.getAddress();
        String operator=Constants.PRE+topics.get(1).substring(26);
        String from=Constants.PRE+topics.get(2).substring(26);
        String to=Constants.PRE+topics.get(3).substring(26);
		Event event = new Event("TransferBatch", Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, 
					new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));				
		List<Type> nonIndexedValues = FunctionReturnDecoder.decode(logItem.getData(), event.getNonIndexedParameters());
		if(nonIndexedValues.size()>=2){
			List<Uint256> tokenIds = (List<Uint256>) nonIndexedValues.get(0).getValue();
			List<Uint256> values = (List<Uint256>) nonIndexedValues.get(1).getValue();
		//	BigDecimal amount = NumericUtil.convertToBigDecimal(logItem.getData(),BigDecimal.ZERO);
			for (int i = 0; i < tokenIds.size(); i++) {
				BigInteger tokenId = tokenIds.get(i).getValue();
	            BigInteger value=values.get(i).getValue();
	            
	            TransToken item = new TransToken();
	        	item.setTransHash(logItem.getTransactionHash());
	        	item.setLoginIndex(logItem.getLogIndex().intValue());
	        	item.setCoinType(token.getType());		
	        	item.setFromAddr(from);
	            item.setToAddr(to);        
	        //    item.setAmount(amount);
	            item.setContract(contract);
	            item.setBlockHash(logItem.getBlockHash());
	            item.setBlockNumber(logItem.getBlockNumber().longValue());
	            item.setTokenId(tokenId);
	            item.setValue(new BigDecimal(value));
	            item.setOperator(operator);
	        	item.setGasUsed(tx.getGasUsed());
	        	item.setGasPrice(tx.getGasPrice());
	        	item.setGasLimit(tx.getGasLimit());
	        	item.setNonce(tx.getNonce());
	        	item.setTimeStamp(tx.getTimeStamp());
	        		
	        	transferTokenMapper.insertOrUptate(item);
	        }			
			
//	        List<String> tokenAddresses = new ArrayList<>();
//	        tokenAddresses.add(from);
//	        tokenAddresses.add(to);
//	        addressService.handleTokenBalance(contract, tokenAddresses, tx.getBlockNumber());
		}
		
		tx.setContract(contract);
     //   tx.setToAddr(to);
        tx.setUfoprefix("CT");
		tx.setUfoversion("1");
        tx.setUfooperator("TokenTransfer");
        tx.setParam1(to);
    }
	
	
	
	
	
	
	
	
	public void erc20Transaction(Transaction tx, Log logItem, Map<String, Map<String, Boolean>> erc20Address){
        TransToken token = null;
        if(tx.getToAddr()!=null ){
			token = saveTransTokenFromLog(tx, logItem, 0);
           // saveTransactionErc20(timestamp, transaction, receipt,logItem) ;
            Map<String, Boolean> targetMap = erc20Address.get(logItem.getAddress());
            if(null ==targetMap){
                targetMap = new HashMap<String, Boolean> ();
                erc20Address.put(logItem.getAddress(),targetMap);
            }
            if (null != token) {
                erc20Address.put(logItem.getAddress(), targetMap);
            }                                        
            if(token.getCoinType()==0){
                handleERC20Token(logItem.getAddress());
            }
            if(null !=targetMap){
                targetMap.put (token.getFromAddr (), false);
                targetMap.put (token.getToAddr(), false);
            }
        }
	}
	
    /*Get ERC20 tokens*/
    private boolean handleERC20Token(String contract) {
        Tokens token = tokenMapper.getTokens(contract);
        if(null !=token)
            return false;
        try{
        //	ERC20 metadata = web3jService.getERC20Metadata(contract);
			Tokens properties = web3jService.getERC20Properties(contract);
			if (properties.getName() == null)
				return false;
			return saveToken(contract, 0, properties.getName(), properties.getSymbol(), properties.getDecimals(), properties.getTotalSupply(), null, null);
        }catch(Exception e){
        	log.info("parse erc20 param for "+contract+" failed:"+e.getMessage());
        	return false;
        }        
    }

    private boolean saveToken(String contract,Integer type, String name, String symbal, int decimal, BigDecimal totalSupply ,String description,String contractManager) {
        Tokens item = new Tokens();
        item.setContract(contract);
        item.setType(type);
        item.setName(name);
        item.setSymbol(symbal);
        item.setDecimals(decimal);
        item.setTotalSupply(totalSupply);
        item.setCataloged(0);
        item.setDescription(description);
        item.setContractManager(contractManager);
        tokenMapper.saveOrUpdate(item);
        return true;
    }
    
	private TransToken saveTransTokenFromLog(Transaction tx, Log logItem,Integer coinType) {
        TransToken item = new TransToken();
        List<String> topics = logItem.getTopics();
        item.setTransHash(logItem.getTransactionHash());
        item.setLoginIndex(logItem.getLogIndex().intValue());
        item.setBlockHash(logItem.getBlockHash());
        item.setBlockNumber(logItem.getBlockNumber().longValue());
        item.setCoinType(coinType);
        item.setFromAddr(truncateAddress(topics.get(1)));
        item.setToAddr(truncateAddress(topics.get(2)));
        item.setAmount(NumericUtil.convertToBigDecimal(logItem.getData(),BigDecimal.ZERO));
        item.setContract(logItem.getAddress());
   //     item.setGasPrice(gasPrice.longValue());
   //     item.setGasUsed(gasUsed.longValue());
   //     item.setNonce(nonce.longValue());
   //     item.setGasLimit(gasLimit.longValue());
   //     item.setContract(logItem.getAddress());
   //     item.setTimeStamp(date);                
        item.setGasUsed(tx.getGasUsed());
		item.setGasPrice(tx.getGasPrice());
		item.setGasLimit(tx.getGasLimit());
		item.setNonce(tx.getNonce());
		item.setTimeStamp(tx.getTimeStamp());
        transferTokenMapper.insertOrUptate(item);
        log.debug("save token transaction: " + logItem.getTransactionHash() + ", " + logItem.getLogIndex());
        return item;
    }
	
	protected Transaction saveTransactionErc20(TransactionObject item, TransactionReceipt receipt, Log logItem, Date timestamp) {
		Transaction transaction = new Transaction();
		List<String> topics = logItem.getTopics();
		int status = receipt.getStatus().equals("0x1") ? 1 : 0;
		transaction.setHash(item.getHash());
		transaction.setIsTrunk(1);
		transaction.setTimeStamp(timestamp);
		transaction.setFromAddr(truncateAddress(topics.get(1)));
		transaction.setToAddr(truncateAddress(topics.get(2)));
		transaction.setContract(item.getTo());
		transaction.setValue(NumericUtil.convertToBigDecimal(logItem.getData(),BigDecimal.ZERO));
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
		transaction.setType(1);

		transactionMapper.insertorUpdate(transaction);
		return transaction;
	}
	
	
	private String truncateAddress(String address) {
        return Constants.PRE + address.substring(address.length() - 40);
    }

}
