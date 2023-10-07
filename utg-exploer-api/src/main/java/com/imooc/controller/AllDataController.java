package com.imooc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.*;
import com.imooc.pojo.Form.*;
import com.imooc.pojo.Param.TransactionQueryParam;
import com.imooc.pojo.vo.*;
import com.imooc.service.*;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/platform")
@Api(tags="AllDataController")

public class AllDataController {

    @Value("${ipaddress}")
    private String ipaddress;
    private Logger logger = LoggerFactory.getLogger(AllDataController.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AddrBalancesService addrBalancesService;

    @Autowired
    private TransferMinerService transferMinerService;

    @Autowired
    private DposVoterService dposVoterService;

    @Autowired
    private PunishedService punishedService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private NodeExitService nodeExitService;

    @Autowired
    private PledgeDataService pledgeDataService;

    @Autowired
    private UtgStorageMinerService UtgStorageMinerService;
    
    @Autowired
    private OverviewService overviewService;
    
    @Autowired
    private NodeLjRewardService ljRewardService;
    
    @RequestMapping(value = "/getBwPlgeRange", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultMap getBwPlgeRange() throws IOException {
        return blockService.getBwPlgeRange();
    }

    @ResponseBody
    @PostMapping("/getBlockNumber")
    public ResultMap getBlockNumber() {
        return blockService.getBlockNumber();
    }

    @ApiOperation("getDatas")
    @RequestMapping(value = "/getDatas", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultMap getDatas() throws IOException {
    //    return blockService.getDatas();
    	return overviewService.getDatas();
    } 

    @ApiOperation(value = "getDataForUtg", notes = "getDataForUtg")
    @ResponseBody
    @PostMapping("/getDataForUtg")
    public ResultMap getDataForUtg() throws Exception {
        return blockService.getDataForUtg();
    }
    
    @ResponseBody
    @RequestMapping("/getIndexBlock")
    public ResultMap<Page<BlocksVo>> getIndexBlock() {
        return overviewService.getIndexBlock();
    }
    
    @ResponseBody
    @RequestMapping("/getIndexTransaction")
    public ResultMap<Page<Transaction>> getIndexTransaction() {
        return overviewService.getIndexTransaction();
    }
            
    @ResponseBody
    @RequestMapping("/getAddr0Data")
    public ResultMap<?> getAddr0Data(@RequestBody BlockQueryForm blockQueryForm) throws Exception {
        /*if(blockQueryForm!=null&&blockQueryForm.getBlockNumber()!=null&&blockQueryForm.getBlockNumber()>0) {
            return blockService.getAddr0Data(blockQueryForm.getBlockNumber());
        }else{
            return blockService.getAddr0Data(0l);
        }*/
    	return overviewService.getAddr0Data(0L);
    }
    
    @ResponseBody
    @RequestMapping("/getIndexData")
    public ResultMap<?> getIndexData() {
        return overviewService.getIndexData();
    }

    @ApiOperation(value = "getBlockList", notes = "getBlockList")
    @ResponseBody
    @PostMapping("/getBlockList")
    public ResultMap<Page<BlocksVo>> getBlockList(@Valid @RequestBody BlockQueryForm blockQueryForm) {
        return blockService.pageList(blockQueryForm);
    }
    
    @ApiOperation(value = "Latest transaction list", notes = "Latest transaction list")
    @ApiImplicitParam(name = "blockQueryForm", value = "Query parameter", required = true, dataType = "BlockQueryForm")
    @ResponseBody
    @PostMapping("/getTransactionList")
    public ResultMap<Page<Transaction>> getTransactionList(@Valid @RequestBody BlockQueryForm blockQueryForm) {
        blockQueryForm.setToAddr(Constants.pre0XtoNX(blockQueryForm.getToAddr()));
        blockQueryForm.setFromAddr(Constants.pre0XtoNX(blockQueryForm.getFromAddr()));
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        blockQueryForm.setContract(Constants.pre0XtoNX(blockQueryForm.getContract()));
        return transactionService.pageList(blockQueryForm);
    }

    @ApiOperation(value = "getAddressList", notes = "getAddressList")
    @ResponseBody
    @PostMapping("/getAddressList")
    public ResultMap<Page<AddressVo>> getAddressList(@Valid @RequestBody BlockQueryForm blockQueryForm) {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return accountService.pageList(blockQueryForm);
    }
    
    @ResponseBody
    @GetMapping("/getAddressBalance")
    public ResultMap<BigDecimal> getAddressBalance(@RequestParam(value = "addressHash", required = true) String addressHash){
    	BigDecimal balance = accountService.getAddressBalance(addressHash);
    	return ResultMap.getSuccessfulResult(balance);
    }

    @ApiOperation(value = "searchByParam", notes = "searchByParam")
    @ResponseBody
    @PostMapping("/searchByParam")
    public ResultMap getTransactions(@Valid @RequestBody TransactionQueryForm transactionQueryForm) {
        transactionQueryForm.toParam(TransactionQueryParam.class);
        return transactionService.getTransaction(transactionQueryForm);
    }

    @ApiOperation(value = "getBlockInfoByBlockNumber", notes = "getBlockInfoByBlockNumber")
    @ResponseBody
    @PostMapping("/searchByNumber")
    public ResultMap getBlockInfoByBlockNumber(@RequestParam(value = "blockNumber", required = true) Long blockNumber) {
        return blockService.getBlockInfoByBlockNumber(blockNumber);
    }

    @ApiOperation(value = "getTransactionsByBlockNumber", notes = "getTransactionsByBlockNumber")
    @ResponseBody
    @PostMapping("/getTransactionByBlockNumber")
    public ResultMap<Page<Transaction>> getTransactionsByBlockNumber(@Valid @RequestBody TransactionQueryForm transactionQueryForm) {
        transactionQueryForm.toParam(TransactionQueryParam.class);
        return transactionService.getTransactionByBlockNumber(transactionQueryForm);
    }

    @ApiOperation(value = "getAddressInfoByAddressHash", notes = "getAddressInfoByAddressHash")
    @ResponseBody
    @PostMapping("/getAddressInfoByAddressHash")
    public ResultMap getAddressInfoByAddressHash(@Valid @RequestBody TransactionQueryForm transactionQueryForm) {
        transactionQueryForm.toParam(TransactionQueryParam.class);
        transactionQueryForm.setAddressHash(Constants.pre0XtoNX(transactionQueryForm.getAddressHash()));
        return transactionService.getAddressInfoByHash(transactionQueryForm);
    }

    @ApiOperation(value = "getTransactionByAddressHash", notes = "getTransactionByAddressHash")
    @ResponseBody
    @PostMapping("/getTransactionByAddressHash")
    public ResultMap<Page<Transaction>> getTransactionByAddressHash(@Valid @RequestBody TransactionQueryForm transactionQueryForm) {
        transactionQueryForm.toParam(TransactionQueryParam.class);
        transactionQueryForm.setAddressHash(Constants.pre0XtoNX(transactionQueryForm.getAddressHash()));
        return transactionService.getTransactionInfoByAddressHash(transactionQueryForm);
    }

    @ApiOperation(value = "getTransactionByTxHash", notes = "getTransactionByTxHash")
    @ResponseBody
    @PostMapping("/getTransactionByTxHash")
    public ResultMap getTransactionByTxHash(@Valid @RequestBody TransactionQueryForm transactionQueryForm) {
        transactionQueryForm.toParam(TransactionQueryParam.class);
        transactionQueryForm.setTxHash(transactionQueryForm.getTxHash());
        return transactionService.getTransactionInfoByTxHash(transactionQueryForm);
    }

    @ResponseBody
    @PostMapping("/getBlanceForAddressHash")
    public ResultMap getEchartForBalance(@RequestParam(value = "addressHash", required = true) String addressHash) {
        return addrBalancesService.getBalanceForAddress(Constants.pre0XtoNX(addressHash));
    }

    @ApiOperation(value = "getForAddressDetail", notes = "getForAddressDetail")
    @ResponseBody
    @PostMapping("/selectForAddressDetail")
    public ResultMap getForAddressDetail(@Valid @RequestBody TransactionQueryForm transactionQueryForm) throws Exception {
        transactionQueryForm.toParam(TransactionQueryParam.class);
        transactionQueryForm.setAddressHash(Constants.pre0XtoNX(transactionQueryForm.getAddressHash()));
        return transactionService.getForAddressDetail(transactionQueryForm);
    }

    @ApiOperation(value = "Storage mining", notes = "Storage mining")
    @ResponseBody
    @PostMapping("/getMinerInfoForAddress")
    public ResultMap getMinerInfoForAddress(@Valid @RequestBody TransferMinerForm transferMinerForm) throws Exception {
        return transferMinerService.getMinerInfoForAddress(transferMinerForm);
    }

    @ResponseBody
    @PostMapping("/getMinerInfoForWallet")
    public ResultMap getMinerInfoForWallet(@Valid @RequestBody MinerQueryForm minerQueryForm) throws Exception {
        minerQueryForm.setAddress(Constants.pre0XtoNX(minerQueryForm.getAddress()));
        return transferMinerService.getAllMinerInfoWallet(minerQueryForm);
    }



    @ApiOperation(value = "getAllParamters", notes = "getAllParamters")
    @ResponseBody
    @PostMapping("/getAllParamters")
    public ResultMap getAllParamters(@RequestParam(value = "types", required = true) long types, @RequestParam(value = "language", required = true) String language) throws Exception {
        return blockService.getAllParamters(types, language);
    }


    @ApiOperation(value = "getTransactionValue", notes = "getTransactionValue")
    @ResponseBody
    @PostMapping("/getTransactionValue")
    public ResultMap getTransactionValue() throws Exception {
        return transactionService.getTransactionValue();
    }


    @ApiOperation(value = "getNewAddress", notes = "getNewAddress")
    @ResponseBody
    @PostMapping("/getNewAddress")
    public ResultMap getNewAddress() throws Exception {
        return transactionService.getNewAccounts();
    }

    @ResponseBody
    @PostMapping("/lockAndPledgeInfo")
    public ResultMap<Page<TransferMiner>> getLockAndPledgeInfo(@Valid @RequestBody LockUpTransferForm lockUpTransferForm) throws Exception {
        return transferMinerService.getLockAndPledgeInfo(lockUpTransferForm);
    }



    @ResponseBody
    @PostMapping("/getDposVoterInfo")
    public ResultMap<Page<DposVotes>> getDposVoterInfo(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        return dposVoterService.getDposVoterInfo(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getDposNodeInfo")
    public ResultMap<Page<DposNode>> getDposNodeInfo(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        return dposVoterService.getDposNodeInfo(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/lockUtgMinerInfo")
    public ResultMap<Page<TransferMinerVo>> getlockUtgMinerInfo(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        blockQueryForm.setRevenueAddress(Constants.pre0XtoNX(blockQueryForm.getRevenueAddress()));
        return transferMinerService.getLockUtgMinerInfo(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getUtgMinerLockSum")
    public ResultMap<Page<TransferMinerVo>> getUtgMinerLockSum(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        blockQueryForm.setRevenueAddress(Constants.pre0XtoNX(blockQueryForm.getRevenueAddress()));
        return transferMinerService.getUtgMinerLockSum(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/lockSummaryByRevAddress")
    public ResultMap getLockSummaryByRevAddress(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        blockQueryForm.setRevenueAddress(Constants.pre0XtoNX(blockQueryForm.getRevenueAddress()));
        return transferMinerService.getLockSummaryByRevAddress(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/lockUtgMinersByRevAddress")
    public ResultMap<Page<TransferMinerVo>> lockUtgMinersByRevAddress(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        blockQueryForm.setRevenueAddress(Constants.pre0XtoNX(blockQueryForm.getRevenueAddress()));
        return transferMinerService.getlockUtgMinersByRevAddress(blockQueryForm);
    }


    @ResponseBody
    @PostMapping("/getVoterListInfo")
    public ResultMap<Page<DposVoterVo>> getVoterListInfo(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        return dposVoterService.getVoterListInfo(blockQueryForm);

    }

    @ResponseBody
    @PostMapping("/getOverData")
    public ResultMap<Page<TransferMiner>> getOverData(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return transferMinerService.getOverData(blockQueryForm);

    }

    @ResponseBody
    @PostMapping("/getDposVoterTotalForRound")
    public ResultMap getDposVoterTotalForRound() throws IOException {
        return dposVoterService.getDposVoterTotalForRound();
    }

    @ResponseBody
    @PostMapping("/getPledgeExit")
    public ResultMap getPledgeExit() throws Exception {
        return transferMinerService.getPledgeExit();
    }

    @ResponseBody
    @PostMapping("/getDposVoterAddressTotal")
    public ResultMap getDposVoterAddressTotal(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        return dposVoterService.getDposVoterAddressTotal(blockQueryForm);
    }


    @ApiOperation(value = "getPunished", notes = "getPunished")
    @ResponseBody
    @PostMapping("/utg/getPunished")
    public ResultMap<Page<Punished>> getPunishedInfo(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return punishedService.getPunishedInfo(blockQueryForm);
    }


    @ApiOperation(value = "getTokenList", notes = "getTokenList")
    @ResponseBody
    @PostMapping("/getTokensInfo")
    public ResultMap<Page<Tokens>> getTokenList(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return tokenService.getTokenList(blockQueryForm);
    }

    @ApiOperation(value = "getTokenContractList", notes = "getTokenContractList")
    @ResponseBody
    @PostMapping("/getContractToken")
    public ResultMap<Page<TokenContract>> getTokenContractList(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return tokenService.getTokenContractList(blockQueryForm);
    }


    @ApiOperation(value = "getAddressesList")
    @ResponseBody
    @PostMapping("/getAddressInfoList")
    public ResultMap<Page<Addresses>> getAddressesList(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return accountService.getContractPageTokenList(blockQueryForm);
    }

    @ResponseBody
    @RequestMapping("/getAddressTokenList")
    public ResultMap getAddressTokenList(@RequestParam(value="address")String address) throws Exception {
        return tokenService.getAddressTokenList(address);
    }
    
    @ResponseBody
    @PostMapping("/getTokenHolderList")
    public ResultMap getTokenHolderList(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return tokenService.getTokenHolderList(blockQueryForm);
    }
    
    @ResponseBody
    @PostMapping("/getTokenInventoryList")
    public ResultMap getTokenInventoryList(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return tokenService.getTokenInventoryList(blockQueryForm);
    }
    

    @ApiOperation(value = "getTransTokenListForContract")
    @ResponseBody
    @PostMapping("/getTransTokenListForContract")
    public ResultMap<Page<TransToken>> getTransTokenListForContract(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return tokenService.getTransTokenListForContract(blockQueryForm);
    }

    @ApiOperation(value = "getTransTokenList")
    @ResponseBody
    @PostMapping("/getTransTokenList")
    public ResultMap<Page<TransToken>> getTransTokenList(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        blockQueryForm.setContract(Constants.pre0XtoNX(blockQueryForm.getContract()));
        return tokenService.getTransTokenList(blockQueryForm);
    }

    @ApiOperation(value = "getContractMap", notes = "getContractMap")
    @ResponseBody
    @PostMapping("/getTokensDescription")
    public ResultMap<Tokens> getContractMap(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return tokenService.getContractMap(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getNodeExitForRn")
    public ResultMap<NodeExit> getNodeExit(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        return nodeExitService.getNodeExit(blockQueryForm);
    }

    @ApiOperation(value = "Node list query", notes = "Node list query")
    @ResponseBody
    @PostMapping("/node/getcadnodelist")
    public ResultMap<Page<UtgNodeMiner>> getcadnodelist(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm){
        UtgNodeMinerQueryForm.setNode_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getNode_address()));
        UtgNodeMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getRevenue_address()));
        UtgNodeMinerQueryForm.setAddress(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getAddress()));
        return nodeExitService.getNodeList(UtgNodeMinerQueryForm);
    }

    @ResponseBody
    @PostMapping("/getNodePledgeList")
    public ResultMap<Page<UtgNodeMiner>> getNodePledgeList(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm){
//        if (UtgNodeMinerQueryForm.getEtType()==null || "".equals(UtgNodeMinerQueryForm.getEtType())){
//        	return ResultMap.getApiFailureResult("etType is null ");
//        }
    	UtgNodeMinerQueryForm.setNode_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getNode_address()));
        UtgNodeMinerQueryForm.setPledge_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getPledge_address()));
        UtgNodeMinerQueryForm.setAddress(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getAddress()));
        return nodeExitService.getNodePledgeList(UtgNodeMinerQueryForm);
    }
    
    @ResponseBody
    @PostMapping("/getNodeRewardList")
    public ResultMap getNodeRewardList(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm){
        UtgNodeMinerQueryForm.setNode_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getNode_address()));
        UtgNodeMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getRevenue_address()));
        UtgNodeMinerQueryForm.setAddress(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getAddress()));
        return nodeExitService.getNodeRewardList(UtgNodeMinerQueryForm);
    }
    
