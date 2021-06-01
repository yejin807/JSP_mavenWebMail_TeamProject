/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import cse.maven_webmail.model.BookmarkMessageAgent;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.SpamMessageAgent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleyd
 */
@WebServlet(name = "SpamDatabaseHandler", urlPatterns = {"/spam_database.do"})
public class SpamSettingDatabaseHandler extends HttpServlet {

    private ArrayList<String> spamWord = null;
    private ArrayList<String> spamEmail = null;

    SpamMessageAgent spamMessageAgent;

    private String userid = null;

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
        userid = (String) session.getAttribute("userid");
        //word가 아니라 command 추가로 바꿔야할듯.
        String word = request.getParameter("word").replaceAll(" ", "");
        String spamword = request.getParameter("spamword");
        String isEmail = request.getParameter("isEmail");
        String sql = "null이에요.";
        spamMessageAgent = SpamMessageAgent.getInstance(userid);

        PrintWriter out = response.getWriter();

        try {

            if (!(word == null) && !(word.trim().length() == 0)) {
                insertSpamCommand(userid, word, isEmail);
                spamMessageAgent.setNeedUpdate(true);
                System.out.println("추가된문자열=" + word + "=");
                response.sendRedirect("spam_settings.jsp");
            } else {
                out.println("<script>alert('스팸처리할 단어나 이메일을 입력하세요!');location.href='spam_settings.jsp'</script>");

            }
            if ((request.getParameter("command") != null) && (request.getParameter("spamword") != null)) {
                int select = Integer.parseInt((String) request.getParameter("command"));

                switch (select) {
                    case CommandType.DELETE_SPAM_WORD_COMMAND:
                        deleteSpamCommand(userid, spamword, CommandType.IS_EMAIL_FALSE);
                        spamMessageAgent.setNeedUpdate(true);
                        response.sendRedirect("spam_settings.jsp");
                        break;
                    case CommandType.DELETE_SPAM_EMAIL_COMMAND:
                        deleteSpamCommand(userid, spamword, CommandType.IS_EMAIL_TRUE);
                        spamMessageAgent.setNeedUpdate(true);
                        response.sendRedirect("spam_settings.jsp");
                        break;
                }
                request.setAttribute("command", null);
            }
        } catch (Exception ex) {
            out.println("exception : " + ex);
        }
    }

    public ArrayList<String> getSpamWord() {
        return spamWord;
    }

    public ArrayList<String> getSpamEmail() {
        return spamEmail;
    }

    public void getSpamSettingData(String userid) {
        this.userid = userid;
        spamWord = new ArrayList<String>();
        spamEmail = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            //spam단어 읽어오기
            String sql = "select word from webmail.spam_setting where email =? and is_email=0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                spamWord.add(rs.getString("word"));
            }

            //spam 이메일 읽어오기
            sql = "select word from webmail.spam_setting where email =? and is_email=1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                spamEmail.add(rs.getString("word"));
            }

            rs.close();

        } catch (Exception ex) {
            System.out.println("SpamSettingDatabaseHandler.getSpamSettingData Error : " + ex);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookmarkMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookmarkMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void insertSpamCommand(String userid, String word, String isEmail) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "INSERT INTO `webmail`.`spam_setting` (`email`, `word`, `is_email`) VALUES (?,?,?)";
            pstmt = conn.prepareStatement(sql);
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
            getSpamSettingData(userid);
        } catch (Exception ex) {
            System.out.println("SpamSettingDatabaseHandler.insertSpamCommand error : " + ex);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookmarkMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookmarkMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deleteSpamCommand(String userid, String word, int isEmail) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "DELETE FROM `webmail`.`spam_setting` WHERE (`email` = ?) and (`word` = ?) and (`is_email` = ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);
            pstmt.setString(2, word);
            pstmt.setInt(3, isEmail);

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            getSpamSettingData(userid);
        } catch (Exception ex) {
            System.out.println("SpamSettingDatabaseHandler.deleteSpamCommand error : " + ex);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookmarkMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookmarkMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
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
