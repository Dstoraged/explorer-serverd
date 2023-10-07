package com.imooc.pojo;

import lombok.Data;

@Data
public class ApiConfig {

    private Long id;

    private String configKey;

    private String configDemo;

    private String description;

    private String configValue;

    private Integer type;

    private Integer fokId;
}
