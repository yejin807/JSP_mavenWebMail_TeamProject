/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import javax.mail.Flags;

public class CommandType {

    private CommandType() {
        // not called private constructor
    }
    public static final int READ_MENU = 1;
    public static final int WRITE_MENU = 2;

    public static final int ADD_USER_MENU = 3;
    public static final int DELETE_USER_MENU = 4;

    public static final int SEND_MAIL_COMMAND = 21;
    public static final int DELETE_MAIL_COMMAND_IN_BOOKMARK = 41;
    public static final int DELETE_MAIL_COMMAND_IN_SPAM = 42;
    public static final int DOWNLOAD_COMMAND = 51;

    public static final int ADD_USER_COMMAND = 61;
    public static final int DELETE_USER_COMMAND = 62;

    public static final int MAIL_REMOVE_COMMAND = 71;
    public static final int VIN_DBDELETE_COMMAND = 72;

      
    public static final int JOIN = 81;
    public static final int SECESSION = 82;

    public static final int LOGIN = 91;
    public static final int LOGOUT = 92;

    public static final int SET_BOOKMARK = 801;
    public static final int CANCLE_BOOKMARK = 800;

    public static final int DELETE_SPAM_WORD_COMMAND = 901;
    public static final int DELETE_SPAM_EMAIL_COMMAND = 902;
    
    public static final int IS_EMAIL_TRUE = 1;
    public static final int IS_EMAIL_FALSE = 0;
    
    public static final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
    public static final String JdbcUrl = "jdbc:mysql://localhost:3306/webmail?serverTime=Asia/Seoul";
    public static final String JdbcUser = "jdbctester";
    public static final String JdbcPassword = "1895";
}
