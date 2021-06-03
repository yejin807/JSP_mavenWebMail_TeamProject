<%-- 
    Document   : trash_can
    Created on : 2021. 5. 4., 오후 6:17:52
    Author     : 배민정
    휴지통 페이지
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="mytags" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitiona1//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">

<html lang="kor">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>휴지통</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
         <jsp:directive.include file="header_bmj.jspf" />
        <hr>
  

        <div id="sidebar">
        <a href="main_menu.jsp"> 메인메뉴로 </a>
        <p> <a href="write_mail.jsp"> 메일 쓰기 </a> </p>
        <p> <a href="spam_mail_list.jsp"> 스팸메일함 </a> </p>
        <p> <a href="bookmarked_mail.jsp"> 즐겨찾기함 </a> </p>
        </div>
        
        <!-- bin DB와 연결/ bin테이블 보여줌-->
        <c:catch var= "errorReason">
            <mytags:trashbin user="jdbctester" password="12345*"
                        schema="goto_bin" table="bin" />
        </c:catch>
       
        ${empty errorReason?"<noerror/>" : errorReason}

        <jsp:directive.include file="footer_bmj.jspf" />

    </body>
</html>