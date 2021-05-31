/*
 * File: Pop3Agent.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import cse.maven_webmail.control.SpamSettingDatabaseHandler;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;
import cse.maven_webmail.model.VinMessageHandler;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

//https://pythonq.com/so/java/824272 javaflag 사용
//todo : getBookmarkedMessageList 에서 북마크된 메일 추려내기.
//q북마크디비agent삭제
/**
 *
 * @author jongmin
 */
public class Pop3Agent {

    private String host;
    private String userid;
    private String password;
    private Store store;
    private String exceptionType;
    private HttpServletRequest request;
    private BookmarkMessageAgent bookmarkMessageAgent = BookmarkMessageAgent.getInstance();
    private SpamMessageAgent spamMessageAgent = SpamMessageAgent.getInstance();

    public Pop3Agent() {
    }

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
        this.bookmarkMessageAgent = BookmarkMessageAgent.getInstance(userid);
        this.spamMessageAgent = SpamMessageAgent.getInstance(userid);

        System.out.println("pop3Agent.생성자 BookmarkMessageAgent userid check == " + bookmarkMessageAgent.getUserid());
        System.out.println("pop3Agent.생성자 SpamMessageAgent userid check == " + spamMessageAgent.getUserid());

    }

    public boolean validate() {
        boolean status = false;

        try {
            status = connectToStore();
            store.close();
        } catch (Exception ex) {
            System.out.println("Pop3Agent.validate() error : " + ex);
            status = false;  // for clarity
        } finally {
            return status;
        }
    }

    public boolean deleteMessage(int msgid, boolean really_delete) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        try {
            // Folder 설정
//          Folder folder = store.getDefaultFolder();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, really_delete);

            // 폴더에서 메시지 삭제
            // Message [] expungedMessage = folder.expunge();
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            System.out.println("deleteMessage() error: " + ex);
        } finally {
            return status;
        }
    }
//----delete 메시지 기반 수정중
    //메인 화면에있는 삭제버튼 누를시 휴지통으로 이동함

   /* public boolean TMessage(int msgid, boolean Tremoves) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        try {
            // Folder 설정
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, Tremoves);

            // 폴더에서 메시지 삭제
            // Message [] expungedMessage = folder.expunge();
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            //folder.close(true);  // expunge == true --
            //store.close();                          --
            status = true;
        } catch (Exception ex) {
            System.out.println("flag Message() error: " + ex);
        } finally {
            return status;
        }
    }

//---- 
    */
    
    
    /*
     * 페이지 단위로 메일 목록을 보여주어야 함.
     */
    public String getMessageList() {

        String result = "";
        Message[] messages = null;

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            FetchProfile fpFlags = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fpFlags);

            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            //result = formatter.getMessageTable(spamMessageAgent.getMessagesWithoutSpamMsg(messages));   // 3.6
            result = formatter.getMessageTable(messages);   // 3.6
            
            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

    // 메인 화면에있는 삭제 버튼 누를시 그 메일을 DB로 보내고
// 선택한 메일은 메인 화면에서는 없어져야 함.
    public Message Go_to_trash(int msgid) {

        boolean status = false;
        Message newMsg = null;

        if (!connectToStore()) {
            System.out.println("Go_to_trash() error: connectToStore   error");
        }
        try {
            // Folder 설정
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message msg = folder.getMessage(msgid);
            // Message에 DELETED flag true로 설정
            //삭제플래그 true 설정
            msg.setFlag(Flags.Flag.DELETED, true);

            newMsg = msg;

            VinMessageHandler vinMessageHandler = new VinMessageHandler(newMsg, userid);
            vinMessageHandler.addMessageBin();

            folder.close(true); //expunge == true 메시지 삭제
            store.close();
            status = true;

        } catch (Exception ex) {
            System.out.println("get bin Message() error: " + ex);
        }
        return newMsg;
    }

    public String checkMsgAlive(Message newMsg) {
        String result = "";

        if (newMsg == null) {
            result = "Message dead";
        } else {
            result = "Message alive";
        }

        return result;
    }

    public String get_VinMessageList() {
        String result = "";
        Message[] messages = null;

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            FetchProfile fpFlags = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fpFlags);

            
            MessageFormatter formatter = new MessageFormatter(userid);  //3.5

            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

    //-------- delete 플래그가 꽂힌 메시지만 테이블형식으로 리스트처럼 보여주는 그거-----------//
    // trash.can.jsp에 (휴지통 페이지) 가져가서 보여줄것.
    /* public String get_TMessageList() {
        String result = "";
        Message[] messages = null;

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date           
            //delete flag가 꽂혀있는 메일만 가져오기?? 이게맞냐     
            fp.add(FetchProfile.Item.FLAGS); //메시지 플래그에 대한 정보를 가져옴
            folder.fetch(messages, fp);
            //http://geronimo.apache.org/maven/specs/geronimo-javamail_1.4_spec/1.6/apidocs/javax/mail/FetchProfile.Item.html#FLAGS
            // 플래그 관련 https://docs.oracle.com/javaee/6/api/javax/mail/Flags.html        
            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            result = formatter.get_TMessageTable(messages);   // 3.6

            //  folder.close(true);  // 3.7
            // store.close();       // 3.8
        } catch (Exception ex) {
            System.out.println("Pop3Agent.get_TMessageList() : exception = " + ex);
            result = "Pop3Agent.get_TMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

    // 폴더를 닫아서 삭제 플래그를 완성시킬것
    // 전체삭제
    public boolean delete_TMessage(int msgid) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        try {
            // Folder 설정
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);
            //플래그가 설정되어있을시
            if (msg.isSet(Flags.Flag.DELETED)) {
                // 폴더에서 메시지 삭제
                folder.close(true);  // expunge == true
                store.close();
                status = true;
            }
        } catch (Exception ex) {
            System.out.println("deleteMessage() error: " + ex);
        } finally {
            return status;
        }
    }
     */
    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            System.err.println("POP3 connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  // 210308 LJM - added
            result = formatter.getMessage(message);

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessage() : exception = " + ex;
        } finally {
            return result;
        }
    }

    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  // 200102 LJM - added cf. https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html
        props.setProperty("mail.debug", "true");

        Session session = Session.getInstance(props);
        session.setDebug(true);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            exceptionType = ex.toString();
        } finally {
            return status;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getBookmarkedMessageList() {
        String result = "";
        Message[] messages = null;
        //this.bookmarkMessageAgent = BookmarkMessageAgent.getInstance(userid);
        System.out.println("pop3Agent.getBookmarkedMessageList BookmarkMessageAgent userid check == " + bookmarkMessageAgent.getUserid());

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            FetchProfile fpFlags = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fpFlags);

            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            ArrayList<Message> bookmarkedMessages = bookmarkMessageAgent.getMessageList(messages);
            System.out.println("bookmarkedMessages size : " + bookmarkedMessages.size());
            result = formatter.getBookmarkedMessageTable(bookmarkedMessages);   // 3.6
            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getBookmarkMessageList() : exception = " + ex);
            result = "Pop3Agent.getBookmarkMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

    public String getSpamMessageList() {
        String result = "";
        Message[] messages = null;

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            FetchProfile fpFlags = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fpFlags);

            ArrayList<Message> spamMessages = spamMessageAgent.getMessageList(messages);

            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getSpammedMessageTable(spamMessages);   // 3.6

            //result = formatter.getMessageTable(messages);   // 3.6
            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getBookmarkMessageList() : exception = " + ex);
            result = "Pop3Agent.getBookmarkMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

}  // class Pop3Agent
