/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.control.CommandType;
import java.sql.ResultSet;

/**
 *
 * @author gleyd
 */
@WebServlet(name = "SpamDatabaseHandler", urlPatterns = {"/spam_database.do"})
public class SpamDatabaseHandler extends HttpServlet {

    private final String JdbcDriver = CommandType.JdbcDriver;
    private final String JdbcUrl = CommandType.JdbcUrl;
    private final String User = CommandType.User;
    private final String Password = CommandType.Password;

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

        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        //word가 아니라 command 추가로 바꿔야할듯.
        String word = request.getParameter("word");
        String spamword = request.getParameter("spamword");
        String isEmail = request.getParameter("isEmail");
        String sql = "null이에요.";

        PrintWriter out = response.getWriter();

        try {
            //infoHTML(out, select);
            //
            if (!(word == null) && !(word.equals(""))) {
                insertSpamCommand(userid, word, isEmail);
                response.sendRedirect("spam_settings.jsp");
            }
            //스팸단어 삭제 기능
            if ((request.getParameter("command") != null) && (request.getParameter("spamword") != null)) {
                //int select = Integer.parseInt((String) request.getParameter("delete"));
                int select = Integer.parseInt((String) request.getParameter("command"));
                switch (select) {
                    case CommandType.DELETE_SPAM_WORD_COMMAND:
                        deleteSpamCommand(userid, spamword, CommandType.IS_EMAIL_FALSE);
                        //response.sendRedirect("spam_settings.jsp");
                        break;
                    case CommandType.DELETE_SPAM_EMAIL_COMMAND:
                        deleteSpamCommand(userid, spamword, CommandType.IS_EMAIL_TRUE);
                        //response.sendRedirect("spam_settings.jsp");
                        break;

                }
                request.setAttribute("command", null);
            } //end 스팸단어 조건 
            response.sendRedirect("spam_settings.jsp");
        } catch (Exception ex) {// end try
            out.println("exception : " + ex);
        }
    }

    private void infoHTML(PrintWriter out, String str) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet SpamDatabaseHandler</title>");
        out.println("</head>");

        out.println("<body>");
        out.println("00");
        out.println(".." + str + ",,");
        out.println("00");
        out.println("<p> <a href=\"spam_settings.jsp\"> 원상복구 </a> </p>");
        out.println("</body>");

        out.println("</html>");

    }

    private void insertSpamCommand(String userid, String word, String isEmail) throws ClassNotFoundException, SQLException {
        Class.forName(JdbcDriver);
        Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);

        String sql = "INSERT INTO `webmail`.`spam` (`email`, `word`, `is_email`) VALUES (?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        if (isEmail == null) { //스팸 단어를 추가하는 것이면
            pstmt.setString(1, userid);
            pstmt.setString(2, word);
            pstmt.setInt(3, 0);
        } else {
            pstmt.setString(1, userid);
            pstmt.setString(2, word);
            pstmt.setInt(3, 1);
        }
        pstmt.executeUpdate();

        pstmt.close();
        conn.close();
        //sql문 완성
    }

    private void deleteSpamCommand(String email, String word, int isEmail) throws ClassNotFoundException, SQLException {
        // 참고 : https://doublesprogramming.tistory.com/60
        Class.forName(JdbcDriver);
        Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);
        /*        infoHTML(out, email);
        infoHTML(out, word);
        infoHTML(out, Integer.toString(isEmail)); */

        String sql = "DELETE FROM `webmail`.`spam` WHERE (`email` = ?) and (`word` = ?) and (`is_email` = ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, email);
        pstmt.setString(2, word);
        pstmt.setInt(3, isEmail);

        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
        //sql문 완성
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
