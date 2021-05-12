<%-- 
    Document   : bookmarked_mail.jsp
    Created on : 2021. 5. 4., 오후 2:31:09
    Author     : gleyd
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<jsp:useBean id="pop3" scope="page" class="cse.maven_webmail.model.Pop3Agent" />
<%
    pop3.setHost((String) session.getAttribute("host"));
    pop3.setUserid((String) session.getAttribute("userid"));
    pop3.setPassword((String) session.getAttribute("password"));
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주메뉴 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:directive.include file="header_bookmarked_mail.jspf" />
        <hr>

        <div id="sidebar">
            <a href="main_menu.jsp"> 이전 메뉴로 </a>
        </div>


        <div id="main">
            <%= pop3.getBookmarkMessageList()%>
        </div>

        <jsp:directive.include file="footer_hwi.jspf" />    </body>
</html>
