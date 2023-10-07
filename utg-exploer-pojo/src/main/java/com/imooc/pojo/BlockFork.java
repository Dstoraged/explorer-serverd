package com.imooc.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data

@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("block_fork")
public class BlockFork {

    @TableId("id")
    private  Long id;

    @Column(name = "nephewhash")
    private  String nepHewHash;

    @Column(name = "nephewnumber")
    private Long nepHewNumber;

    @Column(name = "istrunk")
    private  Integer isTrunk;

    @Column(name = "unclehash")
    private  String uncleHash;

    @Column(name = "unclehandled")
    private  Integer uncleHandled;




}
