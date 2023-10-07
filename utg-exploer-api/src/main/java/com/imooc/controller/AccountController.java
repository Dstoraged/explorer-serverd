package com.imooc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.BlockRewards;
import com.imooc.pojo.Form.BlockRewardQueryForm;
import com.imooc.service.AccountService;
import com.imooc.service.BlockRewardsService;
import com.imooc.service.TokenService;
import com.imooc.service.TransactionService;


import com.imooc.utils.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(value = "account", tags = "account")
@RequestMapping("/account")
public class AccountController {


    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BlockRewardsService blockRewardsService;

    private Logger logger = LoggerFactory.getLogger(AccountController.class);


    @PostMapping("/balance")
    public ResultMap getLeatestBlockNumber(@RequestParam(value="address",required=true)String address) throws Exception {
        logger.info(""+address);
        return accountService.getSingleAddressBalance(address);
    }

    @PostMapping("/balancemulti")
    public ResultMap getbalancemulti(@RequestParam(value="address",required=true)String address) throws Exception {
        logger.info(""+address);
        return accountService.getbalancemulti(address);
    }

    @PostMapping("/txList")
    public ResultMap getAccountTxList(@RequestParam(value="address",required=true)String address,@RequestParam(value="startBlock")long startBlock,
                                      @RequestParam(value="endBlock")long endBlock) throws Exception {

        return transactionService.getTxList(address,startBlock,endBlock);
    }


    @PostMapping("/ternalList")
    public ResultMap getTernalList(@RequestParam(value="address",required=true)String address,@RequestParam(value="startBlock")long startBlock,
                                      @RequestParam(value="endBlock")long endBlock) throws Exception {

        return transactionService.getTernalList(address,startBlock,endBlock);
    }

    @PostMapping("/ternalListByTransactionHash")
    public ResultMap getTernalListByTransactionHash(@RequestParam(value="txHash",required=true)String txHash   ) throws Exception {
        return transactionService.getTernalByTransactionHash(txHash);
    }

    @PostMapping("/ternalListByBlock")
    public ResultMap getTxListByBlock(@RequestParam(value="startBlock",required=true)long startBlock,@RequestParam(value="endBlock",required=true)long endBlock ) throws Exception {
       return transactionService.ternalListByBlock(startBlock,endBlock);
    }

    @PostMapping("/ternalListByBlockErc20")
    public ResultMap getTxListByBlock(@RequestParam(value="coinType",required=true)int coinType ) throws Exception {
         return tokenService.getTransferListByCoinType(coinType);

    }


    @PostMapping("/getMinedByBlock")
    public ResultMap getMinedByBlock(@Valid @RequestBody BlockRewardQueryForm blockRewardQueryForm ) throws Exception {
        return blockRewardsService.getBlockRewardByAddress(blockRewardQueryForm);

    }




}
