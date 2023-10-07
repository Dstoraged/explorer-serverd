package com.imooc.job.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.imooc.Utils.TimeSpend;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.AddrBalancesMapper;
import com.imooc.pojo.AddrBalances;
import com.imooc.pojo.Addresses;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressService {	
	@Autowired
	AccountMapper accountMapper;
	@Autowired
	AddrBalancesMapper addrBalancesMapper;
	@Autowired
	Web3jService web3jService;

	@Async
	public void syncAddressesBalance(Map<String, Long> addressMap) {
		try {
			TimeSpend timeSpend = new TimeSpend();
			log.info("Synchronize addresses balance thread start ...");
			for (String address : addressMap.keySet()) {
				Long blockNumber = addressMap.get(address);
				if (address == null || blockNumber == null)
					continue;
				BigInteger balance = web3jService.getAddressBalance(address);
				BigInteger nonce = web3jService.getAddressNonce(address);
				if (balance != null && nonce != null) {
					if (saveAddressBalance(address, null, blockNumber, balance, nonce, new Date()))
						saveWithDraw(address, null, blockNumber, balance, nonce);
				}
			}
			log.info("Synchronize addresses balance thread end, total " + addressMap.size() + " addresses, spend " + timeSpend.getSpendTime() + "\n");
		} catch (Exception e) {
			log.warn("syncAddressesBalance error," + e.getMessage(), e);
		}
	}

	public boolean saveOrUpdateAddress(String address, Long blockNumber) {
		Addresses account = accountMapper.getAddressInfo(address);
		try {
			BigInteger balance = web3jService.getAddressBalance(address);
			BigInteger nonce = web3jService.getAddressNonce(address);
		//	if (balance != null && nonce != null) {				
		//		if (saveAddressBalance(address, null, blockNumber, balance, nonce, new Date())){
			if(account==null || account.getBalance().compareTo(new BigDecimal(balance))!=0){
					saveAddressBalance(address, null, blockNumber, balance, nonce, new Date());
					saveWithDraw(address, null, blockNumber, balance, nonce);
					log.info("Update address " + address + " balance to " + balance);
					return true;
		//		}
			}			
		} catch (Exception e) {
			log.warn("saveOrupdateAddress error," + e.getMessage(), e);
		}
		return false;
	}

    @Async
	public void syncAddressesSrtBalance(Long blockNumber) {
		try {
			TimeSpend timeSpend = new TimeSpend();
			log.info("Synchronize addresses srt balance thread start...");
			Map<String, Object> srtbal = web3jService.getAllSrtBalance(blockNumber);
			if (srtbal != null) {
				Long srtBlock = accountMapper.getLeasetSrtBlock();
				log.info("SrtBalance data:" + srtbal);
				Long srt_nonce = 0L;
				for (String address : srtbal.keySet()) {
					Object value = srtbal.get(address);
					BigDecimal srt_balance = value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
					saveAddressSrtBalance(address, blockNumber, srt_balance, srt_nonce);
				}
				if (srtBlock != null) {
					Map<String, Object> leaseSrtbal = web3jService.getAllSrtBalance(srtBlock);
					if (leaseSrtbal != null) {
						for (String address : leaseSrtbal.keySet()) {
							if (!srtbal.containsKey(address)) {
								saveAddressSrtBalance(address, blockNumber, BigDecimal.ZERO, srt_nonce);
							}
						}
					}
				}
			}
			log.info("Synchronize addresses srt balance thread end, total " + (srtbal == null ? 0 : srtbal.size()) + " addresses, spend " + timeSpend.getSpendTime()+"\n");
		} catch (Exception e) {
			log.warn("syncAddressesSrtBalance error," + e.getMessage(), e);
		}
	}

	public void updateAddressSrtBalance(String address, Long blockNumber) {
		try {
			BigInteger balance = web3jService.getAddrSrtBalance(address);
			if (balance == null)
				return;
			BigDecimal srt_balance = balance == null ? BigDecimal.ZERO : new BigDecimal(balance);
			Long srt_nonce = 0L;
			saveAddressSrtBalance(address, blockNumber, srt_balance, srt_nonce);
			log.info("Update address " + address + " srt balance to " + srt_balance);
		} catch (Exception e) {
			log.warn("updateAddressSrtBalance error," + e.getMessage(), e);
		}
	}

	public void handleTokenBalance(String contract, Collection<String> addresses, Long blockNumber) {
		try {
			TimeSpend timeSpend = new TimeSpend();
			log.info("Synchronize token "+contract+" addresses balance start...");
			int count = 0;
			for (String address : addresses) {
				BigInteger balance = web3jService.getAddressTokenBalance(contract, address);
				if (null == balance)
					continue;
				BigInteger nonce = web3jService.getAddressNonce(address);
				if (null == nonce)
					continue;
				if(saveAddressTokenBalance(address, contract, blockNumber, balance, nonce, new Date()))
					count ++;
			}
			log.info("Synchronize token "+contract+" addresses balance thread end, total " + count + " addresses, spend " + timeSpend.getSpendTime());
		} catch (Exception e) {
			log.warn("handleTokenBalance error," + e.getMessage(), e);
		}
	}
	
	public void setAddressLocked(String address,Long blockNumber) {
		Addresses addrParam = new Addresses();
		addrParam.setAddress(address);
		Addresses addrs = accountMapper.selectOne(addrParam);
		if (addrs == null) {
			addrs = new Addresses();
			addrs.setAddress(address);
			addrs.setContract(address);
			addrs.setBlockNumber(blockNumber);
			addrs.setBalance(new BigDecimal("0"));
			addrs.setNonce(0l);
			addrs.setHaslock(1);
			addrs.setInsertedTime(new Date());
			accountMapper.saveOrUpdataHaslock(addrs);
		} else {
			if (null == addrs.getHaslock() || 0 == addrs.getHaslock()) {
				accountMapper.updateHaslock(address);
			}
		}		
    }
		
	private boolean saveAddressBalance(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce, Date insert) {
		if (contract == null)
			contract = address;
		Addresses addresses = new Addresses();
		addresses.setAddress(address);
		addresses.setContract(contract);
		addresses.setBlockNumber(blockNumber);
		addresses.setBalance(new BigDecimal(balance));
		addresses.setNonce(nonce.longValue());
		addresses.setInsertedTime(insert);
	//	addresses.setBalance_block(blockNumber);
		int result = accountMapper.saveOrUpdata(addresses);
		return result ==1;
	}

	private boolean saveAddressTokenBalance(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce, Date insert) {
		if (contract == null)
			contract = address;
		Addresses addrToken = new Addresses();
		addrToken.setAddress(address);
		addrToken.setContract(contract);
		addrToken.setBlockNumber(blockNumber);
		addrToken.setBalance(new BigDecimal(balance));
		addrToken.setNonce(nonce.longValue());
		addrToken.setInsertedTime(insert);
		int result = accountMapper.saveOrUpdataToken(addrToken);
		return result ==1;
	}
	
	private AddrBalances saveWithDraw(String address, String contract, Long blockNumber, BigInteger balance, BigInteger nonce) {
		if (contract == null)
			contract = address;
		AddrBalances addrBalances = new AddrBalances();
		addrBalances.setAddress(address);
		addrBalances.setContract(contract);
		addrBalances.setBlockNumber(blockNumber);
		addrBalances.setBalance(new BigDecimal(balance));
		addrBalances.setNonce(nonce.longValue());
		addrBalancesMapper.saveOrUpdate(addrBalances);
		return addrBalances;
	}
	
	private Addresses saveAddressSrtBalance(String address,Long blockNumber,BigDecimal srt_balance,Long srt_nonce){
		Addresses addrSrtBalances = new Addresses();
		addrSrtBalances.setAddress(address);
		addrSrtBalances.setContract(address);
		addrSrtBalances.setBlockNumber(blockNumber);
		addrSrtBalances.setInsertedTime(new Date());
		addrSrtBalances.setSrt_balance(srt_balance);
		addrSrtBalances.setSrt_nonce(srt_nonce);
		addrSrtBalances.setSrt_block(blockNumber);
		accountMapper.saveOrUpdateSrt(addrSrtBalances);
		return addrSrtBalances;
	}
}
