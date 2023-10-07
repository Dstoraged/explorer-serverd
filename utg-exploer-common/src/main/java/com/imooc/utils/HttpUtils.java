package com.imooc.utils;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiremock.org.apache.http.client.ResponseHandler;
import wiremock.org.apache.http.client.methods.HttpPost;
import wiremock.org.apache.http.entity.StringEntity;
import wiremock.org.apache.http.impl.client.BasicResponseHandler;
import wiremock.org.apache.http.impl.client.CloseableHttpClient;
import wiremock.org.apache.http.impl.client.HttpClients;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
public class HttpUtils {
    private static final String ENCODING = "UTF-8";
    private static Logger log= LoggerFactory.getLogger(HttpUtils.class);
    public static String getSend(String strUrl, String param) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(strUrl + "?" + param);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String post(String url) {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
        client.getHttpConnectionManager().getParams().setSoTimeout(20000);

        HttpMethod post = new PostMethod(url);
        try {
            client.executeMethod(post);
            byte[] responseBody = post.getResponseBody();
            String result = new String(responseBody, "UTF-8");
            return result;
        } catch (Exception e) {
            client.getHttpConnectionManager().closeIdleConnections(0);
        } finally {
            post.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
        return null;
    }

    public static String postSend(String strUrl, String param) {
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL(strUrl);
            //   url = new URL(strUrl + "?" + param);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.connect();
            // DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            OutputStream outs=null;
            outs =connection.getOutputStream();
            /*out.writeBytes(param);
            out.flush();
            out.close();*/
            outs.write(param.getBytes());
            outs.flush();
            outs.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String paraTo16(String str) throws UnsupportedEncodingException {
        String hs = "";

        byte[] byStr = str.getBytes("UTF-8");
        for (int i = 0; i < byStr.length; i++) {
            String temp = "";
            temp = (Integer.toHexString(byStr[i] & 0xFF));
            if (temp.length() == 1){ temp = "%0" + temp;}
            else {temp = "%" + temp;}
            hs = hs + temp;
        }
        return hs.toUpperCase();


    }



    public static String yunpianPost(String url, Map<String, String> paramsMap) {
        HttpClient client = new HttpClient();
        try {
            PostMethod method = new PostMethod(url);
            if (paramsMap != null) {
                NameValuePair[] namePairs = new NameValuePair[paramsMap.size()];
                int i = 0;
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new NameValuePair(param.getKey(),
                            param.getValue());
                    namePairs[i++] = pair;
                }
                method.setRequestBody(namePairs);
                HttpMethodParams param = method.getParams();
                param.setContentCharset("utf-8");
            }
            client.executeMethod(method);
            return method.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String HttpPostWithJson(String url, String json) {
        String returnValue = "0.0";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try{
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            StringEntity requestEntity = new StringEntity(json,"utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);
            returnValue = httpClient.execute(httpPost,responseHandler);
        }
        catch(Exception e)
        {
        	log.error("HttpPostWithJson error",e);
        }
        finally {
            try {
                httpClient.close();
            } catch (IOException e) {
            	log.error("httpClient close error",e);
            }
        }
        return returnValue;

    }
    
    
    public static String getRealIp(HttpServletRequest request){ 
		String ip = request.getHeader("x-forwarded-for");
		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("X-Forward-For");
		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("X-Real-IP");
//		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip))
//			ip = request.getHeader("Proxy-Client-IP");
//		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip))
//			ip = request.getHeader("WL-Proxy-Client-IP");
		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();
		return ip;
	}
}
