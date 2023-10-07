package com.imooc.mapper;
import com.imooc.pojo.BlockFork;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface BlockForkMapper extends MyMapper<BlockFork> {
    List<BlockFork> getBlockFork();

    void saveOrUpdate(@Param("fork")BlockFork item);
}
