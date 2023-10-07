package com.imooc.pojo;

import com.imooc.form.BasePo;
import lombok.Data;

import javax.persistence.Column;

@Data
public class TransLogs extends BasePo {

   @Column(name="id")
   private long id;

   @Column(name="transhash")
    private String transHash;

   @Column(name="logindex")
    private Integer logIndex;

   @Column(name="type")
    private String type;

   @Column(name="address")
    private String address;

   @Column(name="firsttopic")
    private String firstTopic;

   @Column(name="secondtopic")
    private String secondTopic;

   @Column(name="thirdtopic")
    private String thirdTopic;

   @Column(name="fourthtopic")
    private String  fourthTopic;

   @Column(name="data")
    private String data;
}
