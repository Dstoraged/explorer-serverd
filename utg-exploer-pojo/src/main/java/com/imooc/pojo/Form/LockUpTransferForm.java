package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.LockUpTransferParam;
import lombok.Data;

@Data
public class LockUpTransferForm extends BaseQueryForm<LockUpTransferParam> {

    private  Integer type;

    private  String address;

}
