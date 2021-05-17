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
        <%@include file="header.jspf"%> <br>
        <div class="join_form">
            <div id="join">
                <form  action="join_handler.do?menu=<%= CommandType.JOIN%>" method="POST"> 
                    <table border="0" align="left">
                        <tr>
                            <td>ID</td>
                            <td> <input type="text" name="userid" maxlength="20"/>  </td>
                        </tr>
                        <tr>
                            <td>암호</td>
                            <td> <input type="password" name="password" maxlength="20"/> </td>
                        </tr>
                        <tr>
                            <td>암호 확인</td>
                            <td> <input type="password" name="password_check" maxlength="20"/> </td>
                        </tr>
                        <tr>
                            <td>이름</td>
                            <td> <input type="text" name="username"/> </td>
                        </tr>
                        <tr>
                            <td>생일</td>
                            <td> <input type="text" name="birth" maxlength="8"/> </td>
                        </tr>
                        <tr>
                            <td>전화번호</td>
                            <td> <input type="text" name="phone" maxlength="16"/> </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <input type="submit" value="저장" name="register"/>&nbsp;&nbsp;&nbsp;
                                <input type="button" value="나가기" onClick="location.href = '<%= getServletContext().getInitParameter("HomeDirectory")%>'"/>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
            <div id="constraint_text">
                <font color="red">* 다음의 형식으로 입력하세요.</font> <br>
                &nbsp;&nbsp;ID: 5자 이상 <br>
                &nbsp;&nbsp;암호: 6자 이상 <br>
                &nbsp;&nbsp;생일: 예)900202 <br>
                &nbsp;&nbsp;전화번호: 010-xxxx-xxxx <br> <br>
            </div>
        </div>

        <%@include file="footer_kwang.jspf"%>
    </body>
</html>
