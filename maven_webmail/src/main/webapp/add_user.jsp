<%-- 
    Document   : add_user.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>
<%@page errorPage="ErrorPage.jsp"%>

<!DOCTYPE html>

<html lang="en" xml:lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>사용자 추가 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <%
            String userid = (String) session.getAttribute("userid");

            if (userid.equals("admin")) {
        %>
        <div id="sidebar">
            <jsp:include page="sidebar_admin_previous_menu.jsp" />
        </div>

        <div id="main">
            추가로 등록할 사용자 ID와 암호를 입력해 주시기 바랍니다. <br> <br>

            <form name="AddUser" action="UserAdmin.do?menu=<%= CommandType.ADD_USER_COMMAND%>"
                  method="POST">
                <table border="0" align="left">
                    <caption> admin 사용자 추가 </caption>
                    <tr>
                        <th colspan="2">정보입력</th>
                    </tr>
                    <tr>
                        <td>사용자 ID</td>
                        <td> <input type="text" name="userid" maxlength="20"/>  </td>
                    </tr>
                    <tr>
                        <td>암호</td>
                        <td> <input type="password" name="password" maxlength="20"/> </td>
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
                        </td>
                    </tr>
                </table>
            </form>
            <div id="constraint_text">
                <span style="color:red">* 다음의 형식으로 입력하세요.</span> <br>
                &nbsp;&nbsp;ID: 5자 이상 <br>
                &nbsp;&nbsp;암호: 6자 이상 <br>
                &nbsp;&nbsp;생일: 예)900202 <br>
                &nbsp;&nbsp;전화번호: 010-xxxx-xxxx <br> <br>
            </div>
        </div>
        <%
            }
        %>
        <jsp:include page="footer.jsp" />
    </body>
</html>