    @ResponseBody
    @PostMapping("/getPledgeRewardList")
    public ResultMap getPledgeRewardList(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm){
        UtgNodeMinerQueryForm.setNode_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getNode_address()));
        UtgNodeMinerQueryForm.setPledge_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getPledge_address()));
     //   UtgNodeMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getRevenue_address()));
        UtgNodeMinerQueryForm.setAddress(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getAddress()));
        return nodeExitService.getPledgeRewardList(UtgNodeMinerQueryForm);
    }

    @ApiOperation(value = "Node detailed query", notes = "Node detailed query")
    @ResponseBody
    @PostMapping("/node/getcadnode")
    public ResultMap getcadnode(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm) throws Exception {
        UtgNodeMinerQueryForm.setNode_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getNode_address()));
        return nodeExitService.getNode(UtgNodeMinerQueryForm);
    }

    @ApiOperation(value = "Node rev detailed query", notes = "Node  rev detailed query")
    @ResponseBody
    @PostMapping("/node/getcadnodeByRev")
    public ResultMap getcadnodeByRev(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm) throws Exception {
        UtgNodeMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getRevenue_address()));
        return nodeExitService.getcadnodeByRev(UtgNodeMinerQueryForm);
    }

    @ApiOperation(value = "Node getNodeExistRealse query", notes = "Node getNodeExistRealse query")
    @ResponseBody
    @PostMapping("/node/getNodeExistRealse")
    public ResultMap getNodeExistRealse(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm) throws Exception {
        UtgNodeMinerQueryForm.setAddress(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getAddress()));
        return nodeExitService.getNodeExistRealse(UtgNodeMinerQueryForm);
    }

    @ApiOperation(value = "Node pledge amount", notes = "Node pledge amount")
    @ResponseBody
    @PostMapping("/node/getNodePledgeAmount")
    public ResultMap getNodePledgeAmount(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm)throws Exception  {
        UtgNodeMinerQueryForm.setNode_address(Constants.pre0XtoNX(UtgNodeMinerQueryForm.getNode_address()));
        return  nodeExitService.getNodePledgeAmount(UtgNodeMinerQueryForm);
    }

    @ResponseBody
    @PostMapping("/getTokenIsExitCount")
    public ResultMap getTokenIsExitCount(@Valid @RequestBody TokenQueryForm tokenQueryForm) {
        return tokenService.getTokenIsExitCount(tokenQueryForm);
    }

    @ResponseBody
    @PostMapping("/getTransactionByToAddress")
    public ResultMap getTransactionByToAddress(@Valid @RequestBody BlockQueryForm blockQueryForm) {
        blockQueryForm.setFromAddr(Constants.pre0XtoNX(blockQueryForm.getFromAddr()));
        blockQueryForm.setToAddr(Constants.pre0XtoNX(blockQueryForm.getToAddr()));
        return transactionService.getTransactionByToAddress(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getFromAndToTransaction")
    public ResultMap<Page<Transaction>> getFromAndToTransaction(@Valid @RequestBody BlockQueryForm blockQueryForm) {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        blockQueryForm.setFromAddr(Constants.pre0XtoNX(blockQueryForm.getFromAddr()));
        blockQueryForm.setToAddr(Constants.pre0XtoNX(blockQueryForm.getToAddr()));
        return transactionService.getFromAndToTransaction(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getAllTotalForReceive")
    public ResultMap<Page<Transaction>> getAllTotalForReceive(@RequestParam(value = "address", required = true) String address) {
        return transferMinerService.getAllTotalForReceive(Constants.pre0XtoNX(address));
    }

    @ResponseBody
    @PostMapping("/getDposNodeInfoWallet")
    public ResultMap<Page<DposNode>> getDposNodeInfoWallet(@Valid @RequestBody BlockQueryForm blockQueryForm) throws IOException {
        return dposVoterService.getDposNodeInfoWallet(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getVoterListInfoForRound")
    public ResultMap<DposVoterVo> getVoterListInfoForRound(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        return dposVoterService.getVoterListInfoForRound(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getDposVoterForCandidate")
    public ResultMap<Page<DposVotes>> getDposVoterForCandidate(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        return dposVoterService.getDposVoterForCandidate(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getDposByRoundAndCandidateForAddress")
    public ResultMap<Page<DposVotes>> getDposByRoundAndCandidateForAddress(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        return dposVoterService.getDposByRoundAndCandidateForAddress(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getDposNodeForAddress")
    public ResultMap<Page<DposVotesWallet>> getDposNodeForAddress(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        return dposVoterService.getDposVoterWallet(blockQueryForm);
    }

    @ResponseBody
    @PostMapping("/getVotersInfoPageList")
    public ResultMap<Page<DposVotesWallet>> getVotersInfoPageList(@Valid @RequestBody DposVoterQueryForm dposVoterQueryForm) throws Exception {
        return dposVoterService.getVotersInfoPageList(dposVoterQueryForm);
    }

    @ResponseBody
    @PostMapping("/getRewardVoterPageList")
    public ResultMap<Page<Blocks>> getRewardVoterPageList(@Valid @RequestBody DposVoterQueryForm dposVoterQueryForm) throws Exception {
        return blockService.getRewardVoterPageList(dposVoterQueryForm);
    }


    @ResponseBody
    @PostMapping("/getTransactionByTokenHash")
    public ResultMap getTransactionByTokenHash(@Valid @RequestBody TransactionQueryForm transactionQueryForm) {
        transactionQueryForm.toParam(TransactionQueryParam.class);
        transactionQueryForm.setTxHash(Constants.pre0XtoNX(transactionQueryForm.getTxHash()));
        return transactionService.getTransactionByTokenHash(transactionQueryForm);
    }


    @ResponseBody
    @PostMapping("/getTransactionByTokenHashInfo")
    public ResultMap getTransactionByTokenHashInfo(@RequestParam(value = "txHash", required = true) String txHash) {
        return transactionService.getTransactionByTokenHashInfo(Constants.pre0XtoNX(txHash));
    }

    @ResponseBody
    @PostMapping("/validateCaladateAddress")
    public ResultMap validateCaladateAddress(@RequestParam(value = "address", required = false) String address) {
        return transactionService.validateCaladateAddress(Constants.pre0XtoNX(address));
    }

    @ResponseBody
    @PostMapping("/pagePledgeInfoList")
    public ResultMap <Page<PledgeTotalData>>pagePledgeInfoList(@Valid @RequestBody PledgeQueryForm pledgeQueryForm) {
        pledgeQueryForm.setAddress(Constants.pre0XtoNX(pledgeQueryForm.getAddress()));
       return pledgeDataService.selectPagePledgeList(pledgeQueryForm);
    }


    @ResponseBody
    @PostMapping("/pagePledgeInfoDetail")
    public ResultMap <PledgeTotalData>pagePledgeInfoDetail(@Valid @RequestBody PledgeQueryForm pledgeQueryForm) {
        pledgeQueryForm.setAddress(Constants.pre0XtoNX(pledgeQueryForm.getAddress()));
        return pledgeDataService.pagePledgeInfoDetail(pledgeQueryForm);
    }

    @ResponseBody
    @PostMapping("/pagePledgeDetail")
    public ResultMap <PledgeData>pagePledgeEnDetail(@Valid @RequestBody PledgeRnQueryForm pledgeRnQueryForm) {
        pledgeRnQueryForm.setAddress(Constants.pre0XtoNX(pledgeRnQueryForm.getAddress()));
        return pledgeDataService.pagePledgeEnDetail(pledgeRnQueryForm);
    }

    @ApiOperation(value = "getstorageMinerlist", notes = "getstorageMinerlist")
    @ResponseBody
    @PostMapping("/storage/getstorageMinerlist")
    public ResultMap getstorageMinerlist(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        UtgStorageMinerQueryForm.setMiner_addr(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getMiner_addr()));
        UtgStorageMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getRevenue_address()));
        return UtgStorageMinerService.pageList(UtgStorageMinerQueryForm);
    }

    @ApiOperation(value = "Miner Storage mining query", notes = "Miner Storage mining query")
    @ResponseBody
    @PostMapping("/storage/pageUtgCltList")
    public ResultMap pageUtgCltList(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        UtgStorageMinerQueryForm.setMiner_addr(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getMiner_addr()));
        UtgStorageMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getRevenue_address()));
        return UtgStorageMinerService.pageUtgCltList(UtgStorageMinerQueryForm);
    }

    @ApiOperation(value = "Miner Storage mining details", notes = "Miner Storage mining details")
    @ResponseBody
    @PostMapping("/storage/utgCltDetail")
    public ResultMap utgCltDetail(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        return UtgStorageMinerService.getUtgStorageMinerDetail(UtgStorageMinerQueryForm);
    }

    @ApiOperation(value = "Miner mining day statistics query ", notes = "Miner mining day statistics query ")
    @ResponseBody
    @PostMapping("/storage/getMinerDayStatislist")
    public ResultMap getMinerDayStatislist(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        UtgStorageMinerQueryForm.setMiner_addr(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getMiner_addr()));
        return UtgStorageMinerService.getMinerDayStatislist(UtgStorageMinerQueryForm);
    }

    @ApiOperation(value = "Overview query", notes = "Overview of query by income address")
    @ResponseBody
    @PostMapping("/my/outLine")
    public ResultMap outLine(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        UtgStorageMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getRevenue_address()));
        return UtgStorageMinerService.outLine(UtgStorageMinerQueryForm);
    }

    @ApiOperation(value = "srt conversion ratio query", notes = "srt conversion ratio query")
    @ResponseBody
    @PostMapping("/srt/getExchRate")
    public ResultMap getExchRate(@Valid @RequestBody BlockQueryForm blockQueryForm) {
         return transactionService.getExchRate(blockQueryForm.getTxType());
    }

    @ApiOperation(value = "Recharge record query", notes = "Recharge record query")
    @ResponseBody
    @PostMapping("/getTxRecord")
    public ResultMap getTxRecord(@Valid @RequestBody BlockQueryForm blockQueryForm) {
        blockQueryForm.setParam1(Constants.pre0XtoNX(blockQueryForm.getParam1()));
        blockQueryForm.setFromAddr(Constants.pre0XtoNX(blockQueryForm.getFromAddr()));
        return transactionService.pageList(blockQueryForm);
    }

    @ApiOperation(value = "My list of miners", notes = "My list of miners")
    @ResponseBody
    @PostMapping("/my/getminerlist")
    public ResultMap getminerlist(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        UtgStorageMinerQueryForm.setMiner_addr(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getMiner_addr()));
        UtgStorageMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getRevenue_address()));
        return UtgStorageMinerService.pageList(UtgStorageMinerQueryForm);
    }

    @ApiOperation(value = "Miner details", notes = "Miner details")
    @ResponseBody
    @PostMapping("/my/getminerdetail")
    public ResultMap getminerdetail(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        UtgStorageMinerQueryForm.setMiner_addr(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getMiner_addr()));
        UtgStorageMinerQueryForm.setRevenue_address(Constants.pre0XtoNX(UtgStorageMinerQueryForm.getRevenue_address()));
        return UtgStorageMinerService.getUtgStorageMinerDetail(UtgStorageMinerQueryForm);
    }

    @ApiOperation(value = "Bandwidth (M) is calculated as utg", notes = "Bandwidth (M) is calculated as utg")
    @ResponseBody
    @PostMapping("/bandWidthToUtg")
    public ResultMap bandWidthToUtg(@Valid @RequestBody JSONObject json) {
        return UtgStorageMinerService.bandWidthToUtg(json.getLong("bandWidth"));
    }

    @ApiOperation(value = "getArea", notes = "getArea")
    @ResponseBody
    @PostMapping("/getArea")
    public ResultMap getArea() {
        return UtgStorageMinerService.getArea();
    }

    @ApiOperation(value = "getOperatorConfig", notes = "getOperatorConfig")
    @ResponseBody
    @PostMapping("/getOperatorConfig")
    public ResultMap getOperatorConfig() {
        return UtgStorageMinerService.getOperatorConfig();
    }

    @ApiOperation(value = "getUtgNetStatics", notes = "getUtgNetStatics")
    @ResponseBody
    @PostMapping("/getUtgNetStatics")
    public ResultMap getUtgNetStatics(@Valid @RequestBody UtgNetStaticsForm net) {
        return UtgStorageMinerService.getUtgNetStatics(net);
    }

    @ApiOperation(value = "getNetServiceRank", notes = "getNetServiceRank")
    @ResponseBody
    @PostMapping("/getNetServiceRank")
    public ResultMap getNetServiceRank(@Valid @RequestBody UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        return UtgStorageMinerService.getNetServiceRank(UtgStorageMinerQueryForm);
    }
    @ResponseBody
    @PostMapping("/getPledgeByPledgeAddr")
    public ResultMap<NodePledge> getNodePledgeByPledgeAddr(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm){
    	String peldgeAddr=UtgNodeMinerQueryForm.getPledge_address();
    	if(peldgeAddr==null||"".equals(peldgeAddr)) {
    		return ResultMap.getFailureResult("Pledge_address is empty");  
    	}
    	NodePledge pledge=new NodePledge();
    	pledge.setPledge_address(UtgNodeMinerQueryForm.getPledge_address());
    	return nodeExitService.getNodePledgeByPledgeAddr(pledge);

    }
    @ResponseBody
    @PostMapping("/isPledgeTransfer")
    public ResultMap<Map<String,Object>> isPledgeTransfer(@Valid @RequestBody UtgNodeMinerQueryForm UtgNodeMinerQueryForm){
    	NodePledge pledge=new NodePledge();
    	pledge.setPledge_address(UtgNodeMinerQueryForm.getPledge_address());
    	pledge.setPledge_number(UtgNodeMinerQueryForm.getPledge_number());
    	return nodeExitService.isPledgeTransfer(pledge);
    	
    }
    @ResponseBody
    @PostMapping("/isEntrustPledge")
    public ResultMap<Map<String,Object>> isEntrustPledge(@Valid @RequestBody UtgNodeMinerQueryForm utgNodeMinerQueryForm){
    	if(utgNodeMinerQueryForm.getPledge_address()==null 
    			||utgNodeMinerQueryForm.getPledge_address().length()<=0
    			||utgNodeMinerQueryForm.getNode_address()==null
    			||utgNodeMinerQueryForm.getNode_address().length()<=0) {
    		return ResultMap.getFailureResult("param is error");
    	}
    	NodePledge pledge=new NodePledge();
    	pledge.setPledge_address(utgNodeMinerQueryForm.getPledge_address());
    	pledge.setNode_address(utgNodeMinerQueryForm.getNode_address());
    	
    	return nodeExitService.isEntrustPledge(pledge);
    }
    @ApiOperation(value = "getNodeLjRewardlist", notes = "getNodeLjRewardlist")
    @ResponseBody
    @PostMapping("/getNodeLjRewardlist")
    public ResultMap<Page<NodeReward>> getNodeLjRewardlist(@Valid @RequestBody NodeLjQueryForm ljForm) {
       if(ljForm.getNodeAddress()==null ||ljForm.getNodeType()==null) {
    	   return ResultMap.getFailureResult("param is error");
       }
    	return ljRewardService.pageList(ljForm);
    }
}