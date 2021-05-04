<%-- 
    Document   : spam_mail_list.jsp
    Created on : 2021. 5. 4., 오후 1:00:07
    Author     : gleyd
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주메뉴 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:directive.include file="header_spam_mail.jspf" />
        <hr>

        <div id="sidebar">
            <a href="main_menu.jsp"> 이전 메뉴로 </a>
        </div>

        <div id="msgBody">
            helloWorld
        </div>

        <jsp:directive.include file="footer_hwi.jspf" />
    </body>
</html>
