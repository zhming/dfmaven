<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8" %>
<%@taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h3>FieldError in Struts 2</h3>
<s:form action="loginUser">
    <s:textfield name="username" placeholder="Username" label="Username" />
    <s:password name="password" placeholder="Password" label="Password" />
    <s:submit value="Submit" />
</s:form>
<p>
    <s:property value="#username"/>
</p>
</body>
</html>