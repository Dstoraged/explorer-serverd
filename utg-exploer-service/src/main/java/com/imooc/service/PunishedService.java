package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Punished;
import com.imooc.utils.ResultMap;

public interface PunishedService {
    ResultMap<Page<Punished>> getPunishedInfo(BlockQueryForm blockQueryForm);
}
