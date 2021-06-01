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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        System.out.println("BookmarkMessageAgent userid setting =" + BookmarkMessageAgent.userid);

        return uniqueInstance;
    }

    public static BookmarkMessageAgent getInstance() {
        return uniqueInstance;
    }

    protected boolean getMsgIdListFromDB() {

        boolean status = false;
        System.out.println("BookmarkMessageAgent.SetMsgId에서 msgId Array생성 시도.");
        String sql = "select msgid from webmail.bookmark_list where email = ?";

        //만약 유저아이디 값이 설정이 안되어있다면 return fale;
        if (isUserIdNull()) {
            System.out.println("BookmarkMessageAgent.SetMsgId에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);
            return status;
        }
        try (Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD); 
                PreparedStatement pstmt = conn.prepareStatement(sql); 
                ResultSet rs = pstmt.executeQuery();) {
            super.setNeedUpdate(false);
            super.resetMsgIdList();
            System.out.println("BookmarkMessageAgent.SetMsgId에서 msgId 초기화 후 새 Array생성 시도.");

            Class.forName(CommandType.JDBCDRIVER);

            pstmt.setString(1, userid);

            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                int buf_msgid = rs.getInt("msgid");	// 컬럼 값 받아오기
                super.addMsgId(buf_msgid);
            }

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
                System.out.println("BookmarkmessageAgent.getMessageList need Initialize");
                System.out.println("BookmarkmessageAgent.getMessageList userid=" + userid);
                getMsgIdListFromDB();
                needInitialize = false;
            }
            System.out.println("BookmarkmessageAgent.getMessageList end Initialize");
            System.out.println("BookmarkmessageAgent.getMessageList userid=" + userid);

            if (super.isNeedUpdate()) {     //북마크 메시지의 업데이트가 필요하면
                System.out.println("BookmarkMessageAgent.getMessageList isNeedUpdate가 필요합니다. value=." + super.isNeedUpdate());

                if (getMsgIdListFromDB()) {               //북마크 메시지 리스트를 세팅하는것이 실패했으면
                    System.out.println("setMsgId성공했음했음..");

                    bookmarkedMessages = filter(messages, super.getMsgIdList());
                    return bookmarkedMessages;
                } else {                            //북마크 메시지들의 리스트를 세팅하는 것이 성공햇으면
                    System.out.println("setMsgI 실패햇씁니다아아ㅏ했음했음..");
                }
            } else {                         //북마크 메시지의 업데이트가 필요없으면
                System.out.println("BookmarkMessageAgent.getMessageList isNeedUpdate가 필요없습합니다. value=." + super.isNeedUpdate());

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
            System.out.println("bookmarkedMsgid=" + msgIdList.get(i) + " realMessageId=" + messages[msgIdList.get(i) - 1]);
            bookmarkedMessages.add(messages[msgIdList.get(i) - 1]);
            System.out.println("bookmarkedMsgid=" + msgIdList.get(i) + " realMessageId=" + messages[msgIdList.get(i) - 1]);
        }
        System.out.println("end filterling");

        return bookmarkedMessages;
    }

    protected boolean insertMsgId(int msgid) {
        boolean status = false;
            String sql = "INSERT INTO `webmail`.`bookmark_list` (`email`, `msgid`) VALUES (?,?)";

try (Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql);){
            if (isUserIdNull()) {
                System.out.println("BookmarkMessageAgent.insertMsgId에서 유저아이디 설정이 안되어있음.");
                System.out.println("userid setting =" + userid);

                return status;
            }
            Class.forName(CommandType.JDBCDRIVER);

            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
                pstmt.setInt(2, msgid);
            }
            pstmt.executeUpdate();
            //sql문 완성

            status = true;
            return status;
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.insertMsgId error : " + ex);
        } 
        return status;
    }

    protected boolean deleteMsgId(int msgid) {
        boolean status = false;
        String sql = "DELETE FROM `webmail`.`bookmark_list` WHERE (`email` = ?) and (`msgid` = ?)";

        if (isUserIdNull()) {
            System.out.println("BookmarkMessageAgent.deleteMsgId에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);

            return status;
        }

        try {Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            Class.forName(CommandType.JDBCDRIVER);

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

    //todo 이미 추가된 메시지입니다. 메시지 넘겨주기.
    public boolean addMessage(int msgid) {
        boolean status = false;

        try {
            if (!super.getMsgIdList().contains(Integer.valueOf(msgid))) {        //msgIdList에 추가된 적 없는 메시지 번호이면
                super.addMsgId(msgid);
                status = insertMsgId(msgid);
                super.setNeedUpdate(true);
                return status;
            }
            /*else {                                                        //msgIdList에 추가된 적 있는 메시지 번호이면
                status = true;
                return status;

            }*/
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
                System.out.println("BookmarkMessageAgent.removeMessage message번호 지웠어요.");
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
        //    public abstract void updateMsgId(int msgid);
        for (int i = 0; i > getMsgIdSize(); i++) {
            if (getMsgIdValue(i) > deletedMsgid) {
                System.out.println("BookmarkMessageAgent.updateMsgId index " + i + " update 해야할 값: " + getMsgIdValue(i) + " 업데이트 된 값: " + (getMsgIdValue(i) - 1));
                updateMsgId(i, getMsgIdValue(i) - 1);
            } else {
                System.out.println("BookmarkMessageAgent.updateMsgId index " + i + " update 안해도됨. " + getMsgIdValue(i));
            }
        } //end for

        boolean isSuccess = updateBookmarkListDB(deletedMsgid);
    }

    private boolean updateBookmarkListDB(int deletedMsgId) {
        boolean status = false;
        String sql = "update webmail.bookmark_list set msgid=msgid-1 where email=? and msgid>?;";
        if (isUserIdNull()) {
            System.out.println("BookmarkMessageAgent.updateBookmarkListDB 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);

            return status;
        }

        try {Connection conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            Class.forName(CommandType.JDBCDRIVER);

            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
                pstmt.setInt(2, deletedMsgId);
            }
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            //sql문 완성

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.updateBookmarkListDB error : " + ex);
        } 
        return status;
    }

}//end BookmarkMessageAgent
