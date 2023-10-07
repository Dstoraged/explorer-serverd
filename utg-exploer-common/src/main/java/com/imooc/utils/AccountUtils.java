package com.imooc.utils;

import org.apache.commons.lang3.StringUtils;

public class AccountUtils {

    public static boolean validateAddressHash(String hash) {
        if(StringUtils.isEmpty(hash)|| hash.equals("")){
            return false;
        }

        if(!(hash.substring(0,2).equalsIgnoreCase(Constants.PRE_0X))&&!hash.substring(0,2).equalsIgnoreCase(Constants.PRE)){
            return false;
        }

        if(!(hash.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{42}$"))){
            //throw new AccountValidateException("hash param is wrong");
            return false;
        }

        return true;
    }

}
