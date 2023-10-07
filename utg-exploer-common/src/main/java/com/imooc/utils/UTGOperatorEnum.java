package com.imooc.utils;

/**
 * @author zhc 2021-09-08 15:12
 */
public enum UTGOperatorEnum {
    Exch(		"Exch",			"0x5554473a313a45786368"),
    Bind(		"Bind",			"0x5554473a313a42696e64"),
    Unbind(		"Unbind",		"0x5554473a313a556e62696e64"),
    Rebind(		"Rebind",		"0x5554473a313a526562696e64"),
    CandReq(	"CandReq",		"0x5554473a313a43616e64526571"),    
    CandExit(	"CandExit",		"0x5554473a313a43616e6445786974"),
    CandPnsh(	"CandPnsh",		"0x5554473a313a43616e64506e7368"),
    FlwReq(		"FlwReq",		"0x5554473a313a466c77526571"),
    FlwExit(	"FlwExit",		"0x5554473a313a466c7745786974"),
	CandEntrust("CandEntrust",	"0x5554473a313a43616e64456e7472757374"),
	CandETExit(	"CandETExit",	"0x5554473a313a43616e64455445786974"),
	CandChaRate("CandChaRate",	"0x5554473a313a43616e6443686152617465")
    ;
	
    private String code;
    private String encode;

    UTGOperatorEnum(String code, String encode) {
        this.code = code;
        this.encode = encode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }
}
