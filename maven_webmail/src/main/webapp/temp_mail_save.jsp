<%-- 
    Document   : index
    Created on : 2021. 5. 3., 오후 1:03:03
    Author     : Angel
--%>

<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<% request.setCharacterEncoding("utf-8"); %>

<%@page import="java.sql.*"%>
<%@page import="javax.servlet.*"%>

<!DOCTYPE HTML PUBLIC "-//w3c//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose,dtd">
<script type="text/javascript">
    <!--
    function goread(){
        window.location = "/maven_webmail/main_menu.jsp"
    }
    -->
</script>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body onload="setTimeout('goread()', 1000)">
        
        <%
            String userid = (String) session.getAttribute("userid");
            String email = request.getParameter("to");
            String title = request.getParameter("subj");
            String content = request.getParameter("body");
            final String JdbcDriver = "com.mysql.jdbc.Driver";
            String JdbcUrl = "jdbc:mysql://localhost:3306/webmail?useUnicode=true&characterEncoding=utf8";
            final String User = "jdbctester";
            final String Password = "0000";
            response.setContentType("text/html;charset=UTF-8");
            
            try{
                Class.forName(JdbcDriver);
                
                Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);
                
                Statement stmt = conn.createStatement();
                

                String sql = "INSERT INTO tempmail (user, email, title, content) VALUES('"
                        + userid + "','" + email + "','" + title + "','" + content + "')"
                        + "ON DUPLICATE KEY UPDATE email='"+email+"', title='"+email+"', content='"+content+"';";
                
                stmt.executeUpdate(sql);

                    stmt.close();
                    conn.close();

            } catch (Exception ex) {
                out.println("오류가 발생했습니다. (발생 오류: "+ ex.getMessage() + ")");
            }
        %>
        
    </body>
</html>
