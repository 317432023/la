package com.jeetx.interceptors;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/*** 防止SQL注入的拦截器*/
public class SqlInjectInterceptor implements HandlerInterceptor {
	private static final Logger logger = Logger.getLogger(SqlInjectInterceptor.class);

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
			throws Exception {
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String[] values = request.getParameterValues(name);
			for (String value : values) {
				// sql注入直接拦截
				if (judgeSQLInject(value.toLowerCase())) {
					logger.info("-----------Sql注入拦截-----------name: " + name + " -------------value:" + value);
					response.setContentType("text/html;charset=UTF-8");
					response.getWriter().print("参数含有非法攻击字符,已禁止继续访问！");
					return false;
				}
				// 跨站xss清理
				clearXss(value);
			}
		}
		return true;
	}

	/**
	 * 判断参数是否含有攻击串
	 * 
	 * @param value
	 * @return
	 */
	public boolean judgeSQLInject(String value) {
		if (value == null || "".equals(value)) {
			return false;
		}
		String xssStr = "and |or |select |update |delete |drop |truncate |%20|=|--|!=";
		String[] xssArr = xssStr.split("\\|");
		for (int i = 0; i < xssArr.length; i++) {
			if (value.indexOf(xssArr[i]) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理跨站xss字符转义
	 *
	 * @param value
	 * @return
	 */
	private String clearXss(String value) {
		logger.debug("----before--------处理跨站xss字符转义----------" + value);
		if (value == null || "".equals(value)) {
			return value;
		}
		value = value.replaceAll("<", "<").replaceAll(">", ">");
		value = value.replaceAll("\\(", "(").replace("\\)", ")");
		value = value.replaceAll("'", "'");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replace("script", "");

		// 为了用户密码安全，禁止列表查询展示用户密码----------
		value = value.replace(",password", "").replace("password", "");
		logger.debug("----end--------处理跨站xss字符转义----------" + value);
		return value;
	}
}