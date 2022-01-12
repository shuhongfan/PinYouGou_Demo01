<%--
  Created by IntelliJ IDEA.
  User: shuho
  Date: 2022/1/12
  Time: 11:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>欢迎访问二品优购</h1>

<%=request.getRemoteUser()%>

<a href="http://localhost:9100/cas/logout">退出登录</a>
</body>
</html>
