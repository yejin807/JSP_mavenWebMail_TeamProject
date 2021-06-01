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
import java.util.ArrayList;
import javax.mail.Message;
import java.sql.SQLException;

/**
 *
 * @author 배민정 데이터 베이스에 선택한 메시지 추가 newMsg
 */
public class VinMessageHandler {

    private ArrayList<Integer> VinMessageMsgid = new ArrayList<Integer>();
    private String userid = null;
    private Message newMsg = null;
    private String send_person;
    private String m_title;

    public VinMessageHandler() {

    }

    public VinMessageHandler(String send_person, String m_title) {
        this.send_person = send_person;
        this.m_title = m_title;
    }

    public VinMessageHandler(Message newMsg, String userid) {

        this.newMsg = newMsg;
        this.userid = userid;

    }

    public void addMessageBin() {
        try {
            MessageParser messageparser = new MessageParser(newMsg, userid);
            messageparser.parse(false);
            String sql = "insert into goto_bin.bin ( send_person, send_date, m_title) VALUES (?,?,?)";

            Class.forName(CommandType.JDBCDRIVER);
            try (Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, "12345*");
                    PreparedStatement pstmt = conn.prepareStatement(sql);) {

            if (userid != null || !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, messageparser.getFromAddress()); //보낸사람
                pstmt.setString(2, messageparser.getSentDate()); //보낸날짜
                pstmt.setString(3, messageparser.getSubject()); //제목
            }
            pstmt.executeUpdate();

            }
        } catch (Exception ex) { //에러띄움
            System.out.println("VinMessageHandler.AddMessageBin error : " + ex);
        }
    }

    public boolean deleteVinMessage(String send_person, String m_title) throws ClassNotFoundException, SQLException {
        boolean status = false;
        try {
            Class.forName(CommandType.JDBCDRIVER);
            Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, "12345*");
            String sql = "DELETE FROM `goto_bin`.`bin` WHERE (`send_person` = ?) and (`m_title` = ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, send_person);
            pstmt.setString(2, m_title);

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            //sql문 완성
        } catch (Exception ex) {
            System.out.println("deletevinmessage error " + ex);

        }
        return status;

    }
}
