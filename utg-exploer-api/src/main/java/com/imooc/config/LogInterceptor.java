package com.imooc.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.imooc.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor{
	
	long starttime = 0;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
		starttime = System.currentTimeMillis();
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handle, ModelAndView modelView) throws Exception {
				
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handle, Exception exception) throws Exception {
		String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
		long spendtime = System.currentTimeMillis() - starttime;
		log.info("Request api from " + HttpUtils.getRealIp(request) + " url=" + request.getRequestURL() + queryString + ", spend " + spendtime	+ " ms");
		if (exception != null) {
			log.warn("Request api from " + HttpUtils.getRealIp(request) + " url=" + request.getRequestURL() + queryString + " error:" + exception.getMessage());
		}
	}

}
