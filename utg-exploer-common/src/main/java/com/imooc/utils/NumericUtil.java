package com.imooc.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Numeric;

public class NumericUtil {
	
	private static Logger logger = LoggerFactory.getLogger(NumericUtil.class);
	
	public static String convertToString(String value) {
		if (value == null || "".equals(value) || "0x".equals(value))
			return value;
		String str = new String(Numeric.hexStringToByteArray(value));
		return str;
	}

	public static BigInteger convertToBigInteger(String value, BigInteger defaultValue) {
		if (value == null || "".equals(value) || "0x".equals(value))
			return defaultValue;
		try {
			BigInteger number = Numeric.decodeQuantity(value);
			if (number.compareTo(new BigInteger("10000000000000000000000000000000000000000000000000000000000000000")) >= 0) {
				logger.warn("Data " + value + " out of decimal(65) range :" + number);
				return defaultValue;
			}
			return number;
		} catch (Exception e) {
			logger.warn("Decode " + value + " number error:" + e.getMessage());
			return defaultValue;
		}
	}

	public static BigInteger convertToBigInteger(String value) {
		return convertToBigInteger(value, null);
	}

	public static BigDecimal convertToBigDecimal(String value, BigDecimal defaultValue) {
		if (value == null || "".equals(value) || "0x".equals(value))
			return defaultValue;
		try {
			BigDecimal number = new BigDecimal(Numeric.decodeQuantity(value));
			if (number.compareTo(new BigDecimal("1E64")) >= 0) {
				logger.warn("Data " + value + " out of decimal(65) range :" + number);
				return defaultValue;
			}
			return number;
		} catch (Exception e) {
			logger.warn("Decode " + value + " number error:" + e.getMessage());
			return defaultValue;
		}
	}

	public static BigDecimal convertToBigDecimal(String value) {
		return convertToBigDecimal(value, null);
	}

	public static Long convertToLong(String value, Long defaultValue) {
		if (value == null || "".equals(value) || "0x".equals(value))
			return defaultValue;
		try {
			BigInteger number = Numeric.decodeQuantity(value);
			if (number.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) >= 0) {
				logger.warn("Data " + value + " out of long range :" + number);
				return defaultValue;
			}
			return number.longValue();
		} catch (Exception e) {
			logger.warn("Decode " + value + " number error:" + e.getMessage());
			return defaultValue;
		}
	}

	public static Long convertToLong(String value) {
		return convertToLong(value, null);
	}

	public static Integer convertToInteger(String value, Integer defaultValue) {
		if (value == null || "".equals(value) || "0x".equals(value))
			return defaultValue;
		try {
			BigInteger number = Numeric.decodeQuantity(value);
			if (number.compareTo(new BigInteger(String.valueOf(Integer.MAX_VALUE))) >= 0) {
				logger.warn("Data " + value + " out of long range :" + number);
				return defaultValue;
			}
			return number.intValue();
		} catch (Exception e) {
			logger.warn("Decode " + value + " number error:" + e.getMessage());
			return defaultValue;
		}
	}

	public static Integer convertToInteger(String value) {
		return convertToInteger(value, null);
	}
	
	public static BigDecimal add(BigDecimal a, BigDecimal b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return a.add(b);
	}
	
	
	/**
	 * Bandwidth Reward Ratio
	 * @param bandwidth
	 * @return
	 */
	public static double getBandwidthRewardRatio(int bandwidth) {
		if (bandwidth >= 1024) {
			return 1.32639;
		}
		if (bandwidth < 20) {
			return 0;
		}		
		double bwroleval = 2.5;
		double correctVal = 0.3;		
		double plbwRatio = getBandwidthPledgeRatio(bandwidth);
		double rewardRatio = plbwRatio / bwroleval;
		rewardRatio -= correctVal;
	//  return rewardRatio.Round(5);
	//	return rewardRatio;
		return new BigDecimal(rewardRatio).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * Bandwidth Pledge Ratio
	 * @param bandwidth
	 * @return
	 */
	public static double getBandwidthPledgeRatio(int bandwidth) {
		if (bandwidth >= 1024) {
			return 4.06598;
		}
		double logindex = 0.7403626894942439;
		double bwRatio = Math.log10(bandwidth) / logindex;
	//	return bwRatio.Round(4)
	//	return bwRatio;
		return new BigDecimal(bwRatio).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	public static void main(String[] args){
	//	System.out.println(getBandwidthPledgeRatio(1));
	//	System.out.println(getBandwidthRewardRatio(1));
	//	System.out.println(convertToBigInteger("0x1d099bfa03e870"));
	//	System.out.println(convertToString("0xffffffffffffffffffffffffffffffffffffffffffffffbef2a795df5b3fffff"));
	}
}
