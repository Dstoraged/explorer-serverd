package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.UtgFaucetMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.UtgFaucet;
import com.imooc.pojo.vo.AddressVo;

import com.imooc.service.UtgFaucetService;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;
import com.imooc.utils.Web3jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.*;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class UtgFaucetServiceImpl implements UtgFaucetService {
    private static final Logger logger= LoggerFactory.getLogger(UtgFaucetServiceImpl.class);
    @Autowired
    private UtgFaucetMapper UtgFaucetMapper;

    @Value("${ipaddress}")
    private String ipaddress;


    @Value("${faucetAccountKey}")
    private String faucetAccountKey;

    @Value("${faucetCoinNum}")
    private String faucetCoinNum;


    @Override
    public ResultMap getFaucetByAddress(String address) {
        UtgFaucet vo = UtgFaucetMapper.getFaucetByAddress(address);
        if(vo!=null){
            return ResultMap.getSuccessfulResult(vo);
        }else{
            return ResultMap.getFailureResult("query no data");
        }
    }

    @Override
    public ResultMap sendCoinToAddress(String address) {
        UtgFaucet vo = UtgFaucetMapper.getFaucetByAddress(address);
        if(vo!=null){
            return ResultMap.getSuccessfulResult(vo.getHash());
        }
        try {
            BigInteger coinNum=new BigInteger(faucetCoinNum);
            coinNum = coinNum.multiply(new BigInteger("10").pow(18));
            BigInteger value = coinNum;
            String hash=sendTrace(address,value);
            UtgFaucet newvo=new UtgFaucet();
            newvo.setAddress(address);
            newvo.setHash(hash);
            newvo.setCreateTime(new Date());
            newvo.setNum(new BigDecimal(value));
            UtgFaucetMapper.insert(newvo);
            return ResultMap.getSuccessfulResult(hash);
        } catch (Exception e) {
            logger.error("sendCoinToAddress error",e);
        }
        return ResultMap.getFailureResult(null);

    }

    public String sendTrace(String to,BigInteger value) throws Exception {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        String hash = "";
        Credentials credentials = Credentials.create(faucetAccountKey);
        String from=credentials.getAddress();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();//web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = new BigInteger("24693");
        BigInteger nonce;
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();
        if (ethGetTransactionCount == null) {
            // return null;
        }
        nonce = ethGetTransactionCount.getTransactionCount();
        String data="";
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,gasPrice, gasLimit, to, value,data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction,1337, credentials);
        String hexValue = Numeric.toHexString(signMessage);
        hexValue= Constants.PRE +hexValue.substring(2);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        hash=ethSendTransaction.getTransactionHash();
        if (hash == null) {
            Response.Error error = ethSendTransaction.getError();
            String msg = error.getCode() + ":" + error.getMessage();
            throw new Exception(msg);
        }
        return hash;
    }
}
