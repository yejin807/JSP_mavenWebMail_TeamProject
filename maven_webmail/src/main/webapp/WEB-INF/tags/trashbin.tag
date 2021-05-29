<%-- 
    Document   : bin
    Created on : 2021. 5. 18., 오후 9:31:18
    Author     : 배민정
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

<%-- any content can be specified here e.g.: --%>

<sql:setDataSource var="dataSrc"
                   url="jdbc:mysql://localhost:3306/${schema}?serverTimezone=Asia/Seoul"
                   driver="com.mysql.cj.jdbc.Driver"
                   user="${user}"  
                   password="${password}" />

<sql:query var ="rs" dataSource = "${dataSrc}">
    SELECT send_person, send_date, m_title
    FROM ${table}
   
</sql:query>
    
    <table border ="1">
        <thead>
            <tr>
               
                 <th>보낸 사람</th>
                 <th>제목</th>
                 <th>보낸 날짜</th>
                 <th>삭제</th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${rs.rows}">
                <tr>
                    <td>${row.send_person}</td>         
                    <td>${row.m_title}</td>
                    <td>${row.send_date}</td>
                    <td> <a href=ReadMail.do?menu=<%=CommandType.VIN_DBDELETE_COMMAND%>&sendPerson=${row.send_person}&mTitle=${row.m_title}>삭제</a></td>
                    <%--&sendDate=${row.send_date}--%>
                                                              <!-- 값을 sendPerson에 집어넣는다?-->
                </tr>
            </c:forEach>
        </tbody>
    </table>