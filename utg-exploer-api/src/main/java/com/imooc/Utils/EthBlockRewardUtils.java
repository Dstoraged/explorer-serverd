package com.imooc.Utils;
import com.imooc.mapper.BlockMapper;
import com.imooc.pojo.Blocks;
import com.imooc.utils.Web3jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EthBlockRewardUtils {
    @Value("${ipaddress}")
    private  String ipaddress;

    @Value("${yeartotal}")
    private   String yeartotal;

    @Value("${year}")
    private static String year;

    @Value("${allrewards}")
    private  Long allrewards;

    @Value("${blocktotal}")
    private   String blocktotal;

    private static Logger logger = LoggerFactory.getLogger(EthBlockRewardUtils.class);

    public  BigDecimal getMinerNumber(BlockMapper blockMapper){
        try {
            Web3j web3j =  Web3jUtils.getWeb3j(ipaddress);
            Blocks blocks =blockMapper.getminBlock(1);
            Date blockTime= blocks.getTimeStamp();
            long timeStamp =blockTime.getTime();//
            String y=yeartotal;
            logger.info(""+yeartotal);
            Long tempTimpStamp=Long.parseLong(yeartotal);//
            long nowStamp=System.currentTimeMillis();//
            long  temp= nowStamp-timeStamp;//
            double nYear= (double)temp/tempTimpStamp;
            double nFee= Math.ceil(nYear);
            if(nFee <=1){ //
                nFee=1;//
            }else{
                nFee=nFee;//
            }
            BigInteger blockNumber= web3j.ethBlockNumber().send().getBlockNumber();
            long leatest=blockNumber.longValue();//
            BigDecimal bigNumber= new BigDecimal(leatest);
            BigDecimal total=getRewardByYear(nFee,nowStamp,bigNumber);//
            return total;
        } catch (IOException e) {
            e.printStackTrace();
            return new BigDecimal("0");
        }
    }

    /* */
    public  BigDecimal getRewardByYear(Double nFee,Long nowStamp,BigDecimal bigNumber){
        //
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
        String dateTime =format.format(nowStamp);
        if(nFee<=1){
            //
            BigDecimal rewardFirst= getRewards(nFee);
            BigDecimal yearBlockTotal= new BigDecimal(blocktotal);
            BigDecimal reward = rewardFirst.divide(yearBlockTotal,4);
            return reward;
        }else{
            BigDecimal rewardYear=getRewards(nFee);
            Double fee= nFee-1;
            BigDecimal lastYear=getRewards(fee);
            BigDecimal yearReward=rewardYear.subtract(lastYear);
            BigDecimal yearBlockTotal= new BigDecimal(blocktotal);
            BigDecimal reward=yearReward.divide(yearBlockTotal,4);
            return reward;
        }
    }

    /*Tf*/
    public  BigDecimal getRewards(Double nFee){
       // double allReward =Double.parseDouble(allrewards);//
        double allReward=(double)allrewards;
        int years=Integer.valueOf(year).intValue();
        double bb=(double)nFee/years;//
        double hh=1-(Math.pow(0.5,bb));//
        double cc=allReward*hh;
        BigDecimal total= BigDecimal.valueOf(cc);
        return total;
    }
}
