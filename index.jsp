<%--
  Created by IntelliJ IDEA.
  User: limit
  Date: 2017/12/21
  Time: 16:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>文件上传</title>
</head>
<body>
<h1>显示图片</h1>
<form method="get" action="transfer_server">
  用户名:
  <input type="text" name="usrname"/>
  密码:
  <input type="password" name="password"/>
  <br/><br/>
  你想看:
  自己的:
  <input type="radio" name="type" value="1"/>
  别人的:
  <input type="radio" name="type" value="2"/>
  <br/><br/>
  <input type="submit" value="开始请求">
</form>
<br/><br/>
<h1>上传与选择模型</h1>
<form method="post" action="transfer_server" enctype="multipart/form-data">
  <br/>
  用户名:
  <input type="text" name="usrname"/>
  密码:
  <input type="password" name="password"/>
  <br/>
  选择一个模型:
  <br/>
  La muse:
  <input type="radio" name="model" value="la_muse"/>
  Rain princess:
  <input type="radio" name="model" value="rain_princess"/>
  Scream:
  <input type="radio" name="model" value="scream"/>
  Udnie:
  <input type="radio" name="model" value="udnie"/>
  Wave:
  <input type="radio" name="model" value="wave"/>
  Wreck:
  <input type="radio" name="model" value="wreck"/>
  <br/>
  选择一个文件:
  <input type="file" name="uploadFile" />
  <br/><br/>
  <input type="submit" value="上传" />
</form>
</body>
</html>