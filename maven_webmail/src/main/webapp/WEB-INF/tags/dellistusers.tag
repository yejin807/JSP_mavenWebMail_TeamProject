<%-- 
    Document   : dellistusers
    Created on : 2021. 5. 18., 오전 2:49:09
    Author     : kwangmin
--%>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="user" required="true"%>
<%@attribute name="password" required="true"%>
<%@attribute name="schema" required="true"%>
<%@attribute name="table" required="true"%>


<%-- any content can be specified here e.g.: --%>
<sql:setDataSource var="dataSrc"
                   url="jdbc:mysql://localhost:3306/${schema}?serverTimezone=Asia/Seoul"
                   driver="com.mysql.cj.jdbc.Driver"
                   user="${user}" 
                   password="${password}"/>

<sql:query var="rs" dataSource="${dataSrc}">
    SELECT USER_ID, USER_NAME, BIRTH, PHONE FROM ${table}
</sql:query>

<table border="1">
    <thead>
        <tr>
            <th>선택</th>
            <th>ID</th>
            <th>이름</th>
            <th>생일</th>
            <th>전화번호</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="row" items="${rs.rows}">
            <tr>
                <td><input type="checkbox" name="selectedUsers" value="${row.USER_ID}"/></td>
                <td>${row.USER_ID}</td>
                <td>${row.USER_NAME}</td>
                <td>${row.BIRTH}</td>
                <td>${row.PHONE}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>