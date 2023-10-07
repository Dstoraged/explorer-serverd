package com.imooc.Utils;

import com.imooc.pojo.vo.TransactionVo;
import com.imooc.utils.Constants;
import com.imooc.utils.UTGOperatorEnum;
import com.imooc.utils.SscOperatorEnum;
import com.imooc.utils.UfoPrefixEnum;

import java.util.HashMap;
import java.util.Map;

/**
 *  mapcache
 * @author zhc 2021-09-08 15:31
 */
public class MapCache {

    public  static Map<String,TransactionVo> ufoPreMap = new HashMap<>();
    static {
        loadMap();
    }

    /**
     * UTG SSC
     */
    public  static  void loadMap(){
        //UTG
        ufoPreMap.put(UTGOperatorEnum.Exch.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(), Constants.ufoVersion, UTGOperatorEnum.Exch.getCode()));
        ufoPreMap.put(UTGOperatorEnum.Bind.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.Bind.getCode()));
        ufoPreMap.put(UTGOperatorEnum.Unbind.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.Unbind.getCode()));
        ufoPreMap.put(UTGOperatorEnum.Rebind.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.Rebind.getCode()));
        ufoPreMap.put(UTGOperatorEnum.CandReq.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.CandReq.getCode()));
        ufoPreMap.put(UTGOperatorEnum.CandExit.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.CandExit.getCode()));
        ufoPreMap.put(UTGOperatorEnum.CandPnsh.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.CandPnsh.getCode()));
        ufoPreMap.put(UTGOperatorEnum.FlwReq.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.FlwReq.getCode()));
        ufoPreMap.put(UTGOperatorEnum.FlwExit.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.FlwExit.getCode()));
        ufoPreMap.put(UTGOperatorEnum.CandEntrust.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.CandEntrust.getCode()));
        ufoPreMap.put(UTGOperatorEnum.CandETExit.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.CandETExit.getCode()));
        ufoPreMap.put(UTGOperatorEnum.CandChaRate.getEncode(),new TransactionVo(UfoPrefixEnum.UTG.getCode(),Constants.ufoVersion, UTGOperatorEnum.CandChaRate.getCode()));
        //SSC
        ufoPreMap.put(SscOperatorEnum.ExchRate.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.ExchRate.getCode()));
        ufoPreMap.put(SscOperatorEnum.Deposit.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.Deposit.getCode()));
        ufoPreMap.put(SscOperatorEnum.CndLock.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.CndLock.getCode()));
        ufoPreMap.put(SscOperatorEnum.FlwLock.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.FlwLock.getCode()));
        ufoPreMap.put(SscOperatorEnum.RwdLock.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.RwdLock.getCode()));
        ufoPreMap.put(SscOperatorEnum.OffLine.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.OffLine.getCode()));
        ufoPreMap.put(SscOperatorEnum.QOS.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.QOS.getCode()));
        ufoPreMap.put(SscOperatorEnum.WdthPnsh.getEncode(),new TransactionVo(UfoPrefixEnum.SSC.getCode(),Constants.ufoVersion,SscOperatorEnum.WdthPnsh.getCode()));
    }

    public static  TransactionVo getValue(String data){
        if(null == data){
            return null;
        }
        for (Map.Entry<String,TransactionVo> entry : ufoPreMap.entrySet()) {
            if(data.startsWith(entry.getKey())){
                return entry.getValue();
            }
        }
       return null;
    }
}
