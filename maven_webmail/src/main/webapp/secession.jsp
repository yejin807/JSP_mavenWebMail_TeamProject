<%-- 
    Document   : secession
    Created on : 2021. 5. 17., 오후 3:02:00
    Author     : kwangmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원탈퇴</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%@include file="header.jspf"%> <br>

        &nbsp;&nbsp;&nbsp;회원탈퇴를 진행합니다. 비밀번호를 다시 입력해주세요.

        <div id="sidebar">
            <a href="main_menu.jsp"> 돌아가기 </a>
            <p><a href="Login.do?menu=<%= CommandType.LOGOUT%>">로그아웃</a></p>
        </div>

        <form  action="join_handler.do?menu=<%= CommandType.SECESSION%>" method="POST"> 
            <table border="0" align="left">
                <tr>
                    <td>ID</td>
                    <td> <input type="text" name="userid" value="<%= session.getAttribute("userid")%>" readonly/>  </td>
                </tr>
                <tr>
                    <td>비밀번호</td>
                    <td> <input type="password" name="password" maxlength="20"/> </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit" value="회원탈퇴" name="secession"/>
                    </td>
                </tr>
            </table>
        </form>

        <jsp:include page="footer.jsp" />
    </body>
</html>