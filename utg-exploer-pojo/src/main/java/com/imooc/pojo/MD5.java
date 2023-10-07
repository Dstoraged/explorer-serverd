package com.imooc.pojo;

import java.security.MessageDigest;

public class MD5 {
    /**
     */
    private final static String[] HEX_DIGITS = {"0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

/**
 *
 *
 *
 */
public static String encodeByMD5(String originString){
    if (originString != null){
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] results = md.digest(originString.getBytes());
            String resultString = byteArrayToHexString(results);
            return resultString.toLowerCase();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    return null;
}


    private static String byteArrayToHexString(byte[] b){
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++){
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b){
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }
}
