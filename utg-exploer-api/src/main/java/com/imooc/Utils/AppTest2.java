package com.imooc.Utils;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class AppTest2 {

    public static void testSendTrace() throws Exception {

        Web3j web3j = Web3j.build(new HttpService("http://192.168.9.100:8510"));// http://192.168.20.108:8545/  http://192.168.100.83:8546/ http://192.168.22.10:8501
        String privatekey="64b87ff4dfe358dfbf54d9eeec538d8ea9e7e96b3d77cc48a7b441fced793be7";
        String from="NX7A4539Ed8A0b8B4583EAd1e5a3F604e83419f502";
        String to="0xb42eb4a0246c6d486dbd86d382539818df5a859e";
//        String to = from;
        String hash = "";
        BigDecimal coinNum=new BigDecimal("0");
        Credentials credentials = Credentials.create(privatekey);
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = new BigInteger("210000");
        BigInteger nonce;
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();
        if (ethGetTransactionCount == null) {
            // return null;
        }
        nonce = ethGetTransactionCount.getTransactionCount();
        coinNum = coinNum.multiply(new BigDecimal(10).pow(18));
        BigInteger value = coinNum.toBigInteger();
//       String data=Numeric.toHexString("UTG:1:Exch:6e83430ca56ee33a26e5ce87239cb251981ccc2b:3B9ACA00".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("UTG:1:Bind:1807efcb4dc252ff6958eaab770c8b3936a5378f:0:0000000000000000000000000000000000000000:0000000000000000000000000000000000000000".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("UTG:1:Unbind:1807efcb4dc252ff6958eaab770c8b3936a5378f:1".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("UTG:1:Rebind:1807efcb4dc252ff6958eaab770c8b3936a5378f:1:d5653ba53edfc318f88044a141e9575345a9ee81:21f981af36218aaf1a40621bfe1e57cf2f43455a:350fccf36124cecd26318e9931414ce872bdb68".getBytes(StandardCharsets.UTF_8));
//        String data = Numeric.toHexString("UTG:1:CandReq:1807efcb4dc252ff6958eaab770c8b3936a5378f".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("UTG:1:CandExit:1807efcb4dc252ff6958eaab770c8b3936a5378f".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("UTG:1:CandPnsh:1807efcb4dc252ff6958eaab770c8b3936a5378f".getBytes(StandardCharsets.UTF_8));
//        String data = Numeric.toHexString("UTG:1:FlwReq:1807efcb4dc252ff6958eaab770c8b3936a5378f:01:0400".getBytes(StandardCharsets.UTF_8));//+"000000000000000000000000b42eb4a0246c6d486dbd86d382539818df5a859e000100000400";
        String data =Numeric.toHexString("UTG:1:FlwExit:1807efcb4dc252ff6958eaab770c8b3936a5378f".getBytes(StandardCharsets.UTF_8));//+ "000000000000000000000000b42eb4a0246c6d486dbd86d382539818df5a859e";
//        String data =Numeric.toHexString("SSC:1:ExchRate:15000".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("SSC:1:Deposit:1BC16D674EC800000".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("SSC:1:CndLock:03F480:17BB00:21C0".getBytes(StandardCharsets.UTF_8));
//        String data = Numeric.toHexString("SSC:1:FlwLock:03F480:00:00".getBytes(StandardCharsets.UTF_8));
//        String data =Numeric.toHexString("SSC:1:RwdLock:03F480:17BB00:21C0".getBytes(StandardCharsets.UTF_8));
//        String data = Numeric.toHexString("SSC:1:OffLine".getBytes(StandardCharsets.UTF_8))+"9850";
//        String data = Numeric.toHexString("SSC:1:QOS".getBytes(StandardCharsets.UTF_8))+"00018000";
//        String data = Numeric.toHexString("SSC:1:WdthPnsh:1807efcb4dc252ff6958eaab770c8b3936a5378f:01F4".getBytes(StandardCharsets.UTF_8));
        //String src=new String(Numeric.hexStringToByteArray(h));
        //log.info("src:"+src);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,gasPrice, gasLimit, to, value,data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction,1337, credentials);
        String hexValue = Numeric.toHexString(signMessage);
        log.info("nonce:" + nonce);
        log.info("gasPrice:" + gasPrice);
        log.info("gasLimit:" + gasLimit);
        log.info("to:" + to);
        log.info("value:" + value);
        log.info("data:" + data);
        log.info("hexValue:" + hexValue);
        //transMainid
        // sendAsync().get()
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        hash=ethSendTransaction.getTransactionHash();
        if (hash == null) {
            Response.Error error = ethSendTransaction.getError();
            String msg = error.getCode() + ":" + error.getMessage();
            throw new Exception(msg);
        }
        log.info("hash:" + hash);
    }

    public static void testDecode() throws Exception {
        String hash="NXe5378e9a20ca33d350f55e5533357ed518eeca308dc18e3a80e14524b3e7c2de";
        Web3j web3 = Web3j.build(new HttpService("http://192.168.9.100:8510"));//
        EthTransaction t=web3.ethGetTransactionByHash(hash).send();
        String data= t.getResult().getInput();
        log.info(data);
        String src=new String(Numeric.hexStringToByteArray(data));
        log.info(src);
        //data=new String(Numeric.hexStringToByteArray(data));
        //log.info("data:" + data);
        /*Function function = new Function(
                "aaaa",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>() {},
                        new TypeReference<Uint256>() {}
                ));
        data=data.substring(10);
        List<Type> results= FunctionReturnDecoder.decode(data, function.getOutputParameters());
        String addr= (String) results.get(0).getValue();
        BigInteger num=(BigInteger) results.get(1).getValue();
        log.info("addr:" + addr);
        log.info("num:" + num);*/
    }

    public static BigInteger bigNumberConvert(String num){
        return  new BigInteger(num,16);
    }
    public static void main(String[] args)throws Exception {
        testSendTrace();
//      testDecode();
        //NX72d913dd6db8f8b588c3921e215a201aa22ab485b3169148c15c374eb5fffd97
//         String data="UTG1Exch0000000000000000000000006e83430ca56ee33a26e5ce87239cb251981ccc2b000000000000000000000000000000000000000000000000000000003B9ACA00";
//        String input="0000000000000000000000006e83430ca56ee33a26e5ce87239cb251981ccc2b000000000000000000000000000000000000000000000000000000003b9aca00";
//        System.out.println(input.substring(0,64));
//        System.out.println(new BigInteger("000000000000000000000000000000000000000000000000000000003b9aca00",16).longValue());
//        System.out.println("000000000000000000000000350fccf36124cecd26318e9931414ce872bdb68c".length());
//        System.out.println("000000000000000000000000350fccf36124cecd26318e9931414ce872bdb68c0000000000000000001807efcb4dc252ff6958eaab770c8b3936a5378f".length());
//        System.out.println(new BigInteger("1BC16D674EC800000",16));

//        System.out.println(bigNumberConvert("3B9ACA00"));
//       String to = Numeric.toHexString("SSC:1:ExchRate:15000".getBytes(StandardCharsets.UTF_8));
//        System.out.println(to);
//        String src=new String(Numeric.hexStringToByteArray("3a3135303030"));
//        System.out.println(":15000".substring(1));
//        String  a = "111111:adfg:2";
//        String s =  a.substring(7) ;
//        System.out.println(s);

//      String l =  "36000000000000000000";
//        System.out.println(new BigInteger(l));
    }

    private static BigDecimal convertToBigDecimal(String value) {
        if (null != value)
            return new BigDecimal(Numeric.decodeQuantity(value));
        return null;
    }
}