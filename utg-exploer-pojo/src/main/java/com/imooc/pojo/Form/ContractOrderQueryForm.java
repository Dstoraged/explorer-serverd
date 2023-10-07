package com.imooc.pojo.Form;

import lombok.Data;

@Data
public class ContractOrderQueryForm {

    private  Integer coinType;

    private  String  contractAddress;

    private  Long current;

    private  Long customerId;

    private  Long size;

}
