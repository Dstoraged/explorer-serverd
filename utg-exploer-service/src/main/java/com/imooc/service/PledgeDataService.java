package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.Form.PledgeRnQueryForm;
import com.imooc.pojo.PledgeData;
import com.imooc.pojo.PledgeTotalData;
import com.imooc.utils.ResultMap;


public interface PledgeDataService {
    ResultMap<Page<PledgeTotalData>> selectPagePledgeList(PledgeQueryForm pledgeQueryForm);

    ResultMap<PledgeTotalData> pagePledgeInfoDetail(PledgeQueryForm pledgeQueryForm);

    ResultMap<PledgeData> pagePledgeEnDetail(PledgeRnQueryForm pledgeRnQueryForm);
}
