package com.imooc.service.impl;

import com.imooc.mapper.LogMapper;
import com.imooc.pojo.Form.LogQueryForm;
import com.imooc.pojo.TransLogs;
import com.imooc.service.LogService;
import com.imooc.utils.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private LogMapper logMapper;
    public ResultMap getLogs(LogQueryForm logQueryForm) {
        String address=logQueryForm.getAddress();
        String topic =logQueryForm.getFirstTopic();
        List<TransLogs> logs =logMapper.getLogs(address,topic);
        return ResultMap.getSuccessfulResult(logs);
    }
}
