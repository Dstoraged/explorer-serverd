package com.imooc.utils;

public class NumberUtilsToString {
    public static String decodeHex(String hexStr) {
        byte[] strs = new byte[hexStr.length() / 2];
        for (int i = 0; i < strs.length; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            strs[i] = (byte)(high * 16 + low);
        }
        return new String(strs);
    }
}
