/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import cse.maven_webmail.model.Pop3Agent;
import cse.maven_webmail.model.UserJoinAgent;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kwangmin 회원가입, 탈퇴 핸들러
 */
public class TempMailHandler extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            int select = Integer.parseInt((String) request.getParameter("menu"));

            switch (select) {
                case CommandType.SAVE_MAIL_COMMAND:
                    saveMail(request, response, out);
                    break;

                case CommandType.LOAD_MAIL_COMMAND:
                    loadMail(request, response, out);
                    break;

                default:
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                    break;
            }

        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

    private void saveMail(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        HttpSession session = request.getSession(true);
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
            
                String sql = "UPDATE tempmail SET email='"
                             + email + "', title='" + title + "', content='" + content + "'"
                             + "WHERE user='" + userid + "';" ;
                stmt.executeUpdate(sql);

                stmt.close();
                conn.close();

            } catch (Exception ex) {
                out.println("오류가 발생했습니다. (발생 오류: "+ ex.getMessage() + ")");
            }
    }

    //DB추가
    private void loadMail(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        //db 변수
        HttpSession session = request.getSession(true);
        String userid = (String) session.getAttribute("userid");
        final String JdbcDriver = "com.mysql.jdbc.Driver";
        String JdbcUrl = "jdbc:mysql://localhost:3306/webmail?useUnicode=true&characterEncoding=utf8";
        final String User = "jdbctester";
        final String Password = "0000";
        response.setContentType("text/html;charset=UTF-8");

        try{
                Class.forName(JdbcDriver);
                
                Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);
                
                Statement stmt = conn.createStatement();
                
                String sql = "SELECT * FROM tempmail WHERE user='"+userid+"';";
                
                ResultSet rs = stmt.executeQuery(sql);
                
                while (rs.next()){
                    String DBuser = rs.getString("user");
                    String DBemail = rs.getString("email");
                    String DBtitle = rs.getString("title");
                    String DBcontent = rs.getString("content");
                    }
                    
                    rs.close();
                    stmt.close();
                    conn.close();
                    
        } catch (Exception ex) {
                out.println("오류가 발생했습니다. (발생 오류: "+ ex.getMessage() + ")");
         }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
