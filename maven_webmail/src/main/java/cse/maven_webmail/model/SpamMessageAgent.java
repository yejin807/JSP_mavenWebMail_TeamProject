/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import cse.maven_webmail.control.CommandType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author gleyd
 */
public class SpamMessageAgent {

    private static SpamMessageAgent uniqueInstance = new SpamMessageAgent();
    private ArrayList<Integer> spamMsgId = new ArrayList<Integer>();
    private String email = null;

    private SpamMessageAgent() {
    }

    public static SpamMessageAgent getInstance() {
        return uniqueInstance;
    }

    public ArrayList<Integer> getspamMsgID(String userid) {
        spamMsgId = null;
        spamMsgId = new ArrayList<Integer>();
        setEmail(userid);
        System.out.println("SpamMessageAgent.getspamMsgID : ");

        spamMsgId = readspamMsgIdData();
        return spamMsgId;
    }

    private ArrayList<Integer> readspamMsgIdData() {
        try {
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "select msgid from webmail.spam_list where email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                int buf_msgid = rs.getInt("msgid");	// 컬럼 값 받아오기
                spamMsgId.add(buf_msgid);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.readspamMsgIdData Error : " + ex);
        } finally {
            return spamMsgId;
        }
    }

    public String showSpmaMsgIdList() {
        String result = "";
        for (int list : spamMsgId) {
            result += Integer.toString(list) + " ";
        }
        result += "<br><br><p> <a href=\"main_menu.jsp\"> 메인메뉴 </a> </p>";
        return result;
        /* test해볼것.
                StringBuffer str;
        for (int list : bookmarkMsgID) {
            str.append(Integer.toString(list) + " ");
        }
        str.append("<br><br><p> <a href=\"main_menu.jsp\"> 즐겨찾기함 </a> </p>");
        return str;
         */
    }

    private void setEmail(String email) {
        this.email = email;
    }
}
