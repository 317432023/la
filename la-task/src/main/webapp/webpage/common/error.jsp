<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<link rel="icon" href="favicon.ico">
<title>错误提示</title>
</head>
<body>

<%--
<c:choose>
<c:when test="${not empty param.msg}">
${param.msg}
</c:when>
<c:otherwise>
${msg }
</c:otherwise>
</c:choose> --%>
<h2>
<% // 另一种方式是使用自定义jsp标签
String msg = null;
//if(request.getParameter("msg")!=null) msg = new String(request.getParameter("msg").getBytes("iso-8859-1"),"utf-8");
if(request.getParameter("msg")!=null) msg = request.getParameter("msg");
if(msg==null) msg = (String)pageContext.getAttribute("msg");
if(msg==null) msg = (String)request.getAttribute("msg");
if(msg==null) msg = (String)session.getAttribute("msg");
if(msg==null) msg ="出错了";
%>
<%=msg %>
</h2>
</body>
</html>
