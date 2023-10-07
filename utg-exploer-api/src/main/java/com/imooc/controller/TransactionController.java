package com.imooc.controller;

import com.imooc.service.TransactionService;
import com.imooc.utils.ResultMap;
import com.imooc.utils.Web3jUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;


@RestController
@Api(value = "TransactionController", tags = "TransactionController")
@RequestMapping("/transaction")
public class TransactionController {
    @Value("${ipaddress}")
    private  String ipaddress;

    @Autowired
    private TransactionService transactionService;

    private Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/checkStatus")
    public ResultMap checkContractStatus(@RequestParam(value="txHash",required=true)String txHash
                                     )throws Exception {
        logger.info("====>"+txHash);
      return  transactionService.checkStatus(txHash);
    }

    @PostMapping("/checkTransactionStatus")
    public ResultMap checkTransactionStatus(@RequestParam(value="txHash",required=true)String txHash
    )throws Exception {
        logger.info("====>"+txHash);
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        String status= web3j.ethGetTransactionReceipt(txHash).send().getResult().getStatus();
        return ResultMap.getSuccessfulResult(status);
    }
}
