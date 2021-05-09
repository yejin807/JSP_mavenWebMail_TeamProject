<%-- 
    Document   : sign_up
    Created on : 2021. 5. 8., 오후 9:11:19
    Author     : kwangmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원가입</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%@include file="header.jspf"%>
        <form action="#">
            <p>
                <label for="id">ID: </label> 
                <input id="id" type="text" name="id">
            </p>
            <p>
                <label for="passwd">비밀번호: </label> 
                <input id="passwd" type="text" name="passwd">
            </p> 
            <p>
                <label for="passwd_check">비밀번호 확인: </label> 
                <input id="passwd_check" type="text" name="passwd_check">
            </p>
            <p>
                <label for="username">이름: </label> 
                <input id="username" type="text" name="username">
            </p>
            <p>
                <label for="age">나이: </label>
                <input id="age" type="text" name="age">
            </p>
            <p>
                <label for="phone">전화번호: </label>
                <input id="phone" type="text" name="phone">
            </p>
            <p>
                <input type="submit" value="회원가입"/>
            </p>
        </form>
        <%@include file="footer_kwang.jspf"%>
    </body>
</html>
