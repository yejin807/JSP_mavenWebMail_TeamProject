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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleyd
 */
public class BookmarkMessageAgent {

    private final static Logger LOG = Logger.getGlobal();

    private static BookmarkMessageAgent uniqueInstance = new BookmarkMessageAgent();
    private ArrayList<Integer> bookmarkMsgID = new ArrayList<Integer>();
    private String email = null;

    private BookmarkMessageAgent() {
    }

    public static BookmarkMessageAgent getInstance() {
        return uniqueInstance;
    }

    public ArrayList<Integer> getBookmarkMessageList(String userid) {
        bookmarkMsgID = null;
        setEmail(userid);

        bookmarkMsgID = readBookmarkMsgData();
        return bookmarkMsgID;
    }

    public boolean addBookmarking(String userid, int msgid) {
        boolean status = false;
        String result = "";

        try {
            setEmail(userid);
            if (!bookmarkMsgID.contains(Integer.valueOf(msgid))) {
                bookmarkMsgID.add(msgid);
                result = insertBookmarkMsgID(msgid);
                status = true;

                System.out.println(result);

            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.addBookmarkMessage error : " + ex);
        } finally {
            return status;
        }
    }

    public boolean cancelBookmarking(String userid, int msgid) {
        boolean status = false;
        String result = "";

        try {
            setEmail(userid);
            if (bookmarkMsgID.contains(Integer.valueOf(msgid))) {
                bookmarkMsgID.remove(Integer.valueOf(msgid));
                result = deleteBookmarkMsgID(msgid);
                status = true;

                System.out.println(result);
            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.cancelBookmarking error : " + ex);
        } finally {
            return status;
        }
    }

    public String showBookmarkingList() {
        String result = "";
        for (int list : bookmarkMsgID) {
            result += Integer.toString(list) + " ";
        }
        result += "<br><br><p> <a href=\"main_menu.jsp\"> 즐겨찾기함 </a> </p>";
        return result;
        /*
                StringBuffer str;
        for (int list : bookmarkMsgID) {
            str.append(Integer.toString(list) + " ");
        }
        str.append("<br><br><p> <a href=\"main_menu.jsp\"> 즐겨찾기함 </a> </p>");
        return str;
        */
        
    }

    private String insertBookmarkMsgID(int msgid) {
        String result = null;

        try {
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "INSERT INTO `webmail`.`bookmark_list` (`email`, `msgid`) VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (email != null || !(email.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, email);
                pstmt.setInt(2, msgid);
            }
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
            //sql문 완성

        } catch (Exception ex) {
            result = "BookmarkMessageAgent.insertBookmarkMsgID error : " + ex;
        }

        return result;

    }

    private String deleteBookmarkMsgID(int msgid) {
        String result = null;

        try {
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "DELETE FROM `webmail`.`bookmark_list` WHERE (`email` = ?) and (`msgid` = ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (email != null || !(email.equals(""))) { //email 값이 null이 아니면.
                pstmt.setString(1, email);
                pstmt.setInt(2, msgid);
            }
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
            //sql문 완성

        } catch (Exception ex) {
            result = "BookmarkMessageAgent.deleteBookmarkMsgID error : " + ex;
        }

        return result;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    private ArrayList<Integer> readBookmarkMsgData() {
        try {
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "select msgid from webmail.bookmark_list where email = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { // ResultSet에 다음 값이 없을때까지 출력
                int buf_msgid = rs.getInt("msgid");	// 컬럼 값 받아오기
                bookmarkMsgID.add(buf_msgid);
                System.out.println("BookmarkMessageAgent.readBookmarkMsgData list : " + bookmarkMsgID);
            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.readBookmarkMsgData Error : " + ex);
        } finally {
            return bookmarkMsgID;
        }

    }
}

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
