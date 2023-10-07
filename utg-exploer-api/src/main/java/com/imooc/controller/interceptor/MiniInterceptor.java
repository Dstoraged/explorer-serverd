package com.imooc.controller.interceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;

public class MiniInterceptor implements HandlerInterceptor {
	@Autowired
	public RedisOperator redis;

	public static final String USER_REDIS_SESSION = "user_redis_session";

	/**
	 * Called before the request is processed (before the Controller method is called)
	 */
	@SuppressWarnings("static-access")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		

		String headerUserId = request.getHeader("headerUserId");
		String headerUserToken = request.getHeader("headerUserToken");

		if (StringUtils.isNoneBlank(headerUserId) && StringUtils.isNoneBlank(headerUserToken)) {
			String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + headerUserId);
			if (StringUtils.isBlank(headerUserId) || StringUtils.isEmpty(headerUserId)) {
				returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("please login"));
				
				return false;
			}else {
				if(!headerUserToken.equals(uniqueToken)) {
					returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("Account is squeezed out..."));
					return false;
				}
			}
			System.out.println("please login");
		} else {
			returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("please login"));
			return false;
		}
		
		return true;

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}
	
	public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result) 
			throws IOException, UnsupportedEncodingException {
		OutputStream out=null;
		try{
		    response.setCharacterEncoding("utf-8");
		    response.setContentType("text/json");
		    out = response.getOutputStream();
		    out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
		    out.flush();
		} finally{
		    if(out!=null){
		        out.close();
		    }
		}
	}
	

}
