<%--
Created by IntelliJ IDEA.
User: Bourne
Date: 16-5-6
Time: 上午11:49
To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html xmlns:s="http://www.w3.org/1999/html">
<head>
<title>Struts2</title>
    <link type="text/css" href="/style/style.css">

</head>
<h3>Documentum DFC</h3>
<body bgcolor="white">
	<s:form action="login.action">
        <s:textfield name="username" placeholder="Username" label="Username" />
        <s:password name="password" placeholder="Password" label="Password" />
        <s:submit value="Submit" />
	</s:form>
 </body>
</html>
