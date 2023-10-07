package com.imooc.mapper;

import com.imooc.pojo.SysCfg;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface SysCfgMapper extends MyMapper<SysCfg> {
    Long getCfgValueByCgfName(@Param("pledgeblockkey") String pledgeblockkey);

    void updateCfgValueByCfgName(@Param("pledgeblockkey") String pledgeblockkey, @Param("cfgValue") Long cfgValue);
}
