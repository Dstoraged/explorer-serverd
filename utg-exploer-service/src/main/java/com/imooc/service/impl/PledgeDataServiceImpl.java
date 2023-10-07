package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.PledgeDataMapper;
import com.imooc.mapper.PledgeTotalDataMapper;
import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.Form.PledgeRnQueryForm;
import com.imooc.pojo.PledgeData;
import com.imooc.pojo.PledgeTotalData;
import com.imooc.pojo.TransferMiner;
import com.imooc.service.PledgeDataService;
import com.imooc.utils.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class PledgeDataServiceImpl implements PledgeDataService {

    @Autowired
    private PledgeDataMapper pledgeDataMapper;

    @Autowired
    private PledgeTotalDataMapper pledgeTotalDataMapper;


    @Override
    public ResultMap<Page<PledgeTotalData>> selectPagePledgeList(PledgeQueryForm pledgeQueryForm) {
        Page page = pledgeQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        pledgeQueryForm.setCurrent(pageCurrent);
        pledgeQueryForm.setSize(pageSize);
        List<PledgeTotalData> list=pledgeTotalDataMapper.selectPageList(pledgeQueryForm);
        page.setRecords(list);
        long total =pledgeTotalDataMapper.seleTotalCount(pledgeQueryForm);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap<PledgeTotalData> pagePledgeInfoDetail(PledgeQueryForm pledgeQueryForm) {
        List<PledgeTotalData> list=pledgeTotalDataMapper.selectPledgeTotalDataInfo(pledgeQueryForm);
        return ResultMap.getSuccessfulResult(list);
    }

    @Override
    public ResultMap<PledgeData> pagePledgeEnDetail(PledgeRnQueryForm pledgeRnQueryForm) {
        List<PledgeData> listInfo =pledgeDataMapper.selectPledgeDataInfo(pledgeRnQueryForm);
        return ResultMap.getSuccessfulResult(listInfo);
    }
}
