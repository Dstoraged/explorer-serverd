package com.imooc.utils;

import java.util.HashMap;

public class ResultMap <T> extends HashMap<String,Object> {
    private static final long serialVersionUID = -4970973511892646114L;

    public static final Result STATUS_CODE_SUCCESSFUL = Result.getResult("successful", 0);

    public static final Result STATUS_ERRORCODE_SYSTEMERROR = Result.getResult("errorcode.system.error", 1);

    public static final Result STATUS_CODE_FAIL =Result.getResult("fail", 1);

    public final static String RESULT="result";

    public final static String STATUSCODE="statusCode";

    public final static String RESULT_DESC="message";

    private ResultMap(Result result,Object obj){
        this.put(STATUSCODE, result.getStatusCode());
        this.put(RESULT, obj);
        this.put(RESULT_DESC, result.getResultDesc());
    }

    public ResultMap(int statusCode, String resultDesc, Object obj) {
        this.put(STATUSCODE, statusCode);
        this.put(RESULT, obj);
        this.put(RESULT_DESC, resultDesc);
    }

    public static ResultMap getSuccessfulResult(Object result) {
        return new ResultMap(STATUS_CODE_SUCCESSFUL, result);
    }

    public static ResultMap getFailureResult(int statusCode,String resultDesc,Object result){
        return new ResultMap(statusCode,resultDesc,result);

    }

    public static ResultMap getFailureResult(Result result) {
        return getFailureResult(result.getStatusCode(), result.getResultDesc(), result);
    }

    public static ResultMap getFailureResult(Object result){
        return new ResultMap(STATUS_CODE_FAIL, result);
    }

    public static ResultMap getApiFailureResult(String resultDesc) {
        return getFailureResult(STATUS_CODE_FAIL.getStatusCode(), resultDesc, STATUS_CODE_FAIL);
    }

}
