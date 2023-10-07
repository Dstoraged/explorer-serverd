package com.imooc.mapper;

import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.TransToken;
import com.imooc.utils.MyMapper;
import com.imooc.utils.ResultMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TransferTokenMapper extends MyMapper<TransToken> {

    ResultMap getTokenSupply(@Param("contractAddress")String contractAddress);

    List<TransToken> getTransferList(@Param("coinType")long coinType);

    List<TransToken> getTransToken(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

    List<TransToken> getTransTokenList(@Param("blockQueryForm") BlockQueryForm blockQueryForm);
    long getTransTokenListCount(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

    long getTransTokenCount(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

    void insertOrUptate(@Param("transaction") TransToken item);

    List<TransToken> getTransactionByTxHash(@Param("txHash")String txHash);

    long getContractCount(@Param("contractAddress")String contractAddress);
   
	List<Map> getTokenHolderList(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

	long getTokenHolderCount(@Param("blockQueryForm") BlockQueryForm blockQueryForm);
	
	List<Map> getTokenInventoryList(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

	long getTokenInventoryCount(@Param("blockQueryForm") BlockQueryForm blockQueryForm);
	
}
