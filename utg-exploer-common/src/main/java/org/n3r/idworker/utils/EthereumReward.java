package org.n3r.idworker.utils;

import java.math.BigInteger;

public class EthereumReward {

    private static final Long ByzantiumBlockNumber = 4370000l;
    private static final Long ConstantinopleBlockNumber = 7280000l;
    private static final BigInteger FrontierBlockReward = new BigInteger ("5000000000000000000");
    private static final BigInteger ByzantiumBlockReward = new BigInteger ("3000000000000000000");
    private static final BigInteger ConstantinopleBlockReward = new BigInteger ("2000000000000000000");

    public static BigInteger getBlockReward (Long blockNumber) {
        if (ConstantinopleBlockNumber <= blockNumber)
            return ConstantinopleBlockReward;
        if (ByzantiumBlockNumber <= blockNumber)
            return ByzantiumBlockReward;
        return FrontierBlockReward;
    }

    public static BigInteger getUnclesReward (Long blockNumber, Integer unclesNumber) {
        return  getBlockReward (blockNumber).multiply (BigInteger.valueOf (2 < unclesNumber ? 2 : (0 > unclesNumber ? 0 : unclesNumber))).divide (BigInteger.valueOf (32));
    }

    public static BigInteger getUncleBlockReward (Long blockNumber, Long uncleNumber) {
        if (uncleNumber >= blockNumber || uncleNumber + 7 <= blockNumber)
            return BigInteger.ZERO;
        return getBlockReward (blockNumber).multiply (BigInteger.valueOf (uncleNumber + 8 - blockNumber)).divide (BigInteger.valueOf (8));
    }
}
