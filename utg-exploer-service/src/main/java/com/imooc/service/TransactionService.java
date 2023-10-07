package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.TransactionQueryForm;
import com.imooc.pojo.Form.TxQueryForm;
import com.imooc.pojo.Transaction;
import com.imooc.utils.ResultMap;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    ResultMap getTxList(String address, long startBlock, long endBlock);

    ResultMap getTernalList(String address, long startBlock, long endBlock);

    ResultMap getTernalByTransactionHash(String txHash);

    ResultMap ternalListByBlock(long startBlock, long endBlock);

    ResultMap checkStatus(String txHash);

    List<Transaction> findForkTransaction(String blockHash, Long blockNumber, String transHash, String fromAddress, Long nonce);

    ResultMap<Page<Transaction>> pageList(BlockQueryForm blockQueryForm);

    ResultMap<Page<Map>> txPageList(TxQueryForm tx);

    ResultMap getTransaction(TransactionQueryForm transactionQueryForm);

    ResultMap<Page<Transaction>> getTransactionByBlockNumber(TransactionQueryForm transactionQueryForm);

    ResultMap getAddressInfoByHash(TransactionQueryForm transactionQueryForm);

    ResultMap<Page<Transaction>> getTransactionInfoByAddressHash(TransactionQueryForm transactionQueryForm);

    ResultMap getTransactionInfoByTxHash(TransactionQueryForm transactionQueryForm);

    ResultMap getForAddressDetail(TransactionQueryForm transactionQueryForm) throws Exception;

    ResultMap getTransactionValue();

    ResultMap getNewAccounts();

    ResultMap getTransactionByToAddress(BlockQueryForm blockQueryForm);

    ResultMap<Page<Transaction>> getFromAndToTransaction(BlockQueryForm blockQueryForm);

    ResultMap getTransactionByTokenHash(TransactionQueryForm transactionQueryForm);

    ResultMap getTransactionByTokenHashInfo(String txHash);

    ResultMap validateCaladateAddress(String address);

    ResultMap getExchRate(String txType);

    //ResultMap getTransactionInfoByTxHashParam(String txHash);
}
