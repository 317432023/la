package com.jeetx.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jeetx.common.redis.JedisClient;
import com.jeetx.controller.api.ApiUtil;
import com.jeetx.util.JsonUtil;

/*** 字符集拦截器*/
public class EncodingInterceptor implements HandlerInterceptor {
	 @Autowired JedisClient jedisClient;
	  
	/** * 在controller后拦截*/
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
			Exception exception) throws Exception {

	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object,
			ModelAndView modelAndView) throws Exception {

	}

	/*** 在controller前拦截*/
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		//禁止重复提交
		String requestURI = ApiUtil.getRequestUrl(request);
		if(requestURI.contains("api/common/login?") || requestURI.contains("api/common/weChatLogin?")
				|| requestURI.contains("api/common/touristLogin?")) {
			
			String sign = ApiUtil.getParameterValues(request, "sign");
			String timestamp = ApiUtil.getParameterValues(request, "timestamp");
			String key = request.getRequestURI().concat("/").concat(sign).concat("/").concat(timestamp);
			//System.out.println(key);
			
			String jedisRequestURI = jedisClient.get(key);
			if(StringUtils.isNotBlank(jedisRequestURI)) {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().print("禁止重复提交");
				return false;
			}else {
				jedisClient.set(key, requestURI);
				jedisClient.expire(key, 60*1); //设置会话过期时间180秒=3分钟
				
				//System.out.println(jedisClient.get(key));
			}
		}

		return true;
	}
}
