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

/**
 *
 * @author gleyd
 */
public class BookmarkMessageAgent {

    private static BookmarkMessageAgent uniqueInstance = new BookmarkMessageAgent();
    private ArrayList<Integer> bookmarkMsgID = new ArrayList<Integer>();
    private String email = null;

    private BookmarkMessageAgent() {
    }

    public static BookmarkMessageAgent getInstance() {
        return uniqueInstance;
    }

    public ArrayList<Integer> getBookmarkMessageList() {
        return bookmarkMsgID;
    }

    public boolean addBookmarking(int msgid) {
        boolean status = false;

        try {
            if (!bookmarkMsgID.contains(Integer.valueOf(msgid))) {
                bookmarkMsgID.add(msgid);
                status = true;
            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.addBookmarkMessage error : " + ex);
        } finally {
            return status;
        }
    }

    public boolean cancelBookmarking(int msgid) {
        boolean status = false;

        try {
            if (bookmarkMsgID.contains(Integer.valueOf(msgid))) {
                bookmarkMsgID.remove(Integer.valueOf(msgid));
                status = true;

            }
        } catch (Exception ex) {
            System.out.println("BookmarkMessageAgent.addBookmarkMessage error : " + ex);
        } finally {
            return status;
        }
    }

    public String showBookmarkingList() {
        String result = "";
        for (int list : bookmarkMsgID) {
            result += Integer.toString(list) + " ";
        }
        return result;
    }

    private String insertBookmarkMsgID(int msgid) {
        String result = null;

        try {
            Class.forName(CommandType.JdbcDriver);
            Connection conn = DriverManager.getConnection(CommandType.JdbcUrl, CommandType.JdbcUser, CommandType.JdbcPassword);

            String sql = "INSERT INTO `webmail`.`bookmark` (`email`, `msgid`) VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (email != null && email.equals("")) { //email 값이 null이 아니면.
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

        return result;
    }

    public void setEmail(String email) {
        this.email = email;
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
