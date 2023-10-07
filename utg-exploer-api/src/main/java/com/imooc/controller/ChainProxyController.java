package com.imooc.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.imooc.mapper.DposNodeMapper;
import com.imooc.mapper.DposVotesMapper;
import com.imooc.pojo.DposNode;
import com.imooc.pojo.DposVotes;
import com.imooc.pojo.VoterInfo;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.ResultMap;
import com.imooc.utils.Web3jUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.abi.datatypes.Array;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@Api(value = "ChainProxyController", tags = "ChainProxyController")
@RequestMapping("/proxy")
public class ChainProxyController {
    @Value("${ipaddress}")
    private  String ipaddress;

    @Value("${round}")
    private long round;

    private Logger logger = LoggerFactory.getLogger(ChainProxyController.class);

    @Autowired
    private DposVotesMapper dposVotesMapper;

    @Autowired
    private DposNodeMapper dposNodeMapper;

    @ApiOperation(value = "getLeatestBlockNumber", notes = "getLeatestBlockNumber")
    @PostMapping("/eth_blocNumber")
    public ResultMap getLeatestBlockNumber() throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        BigInteger blockNumber= web3j.ethBlockNumber().send().getBlockNumber();
        List<String> allAccounts =web3j.ethAccounts().send().getAccounts();
        return ResultMap.getSuccessfulResult(blockNumber);

    }

    @ApiOperation(value = "eth_getBlockByNumber", notes = "eth_getBlockByNumber")
    @PostMapping("/eth_getBlockByNumber")
    public ResultMap getBlockByNumber(@RequestParam(value="blockNumber",required=true)Long blockNumber) throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        Request<?,EthBlock> request=web3j.ethGetBlockByNumber(defaultBlockParameter,true);
        EthBlock blockNumberInfo =request.send();
        if(blockNumberInfo.hasError()){
            return ResultMap.getFailureResult("error");
        }
           EthBlock.Block kk =blockNumberInfo.getResult();
           return ResultMap.getSuccessfulResult(kk);
    }

    @PostMapping("/eth_getUncleByBlockNumber")
    public ResultMap getBlockByNumber(@RequestParam(value="blockNumber",required=true)Long blockNumber,
                                      @RequestParam(value="index",required=true)Long index )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        BigInteger indexs = BigInteger.valueOf(index);
        EthBlock uncleNumber=web3j.ethGetUncleByBlockNumberAndIndex(defaultBlockParameter,indexs).send();
        EthBlock.Block uncleInfo =uncleNumber.getResult();
        return ResultMap.getSuccessfulResult("uncleByBlockNumber"+uncleInfo);
    }


    @ApiOperation(value = "getBlockTransactionCountByNumber", notes = "getBlockTransactionCountByNumber")
    @PostMapping("/eth_getBlockTransactionCountByNumber")
    public ResultMap getBlockTransactionCountByNumber(@RequestParam(value="blockNumber",required=true)Long blockNumber
                                     )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
       EthGetBlockTransactionCountByNumber eth = web3j.ethGetBlockTransactionCountByNumber(defaultBlockParameter).send();
       BigInteger count = eth.getTransactionCount();
        return ResultMap.getSuccessfulResult("getBlockTransactionCountByNumber:"+count);
    }

    @ApiOperation(value = "getBlockTransactionCountByNumber", notes = "getBlockTransactionCountByNumber")
    @PostMapping("/eth_getTransactionByHash")
    public ResultMap getBlockTransactionCountByNumber(@RequestParam(value="txHash",required=true)String  txHash
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        Request<?,EthTransaction> tx =web3j.ethGetTransactionByHash(txHash);
        EthTransaction eth =tx.send();
        Transaction transaction=eth.getResult();
        return ResultMap.getSuccessfulResult(transaction);
    }

    @ApiOperation(value = "eth_getTransactionByBlockNumberAndIndex", notes = "eth_getTransactionByBlockNumberAndIndex")
    @PostMapping("/eth_getTransactionByBlockNumberAndIndex")
    public ResultMap getTransactionByBlockNumberAndIndex(@RequestParam(value="blockNumber",required=true)Long blockNumber,
                                                         @RequestParam(value="index",required=true)Long index
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        BigInteger indexs= BigInteger.valueOf(index);
        Request<?,EthTransaction>ss=web3j.ethGetTransactionByBlockNumberAndIndex(defaultBlockParameter, indexs);
        EthTransaction transaction =ss.send();
        return ResultMap.getSuccessfulResult(transaction);
    }

    @ApiOperation(value = "getTransactionCount", notes = "getTransactionCount")
    @PostMapping("/eth_getTransactionCount")
    public ResultMap getTransactionCount(@RequestParam(value="address",required=true)String address
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(web3j.ethBlockNumber().send().getBlockNumber());
        EthGetTransactionCount count= web3j.ethGetTransactionCount(address,defaultBlockParameter).send();
        BigInteger counts=count.getTransactionCount();
        return ResultMap.getSuccessfulResult(counts);
    }

    @ApiOperation(value = "sendRawTransaction", notes = "sendRawTransaction")
    @PostMapping("/eth_sendRawTransaction")
    public ResultMap sendRawTransaction(@RequestParam(value="singTransactionData",required=true)String singTransactionData
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        String result =web3j.ethSendRawTransaction(singTransactionData).send().getResult();
        return ResultMap.getSuccessfulResult(result);
    }


    @ApiOperation(value = "eth_getTransactionReceipt", notes = "eth_getTransactionReceipt")
    @PostMapping("/eth_getTransactionReceipt")
    public ResultMap getTransactionReceipt(@RequestParam(value="txHash",required=true)String txHash
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        TransactionReceipt receipt= web3j.ethGetTransactionReceipt(txHash).send().getResult();
        return ResultMap.getSuccessfulResult(receipt);
    }

    @ApiOperation(value = "eth_call", notes = "eth_call")
    @PostMapping("/eth_call")
    public ResultMap ethCall(@RequestParam(value="fromAddress")String fromAddress,
                             @RequestParam(value="toAddress")String toAddress,
                             @RequestParam(value="value")BigInteger value
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        BigDecimal amount = new BigDecimal(value);
        BigInteger values = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger nonce=getNonce(web3j,fromAddress);
        org.web3j.protocol.core.methods.request.Transaction transaction = org.web3j.protocol.core.methods.request.Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, null, toAddress, values);
        Request<?,EthCall> call=web3j.ethCall(transaction, DefaultBlockParameterName.LATEST);
        EthCall ethcall =call.send();
        return ResultMap.getSuccessfulResult(ethcall);
    }

    @ApiOperation(value = "eth_getCode", notes = "eth_getCode")
    @PostMapping("/eth_getCode")
    public ResultMap getCode(@RequestParam(value="address",required=true)String address
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        EthGetCode code= web3j.ethGetCode(address,DefaultBlockParameterName.LATEST).send();
        return ResultMap.getSuccessfulResult(code);
    }

    @ApiOperation(value = "eth_getStorageAt", notes = "eth_getStorageAt")
    @PostMapping("/eth_getStorageAt")
    public ResultMap getStorageAt (
                                   @RequestParam(value="address")String address,
                                   @RequestParam(value="position")BigInteger position
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        EthGetStorageAt send = web3j.ethGetStorageAt(address, position,DefaultBlockParameterName.LATEST).send();
        return ResultMap.getSuccessfulResult(send);
    }

    @ApiOperation(value = "eth_gasPrice", notes = "eth_gasPrice")
    @PostMapping("/eth_gasPrice")
    public ResultMap getGasPrice ()throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        EthGasPrice gasPrice= web3j.ethGasPrice().send();
        BigInteger price=gasPrice.getGasPrice();
        return ResultMap.getSuccessfulResult(price);
    }

    @ApiOperation(value = "eth_estimateGas", notes = "eth_estimateGas")
    @PostMapping("/eth_estimateGas")
    public ResultMap estimateGas ( @RequestParam(value="fromHash")String fromHash,
                                   @RequestParam(value="toHash")String toHash,
                                   @RequestParam(value="value")BigInteger value)throws Exception {
        BigDecimal amount= new BigDecimal(value);
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        BigInteger nonce =getNonce( web3j,fromHash);
        BigInteger values =Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        org.web3j.protocol.core.methods.request.Transaction transaction = org.web3j.protocol.core.methods.request.Transaction.createEtherTransaction(fromHash, nonce, gasPrice, null, toHash, values);
        EthEstimateGas ethEstimateGas =web3j.ethEstimateGas(transaction).send();
        if(ethEstimateGas.hasError()){
            throw new RuntimeException(ethEstimateGas.getError().getMessage());
        }


        return ResultMap.getSuccessfulResult(ethEstimateGas);
    }


    public static BigInteger getNonce(Web3j web3j,String addressHash){
        try {
            EthGetTransactionCount getNonce =web3j.ethGetTransactionCount(addressHash,DefaultBlockParameterName.LATEST).send();
            if(getNonce ==null){
                throw new RuntimeException("get nonce fail");
            }
            BigInteger nonce = getNonce.getTransactionCount();
            return nonce;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("net error");
        }
    }

    @ApiOperation(value = "eth_getBalance", notes = "eth_getBalance")
    @ApiImplicitParams({
    })
    @PostMapping("/eth_getBalance")
    public ResultMap getBalance(@RequestParam(value="address",required=true)String address
    )throws Exception {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        EthGetCode code= web3j.ethGetCode(address,DefaultBlockParameterName.LATEST).send();
        BigInteger balance=web3j.ethGetBalance(address,DefaultBlockParameterName.LATEST).send().getBalance();
        return ResultMap.getSuccessfulResult(balance);
    }


    private void saveDposVotes(String voter,String candidate,String stake,long blockNumber,Date date){
        DposVotes dposVotes = new DposVotes();
        dposVotes.setBlockNumber(blockNumber);
        dposVotes.setCandiDate(candidate);
        dposVotes.setVoter(voter);
        dposVotes.setLoopStartTime(date);
        dposVotes.setStake(new BigDecimal(stake));
        dposVotes.setRound(1);
        dposVotesMapper.saveOrUpdate(dposVotes);
    }

    private void saveDposNode(String voter,BigInteger stake,Integer type ,Integer round){
        DposNode dposNode = new DposNode();
        dposNode.setVoter(voter);
        dposNode.setStake(new BigDecimal(stake));
        dposNode.setType(type);
        dposNode.setRound(round);
        dposNodeMapper.saveNodeDpos(dposNode);
    }

    @ApiOperation(value = "getLockParam", notes = "getLockParam")
    @ApiImplicitParams({
    })
    @PostMapping("/getLockParam")
    public ResultMap getLockParam() throws IOException {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        org.web3j.protocol.core.methods.request.Transaction transaction = new org.web3j.protocol.core.methods.request.Transaction(null, null, null, null, "0xE0983f2AcaA23A8a7F106A6B80FcB76c62A28940", null, "0x05216523");
        String result=web3j.ethCall(transaction,DefaultBlockParameterName.LATEST).send().getResult();
        String lockTime=result.substring(2,66);
        String releseTime=result.substring(67,130);
        String apartTime=result.substring(131,193);
        return  ResultMap.getSuccessfulResult(result);
    }

    @ApiOperation(value = "getMinerParam", notes = "getMinerParam")
    @ApiImplicitParams({
    })
    @PostMapping("/eth_getMinerParam")
    public ResultMap getMinerParam() throws IOException {
        Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
        org.web3j.protocol.core.methods.request.Transaction transaction = new org.web3j.protocol.core.methods.request.Transaction(null, null, null, null, "0xf8b161bc8425177D7Af4DA43E28a1AEDDcd6EcA2", null, "0x05216523");
        String result=web3j.ethCall(transaction,DefaultBlockParameterName.LATEST).send().getResult();
        String lockTime=result.substring(2,66);
        String releseTime=result.substring(67,130);
        String apartTime=result.substring(131,193);
        return  ResultMap.getSuccessfulResult(result);
    }

    private  Integer getRound(int blockNumber){
        int rouds=0;
        if(blockNumber<=round){
            rouds=1;
            return rouds;
        }else{
            float cell=blockNumber/round;
            double nYear=(double)blockNumber/round;
            double nFee= Math.ceil(nYear);
            int round = new Double(nFee).intValue();
            return round;
        }
    }
}
