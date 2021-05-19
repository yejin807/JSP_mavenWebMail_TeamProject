<%-- 
    Document   : spam_adder
    Created on : 2021. 5. 10., 오후 6:44:07
    Author     : gleyd
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="spamtag"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
            <jsp:include page="sidebar_menu.jsp" />
        </div>

        <div id="spam_word_adder">
            <form action="spam_database.do"  >
                스팸으로 추가할 단어 : <input type="text" name="word" value="" maxlength="50"/> 
                이메일 인가요?         <input type="checkbox" name="isEmail" value="1"/>
                                      <input type="submit" value="추가하기"/>
            </form>
        </div>
        
        <c:catch var="error">
                <spamtag:spam_setting user="<%=CommandType.JdbcUser%>" password="<%=CommandType.JdbcPassword%>" schema="webmail" table="spam_setting" email="${sessionScope.userid}"/>
        </c:catch>
        ${empty error? "<noerror/>":error}


        <jsp:directive.include file="footer_hwi.jspf" />
    </body>
</html>
