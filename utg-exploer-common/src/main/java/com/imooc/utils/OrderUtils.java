package com.imooc.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhc 2021-09-24 16:25
 */
public class OrderUtils {
    public static Set<String> UtgStorageMinerList = new HashSet<String>(0);
    static {
        String UtgStorageMinerStr = "join_time|sync_time|blocknumber|draw_amount|release_amount|lock_amount|revenue_amount|paysrt|miner_storage|pledge_amount|bandwidth";
        String UtgStorageMiner[] = UtgStorageMinerStr.split("\\|");
        for (String str : UtgStorageMiner) {
            UtgStorageMinerList.add(str);
        }
    }

    /**
     * @param sortBy
     * @param descending
     * @return
     */
    public static String sort(String sortBy,boolean descending) {
           return " order by "+sortBy+(descending?" desc":"");
    }
}
