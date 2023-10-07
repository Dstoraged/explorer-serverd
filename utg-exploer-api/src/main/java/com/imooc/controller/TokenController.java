package com.imooc.controller;

import com.imooc.service.TokenService;
import com.imooc.utils.IMoocJSONResult;
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
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;


@RestController
@Api(value = "TokenController", tags = "TokenController")
@RequestMapping("/token")
public class TokenController {
    private Logger logger = LoggerFactory.getLogger(TokenController.class);
   private static final  String DATA_PREFIX = "0x70a08231000000000000000000000000";
    @Value("${ipaddress}")
    private  String ipaddress;

    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "getTokenBalance", notes = "getTokenBalance")
    @PostMapping("/getTokenBalance")
    public ResultMap getLeatestBlockNumber(@RequestParam(value="address")String address,@RequestParam(value="contractAddress")String contractAddress) throws Exception {
        String value = Admin.build(new HttpService(ipaddress))
                .ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address,
                        contractAddress, DATA_PREFIX + address.substring(2)), DefaultBlockParameterName.PENDING).send().getValue();
        String s = new BigInteger(value.substring(2), 16).toString();
        BigDecimal wei = new BigDecimal("1000");
        BigDecimal balance = new BigDecimal(s).divide(wei, 6, RoundingMode.HALF_DOWN);
        return ResultMap.getSuccessfulResult(balance);

    }

    @ApiOperation(value = "getTokenSupply", notes = "getTokenSupply")
    @PostMapping("/getTokenSupply")
    public ResultMap getTokenSupply(@RequestParam(value="contractAddress")String contractAddress) throws Exception {
        return tokenService.getTotalSupply(contractAddress);
    }

}
