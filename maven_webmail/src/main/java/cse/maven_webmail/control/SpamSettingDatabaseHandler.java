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
import cse.maven_webmail.model.SpamMessageAgent;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * TODO : CommandType으로 전부 교체
 *
 * @author gleyd
 */
@WebServlet(name = "SpamDatabaseHandler", urlPatterns = {"/spam_database.do"})
public class SpamSettingDatabaseHandler extends HttpServlet {

    private ArrayList<String> spamWord = null;
    private ArrayList<String> spamUserId = null;

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
        String word = request.getParameter("word").trim();
        String spamword = request.getParameter("spamword");
        String isUserId = request.getParameter("isEmail");
        String sql = "null이에요.";
        spamMessageAgent = SpamMessageAgent.getInstance(userid);

        PrintWriter out = response.getWriter();

        try {

            if (!(word == null) && !(word.length() == 0)) {
                insertSpamCommand(userid, word, isUserId);
                spamMessageAgent.setNeedUpdate(true);
                System.out.println("추가된문자열=" + word + "=");
                response.sendRedirect("spam_settings.jsp");
            } else {
                out.println("<script>alert('스팸처리할 단어나 이메일을 입력하세요!');location.href='spam_settings.jsp'</script>");

            }
            //스팸단어 삭제 기능
            if ((request.getParameter("command") != null) && (request.getParameter("spamword") != null)) {
                //int select = Integer.parseInt((String) request.getParameter("delete"));
                int select = Integer.parseInt((String) request.getParameter("command"));

                switch (select) {
                    case CommandType.DELETE_SPAM_WORD_COMMAND:
                        deleteSpamCommand(userid, spamword, CommandType.IS_USERID_FALSE);
                        spamMessageAgent.setNeedUpdate(true);
                        response.sendRedirect("spam_settings.jsp");
                        break;
                    case CommandType.DELETE_SPAM_USERID_COMMAND:
                        deleteSpamCommand(userid, spamword, CommandType.IS_USERID_TRUE);
                        spamMessageAgent.setNeedUpdate(true);
                        response.sendRedirect("spam_settings.jsp");
                        break;
                }
                request.setAttribute("command", null);
            } //end 스팸단어 조건 
        } catch (Exception ex) {// end try
            out.println("exception : " + ex);
        }
    }

    public ArrayList<String> getSpamWord() {
        return spamWord;
    }

    public ArrayList<String> getSpamUserId() {
        return spamUserId;
    }

    public String getSpamSettingData(String userid) {
        String result = "";
        this.userid = userid;
        spamWord = new ArrayList<String>();
        spamUserId = new ArrayList<String>();
        String sql = "select word from webmail.spam_setting where userid =? and is_email=0";

        try (Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery();) {
            Class.forName(CommandType.JDBCDRIVER);

            //spam단어 읽어오기
            pstmt.setString(1, userid);

            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                spamWord.add(rs.getString("word"));
                result += "<br><br>getSpamSettingData , word : " + rs.getString("word");
            }
            System.out.println("getSpamSettingData.userid : " + userid);
            System.out.println("getSpamSettingData : " + result);
        } catch (Exception ex) {
            System.out.println("SpamSettingDatabaseHandler.getSpamSettingData Error : " + ex);
        }
        sql = "select word from webmail.spam_setting where userid =? and is_email=1";

        try (Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery();) {
            //spam이메일 읽어오기
            //spam단어 읽어오기
            pstmt.setString(1, userid);

            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                spamUserId.add(rs.getString("word"));
                result += "<br><br>getSpamSettingData , userid : " + rs.getString("word");
            }
            System.out.println("getSpamSettingData.userid : " + userid);
            System.out.println("getSpamSettingData : " + result);

        } catch (Exception ex) {
            System.out.println("SpamSettingDatabaseHandler.getSpamSettingData Error : " + ex);
        }
        return result;
    }

    private void insertSpamCommand(String userid, String word, String isUserId) throws ClassNotFoundException, SQLException {
        
        String sql = "INSERT INTO `webmail`.`spam_setting` (`userid`, `word`, `is_email`) VALUES (?,?,?)";

        try {Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            Class.forName(CommandType.JDBCDRIVER);

            if (isUserId == null) { //스팸 단어를 추가하는 것이면
                pstmt.setString(1, userid);
                pstmt.setString(2, word);
                pstmt.setInt(3, 0);
            } else {
                pstmt.setString(1, userid);
                pstmt.setString(2, word);
                pstmt.setInt(3, 1);
            }
            pstmt.executeUpdate();

            //sql문 완성
            getSpamSettingData(userid);
        } catch(Exception ex){
            
        }
    }

    private void deleteSpamCommand(String userid, String word, int isEmail) throws ClassNotFoundException, SQLException {

        String sql = "DELETE FROM `webmail`.`spam_setting` WHERE (`email` = ?) and (`word` = ?) and (`is_email` = ?)";

        try {Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            Class.forName(CommandType.JDBCDRIVER);

            /*        infoHTML(out, email);
        infoHTML(out, word);
        infoHTML(out, Integer.toString(isEmail)); */

            pstmt.setString(1, userid);
            pstmt.setString(2, word);
            pstmt.setInt(3, isEmail);

            pstmt.executeUpdate();
            //sql문 완성
            getSpamSettingData(userid);
        } catch(Exception ex) {
            
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
