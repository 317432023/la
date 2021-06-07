<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="/webpage/common/tag.jsp"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath%>" />
<title>WebSocket</title>
<meta name="robots" content="all" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content=" initial-scale=1.0,user-scalable=no" />
<meta name="format-detection" content="telephone=no" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<script type="text/javascript" src="jquery.js"></script>
<script>
    $(document).ready(function() {
        var ws;
        if ('WebSocket' in window) {
            ws = new WebSocket("ws://"+window.location.host+"/la-ws/webSocketServer?token=dqAILq2zuRJBKJslrQObmx9iIC2enMnH&roomId=13");
           	//ws = new WebSocket("ws://159.138.22.154:6060/la-ws/webSocketServer?token=Ye0eQJWeKTShvSWnw3Yu8u0VSnw0VpAU&roomId=13");
            //ws = new WebSocket("ws://ws.43018.cn:7070/webSocketServer?token=Ye0eQJWeKTShvSWnw3Yu8u0VSnw0VpAU&roomId=13");
         } else if ('MozWebSocket' in window) {
            ws = new MozWebSocket("ws://"+window.location.host+"/la-api/webSocketServer");
        } else {
            //如果是低版本的浏览器，则用SockJS这个对象，对应了后台“sockjs/webSocketServer”这个注册器，
            ws = new SockJS("http://"+window.location.host+"/la-api/sockjs/webSocketServer"); 
        }
        ws.onopen = function (evnt) {
        	console.log("onopen:"+JSON.stringify(evnt));
        };
        //接收到消息
        ws.onmessage = function (evnt) {
        	console.log("onmessage:"+evnt.data);
        };
        ws.onerror = function (evnt) {
        	console.log("onerror:"+JSON.stringify(evnt))
        };
        ws.onclose = function (evnt) {
        	console.log("onclose:"+JSON.stringify(evnt));
        }
    });

</script>
<body>
254
</body>
</html>

