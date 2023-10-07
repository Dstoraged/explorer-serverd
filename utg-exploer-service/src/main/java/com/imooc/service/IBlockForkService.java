package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.BlockFork;

import java.util.List;

public interface IBlockForkService /*extends IService<BlockFork>*/{
    List<BlockFork> topUnhandledForkBlock(Integer count);

}
