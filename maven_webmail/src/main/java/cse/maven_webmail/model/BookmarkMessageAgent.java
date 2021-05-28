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
        System.out.println("BookmarkMessageAgent userid setting =" + BookmarkMessageAgent.userid);

        return uniqueInstance;
    }

    public static BookmarkMessageAgent getInstance() {
        return uniqueInstance;
    }

    protected boolean setMsgIdList() {

        boolean status = false;
        System.out.println("BookmarkMessageAgent.SetMsgId에서 msgId Array생성 시도.");

        //만약 유저아이디 값이 설정이 안되어있다면 return fale;
        if (isUserIdNull()) {
            System.out.println("BookmarkMessageAgent.SetMsgId에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);
            return status;
        }

        try {
            super.setNeedUpdate(false);
            super.resetMsgIdList();
            System.out.println("BookmarkMessageAgent.SetMsgId에서 msgId 초기화 후 새 Array생성 시도.");

            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "select msgid from webmail.bookmark_list where email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                int buf_msgid = rs.getInt("msgid");	// 컬럼 값 받아오기
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

    public ArrayList<Message> getMessageList(ArrayList<Message> messages) {
        ArrayList<Message> bookmarkedMessages = null;

        try {
            //처음실행이면 MsgIdList초기화.
            if (needInitialize) {
                System.out.println("BookmarkmessageAgent.getMessageList need Initialize");
                System.out.println("BookmarkmessageAgent.getMessageList userid=" + userid);
                setMsgIdList();
                needInitialize = false;
            }
            System.out.println("BookmarkmessageAgent.getMessageList end Initialize");
            System.out.println("BookmarkmessageAgent.getMessageList userid=" + userid);

            if (super.isNeedUpdate()) {     //북마크 메시지의 업데이트가 필요하면
                System.out.println("BookmarkMessageAgent.getMessageList isNeedUpdate가 필요합니다. value=." + super.isNeedUpdate());

                if (setMsgIdList()) {               //북마크 메시지 리스트를 세팅하는것이 실패했으면
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

    protected ArrayList<Message> filter(ArrayList<Message> messages, ArrayList<Integer> msgIdList) {
        ArrayList<Message> bookmarkedMessages = new ArrayList<Message>();
        for (int i = 0; i < msgIdList.size(); i++) {
            System.out.println("bookmarkedMsgid=" + msgIdList.get(i) + " realMessageId=" + messages.get(msgIdList.get(i)-1));
            bookmarkedMessages.add(messages.get(msgIdList.get(i) - 1));
            System.out.println("bookmarkedMsgid=" + msgIdList.get(i) + " realMessageId=" + messages.get(msgIdList.get(i)-1));
        }
        System.out.println("end filterling");

        return bookmarkedMessages;
    }

    protected boolean insertMsgId(int msgid) {
        boolean status = false;

        try {

            if (isUserIdNull()) {
                System.out.println("BookmarkMessageAgent.insertMsgId에서 유저아이디 설정이 안되어있음.");
                System.out.println("userid setting =" + userid);

                return status;
            }
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "INSERT INTO `webmail`.`bookmark_list` (`email`, `msgid`) VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (userid != null || !(userid.equals(""))) { //email 값이 null이 아니면.
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
            System.out.println("BookmarkMessageAgent.insertMsgId error : " + ex);
        }
        return status;
    }

    protected boolean deleteMsgId(int msgid) {
        boolean status = false;

        if (isUserIdNull()) {
            System.out.println("BookmarkMessageAgent.deleteMsgId에서 유저아이디 설정이 안되어있음.");
            System.out.println("userid setting =" + userid);

            return status;
        }

        try {
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "DELETE FROM `webmail`.`bookmark_list` WHERE (`email` = ?) and (`msgid` = ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (userid != null || !(userid.equals(""))) { //email 값이 null이 아니면.
                if (userid != null || !(userid.equals(""))) { //email 값이 null이 아니면.
                    pstmt.setString(1, userid);
                    pstmt.setInt(2, msgid);
                }
                pstmt.executeUpdate();
                pstmt.close();
                conn.close();
                //sql문 완성

                status = true;
                return status;
            }
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
}//end BookmarkMessageAgent

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/*
    public ArrayList<Integer> getMessageList(String userid) {
        bookmarkMsgId = null;
        bookmarkMsgId = new ArrayList<Integer>();
        setUserid(userid);
        System.out.println("BookmarkMessageAgent.getBookmarkMessageList : ");

        bookmarkMsgId = readBookmarkMsgData();
        return bookmarkMsgId;
    }
 */
 /*
    private ArrayList<Integer> readBookmarkMsgData() {
        try {
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "select msgid from webmail.bookmark_list where email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                int buf_msgid = rs.getInt("msgid");	// 컬럼 값 받아오기
                bookmarkMsgId.add(buf_msgid);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.readBookmarkMsgData Error : " + ex);
        } finally {
            return bookmarkMsgId;
        }
    }*/
 /*
    public String showBookmarkingList() {
        String result = "";
        for (int list : bookmarkMsgId) {
            result += Integer.toString(list) + " ";
        }
        result += "<br><br><p> <a href=\"bookmarked_mail.jsp\"> 즐겨찾기함 </a> </p>";
        return result;
        /* test해볼것.
                StringBuffer str;
        for (int list : bookmarkMsgID) {
            str.append(Integer.toString(list) + " ");
        }
        str.append("<br><br><p> <a href=\"main_menu.jsp\"> 즐겨찾기함 </a> </p>");
        return str;
 */
 /*    }
}*/

 /*
    buffer.append(i + " : " +messages[i].getFlags().contains("bookmarked") +"<br>");
    
     //if (!(messages[i].getFlags().contains("bookmarked"))) {
    
                 Flags bookmarkFlag = new Flags("bookmarked");
            msg.setFlags(bookmarkFlag, true );
    
 */
 /*
    * TODO : bookmark할 flag설정
     
    public boolean bookmarkMessage(int msgid) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message msg = folder.getMessage(msgid);

            //set bookmark
            Flags bookmarkFlag = new Flags("bookmarked");
            msg.setFlags(bookmarkFlag, true);

            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            System.out.println("bookmarkMessage() error: " + ex);
        } finally {
            return status;
        }
    }
 */
