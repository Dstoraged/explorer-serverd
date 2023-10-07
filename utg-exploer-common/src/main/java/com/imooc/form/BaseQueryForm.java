package com.imooc.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
@ApiModel
@Slf4j
@Data
public class BaseQueryForm  <P extends BaseParam> {
    @ApiModelProperty(value = "page",example = "1")
    private long current = 1;
    @ApiModelProperty(value = "size",example = "10")
    private long size = 10;

    public P toParam(Class<P> clazz) {
        P p = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(this, p);
        return p;
    }

    public Page newFormPage() {
        return new Page(this.getCurrent(), this.getSize());
    }


}
