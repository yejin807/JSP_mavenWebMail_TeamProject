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

/**
 *
 * @author gleyd
 */
public class BookmarkMessageAgent extends MessageAgent {

    private static BookmarkMessageAgent uniqueInstance = new BookmarkMessageAgent();
    private static String userid = null;
    private boolean needInitialize = true;

    public String getUserid() {
        System.out.println("BookmarkMessageAgent needInitialize setting =" + needInitialize);
        return userid;
    }

    private BookmarkMessageAgent() {
    }

    public static BookmarkMessageAgent getInstance(String userid) {
        BookmarkMessageAgent.userid = userid;
        return uniqueInstance;
    }

    public static BookmarkMessageAgent getInstance() {
        return uniqueInstance;
    }

    protected boolean getMsgIdListFromDB() {

        boolean status = false;
        System.out.println("BookmarkMessageAgent.SetMsgId에서 msgId Array생성 시도.");

        //만약 유저아이디 값이 설정이 안되어있다면 return false;
        if (isUserIdNull()) {
            System.out.println("BookmarkMessageAgent.SetMsgId에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);
            return status;
        }

        try {
            super.setNeedUpdate(false);
            super.resetMsgIdList();
            System.out.println("BookmarkMessageAgent.SetMsgId에서 msgId 초기화 후 새 Array생성 시도.");

            Class.forName(CommandType.JDBCDRIVER);
            Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "select msgid from webmail.bookmark_list where email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { 
                int buf_msgid = rs.getInt("msgid");
                super.addMsgId(buf_msgid);
            }

            rs.close();
            pstmt.close();
            conn.close();

            System.out.println("BookmarkMessageAgent.SetMsgId에서 msgId Array생성 성공. 생성된 MsgID크기=" + super.getMsgIdList().size());

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.setMsgIdList Error : " + ex);
        }
        return status;
    }

    public ArrayList<Message> getMessageList(Message[] messages) {
        ArrayList<Message> bookmarkedMessages = null;

        try {
            //처음실행이면 MsgIdList초기화.
            if (needInitialize) {
                getMsgIdListFromDB();
                needInitialize = false;
            }

            if (super.isNeedUpdate()) {     //북마크 메시지의 업데이트가 필요하면
                if (getMsgIdListFromDB()) {               //북마크 메시지 리스트를 세팅하는것이 실패했으면
                    bookmarkedMessages = filter(messages, super.getMsgIdList());
                    return bookmarkedMessages;
                } 
            } else {                         //북마크 메시지의 업데이트가 필요없으면
                bookmarkedMessages = filter(messages, super.getMsgIdList());
                return bookmarkedMessages;
            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.getMessageList Error" + ex);
        }
        return bookmarkedMessages;
    }

    protected ArrayList<Message> filter(Message[] messages, ArrayList<Integer> msgIdList) {
        ArrayList<Message> bookmarkedMessages = new ArrayList<Message>();
        for (int i = 0; i < msgIdList.size(); i++) {
            bookmarkedMessages.add(messages[msgIdList.get(i) - 1]);
        }
        System.out.println("end filterling");

        return bookmarkedMessages;
    }

    protected boolean insertMsgId(int msgid) {
        boolean status = false;

        try {

            if (isUserIdNull()) {
                return status;
            }
            Class.forName(CommandType.JDBCDRIVER);
            Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "INSERT INTO `webmail`.`bookmark_list` (`email`, `msgid`) VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
                pstmt.setInt(2, msgid);
            }
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

            status = true;
            return status;
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.insertMsgId error : " + ex);
        }
        return status;
    }

    protected boolean deleteMsgId(int msgid) {
        boolean status = false;

        if (isUserIdNull()) {
            return status;
        }

        try {
            Class.forName(CommandType.JDBCDRIVER);
            Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "DELETE FROM `webmail`.`bookmark_list` WHERE (`email` = ?) and (`msgid` = ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
                pstmt.setInt(2, msgid);
            }
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            //sql문 완성

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.deleteMsgId error : " + ex);
        }
        return status;
    }

    public boolean addMessage(int msgid) {
        boolean status = false;

        try {
            if (!super.getMsgIdList().contains(Integer.valueOf(msgid))) {        //msgIdList에 추가된 적 없는 메시지 번호이면
                super.addMsgId(msgid);
                status = insertMsgId(msgid);
                super.setNeedUpdate(true);
                return status;
            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.addMessage error : " + ex);
        }
        return status;

    }

    public boolean removeMessage(int msgid) {
        boolean status = false;
        try {
            if (super.getMsgIdList().contains(Integer.valueOf(msgid))) {           //msgIdList에 존재하는 메시지번호이면
                super.removeMsgId(msgid);
                status = deleteMsgId(msgid);
                super.setNeedUpdate(true);
                status = true;
                return status;
            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.removeMessage error : " + ex);
        }
        return status;

    }

    private boolean isUserIdNull() {
        if (userid == null || userid.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public void updateMsgId(int deletedMsgid) {
        for (int i = 0; i > getMsgIdSize(); i++) {
            if (getMsgIdValue(i) > deletedMsgid) {
                updateMsgId(i, getMsgIdValue(i) - 1);
            } 
        }

        updateBookmarkListDB(deletedMsgid);
    }

    private boolean updateBookmarkListDB(int deletedMsgId) {
        boolean status = false;
        if (isUserIdNull()) {
            System.out.println("BookmarkMessageAgent.updateBookmarkListDB 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);
            return status;
        }

        try {
            Class.forName(CommandType.JDBCDRIVER);
            Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "update webmail.bookmark_list set msgid=msgid-1 where email=? and msgid>?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
                pstmt.setInt(2, deletedMsgId);
            }
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.updateBookmarkListDB error : " + ex);
        }
        return status;
    }

}//end BookmarkMessageAgent