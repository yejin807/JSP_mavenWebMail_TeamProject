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
import javax.mail.Message;
import cse.maven_webmail.model.MessageParser;
import java.sql.SQLException;

/**
 *
 * @author 배민정 데이터 베이스에 선택한 메시지 추가 newMsg
 */
public class VinMessageHandler {

    private ArrayList<Integer> VinMessageMsgid = new ArrayList<Integer>();
    private String userid = null;
    private Message newMsg = null;

    public VinMessageHandler() {

    }

    public VinMessageHandler(Message newMsg, String userid) {

        this.newMsg = newMsg;
        this.userid = userid;

    }
    public void addMessageBin(){ 
    try{
    MessageParser messageparser = new MessageParser(newMsg, userid);
    messageparser.parse(false); 
    
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, "12345*");
            //집어넣을값 -  보낸사람, 보낸날짜, 제목
            String sql = "insert into goto_bin.bin ( send_person, send_date, m_title) VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
           
            if (userid != null || !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, messageparser.getFromAddress()); //보낸사람
                pstmt.setString(2, messageparser.getSentDate()); //보낸날짜
                pstmt.setString(3, messageparser.getSubject()); //제목
            }
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
            //sql문 완성

    } catch(Exception ex) { //ㅇㅔ러띄움
            System.out.println("VinMessageHandler.AddMessageBin error : " + ex);
        }
    
    
    }

     public void deleteVinMessage(String send_person, String m_title, String send_date) throws ClassNotFoundException, SQLException {
        Class.forName(CommandType.JdbcDriver);
        Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

        String sql = "DELETE FROM `goto_bin`.`bin` WHERE (`send_person` = ?) and (`m_title` = ?) and (`send_date` = ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, send_person);
        pstmt.setString(2, m_title);
        pstmt.setString(3, send_date);

        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
        //sql문 완성
    }
    
}
