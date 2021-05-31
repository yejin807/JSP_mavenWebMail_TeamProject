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
        System.out.println("SpamMessageAgent needInitialize setting =" + needInitialize);
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

        System.out.println("SpamMessageAgent.SetMsgId에서 msgId Array생성 시도.");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //만약 유저아이디 값이 설정이 안되어있다면 return fale;
        if (isUserIdNull()) {
            System.out.println("SpamMessageAgent.SetMsgId에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);
            return status;
        }

        try {
            super.setNeedUpdate(false);
            super.resetMsgIdList();
            System.out.println("SpamMessageAgent.SetMsgId에서 msgId 초기화 후 새 Array생성 시도.");

            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "select msgid from webmail.spam_list where email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);

            rs = pstmt.executeQuery();
            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                int buf_msgid = rs.getInt("msgid");	// 컬럼 값 받아오기
                super.addMsgId(buf_msgid);
            }

            System.out.println("SpamMessageAgent.SetMsgId에서 msgId Array생성 성공. 생성된 MsgID크기=" + super.getMsgIdList().size());

            status = true;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.setMsgIdList Error : " + ex);
        } finally {
            try {
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SpamMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            return status;
        }
    }

    public ArrayList<Message> getMessageList(Message[] messages) {
        ArrayList<Message> spammedMessages = null;

        try {
            //처음실행이면 DB에 저장된 MsgIdList읽어들이기
            if (needInitialize) {
                System.out.println("SpamMessageAgent.getMessageList need Initialize");
                System.out.println("SpamMessageAgent.getMessageList userid=" + userid);
                getMsgIdListFromDB();
                needInitialize = false;
            }
            System.out.println("SpamMessageAgent.getMessageList end Initialize");
            System.out.println("SpamMessageAgent.getMessageList userid=" + userid);

            if (super.isNeedUpdate()) {     //스팸 메시지의 업데이트가 필요하면
                System.out.println("SpamMessageAgent.getMessageList isNeedUpdate가 필요합니다. value=." + super.isNeedUpdate());
                updateSpamMessageList(messages);

                super.setNeedUpdate(false);
                if (getMsgIdListFromDB()) {               //스팸 메시지 리스트를 세팅성공시
                    System.out.println("setMsgId성공했음했음..");

                    spammedMessages = filter(messages, super.getMsgIdList());
                    return spammedMessages;
                } else {                            //스팸 메시지들의 리스트를 세팅하는 것이 실패햇으면
                    System.out.println("setMsgI 실패햇씁니다아아ㅏ했음했음..");
                }
            } else {                         //스팸 메시지의 업데이트가 필요없으면
                System.out.println("SpamMessageAgent.getMessageList isNeedUpdate가 필요없습합니다. value=." + super.isNeedUpdate());

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
            System.out.println("spammedMsgid=" + msgIdList.get(i) + " realMessageId=" + messages[msgIdList.get(i) - 1].getMessageNumber());
            spammedMessages.add(messages[msgIdList.get(i) - 1]);
        }
        System.out.println("end filterling");

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
        //    public abstract void updateMsgId(int msgid);
        for (int i = 0; i > getMsgIdSize(); i++) {
            if (getMsgIdValue(i) > deletedMsgid) {
                System.out.println("SpamMessageAgent.updateMsgId index " + i + " update 해야할 값: " + getMsgIdValue(i) + " 업데이트 된 값: " + (getMsgIdValue(i) - 1));
                updateMsgId(i, getMsgIdValue(i) - 1);
            } else {
                System.out.println("SpamMessageAgent.updateMsgId index " + i + " update 안해도됨. " + getMsgIdValue(i));
            }
        } //end for

        boolean isSuccess = updateSpamListDB(deletedMsgid);
    }

    public boolean removeMessage(int msgid) {
        boolean status = false;
        try {
            for (int i = 0; i < super.getMsgIdSize(); i++) {
                System.out.println("SpamMessageAgent.removeMessage 삭제전 msgid = " + super.getMsgIdList().get(i));
            }

            if (super.getMsgIdList().contains(Integer.valueOf(msgid))) {           //msgIdList에 존재하는 메시지번호이면
                super.removeMsgId(msgid);
                status = deleteMsgId(msgid);
                super.setNeedUpdate(true);
                System.out.println("SpamMessageAgent.removeMessage message번호 지웠어요.");
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
                System.out.println("SpamMessageAgent.insertMsgId에서 유저아이디 설정이 안되어있음.");
                System.out.println("userid setting =" + userid);

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
            //sql문 완성

            status = true;
            return status;
        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.insertMsgId error : " + ex);
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SpamMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return status;
    }

    protected boolean deleteMsgId(int msgid) {
        boolean status = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        if (isUserIdNull()) {
            System.out.println("SpamMessageAgent.deleteMsgId에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);

            return status;
        }

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
            //sql문 완성

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.deleteMsgId error : " + ex);
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SpamMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return status;
    }

    private boolean updateSpamListDB(int deletedMsgId) {
        boolean status = false;

        Connection conn = null;
        PreparedStatement pstmt = null;
        if (isUserIdNull()) {
            System.out.println("SpamMessageAgent.updateSpamListDB 에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);

            return status;
        }

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
            //sql문 완성

            status = true;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.updateSpamListDB error : " + ex);
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SpamMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return status;
    }

    private boolean resetSpamDB() {
        boolean status = false;

        Connection conn = null;
        PreparedStatement pstmt = null;
        if (isUserIdNull()) {
            System.out.println("SpamMessageAgent.updateSpamListDB 에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);

            return status;
        }

        try {
            Class.forName(CommandType.JDBCDRIVER);
            conn = DriverManager.getConnection(CommandType.JDBCURL, CommandType.JDBCUSER, CommandType.JDBCPASSWORD);

            String sql = "delete from webmail.spam_list where email=?";
            pstmt = conn.prepareStatement(sql);
            if (userid != null && !(userid.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, userid);
            }
            pstmt.executeUpdate();
            //sql문 완성

            status = true;
            return status;

        } catch (Exception ex) {
            System.out.println("SpamMessageAgent.updateSpamListDB error : " + ex);
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SpamMessageAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return status;
    }

    public void updateSpamMessageList(Message[] messages) {
        SpamSettingDatabaseHandler spamSettingData = new SpamSettingDatabaseHandler();
        boolean overlap = false;

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
                overlap = false;
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
                } //end for spamEmail

            } //end for

            System.out.println("SpamMessageAgent.updateSpamMessageList mid end filtering");
            System.out.println("SpamMessageAgent.updateSpamMessageList bufMessages size : " + Integer.toString(super.getMsgIdSize()));

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
                    }//end msgid for
//if(super.getMsgIdList().contains(bufMessages.get(i).getMessageNumber())){}
                }//end messages for

            }
        }
        //ArrayList<Message> to Message[]
        messagesWithoutSpam = new Message[bufMessages.size()];
        for (int i = 0; i < bufMessages.size(); i++) {
            messagesWithoutSpam[i] = bufMessages.get(i);
            System.out.println("SpamMessage " + Integer.toString(i) + "추가완료");
        }

        return messagesWithoutSpam;
    }

}   //end class
