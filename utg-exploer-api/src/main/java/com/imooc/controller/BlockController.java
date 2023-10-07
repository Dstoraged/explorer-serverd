package com.imooc.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.BlockRewards;
import com.imooc.pojo.Form.BlockRewardQueryForm;
import com.imooc.service.BlockRewardsService;
import com.imooc.utils.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@Api(value = "block", tags = "block")
@RequestMapping("/block")
public class BlockController {

    @Autowired
    private BlockRewardsService blockRewardsService;
    @ApiOperation(value = "getBlockReward", notes = "getBlockReward")
    @PostMapping("/getBlockReward")
    public ResultMap getTxListByBlock(@RequestParam(value="blockNumber",required=true)long blockNumber) throws Exception {
        return blockRewardsService.getRewardByBlockNumber(blockNumber);
    }


    @ApiOperation(value = "getBlockByTimeStamp", notes = "getBlockByTimeStamp")
    @PostMapping("/getBlockByTimeStamp")
    public ResultMap getBlockByTimeStamp(@RequestParam(value="timeStamp",required=true)long timeStamp,
                                         @RequestParam(value="value",required=true)String value) throws Exception {
        return blockRewardsService.getRewardByTimeStamp(timeStamp,value);
    }

}
