<%-- 
    Document   : spam
    Created on : 2021. 5. 11., 오전 11:19:52
    Author     : gleyd
--%>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>
<%@tag import="cse.maven_webmail.control.CommandType" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="user" required="true"%>
<%@attribute name="password" required="true"%>
<%@attribute name="schema" required="true"%>
<%@attribute name="table" required="true"%>
<%@attribute name="email" required="true"%>
<%-- TODO 수정 --%>
<%-- <%@attribute name="JDBC" rtexprvalue="<% CommandType.JdbcDriver %>" %> --%>

<%-- any content can be specified here e.g.: --%>
<sql:setDataSource 
    var="dataSrc"
    url="jdbc:mysql://localhost:3306/${schema}?serverTime=Asia/Seoul"
    driver="com.mysql.cj.jdbc.Driver"
    password="${password}"
    user="${user}" />

<link type="text/css" rel="stylesheet" href="css/main_style.css" />

<br><br><br>

<div id="spam_word">

    <sql:query var="rs" dataSource="${dataSrc}">
        Select word from ${table} where email='${email}' and is_email=0 
        <%--Select word from ${table} where is_email = 0 --%>
    </sql:query>

    <table border="1">
        <thead>
            <tr>
                <th>스팸으로 등록된 단어</th>
                <th>삭제</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${rs.rows}">
                <tr>
                    <td>${row.word}</td>
                    <%-- commandtype 로 하지않고 true/ false로 넣어주기. &word=${rs.rows}로 값도 넘겨줘야함--%>
                    <td id="delete_spam_word"><a href=spam_database.do?command=<%=CommandType.DELETE_SPAM_WORD_COMMAND%>&spamword=${row.word}>삭제</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<div id="spam_email]">
    <sql:query var="rs" dataSource="${dataSrc}">
        Select word from ${table} where email='${email}' and is_email=1
        <%--Select word from ${table} where is_email = 0 --%>
    </sql:query>


    <table border="1">
        <thead>
            <tr>
                <th>스팸으로 등록된 이메일</th>
                <th>삭제</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${rs.rows}">
                <tr>
                    <td>${row.word}</td>
                    <td id="delete_spam_word"><a href=spam_database.do?command=<%=CommandType.DELETE_SPAM_EMAIL_COMMAND%>&spamword=${row.word}>삭제</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</div>