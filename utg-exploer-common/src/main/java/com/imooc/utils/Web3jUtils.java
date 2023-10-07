package com.imooc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;


@Component
public class Web3jUtils {
    private static final Logger logger =LoggerFactory.getLogger(Web3jUtils.class);

    private static  Web3j web3j =null;

    private static synchronized  Web3j init(String ipaddress){
        if(!ObjectUtils.isEmpty(web3j)){
            return web3j;
        }
        web3j =Web3j.build((new HttpService(ipaddress)));
        return web3j;
    }

    public  static synchronized  Admin getAdmin(String ipaddress){
        Admin admin =Admin.build(new HttpService(ipaddress));
        return admin;
    }


    public static Web3j getWeb3j(String ipaddress){
        if(!ObjectUtils.isEmpty(web3j)){
            return web3j;
        }
        return  init(ipaddress);
    }

    
}
