package com.imooc.mapper;

import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Punished;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PunishedMapper extends MyMapper<Punished> {
    List<Punished> getPageList(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getTotal(@Param("blockQueryForm")BlockQueryForm blockQueryForm);
}
