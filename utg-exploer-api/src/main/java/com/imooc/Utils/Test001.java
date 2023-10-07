package com.imooc.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Collection;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Test001 {
    private static final Logger logger = LoggerFactory.getLogger(Test001.class);

    public static void main(String[] args) throws Exception{

      List<String>  list= new ArrayList<String>();
      list.add("3.0.1");
      list.add("1.0.7");
      list.add("1.0.15");
      list.add("2.0.3");
      list.add("1.0.1");
      list.add("1.0.4");
        list.add("8.0.4");

      for(int i=0;i<list.size()-1;i++){
          for(int j=1;j<list.size()-i;j++){
              String a;
              if(compareVersion(list.get(j-1),list.get(j))>=0){
               a=list.get(j-1) ;
               list.set((j-1),list.get(j));
               list.set(j,a);
              }
          }
      }


    }
    public static int compareVersion(String version1, String version2) throws Exception {
        if (version1 == null || version2 == null) {
            throw new Exception("compareVersion error:illegal params.");
        }
        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
            ++idx;
        }
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        System.out.println("result"+diff);
        return diff;
    }
}
