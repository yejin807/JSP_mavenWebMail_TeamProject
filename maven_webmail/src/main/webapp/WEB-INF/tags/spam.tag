<%-- 
    Document   : spam
    Created on : 2021. 5. 11., 오전 11:19:52
    Author     : gleyd
--%>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="user" required="true"%>
<%@attribute name="password" required="true"%>
<%@attribute name="schema" required="true"%>
<%@attribute name="table" required="true"%>
<%@attribute name="email" required="true"%>

<%-- any content can be specified here e.g.: --%>
<sql:setDataSource 
    var="dataSrc"
    url="jdbc:mysql://localhost:3306/${schema}?serverTime=Asia/Seoul"
    driver="com.mysql.cj.jdbc.Driver"
    password="${password}"
    user="${user}" />

<sql:query var="rs" dataSource="${dataSrc}">
     Select word from ${table} where email='${email}' and is_email=0 
    <%--Select word from ${table} where is_email = 0 --%>
</sql:query>

<table border="1">
    <thead>
        <tr>
            <th>스팸으로 등록된 단어</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="row" items="${rs.rows}">
            <tr>
                <td>${row.word}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>