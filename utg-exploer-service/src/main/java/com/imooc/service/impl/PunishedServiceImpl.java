package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.PunishedMapper;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Punished;
import com.imooc.pojo.vo.TransferMinerVo;
import com.imooc.service.PunishedService;
import com.imooc.utils.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PunishedServiceImpl implements PunishedService {

    @Autowired
    private PunishedMapper punishedMapper;
    public ResultMap<Page<Punished>> getPunishedInfo(BlockQueryForm blockQueryForm) {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<Punished> listInfo =punishedMapper.getPageList(blockQueryForm);
        long total=punishedMapper.getTotal(blockQueryForm);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }
}
