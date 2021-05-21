<%-- 
    Document   : trash_can
    Created on : 2021. 5. 4., 오후 6:17:52
    Author     : 배민정
    휴지통 페이지
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%> <!-- 데이터베이스-->

<!--지움
<jsp:useBean id="pop3" scope="page" class="cse.maven_webmail.model.Pop3Agent" />
<%
    pop3.setHost((String) session.getAttribute("host"));
    pop3.setUserid((String) session.getAttribute("userid"));
    pop3.setPassword((String) session.getAttribute("password"));
%> 
--!>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitiona1//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>휴지통</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>

    <body>
        <jsp:include page="header.jsp" />

        <div id="sidebar">
            <a href="main_menu.jsp"> 이전 메뉴로 </a>
        </div>

         <div id="msgBody">
             화아면
             </div>
     
            <%
            final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
            final String JdbcUrl = "jdbc:mysql://localhost:3306/goto_bin";
            final String User = "jdbctester";
            final String Password = "12345*";
            
            try {
            // 1. JDBC 드라이버 적재
            Class.forName(JdbcDriver);
            // 2. Connection 객체생성
            Connection conn = DriverManager.getConnection
                               (JdbcUrl, User, Password);
            // 3. Statement 객체생성
            Statement stmt = conn.createStatement();
            // 4. SQL 질의 쿼리실행
            String sql = "SELECT * FROM bin";
            ResultSet rs = stmt.executeQuery(sql); 
            %>
            <table border =" "1" >
                <thead>
                    <tr>
                        <th>보낸 사람</th>
                        <th>보낸날짜</th>
                        <th>제목</th>

                    </tr>
                </thead>
                <tbody>
                    
                 <%
                 while (rs.next()){ //휴지통 테이블에서 보여줄것
                 out.println("<tr>");
                 out.println("<td>" + rs.getString("send_person")+ "</td>");
                 out.println("<td>" + rs.getString("send_date")+ "</td>");
                 out.println("<td>" + rs.getString("m_title")+ "</td>");
                 }
                 
                     rs.close();
                     stmt.close();
                     conn.close();
                 %>
                </tbody>
            </table>
                <% 
                    } catch (Exception ex){
                    out.println("오류발생. (발생오류:"
                                                    + ex.getMessage()+ ")");
                    }
                            
                %>

        <jsp:include page="footer.jsp" />

    </body>
</html>
