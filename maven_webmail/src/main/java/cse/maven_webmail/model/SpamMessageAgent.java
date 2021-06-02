/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import cse.maven_webmail.control.CommandType;
import cse.maven_webmail.control.SpamSettingDatabaseHandler;
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
public class SpamMessageAgent extends MessageAgent {

    private static SpamMessageAgent uniqueInstance = new SpamMessageAgent();
    private static String userid = null;
    private boolean needInitialize = true;

    public String getUserid() {
        return userid;
    }

    private SpamMessageAgent() {
    }

    public static SpamMessageAgent getInstance() {
        return uniqueInstance;
    }

    public static SpamMessageAgent getInstance(String userid) {
        SpamMessageAgent.userid = userid;
        System.out.println("SpamMessageAgent userid setting =" + SpamMessageAgent.userid);
        return uniqueInstance;
    }

    //DB에 저장된 메시지아이디 가져오기.
    protected boolean getMsgIdListFromDB() {
        boolean status = false;

        if (isUserIdNull()) {
            return status;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            super.setNeedUpdate(false);
            super.resetMsgIdList();
            System.out.println("SpamMessageAgent.SetMsgId에서 msgId 초기화 후 새 Array생성 시도.");

            Class.forName(CommandType.JDBCDRIVER);
             conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "select msgid from webmail.spam_list where email = ?";
             pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int buf_msgid = rs.getInt("msgid");
                super.addMsgId(buf_msgid);
            }

            rs.close();
            pstmt.close();
            conn.close();

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.setMsgIdList Error : " + ex);
        }finally {
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

        return status;
    }

    public ArrayList<Message> getMessageList(Message[] messages) {
        ArrayList<Message> spammedMessages = null;

        try {
            //처음실행이면 DB에 저장된 MsgIdList읽어들이기
            if (needInitialize) {
                getMsgIdListFromDB();
                needInitialize = false;
            }

            if (super.isNeedUpdate()) {     //스팸 메시지의 업데이트가 필요하면
                updateSpamMessageList(messages);

                super.setNeedUpdate(false);
                if (getMsgIdListFromDB()) {               //스팸 메시지 리스트를 세팅성공시
                    spammedMessages = filter(messages, super.getMsgIdList());
                    return spammedMessages;
                }
            } else {                         //스팸 메시지의 업데이트가 필요없으면
                spammedMessages = filter(messages, super.getMsgIdList());
                return spammedMessages;
            }
        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.getMessageList Error" + ex);
        }

        return spammedMessages;
    }
    //public abstract void getMessageList(Message[] messages);

