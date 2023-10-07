package com.imooc.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.imooc.mapper.BlockForkMapper;
import com.imooc.pojo.BlockFork;
import com.imooc.pojo.UsersFans;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.service.IBlockForkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 *
 */
@Service
public  class BlockForkServiceImpl /*extends ServiceImpl<BlockForkMapper,BlockFork> */implements IBlockForkService {
    @Autowired
    private BlockForkMapper blockForkMapper;
    public List<BlockFork> topUnhandledForkBlock(Integer count) {
        Example example = new Example(BlockFork.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", 1);
        List<BlockFork> list = blockForkMapper.selectByExample(example);
        return list;
    }
}
