package com.imooc.controller.job;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.Utils.SpringContextUtils;
import com.imooc.controller.config.ExploerCfg;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.AddrBalancesMapper;
import com.imooc.pojo.AddrBalances;
import com.imooc.pojo.Addresses;
import com.imooc.utils.Web3jUtils;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 */
@Slf4j
public class AddressBalanceModifyJob extends AgentSvcTask {

    private ExploerCfg  cfg;
	private AccountMapper accountMapper;
	private AddrBalancesMapper addrBalancesMapper;
    private Web3j web3j;
	private static Object lock=new Object();
	private static AddressBalanceModifyJob instance;
	private AddressBalanceModifyJob() {
		accountMapper = SpringContextUtils.getBean(AccountMapper.class);
		addrBalancesMapper = SpringContextUtils.getBean(AddrBalancesMapper.class);
		cfg = SpringContextUtils.getBean(ExploerCfg.class);
		web3j = Web3jUtils.getWeb3j(cfg.getIpaddress());
		this.setName("AddressBalanceModifyJob#");
	}

	public synchronized static AddressBalanceModifyJob getInstance() {
		if(instance==null) {
			synchronized (lock) {
				if(instance==null) {
					instance=new AddressBalanceModifyJob();
				}
			}
		}
		return instance;
	}
	protected  void runTask() {
		log.info("AddressBalanceModifyJob start ...");
		while(!shutdown) {
			try {
				Addresses addresse = (Addresses)this.getq(5);
				if(addresse!=null){
					BigInteger balance = getAddressBalance(web3j, addresse.getAddress(), addresse.getBlockNumber());
					BigInteger nonce = getAddressNonce(web3j, addresse.getAddress(), addresse.getBlockNumber());
					if(balance!=null&&nonce!=null){
					//	long balancestart = System.currentTimeMillis();
						saveAddressBalance(addresse.getAddress(), null, addresse.getBlockNumber(), balance, nonce,new Date());
					//	long balancesend = System.currentTimeMillis();
					//	log.info(addresse.getAddress()+ " end ,balanceupdatespends total second ="+(balancesend-balancestart));
						saveWithDraw(addresse.getAddress(), null, addresse.getBlockNumber(), balance, nonce);
					//	long balanceshis = System.currentTimeMillis();
					//	log.info(addresse.getAddress()+ " end ,balancehisspends total second ="+(balanceshis-balancesend));
					}
				}
			}catch (Exception e) {
				log.error("AddressBalanceModifyJob error,"+e.getMessage(),e);
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
	private boolean saveAddressBalance(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce,Date insert) {
		Addresses item = new Addresses();
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

	private boolean saveWithDraw(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce) {
		AddrBalances item = new AddrBalances();
		item.setAddress(address);
		if (null == contract) {
			contract = address;
		}
		item.setContract(contract);
		item.setBlockNumber(blockNumber);
		item.setBalance(new BigDecimal(balance));
		item.setNonce(nonce.longValue());
		addrBalancesMapper.saveOrUpdate(item);
		return true;
	}


}
