package com.imooc.controller.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhc 2021-12-08 13:15
 */
@Configuration
@Data
public class ExploerCfg {

    @Value("${ipaddress}")
    private String ipaddress;
    @Value("${addresssycntime}")
    private String addresssycntime;
}
