package com.imooc.utils;

/**
 * @author zhc 2021-09-08 15:12
 */
public enum SscOperatorEnum {
    ExchRate("ExchRate","0x5353433a313a4578636852617465"),
    Deposit("Deposit","0x5353433a313a4465706f736974"),
    CndLock("CndLock","0x5353433a313a436e644c6f636b"),
    FlwLock("FlwLock","0x5353433a313a466c774c6f636b"),
    RwdLock("RwdLock","0x5353433a313a5277644c6f636b"),
    OffLine("OffLine","0x5353433a313a4f66664c696e65"),
    QOS("QOS","0x5353433a313a514f53"),
    WdthPnsh("WdthPnsh","0x5353433a313a57647468506e7368")
    ;


    private String code;
    private String encode;

    SscOperatorEnum(String code, String encode) {
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
