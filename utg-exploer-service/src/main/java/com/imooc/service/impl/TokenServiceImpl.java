package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.*;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.TokenQueryForm;
import com.imooc.pojo.TokenContract;
import com.imooc.pojo.Tokens;
import com.imooc.pojo.TransToken;
import com.imooc.service.TokenService;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {
   @Autowired
   private TransferTokenMapper transferTokenMapper;

   @Autowired
   private TokenMapper tokenMapper;

   @Autowired
   private AccountMapper accountMapper;
   @Autowired
   private TransactionMapper transactionMapper;
   
   @Autowired
   private ContractTokenMapper contractTokenMapper;

    private static final Logger logger= LoggerFactory.getLogger(TokenServiceImpl.class);

    public ResultMap getTransferListByCoinType(int coinType) {
        List<TransToken> listInfo =transferTokenMapper.getTransferList(coinType);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    public ResultMap getTotalSupply(String contractAddress) {
       List<Tokens> listInfo =tokenMapper.getTokenSupply(contractAddress);
       return ResultMap.getSuccessfulResult(listInfo);
    }
    
    public ResultMap getAddressTokenList(String address) {
        List<Tokens> listInfo =tokenMapper.getAddressTokenList(address);
        return ResultMap.getSuccessfulResult(listInfo);
     }

    @Override
    public ResultMap<Page<Tokens>> getTokenList(BlockQueryForm blockQueryForm) {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<Tokens> list = tokenMapper.getTokenListInfo(blockQueryForm);
//        for(Tokens tokens:list){
//            String contractAddress =tokens.getContract();
//       //     long count =accountMapper.getContractTokenCount(contractAddress);
//            long count = transferTokenMapper.getContractCount(contractAddress);
//            tokens.setAccountTotal(count);
//       //     long txcount = transactionMapper.getTotalTransactionToCount(contractAddress);
//            BlockQueryForm queryForm = new BlockQueryForm();
//            queryForm.setAddress(contractAddress);
//            long txcount = transferTokenMapper.getTransTokenCount(queryForm);
//            tokens.setTxcount(txcount);
//        }
        long total=tokenMapper.getTotalTokens(blockQueryForm);
        page.setRecords(list);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap<Page<TransToken>> getTransTokenListForContract(BlockQueryForm blockQueryForm) {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<TransToken> listInfo =transferTokenMapper.getTransToken(blockQueryForm);
        long total=transferTokenMapper.getTransTokenCount(blockQueryForm);
        page.setRecords(listInfo);
        page.setTotal(total);        
        return ResultMap.getSuccessfulResult(page);
    }

    public ResultMap<Page<TransToken>> getTransTokenList(@Param("blockQueryForm")BlockQueryForm blockQueryForm){
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        long total = transferTokenMapper.getTransTokenListCount(blockQueryForm);
        List<TransToken> listInfo =transferTokenMapper.getTransTokenList(blockQueryForm);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap <Tokens>getContractMap(BlockQueryForm blockQueryForm) {
        String contract=blockQueryForm.getAddress();
        Tokens token = tokenMapper.getTokens(contract);
    //    long count = accountMapper.getContractCount(contract);        
        if(token !=null){
        	long count = transferTokenMapper.getContractCount(contract);
            token.setAccountTotal(count);
         //   long txcount = transactionMapper.getTotalTransactionToCount(contract);
            long txcount = transferTokenMapper.getTransTokenCount(blockQueryForm);
            token.setTxcount(txcount);
        }
        return ResultMap.getSuccessfulResult(token);
    }

    @Override
    public ResultMap getTokenIsExitCount(TokenQueryForm tokenQueryForm) {
        long count =tokenMapper.getCountForExit(tokenQueryForm);
        return ResultMap.getSuccessfulResult(count);
    }

    @Override
    public ResultMap<Page<TokenContract>> getTokenContractList(BlockQueryForm blockQueryForm) {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<TokenContract>list =contractTokenMapper.getTokenContract(blockQueryForm);
        for(TokenContract tokens:list){
            String contractAddress =tokens.getContractAddress();
            long count =transferTokenMapper.getContractCount(contractAddress);
          //  long count =accountMapper.getContractCount(contractAddress);
            tokens.setAccountTotal(count);
        }
        long total =contractTokenMapper.getTotal(blockQueryForm);
        page.setRecords(list);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

	@Override
	public ResultMap getTokenHolderList(BlockQueryForm blockQueryForm) {
		Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        blockQueryForm.setAddr(Constants.addressZero);        
        List<Map>list = transferTokenMapper.getTokenHolderList(blockQueryForm);        
        long total = transferTokenMapper.getTokenHolderCount(blockQueryForm);
        page.setRecords(list);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
	}
	
	@Override
	public ResultMap getTokenInventoryList(BlockQueryForm blockQueryForm) {
		Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        
        List<Map>list = transferTokenMapper.getTokenInventoryList(blockQueryForm);        
        long total = transferTokenMapper.getTokenInventoryCount(blockQueryForm);
        page.setRecords(list);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
	}
}
