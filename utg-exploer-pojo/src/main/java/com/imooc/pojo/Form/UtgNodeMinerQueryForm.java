package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.BlockQueryParam;

import lombok.Data;

@Data
public class UtgNodeMinerQueryForm extends BaseQueryForm<BlockQueryParam> {

    private String  node_address;

    private String revenue_address;
    
    private String manage_address;

    private Integer node_type;
    
    private String pledge_address;
    
    private Integer pledge_status;
    
    private Integer unpledge_type;

    private String address;
    
    private String version;
    private String etType;
    private Long Pledge_number;
    private Integer type;
    private Integer [] types;
    private  Integer isEt;
    private Long dayNumber;
    private Long blockNumber;
    public UtgNodeMinerQueryForm() {
    }

    public UtgNodeMinerQueryForm(String node_address) {
        this.node_address = node_address;
    }
}
