package com.imooc.service.impl;
import com.imooc.mapper.AddrBalancesMapper;
import com.imooc.pojo.AddrBalances;
import com.imooc.service.AddrBalancesService;
import com.imooc.utils.ResultMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddrBalanceServiceImpl implements AddrBalancesService {
    @Autowired
    private AddrBalancesMapper addrBalancesMapper;
    public ResultMap getBalanceForAddress(String addressHash) {
        List<AddrBalances> listInfo = addrBalancesMapper.getForAddressBalance(addressHash);
        return ResultMap.getSuccessfulResult(listInfo);
    }
}
