package com.imooc.utils;

import org.apache.commons.lang3.StringUtils;

public class TxHashUtils {
    public static boolean validateTxHash(String hash) {
        if(StringUtils.isEmpty(hash)|| hash.equals("")){
            //throw new AccountValidateException("hash is empty");
            return false;
        }

        //boolean start =hash.startsWith("0x");
        if(!(hash.substring(0,2).equalsIgnoreCase(Constants.PRE_0X))&&!hash.substring(0,2).equalsIgnoreCase(Constants.PRE)){
            // throw new AccountValidateException("wrong params");
            return false;
        }

        if(!(hash.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{66}$"))){
            // throw new AccountValidateException("hash param is wrong");
            return false;
        }
        return true;
    }

}
