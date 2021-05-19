<%-- 
    Document   : spam_mail_list.jsp
    Created on : 2021. 5. 4., 오후 1:00:07
    Author     : gleyd
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.SpamSettingDatabaseHandler"%>
<!DOCTYPE html>

<jsp:useBean id="pop3" scope="page" class="cse.maven_webmail.model.Pop3Agent"/>
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
        <jsp:directive.include file="header_spam_mail.jspf" />
        <hr>

        <div id="sidebar">
            <jsp:include page="sidebar_menu.jsp" />
        </div>

        <div id="msgBody">
            <a href="spam_settings.jsp"> 스팸 설정 </a>
            <%-- TODO : 스팸 메시지함 테이블 보여주긩.--%>
        </div>
        
        <div id="main">
            <%= pop3.getSpamSettingData()%>
            <%= pop3.getSpamMessageList()%>
        </div>


        <jsp:directive.include file="footer_hwi.jspf" />
    </body>
</html>