    protected ArrayList<Message> filter(Message[] messages, ArrayList<Integer> msgIdList) {

        ArrayList<Message> spammedMessages = new ArrayList<Message>();
        for (int i = 0; i < msgIdList.size(); i++) {
            spammedMessages.add(messages[msgIdList.get(i) - 1]);
        }

        return spammedMessages;
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

        updateSpamListDB(deletedMsgid);
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
            System.out.println("SpamMessageAgent.removeMessage error : " + ex);
        }
        return status;

    }

    protected boolean insertMsgId(int msgid) {
        boolean status = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            if (isUserIdNull()) {
                return status;
            }
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "INSERT INTO `webmail`.`spam_list` (`email`, `msgid`) VALUES (?,?)";
            pstmt = conn.prepareStatement(sql);
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
            System.out.println("SpamMessageAgent.insertMsgId error : " + ex);
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
        return status;
    }

    protected boolean deleteMsgId(int msgid) {
        boolean status = false;

        if (isUserIdNull()) {
            return status;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "DELETE FROM `webmail`.`spam_list` WHERE (`email` = ?) and (`msgid` = ?)";
            pstmt = conn.prepareStatement(sql);
            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
                pstmt.setInt(2, msgid);
            }
            pstmt.executeUpdate();

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.deleteMsgId error : " + ex);
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
        return status;
    }

    private boolean updateSpamListDB(int deletedMsgId) {
        boolean status = false;
        if (isUserIdNull()) {
            return status;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "update webmail.spam_list set msgid=msgid-1 where email=? and msgid>?";
            pstmt = conn.prepareStatement(sql);
            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
                pstmt.setInt(2, deletedMsgId);
            }
            pstmt.executeUpdate();

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.updateSpamListDB error : " + ex);
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
        return status;
    }

    private boolean resetSpamDB() {
        boolean status = false;
        if (isUserIdNull()) {
            return status;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "delete from webmail.spam_list where email=?";
            pstmt = conn.prepareStatement(sql);
            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
            }
            pstmt.executeUpdate();

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.updateSpamListDB error : " + ex);
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
        return status;
    }

    public void updateSpamMessageList(Message[] messages) {
        SpamSettingDatabaseHandler spamSettingData = new SpamSettingDatabaseHandler();

        try {
            super.printMsgIdInfo();
            super.resetMsgIdList();
            resetSpamDB();

            System.out.println("reset spam DB, spamListID");
            spamSettingData.getSpamSettingData(userid);
            System.out.println("SpamMessageAgent.updateSpamMessageList.userid : " + userid);

            ArrayList<String> spamWord = spamSettingData.getSpamWord();
            ArrayList<String> spamEmail = spamSettingData.getSpamEmail();

            //전체 메시지에 대한 
            for (int i = 0; i < messages.length; i++) {
                System.out.println(i + "번 메일 스팸 체크@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                for (int j = 0; j < spamWord.size(); j++) {
                    if (messages[i].getSubject().contains(spamWord.get(j))) {//i번째 메일 제목에 j번째 스팸단어가 포함되어 있다Pop3Agent.filterSpamMessage.spamWord :" + spamWord.get(j) + " ㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅㅅ팸ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")면,
                        insertMsgId(messages[i].getMessageNumber());
                        System.out.println(i + "번 메일 제목 : " + messages[i].getSubject() + " 스팸단어 : " + spamWord.get(j) + " 스팸입니다.");
                    } else {
                        System.out.println(i + "번 메일 제목 : " + messages[i].getSubject() + " 스팸단어 : " + spamWord.get(j) + " 스팸아닙니다.");
                    }
                } //end for spamWord

                for (int j = 0; j < spamEmail.size(); j++) {
                    if ((messages[i].getFrom()[0].toString()).equals(spamEmail.get(j))) { //spamEmail에 저장된 이메일에게서 메일이 왔었다면.
                        System.out.println(i + "번 메일 이메일 : " + (messages[i].getFrom()[0].toString()) + " 스팸 이메일 : " + spamEmail.get(j) + " 스팸입니다.");

                        insertMsgId(messages[i].getMessageNumber());

                    } else {
                        System.out.println(i + "번 메일 이메일 : " + (messages[i].getFrom()[0].toString()) + " 스팸이메일 : " + spamEmail.get(j) + " 스팸아닙니다.");
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.updateMsgId Exception " + ex);

        }
    }

    public Message[] getMessagesWithoutSpamMsg(Message[] messages) {
        ArrayList<Message> bufMessages = new ArrayList<Message>();
        Message[] messagesWithoutSpam;

        if (super.isNeedUpdate()) {     //스팸 메시지의 업데이트가 필요하면
            updateSpamMessageList(messages);
            super.setNeedUpdate(false);
            if (getMsgIdListFromDB()) {               //스팸 메시지 리스트를 세팅성공시
                for (int i = 0; i < messages.length; i++) {
                    for (int j = 0; j < super.getMsgIdSize(); j++) {
                        if (messages[i].getMessageNumber() == super.getMsgIdValue(j)) {
                            continue;
                        } else {
                            bufMessages.add(messages[i]);
                        }
                    }
                }

            }
        }
        messagesWithoutSpam = new Message[bufMessages.size()];
        for (int i = 0; i < bufMessages.size(); i++) {
            messagesWithoutSpam[i] = bufMessages.get(i);
            System.out.println("SpamMessage " + Integer.toString(i) + "추가완료");
        }

        return messagesWithoutSpam;
    }

}   //end class
