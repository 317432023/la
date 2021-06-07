<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%
	response.setHeader("Content-Type", "application/json;charset=UTF-8");//注意加上这一句
	out.print("{\"code\":101,\"message\":\"缺少参数\",\"token\":\"\",\"result\":\"\"}");
%>