<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="com.baidu.ueditor.ActionEnter"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	request.setCharacterEncoding("utf-8");
	response.setHeader("Content-Type", "text/html");
	// 跨域的服务器路径
	response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
	// 支持跨域上传
	response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,X_Requested_With");
	String rootPath = application.getRealPath("/");
	out.write(new ActionEnter(request, rootPath).exec());

	
%>