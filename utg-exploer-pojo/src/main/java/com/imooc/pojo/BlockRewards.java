package com.imooc.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.imooc.form.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("blockrewards")
public class BlockRewards extends BasePo {

    @TableId("id")
    private Long id ;

    @Column(name = "blockhash")
    private String blockHash;

    @Column(name = "blocknumber")
    private Long blockNumber;

    @Column(name = "istrunk")
    private Integer isTrunk;

    @Column(name = "address")
    private String address;

    @Column(name = "rewardtype")
    private String rewardType;

    @Column(name = "rewardhash")
    private String rewardHash;

    @Column(name = "reward")
    private BigDecimal reward;

    @Column(name = "timestamp")
    private Date timeStamp;

}
