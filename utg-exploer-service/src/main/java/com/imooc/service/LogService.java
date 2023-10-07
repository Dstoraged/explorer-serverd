package com.imooc.service;

import com.imooc.pojo.Form.LogQueryForm;
import com.imooc.utils.ResultMap;

public interface LogService {
    ResultMap getLogs(LogQueryForm logQueryForm);
}
