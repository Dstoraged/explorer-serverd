package com.imooc.mapper;

import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.View.TransactionView;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Mapper
public interface TransactionMapper extends MyMapper<Transaction> {
    List<Transaction> getTxListInfo(@Param("paramsMap")Map<String, Object> map);

    Transaction checkStatus(@Param("txHash")String txHash);

    List<Transaction> findWorkTransaction(@Param("blockHash")String blockHash, @Param("blockNumber")Long blockNumber,@Param("hash") String transHash, @Param("fromAddress")String fromAddress,@Param("nonce")Long nonce);

    long getAvgPrice(@Param("number")long number);

    long getTotalByBlockNumber(@Param("blockNumber")Long blockNumber,@Param("fromHash")String fromHash);

    long getTotalTransactionCount(@Param("paramsMap")Map<String, Object> map);

    List<Transaction> pageList(@Param("pageCurrent")Long pageCurrent,@Param("pageSize") Long pageSize,@Param("paramsMap")Map<String, Object> map);

    Transaction selectByTxHash(@Param("txHash")String param);

    List<Transaction> getTransactionByBlockNumber(@Param("pageCurrent")Long pageCurrent,@Param("pageSize") Long pageSize,@Param("blockNumber") Long blockNumber,@Param("fromHash")String fromHash);

    Long getMaxBlockNumberByAddressHash(@Param("fromAddressHash")String addressHash);

    long getFromOrToCount(@Param("addressHash")String addressHash,@Param("fromaddressHash")String fromaddressHash);

    List<Transaction> getTransactionInfoByToHash(@Param("pageCurrent")Long pageCurrent, @Param("pageSize")Long pageSize,@Param("addressHash") String addressHash,@Param("fromaddressHash")String fromaddressHash);

    List<Transaction> getTransactionInfoByAddressHash(@Param("pageCurrent")Long pageCurrent,@Param("pageSize") Long pageSize,@Param("addressHash") String addressHash,@Param("fromaddressHash")String fromaddressHash);

    List<Transaction> getTransactionInfoByFromHash(@Param("pageCurrent")Long pageCurrent,@Param("pageSize") Long pageSize,@Param("addressHash") String addressHash);

    Transaction getTransactionByTxHash(@Param("txHash")String txHash);

    List<TransactionView> getAddressDetail(@Param("addressHash")String addressHash, @Param("pageCurrent")long current, @Param("pageSize")long pageSize);

    long getTotalTransactionToCount(@Param("toAddress")String address);

    void insertorUpdate(@Param("transaction")Transaction transaction);

    BigDecimal getTotalValues(@Param("param")Map<String,Object>map);

    List<Transaction> getToAddress(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    List<Map<String,Object>> getFromAndToTransaction(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    Long getTotalFromAndTo(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    BigDecimal getTotalFromAndToValue(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    BigDecimal getTotalTxValue24H();

    BigDecimal getAddr0Balance(@Param("addressZero")String addressZero);
    
    BigDecimal getAddr0Txin(@Param("addressZero")String addressZero);

    List<Transaction> getTransactionByTxType(@Param("txType")String txType);
    
    Transaction getLatestTransactionByTxType(@Param("txType")String txType);

    void batchTransaction(@Param("list")List<Transaction> list);

	Map<String, Object> getStProofTx(@Param("device_addr") String device_addr, @Param("rent_hash") String rent_hash);
	

    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 1000)
    void queryAll(ResultHandler<Transaction> handler);
    
}
