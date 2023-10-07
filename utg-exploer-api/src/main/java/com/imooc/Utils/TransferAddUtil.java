package com.imooc.Utils;

import com.imooc.controller.job.AddressBalanceModifyJob;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.TransferMiner;
import com.imooc.utils.Constants;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhc 2022-01-11 9:59
 */
public class TransferAddUtil {
    public void transferAdd(TransferMinerMapper transferMinerMapper,Long blockNumber, int type) {
        List<TransferMiner> list = transferMinerMapper.getTransferAdd(blockNumber,type);
        List<TransferMiner> updateList = new ArrayList<>();
        List<TransferMiner> releaseList = new ArrayList<>();
        Map<String, Addresses> addressMap = new HashMap<>();
        for (TransferMiner trans:list) {
            if(trans.getReleaseHeigth()==0 || trans.getReleaseInterval()==0){
                TransferMiner vo = new TransferMiner();
                vo.setId(trans.getId());
                vo.setReleaseamount(trans.getTotalAmount());
                vo.setBurntratio(trans.getBurntratio());
                vo.setBurntaddress(trans.getBurntaddress());
                vo.setBurntamount(trans.getBurntamount());
                updateList.add(vo);
                //release record
                TransferMiner transferMiner= new TransferMiner();
                transferMiner.setTxHash(UUID.randomUUID().toString());
                transferMiner.setRevenueaddress(trans.getRevenueaddress());
                transferMiner.setLogIndex(1);
                transferMiner.setAddress(trans.getAddress());
                transferMiner.setBlockNumber(trans.getBlockNumber());//
                transferMiner.setType(trans.getType()+1);
                transferMiner.setReleaseamount(trans.getTotalAmount());
                transferMiner.setBurntratio(trans.getBurntratio());
                transferMiner.setBurntaddress(trans.getBurntaddress());
                transferMiner.setBurntamount(trans.getBurntamount()); 
                transferMiner.setUnLockNumber(blockNumber);
                transferMiner.setCurtime(new Date());                
                releaseList.add(transferMiner);
                if(null!=transferMiner.getRevenueaddress()){
                    addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                }
            }else{
            //    Long times = trans.getReleaseHeigth()/trans.getReleaseInterval();
                TransferMiner vo = new TransferMiner();
                vo.setId(trans.getId());
                vo.setReleaseamount(trans.getTotalAmount());
                vo.setBurntratio(trans.getBurntratio());
                vo.setBurntaddress(trans.getBurntaddress());
                vo.setBurntamount(trans.getBurntamount());
                updateList.add(vo);
                //release record
                TransferMiner transferMiner= new TransferMiner();
                transferMiner.setTxHash(UUID.randomUUID().toString());
                transferMiner.setRevenueaddress(trans.getRevenueaddress());
                transferMiner.setLogIndex(1);
                transferMiner.setAddress(trans.getAddress());
                transferMiner.setBlockNumber(trans.getBlockNumber());
                transferMiner.setType(trans.getType()+1);
                BigDecimal releaseamount = trans.getTotalAmount().subtract(trans.getReleaseamount());
                BigDecimal burntratio = trans.getBurntratio()==null? BigDecimal.ZERO : trans.getBurntratio();
                BigDecimal burntamount = releaseamount.multiply(burntratio);
                transferMiner.setReleaseamount(releaseamount);
                transferMiner.setBurntratio(burntratio);
                transferMiner.setBurntaddress(trans.getBurntaddress());
           //     transferMiner.setBurntamount(trans.getBurntamount());
                transferMiner.setBurntamount(burntamount);
                transferMiner.setUnLockNumber(blockNumber);
                transferMiner.setCurtime(new Date());
                releaseList.add(transferMiner);
                if(null!=transferMiner.getRevenueaddress()){
                    addressMap.put(transferMiner.getRevenueaddress(),new Addresses(transferMiner.getRevenueaddress(),blockNumber));
                }
            }
            if(updateList.size()>0&&updateList.size()/ Constants.BATCHCOUNT>=1){
                transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
                updateList = new ArrayList<>();
            }
            if(releaseList.size()>0&&releaseList.size()/Constants.BATCHCOUNT>=1){
                transferMinerMapper.insertReleaseBatch(releaseList);
                releaseList = new ArrayList<>();
            }
        }
        if(updateList.size()>0){
            transferMinerMapper.updateMinerTransfRelaseBatch(updateList);
        }
        if(releaseList.size()>0){
            transferMinerMapper.insertReleaseBatch(releaseList);
        }
        for (Map.Entry<String,Addresses> address : addressMap.entrySet()) {
            AddressBalanceModifyJob.getInstance().putq(address.getValue());
        }
    }
}
