package com.jeetx.webSocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.jeetx.common.exception.BusinessException;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
	/**
	 * 握手前
	 * @param request
	 * @param response
	 * @param webSocketHandler
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
		//System.out.println("=====================握手操作=====================");
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
			if(servletServerHttpRequest.getServletRequest().getParameter("token") == null || servletServerHttpRequest.getServletRequest().getParameter("roomId") == null 
					|| servletServerHttpRequest.getServletRequest().getParameter("token") == "" || servletServerHttpRequest.getServletRequest().getParameter("roomId") == "") {
				throw new BusinessException("参数不完整，webSocket创建连接失败");
			}
			
			String token = (String)servletServerHttpRequest.getServletRequest().getParameter("token");
			String roomId = (String)servletServerHttpRequest.getServletRequest().getParameter("roomId");

			attributes.put("token", token);
			attributes.put("roomId", roomId);
			//System.out.println("webSocket握手前的：" + token);
		}
		return true;
	}


	/**
	 * 握手后
	 * @param serverHttpRequest
	 * @param serverHttpResponse
	 * @param webSocketHandler
	 * @param e
	 */
	@Override
	public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
			WebSocketHandler webSocketHandler, Exception e) {
		//System.out.println("=====================握手后=====================");
	}
}
