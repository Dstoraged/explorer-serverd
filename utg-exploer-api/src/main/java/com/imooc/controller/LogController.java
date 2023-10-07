package com.imooc.controller;

import com.imooc.pojo.Form.LogQueryForm;
import com.imooc.service.LogService;
import com.imooc.utils.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(value = "LogController", tags = "LogController")
@RequestMapping("/log")
public class LogController {
    @Autowired
    private LogService logService;
    @ApiOperation(value = "getLogs", notes = "getLogs")
    @PostMapping("/getLogs")
    public ResultMap checkContractStatus(@Valid @RequestBody LogQueryForm logQueryForm)throws Exception {
      return logService.getLogs(logQueryForm);
    }
}
