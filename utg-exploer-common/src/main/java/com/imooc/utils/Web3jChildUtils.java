package com.imooc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;


@Component
public class Web3jChildUtils {
    private static final Logger logger =LoggerFactory.getLogger(Web3jChildUtils.class);

    private static  Web3j web3jChild =null;

    private static synchronized  Web3j init(String ipaddress){
        if(!ObjectUtils.isEmpty(web3jChild)){
            return web3jChild;
        }
        web3jChild =Web3j.build((new HttpService(ipaddress)));
        return web3jChild;
    }


    public static Web3j getWeb3j(String ipaddress){
        if(!ObjectUtils.isEmpty(web3jChild)){
            return web3jChild;
        }
        return  init(ipaddress);
    }


    /**/  /*ok   */
    public BigInteger getAddressBalance(Web3j web3j, String address) {
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

    /*nonce      ok*/
    public BigInteger getAddressNonce(Web3j web3j, String address) {
        EthGetTransactionCount ethResult;
        try {
            ethResult = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ethResult.getTransactionCount();
    }






}
