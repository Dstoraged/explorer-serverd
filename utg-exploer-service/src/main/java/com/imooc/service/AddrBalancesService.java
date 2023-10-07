package com.imooc.service;

import com.imooc.utils.ResultMap;

public interface AddrBalancesService {
    ResultMap getBalanceForAddress(String addressHash);
}
