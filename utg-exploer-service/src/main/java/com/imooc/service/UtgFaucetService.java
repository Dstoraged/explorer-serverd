package com.imooc.service;


import com.imooc.utils.ResultMap;

public interface UtgFaucetService {

    ResultMap getFaucetByAddress(String address);


    ResultMap sendCoinToAddress(String address);
}
