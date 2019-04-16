<%--
  Created by IntelliJ IDEA.
  User: CLS
  Date: 2019/3/6
  Time: 16:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图片上传</title>
</head>
<body>
    <h2>spring-mvc图片上传到ftp服务器</h2>
    
    <form action="/manage/product/uploadPicture" enctype="multipart/form-data" method="post">
        <table>
            <tr>
                <td>请选择文件</td>
                <td><input type="file" name="file"></td>
            </tr>
            <tr>
                <td><input type="submit" value="上传"></td>
            </tr>
        </table>
    </form>

</body>
</html>
