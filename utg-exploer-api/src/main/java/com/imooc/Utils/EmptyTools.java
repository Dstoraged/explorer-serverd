package com.imooc.Utils;

public class EmptyTools {

    public static boolean isNotEmpty(String name) {
        boolean be = true;
        if (name.equals("") || name.equals("null")) {
            be = false;
        } else {
            be = true;
        }
        return be;
    }


}
