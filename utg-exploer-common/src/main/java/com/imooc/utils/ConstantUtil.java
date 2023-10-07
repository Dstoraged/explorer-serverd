package com.imooc.utils;

import java.math.BigInteger;

public class ConstantUtil {
    private ConstantUtil() {}

    public static String IpcAddress = "/Users/bing/test/data/geth.ipc";

    public static String CapAddress = "https://api.coinmarketcap.com/v1/ticker/ethereum/";

    public static BigInteger GWeiFactor = BigInteger.valueOf(10).pow(8);

    public static String LatestBlockNumberKey = "latest";

    public static long TimestampFactor = 1000;

    public static long EightHourToSecond = 8 * 60 * 60;

    public static BigInteger MineReward = GWeiFactor.multiply(GWeiFactor).multiply(BigInteger.valueOf(3));
}
