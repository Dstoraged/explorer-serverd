package com.imooc.controller.job;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.Utils.SpringContextUtils;
import com.imooc.controller.config.ExploerCfg;
import com.imooc.mapper.AccountMapper;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.vo.AddressVo;
import com.imooc.utils.Web3jUtils;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Slf4j
public class DayAddressBalanceModifyJob extends AgentSvcTask {

    private ExploerCfg  cfg;
	private AccountMapper accountMapper;
	protected  void runTask() {
		accountMapper = SpringContextUtils.getBean(AccountMapper.class);
		cfg = SpringContextUtils.getBean(ExploerCfg.class);
		while(!shutdown) {
			long stime=System.currentTimeMillis();
			Calendar ca = Calendar.getInstance();
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int minut = ca.get(Calendar.MINUTE);
			try {
				if ((hour+":"+minut).equals(cfg.getAddresssycntime())){
					log.info("DayAddressBalanceModifyJob start ...");
					Web3j web3j = Web3jUtils.getWeb3j(cfg.getIpaddress());
					List<AddressVo> addressList =  accountMapper.getAllAddress();
					for (AddressVo addresse : addressList) {
						try {
							BigInteger balance = getAddressBalance(web3j, addresse.getAddress(), addresse.getBlockNumber());
							BigInteger nonce = getAddressNonce(web3j, addresse.getAddress(), addresse.getBlockNumber());
							if(balance!=null&&nonce!=null){
								saveAddressBalance(addresse.getId(),addresse.getAddress(), null, addresse.getBlockNumber(), balance, nonce,new Date());
							}
						}catch (Exception e){
							log.error("DayAddressBalanceModifyJob sync  error,address="+addresse.getAddress(),e);
						}
					}
				}
			}catch (Exception e) {
				log.error("DayAddressBalanceModifyJob error",e);
			}finally {
				try {
					long etime=System.currentTimeMillis();
					if(60000-(etime-stime)>0) {
						Thread.sleep(60000-(etime-stime));
					}
				} catch (InterruptedException e) {
				}
			}
		}

	}
	private BigInteger getAddressBalance(Web3j web3j, String address, Long blockNumber) {
		EthGetBalance ethResult;
		try {
			ethResult = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		if (null == ethResult) {
			return null;
		}
		return ethResult.getBalance();
	}
	/*Get address nonce*/
	private BigInteger getAddressNonce(Web3j web3j, String address, Long blockNumber) {
		EthGetTransactionCount ethResult;
		try {
			ethResult = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return ethResult.getTransactionCount();
	}
	/*Save address balance*/
	private boolean saveAddressBalance(Long id,String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce,Date insert) {
		Addresses item = new Addresses();
		item.setId(id);
		item.setAddress(address);
		if (null == contract) {
			contract = address;
		}
		item.setContract(contract);
		item.setBlockNumber(blockNumber);
		item.setBalance(new BigDecimal(balance));
		item.setNonce(nonce.longValue());
		item.setInsertedTime(insert);
		item.setBalance_block(blockNumber);
		accountMapper.saveOrUpdata(item);
		return true;
	}
}
