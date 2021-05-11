/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.io.InputStream;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

/**
 * add parameter in message class for save spamed or not and bookmarked or not
 * 쓰래기가 되어가는중...
 *
 * @author gleyd
 */
public class NewMessage extends MimeMessage {

    public boolean spam = false;
    public boolean bookmark = false;

    public NewMessage(Session session) {
        super(session);
    }

    public NewMessage(Session session, InputStream is) throws MessagingException {
        super(session, is);
    }

    public NewMessage(MimeMessage source) throws MessagingException {
        super(source);
    }

    protected NewMessage(Folder folder, int msgnum) {
        super(folder, msgnum);
    }

    protected NewMessage(Folder folder, InputStream is, int msgnum) throws MessagingException {
        super(folder, is, msgnum);
    }

    protected NewMessage(Folder folder, InternetHeaders headers, byte[] content, int msgnum) throws MessagingException {
        super(folder, headers, content, msgnum);
    }

    public boolean isSpam() {
        return spam;
    }

    public void setSpam(boolean spam) {
        this.spam = spam;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

}
