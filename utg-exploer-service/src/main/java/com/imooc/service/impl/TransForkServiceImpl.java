package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.mapper.TransForkMapper;
import com.imooc.pojo.TransForks;
import com.imooc.service.TransForkService;
import org.springframework.stereotype.Service;




@Service
public class TransForkServiceImpl extends ServiceImpl<TransForkMapper, TransForks> implements TransForkService {
    @Override
    public boolean save(TransForks entity) {
        return false;
    }


}
