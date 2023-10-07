package com.imooc.controller.job;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.AlienSnapshot.Snapshot;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.Utils.TimeSpend;
import com.imooc.mapper.AccountMapper;
import com.imooc.pojo.Addresses;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddressSrtBalanceModifyJob extends AgentSvcTask {

	private static Logger logger = LoggerFactory.getLogger(AddressSrtBalanceModifyJob.class);
	private Long blockNumber;
	private Web3j web3j;
	private AccountMapper accountMapper;
    
    public AddressSrtBalanceModifyJob(Web3j web3j, Long blockNumber, AccountMapper accountMapper) {
    	this.web3j = web3j;
        this.blockNumber = blockNumber;
        this.accountMapper = accountMapper; 
    }
    
	protected  void runTask() {
      //  long starttime = System.currentTimeMillis();
        logger.info("SrtBalanceModifyJob start .....");
       
        SrtBalanceModifyJob(web3j,blockNumber,accountMapper);
        
      //  long endtime = System.currentTimeMillis();
      //  logger.info("SrtBalanceModifyJob end ,spends total second ="+(endtime-starttime)/1000);
    }
	
	public static void SrtBalanceModifyJob(Web3j web3j, Long blockNumber, AccountMapper accountMapper){		
		try {
			TimeSpend timeSpend = new TimeSpend();
			Snapshot snap = web3j.alienSRTbalAtNumber(blockNumber.intValue()).send().getSnapshot();			
			if(snap!=null && snap.getSrtbal()!=null){				
				Map<String, Object> srtbal=snap.getSrtbal();
				Long srtBlock = accountMapper.getLeasetSrtBlock();
				Long srt_nonce = 0L;
				int count=0;
				for(String address : srtbal.keySet()){
					Object value = srtbal.get(address);
					BigDecimal srt_balance = value==null ? BigDecimal.ZERO : new BigDecimal(value.toString());					
					Addresses account = accountMapper.getAddressInfo(address);
					if(account!=null && account.getSrt_balance().compareTo(srt_balance)==0)
						continue;					
					Addresses accounts = new Addresses();
					accounts.setAddress(address);
					accounts.setContract(address);
					accounts.setBlockNumber(blockNumber);
					accounts.setInsertedTime(new Date());
					accounts.setSrt_balance(srt_balance);
					accounts.setSrt_nonce(srt_nonce);
					accounts.setSrt_block(blockNumber);
					accountMapper.saveOrUpdateSrt(accounts);
					count++;
				}
				if(srtBlock!=null){
					Snapshot leasetSnap = web3j.alienSRTbalAtNumber(srtBlock.intValue()).send().getSnapshot();
					if(leasetSnap!=null && leasetSnap.getSrtbal()!=null){
						Map<String, Object> leaseSrtbal = leasetSnap.getSrtbal();
						for(String address : leaseSrtbal.keySet()){
							if(!srtbal.containsKey(address)){
								Addresses accounts = new Addresses();
								accounts.setAddress(address);
								accounts.setContract(address);
								accounts.setSrt_balance(BigDecimal.ZERO);
								accounts.setSrt_nonce(srt_nonce);
								accounts.setSrt_block(blockNumber);
								accountMapper.saveOrUpdateSrt(accounts);
								count++;
							}
						}
					}
				}
				log.info("Synchronize addresses srt balance thread end, total " + count + " addresses, spend " + timeSpend.getSpendTime());
			}
		} catch (IOException e) {
			log.error("SrtBalanceModifyJob error,"+e.getMessage(),e);
		}
		
	}
}
