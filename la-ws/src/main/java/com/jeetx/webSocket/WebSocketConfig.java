package com.jeetx.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
	@Override 
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// 注册websocket实现类
		registry.addHandler(msgSocketHandle(), "/webSocketServer").addInterceptors(new WebSocketHandshakeInterceptor()).setAllowedOrigins("*");
		// 使用socketjs的注册方法
		registry.addHandler(msgSocketHandle(), "/sockjs/webSocketServer").addInterceptors(new WebSocketHandshakeInterceptor()).setAllowedOrigins("*").withSockJS();
	}

	@Bean(name = "msgSocketHandle")
	public WebSocketHandler msgSocketHandle() {
		return new MsgScoketHandle();
	}
}
