package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.*;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.TransactionQueryForm;
import com.imooc.pojo.Form.TxQueryForm;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.TransToken;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.Transinternal;
import com.imooc.pojo.View.TransactionView;
import com.imooc.pojo.vo.AddressVo;
import com.imooc.pojo.vo.TransferMinerVo;
import com.imooc.service.AccountService;
import com.imooc.service.TransactionService;
import com.imooc.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Value("${FROM_ADDRESS_HASH}")
    private String fromaddressHash;

    @Value("${ipaddress}")
    private String ipaddress;
    
    @Value("${dayOneNumber}")
    private Long dayOneNumber;

    @Autowired
    private TransactionMapper transactionMapper;//normal trans list

    @Autowired
    private TransactionInternalMapper transactionInternalMapper;//Internal trans list

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private AddrBalancesMapper addrBalancesMapper;

    @Autowired
    private AccountMapper accountMapper;
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransferTokenMapper transferTokenMapper;
    
    @Autowired
    private TokenMapper tokenMapper;
    
    @Autowired
    private ContractMapper contractMapper;

    @Override
    public ResultMap getTxList(String address, long startBlock, long endBlock) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("address", address);
        map.put("startBlock", startBlock);
        map.put("endBlock", endBlock);
        List<Transaction> listInfo = transactionMapper.getTxListInfo(map);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap getTernalList(String address, long startBlock, long endBlock) {
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        map.put("startBlock", startBlock);
        map.put("endBlock", endBlock);
        List<Transinternal> listInfo = transactionInternalMapper.getTernalListInfo(map);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap getTernalByTransactionHash(String txHash) {
        List<Transinternal> listInfo = transactionInternalMapper.getTernalByTransactionHash(txHash);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap ternalListByBlock(long startBlock, long endBlock) {
        Map<String, Object> map = new HashMap<>();
        map.put("startBlock", startBlock);
        map.put("endBlock", endBlock);
        List<Transinternal> listInfo = transactionInternalMapper.getTernalByBlock(map);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap checkStatus(String txHash) {
        Transaction tx = transactionMapper.checkStatus(txHash);
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("status", tx.getStatus());
            return ResultMap.getSuccessfulResult(map);
        } catch (Exception e) {
            return ResultMap.getFailureResult("");
        }
    }

    @Override
    public List<Transaction> findForkTransaction(String blockHash, Long blockNumber, String transHash, String fromAddress, Long nonce) {
        return null;
    }

    @Override
    public ResultMap<Page<Transaction>> pageList(BlockQueryForm blockQueryForm) {
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        Map<String, Object> paramsMap = new HashMap<>();
        if(!StringUtils.isEmpty(blockQueryForm.getContract())){
            paramsMap.put("contract",blockQueryForm.getContract());
        }
        if(!StringUtils.isEmpty(blockQueryForm.getParam1())){
            paramsMap.put("param1",blockQueryForm.getParam1());
        }
        if(!StringUtils.isEmpty(blockQueryForm.getParam2())){
            paramsMap.put("param2",blockQueryForm.getParam2());
        }
        if(!StringUtils.isEmpty(blockQueryForm.getAddress())){
            paramsMap.put("address",blockQueryForm.getAddress());
        }
        if(!StringUtils.isEmpty(blockQueryForm.getFromAddr())){
            paramsMap.put("fromAddr",blockQueryForm.getFromAddr());
        }
        if(!StringUtils.isEmpty(blockQueryForm.getToAddr())){
            paramsMap.put("toAddr",blockQueryForm.getToAddr());
        }
        if(blockQueryForm.getBlockNumber()!=null){
            paramsMap.put("blockNumber",blockQueryForm.getBlockNumber());
        }
        if(blockQueryForm.getUfooperator()!=null){
            paramsMap.put("ufooperator",blockQueryForm.getUfooperator());
        }
        if(blockQueryForm.getStatus()!=null){
            paramsMap.put("status",blockQueryForm.getStatus());
        }
        if(StringUtils.isEmpty(blockQueryForm.getTxType())){
            paramsMap.put("txType","0");//all trans
        }else{
            paramsMap.put("txType",blockQueryForm.getTxType());
        }
        List<Transaction> list = transactionMapper.pageList(pageCurrent, pageSize, paramsMap);
        if("stProof".equals(blockQueryForm.getUfooperator())){		//For storage proof only
        	long latBlocknumber = blockMapper.getLeatestBlockNumber();
        //	latBlocknumber = latBlocknumber - latBlocknumber % dayOneNumber;
        	for(Transaction transaction : list){
        		Long blocknumber = transaction.getBlockNumber();
        		blocknumber = blocknumber - blocknumber % dayOneNumber;
        		int blockDays = (int) ((latBlocknumber-blocknumber) / dayOneNumber);
        		transaction.setBlockDays(blockDays);
        	}
        }
        long total = transactionMapper.getTotalTransactionCount(paramsMap);
        page.setRecords(list);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap<Page<Map>> txPageList(TxQueryForm tx) {
        Page page = tx.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        String fromHash = fromaddressHash.trim();
        Map<String, Object> paramsMap = new HashMap<>();
        if(!StringUtils.isEmpty(tx.getCharge_address())){
            paramsMap.put("param1",tx.getCharge_address());
        }
        if(!StringUtils.isEmpty(tx.getPay_address())){
            paramsMap.put("fromAddr",tx.getPay_address());
        }
        if(tx.getBlocknumber()!=null){
            paramsMap.put("blocknumber",tx.getBlocknumber());
        }
        paramsMap.put("txType",tx.getTxType());
        List<Map> maps = new ArrayList<>();
        List<Transaction> list = transactionMapper.pageList(pageCurrent, pageSize, paramsMap);
        for (Transaction vo:list) {
            Map<String,Object> m = new HashMap<>();
            m.put("hash",vo.getHash());
            m.put("charge_address",vo.getParam1());
            m.put("pay_address",vo.getFromAddr());
            m.put("blocknumber",vo.getHash());
            m.put("pay_utg",vo.getParam3());
            m.put("charge_srt",vo.getParam4());
            m.put("timeStamp",vo.getTimeStamp());
            maps.add(m);
        };
        long total = transactionMapper.getTotalTransactionCount(paramsMap);
        page.setRecords(maps);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap getTransaction(TransactionQueryForm transactionQueryForm) {
        String param = transactionQueryForm.getParam();
        if (StringUtils.isEmpty(param)) {
            return ResultMap.getFailureResult(Constants.STATUS_ACCOUNT_HASHERROR);
        }
        param = param.trim();
        int length = param.length();
        Page page = transactionQueryForm.newFormPage();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isNumeric(param) && length > 20) {
            paramMap.put("type", 0);
            return ResultMap.getSuccessfulResult(paramMap);
        }
        if (StringUtils.isNumeric(param)) {
            long blockNumber = blockMapper.getLeatestBlockNumber();
            long params = Long.parseLong(param);
            if (params > blockNumber) {
                paramMap.put("type", 0);
                return ResultMap.getSuccessfulResult(paramMap);
            } else {
                paramMap.put("type", 1);
                return ResultMap.getSuccessfulResult(paramMap);
            }
        }
        
        if (length == 42) {
            param = param.trim();
            if (!(AccountUtils.validateAddressHash(param))) {
                paramMap.put("type", 0);
                return ResultMap.getSuccessfulResult(paramMap);            
//            }
//        }
//        if (length == 42) {
//            param = param.trim();
//            if (AccountUtils.validateAddressHash(param)) {
            }else {
            	if(tokenMapper.getTokens(Constants.pre0XtoNX(param))!=null)
            		paramMap.put("type", 4);
            	else if(contractMapper.getContract(Constants.pre0XtoNX(param))!=null)
            		paramMap.put("type", 5);
            	else
            		paramMap.put("type", 3);
            }
        }

        if (length == 66) {
            param = param.trim();
            if (!(TxHashUtils.validateTxHash(param))) {
                paramMap.put("type", 0);
                return ResultMap.getSuccessfulResult(paramMap);    
//            } 
//        }
//        if (length == 66) {
//            param = param.trim();
//            if (TxHashUtils.validateTxHash(param)) {
            }else {	
                Transaction to = transactionMapper.selectByTxHash(Constants.pre0XtoNX(param));
                if (to != null) {
                    paramMap.put("type", 2);
                    return ResultMap.getSuccessfulResult(paramMap);
                } else {
                    paramMap.put("type", 0);
                    return ResultMap.getSuccessfulResult(paramMap);
                }
            }
        }
        return ResultMap.getSuccessfulResult(paramMap);
    }

    @Override
    public ResultMap<Page<Transaction>> getTransactionByBlockNumber(TransactionQueryForm transactionQueryForm) {
        Page page = transactionQueryForm.newFormPage();
        Long blockNumber = transactionQueryForm.getBlockNumber();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        String fromHash = fromaddressHash.trim();
        List<Transaction> list = transactionMapper.getTransactionByBlockNumber(pageCurrent, pageSize, blockNumber, fromHash);
        long total = transactionMapper.getTotalByBlockNumber(blockNumber, fromHash);
        page.setRecords(list);
        page.setTotal(total);
        if (list.size() > 0) {
            return ResultMap.getSuccessfulResult(page);
        } else {
            return ResultMap.getSuccessfulResult(page);
        }
    }

    @Override
    public ResultMap getAddressInfoByHash(TransactionQueryForm transactionQueryForm) {
        try {
            Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
            Page page = transactionQueryForm.newFormPage();
            String addressHash = transactionQueryForm.getAddressHash();
            addressHash = addressHash.trim();
            Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
            /*DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(request.send().getBlockNumber());            
            EthGetBalance ethGetBalance = web3j.ethGetBalance(addressHash, defaultBlockParameter).send();
			BigInteger balance = BigInteger.ZERO;			
			try {
				balance = ethGetBalance.getBalance();
				Addresses address = accountMapper.getAddressInfo(addressHash);
				if(address!=null){
					if(new BigDecimal(balance).compareTo(address.getBalance())!=0){	
						address.setBalance(new BigDecimal(balance));
						accountMapper.saveOrUpdata(address);
					}
				}
			} catch (Exception e) {
				logger.warn("ethGetBalance address "+addressHash+" ethGetBalance:"+ethGetBalance+",error:"+e.getMessage());
			}*/
            BigDecimal balance = accountService.getAddressBalance(addressHash);			
            List<Map<String, Object>> listInfo = new ArrayList<>();
            Map<String, Object> map = new HashMap<String, Object>();
            Long blockNumber = transactionMapper.getMaxBlockNumberByAddressHash(addressHash);
            if (blockNumber == null) {
                blockNumber = request.send().getBlockNumber().longValue();
            }
            Long count = transactionMapper.getFromOrToCount(addressHash, fromaddressHash);
            map.put("blockNumber", blockNumber);
            map.put("transactionCount", count);
            map.put("balance", balance.toString());
            
            Addresses address = accountMapper.getAddressInfo(addressHash);
            if(address!=null){
            	map.put("iscontract", address.getIscontract());
            	map.put("isminer", address.getIsminer());
            	map.put("isstorage", address.getIsstorage());
            	map.put("isrevenue", address.getIsrevenue());
            }            
            listInfo.add(map);
            page.setRecords(listInfo);
            return ResultMap.getSuccessfulResult(page);
        } catch (Exception e) {
            logger.warn("getAddressInfoByHash error:",e);
        }
        return ResultMap.getFailureResult(null);
    }

    public ResultMap<Page<Transaction>> getTransactionInfoByAddressHash(TransactionQueryForm transactionQueryForm) {
        String addressHash = transactionQueryForm.getAddressHash();
        addressHash = addressHash.trim();
        Integer type = transactionQueryForm.getType();
        Page page = transactionQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        if (addressHash.equals(fromaddressHash)) {
            List<Transaction> list = transactionMapper.getTransactionInfoByToHash(pageCurrent, pageSize, addressHash, fromaddressHash);
            long total = transactionMapper.getFromOrToCount(addressHash, fromaddressHash);
            if (list.size() > 0) {
                page.setRecords(list);
                page.setTotal(total);
                return ResultMap.getSuccessfulResult(page);
            } else {
                return ResultMap.getSuccessfulResult(null);
            }

        }
        if (type == null) {
            type = 0;
        }
        if (type == 0) {
            List<Transaction> list = transactionMapper.getTransactionInfoByAddressHash(pageCurrent, pageSize, addressHash, fromaddressHash);
            long total = transactionMapper.getFromOrToCount(addressHash, fromaddressHash);
            if (list.size() > 0) {
                page.setRecords(list);
                page.setTotal(total);
                return ResultMap.getSuccessfulResult(page);
            } else {
                return ResultMap.getSuccessfulResult(null);
            }
        } else if (type == 1) {
            List<Transaction> list = transactionMapper.getTransactionInfoByFromHash(pageCurrent, pageSize, addressHash);
            if (list.size() == 0) {
                return ResultMap.getSuccessfulResult(null);
            }
            long total = transactionMapper.getFromOrToCount(addressHash, fromaddressHash);
            page.setRecords(list);
            page.setTotal(total);
            return ResultMap.getSuccessfulResult(page);
        } else if (type == 2) {
            List<Transaction> list = transactionMapper.getTransactionInfoByToHash(pageCurrent, pageSize, addressHash, fromaddressHash);
            if (list.size() == 0) {
                return ResultMap.getSuccessfulResult(null);
            }
            long total = transactionMapper.getFromOrToCount(addressHash, fromaddressHash);
            page.setRecords(list);
            page.setTotal(total);
            return ResultMap.getSuccessfulResult(page);
        } else {
            return ResultMap.getSuccessfulResult(page);
        }
    }

    @Override
    public ResultMap getTransactionInfoByTxHash(TransactionQueryForm transactionQueryForm) {
        String txHash = transactionQueryForm.getTxHash();
        txHash = txHash.trim();
        Transaction tx = transactionMapper.getTransactionByTxHash(txHash);
        if(tx!=null){
            return ResultMap.getSuccessfulResult(tx);
        }else{
            return ResultMap.getSuccessfulResult(null);
        }

    }


    @Override
    public ResultMap getForAddressDetail(TransactionQueryForm transactionQueryForm) throws Exception {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        Page page = transactionQueryForm.newFormPage();
        long current = transactionQueryForm.getCurrent();
        long pageSize = transactionQueryForm.getSize();
        current = (current - 1) * pageSize;
        String addressHash = transactionQueryForm.getAddressHash();
        addressHash = addressHash.trim();

        List<TransactionView> listInfo = transactionMapper.getAddressDetail(addressHash, current, pageSize);
        if (listInfo.size() == 0) {
            return ResultMap.getSuccessfulResult(null);
        }
        for (TransactionView tx : listInfo) {
            if (tx.getFromAddr().equalsIgnoreCase(addressHash)) {
                BigDecimal gasUsed = new BigDecimal(String.valueOf(tx.getGasUsed()));
                BigDecimal gasPrice = new BigDecimal(String.valueOf(tx.getGasPrice()));
                BigDecimal a = gasUsed.multiply(gasPrice);
                BigDecimal amount = a.add(tx.getValue());
            /*    EthGetBalance ethGetBalance = web3j.ethGetBalance(addressHash, DefaultBlockParameter.valueOf(ConstantUtil.LatestBlockNumberKey)).send();
                BigInteger balance = ethGetBalance.getBalance();*/
                BigDecimal totalBalance = addrBalancesMapper.getBalance(tx.getFromAddr(), tx.getBlockNumber());
                tx.setBalance(totalBalance==null?"0":totalBalance.toString());
                tx.setAmount(amount==null?"0":amount.toString());
                tx.setType(1);
            }
            if (tx.getToAddr() != null) {
                if (tx.getToAddr().equalsIgnoreCase(addressHash)) {
                    EthGetBalance ethGetBalance = web3j.ethGetBalance(addressHash, DefaultBlockParameter.valueOf(ConstantUtil.LatestBlockNumberKey)).send();
                /*    BigInteger balance = ethGetBalance.getBalance();
                    tx.setBalance(balance.toString());*/
                    BigDecimal totalBalance = addrBalancesMapper.getBalance(tx.getToAddr(), tx.getBlockNumber());
                    tx.setBalance(totalBalance==null?"0":totalBalance.toString());
                    tx.setAmount(tx.getValue()==null?"0":tx.getValue().toString());
                    tx.setType(2);
                }
            }

        }
        long total = transactionMapper.getFromOrToCount(addressHash, fromaddressHash);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap getTransactionValue() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String startTime = DateUtil.getFormatDateTime(DateUtil.getBeginDayOfYesterday(), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.getFormatDateTime(DateUtil.getEndDayOfYesterDay(), "yyyy-MM-dd HH:mm:ss");
        BigDecimal totalValue = transactionMapper.getTotalValues(paramMap);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        BigDecimal yesterTotal = transactionMapper.getTotalValues(paramMap);
        map.put("totalValue", totalValue);
        map.put("yesterTotal", yesterTotal);
        // map.put("newAddress",getNewAccount());
        return ResultMap.getSuccessfulResult(map);
    }

    @Override
    public ResultMap getNewAccounts() {
        Map<String, Object> map = new HashMap<>();
        map.put("newAddress", getNewAccount());
        return ResultMap.getSuccessfulResult(map);
    }

    /**/
    public Long getNewAccount() {
        String startTime = DateUtil.getFormatDateTime(DateUtil.getBeginDayOfYesterday(), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.getFormatDateTime(DateUtil.getEndDayOfYesterDay(), "yyyy-MM-dd HH:mm:ss");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        Long count = accountMapper.getNewAddress(paramMap);
        return count;
    }

    @Override
    public ResultMap getTransactionByToAddress(BlockQueryForm blockQueryForm) {
        String to = blockQueryForm.getAddress();
        to = to.trim();
        Long blockNumber = blockQueryForm.getBlockNumber();
        List<Transaction> listInfo = transactionMapper.getToAddress(blockQueryForm);
        if (listInfo.size() > 0 || listInfo != null) {
            return ResultMap.getSuccessfulResult(listInfo);
        } else {
            return ResultMap.getSuccessfulResult(null);
        }
    }

    @Override
    public ResultMap<Page<Transaction>> getFromAndToTransaction(BlockQueryForm blockQueryForm) {
        Map<String, Object> totalMap = new HashMap<String, Object>();
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        String address = blockQueryForm.getAddress();
        List<TransferMinerVo> listInfos = null;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<Map<String, Object>> listInfo = transactionMapper.getFromAndToTransaction(blockQueryForm);
        Long total = transactionMapper.getTotalFromAndTo(blockQueryForm);
        BigDecimal totalValue = transactionMapper.getTotalFromAndToValue(blockQueryForm);
        totalMap.put("totalValue", totalValue);
        listInfo.add(totalMap);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    public ResultMap getTransactionByTokenHash(TransactionQueryForm transactionQueryForm) {
        String txHash = transactionQueryForm.getTxHash();
        txHash = txHash.trim();
        List<TransToken> list = transferTokenMapper.getTransactionByTxHash(txHash);
        if (list.size() > 0 || list != null) {
            return ResultMap.getSuccessfulResult(list);
        } else {
            return ResultMap.getFailureResult(null);
        }
    }

    @Override
    public ResultMap getTransactionByTokenHashInfo(String txHash) {
        txHash = txHash.trim();
        List<TransToken> list = transferTokenMapper.getTransactionByTxHash(txHash);
        if (list.size() > 0 || list != null) {
            return ResultMap.getSuccessfulResult(list);
        } else {
            return ResultMap.getFailureResult(null);
        }
    }

    @Override
    public ResultMap validateCaladateAddress(String address) {
        boolean isExit=false;
        if(address.isEmpty()|| address==null){
            return ResultMap.getFailureResult(isExit);
        }
      /*  try {
            Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
            Map<String,BigInteger> paramMap= web3j.alientSnapshot().send().getSnapshot().getCandidates();
            if(paramMap!=null || paramMap.size()>0){
                if(paramMap.containsKey(address)){
                    return ResultMap.getSuccessfulResult(!isExit);
                }else{
                    return ResultMap.getSuccessfulResult(isExit);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return  ResultMap.getSuccessfulResult(isExit);
    }

    /**
     * utg change  rate set
     * @param txType
     * @return
     */
    @Override
    public ResultMap getExchRate(String txType){
        String rate = null;
        if(txType == null){
            return  ResultMap.getSuccessfulResult(rate);
        }
        List<Transaction> list = transactionMapper.getTransactionByTxType(txType);
        if(list.size()>0){
            rate = list.get(0).getParam1() ;
        }
        return  ResultMap.getSuccessfulResult(rate);

    }
}