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

/**
 *
 * @author gleyd
 */
@WebServlet(name = "SpamDatabaseHandler", urlPatterns = {"/spam_database.do"})
public class SpamDatabaseHandler extends HttpServlet {

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

        final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
        final String JdbcUrl = "jdbc:mysql://localhost:3306/webmail?serverTime=Asia/Seoul";
        final String User = "jdbctester";
        final String Password = "1895";

        try (PrintWriter out = response.getWriter()) {

            HttpSession session = request.getSession();
            String userid = (String) session.getAttribute("userid");

            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SpamDatabaseHandler</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SpamDatabaseHandler at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");

            out.println((String) session.getAttribute("userid"));

            //DB 객체 생성
            Class.forName(JdbcDriver);
            Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);

            //stmt
            String sql = "INSERT INTO `webmail`.`spam` (`email`, `word`, `is_email`) VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            //sql문 완성
            request.setCharacterEncoding("UTF-8");
            String word = request.getParameter("word");
            String isEmail = request.getParameter("isEmail");
            
            if (!(word == null) && !(word.equals(""))) {

                out.println("null아닙니니당.");
                if (isEmail == null) { //스팸 단어를 추가하는 것이면
                    pstmt.setString(1, userid);
                    pstmt.setString(2, word);
                    pstmt.setInt(3, 0);

                    out.println("<br>");
                    out.println("userid : " + userid + "입니다.");
                    out.println("<br>");
                    out.println("word : " + word + "입니다.");
                    out.println("<br>");
                    out.println("int : 0 입니다.");
                    out.println("<br>");

                } else {//스팸 이메일을 추가하는 것이면
                    pstmt.setString(1, userid);
                    pstmt.setString(2, word);
                    pstmt.setInt(3, 1);

                    out.println("<br>");
                    out.println("userid : " + userid + "입니다.");
                    out.println("<br>");
                    out.println("word : " + word + "입니다.");
                    out.println("<br>");
                    out.println("int : 1 입니다.");
                    out.println("<br>");
                }
                pstmt.executeUpdate();
                out.println("insert완료..?.");
            } else {
                out.println("null입니당.");
            }

                                out.println("<br>코드완료");
            //out.println(request.getParameter("word"));
            //out.println(request.getParameter("isEmail"));

            pstmt.close();
            conn.close();

            response.sendRedirect("spam_settings.jsp");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SpamDatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);

        } catch (SQLException ex) {
            Logger.getLogger(SpamDatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);

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
