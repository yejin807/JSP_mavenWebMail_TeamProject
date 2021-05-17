/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author kwangmin
 */
public class UserJoinAgent {

    private String server;
    private int port;
    Socket socket = null;
    InputStream is = null;
    OutputStream os = null;
    boolean isConnected = false;
    private String ROOT_ID = "root";  //  = "root";
    private String ROOT_PASSWORD = "root";  // = "root";
    private String ADMIN_ID = "admin"; //  = "admin";
    private final String EOL = "\r\n";
    String cwd;

    public UserJoinAgent(String server, int port, String cwd) throws Exception {
        System.out.println("UserAdminAgent created: server = " + server + ", port = " + port);
        this.server = server;  // 127.0.0.1
        this.port = port;  // 4555
        this.cwd = cwd;

        socket = new Socket(server, port);
        is = socket.getInputStream();
        os = socket.getOutputStream();

        isConnected = connect();
    }

    // return value:
    //   - true: addUser operation successful
    //   - false: addUser operation failed
    public boolean joinUser(String userId, String password) {
        boolean status = false;
        byte[] messageBuffer = new byte[1024];

        System.out.println("addUser() called");
        if (!isConnected) {
            return status;
        }

        try {
            // 1: "adduser" command
            String addUserCommand = "adduser " + userId + " " + password + EOL;
            os.write(addUserCommand.getBytes());

            // 2: response for "adduser" command
            java.util.Arrays.fill(messageBuffer, (byte) 0);

            is.read(messageBuffer);
            String recvMessage = new String(messageBuffer);
            System.out.println(recvMessage);

            // 3: 기존 메일사용자 여부 확인
            if (recvMessage.contains("added")) {
                status = true;
            } else {
                status = false;
            }
            // 4: 연결 종료
            quit();
            System.out.flush();  // for test
            socket.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
            status = false;
        } finally {
            // 5: 상태 반환
            return status;
        }
    }

    public boolean secessionUser(String userId) {
        byte[] messageBuffer = new byte[1024];
        String command;
        String recvMessage;
        boolean status = false;

        if (!isConnected) {
            return status;
        }

        try {
            // 1: "deluser" 명령 송신
            command = "deluser " + userId + EOL;
            os.write(command.getBytes());
            System.out.println(command);

            // 2: 응답 메시지 수신
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            is.read(messageBuffer);

            // 3: 응답 메시지 분석
            recvMessage = new String(messageBuffer);
            System.out.println(recvMessage);
            if (recvMessage.contains("deleted")) {
                status = true;
            }
            quit();
            System.out.flush();  // for test
            socket.close();
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            return status;
        }
    }

    private boolean connect() throws Exception {
        byte[] messageBuffer = new byte[1024];
        boolean returnVal = false;
        String sendMessage;

        System.out.println("UserAdminAgent.connect() called...");

        // root 인증: id, passwd - default: root
        // 1: Login Id message 수신
        is.read(messageBuffer);
        String recvMessage = new String(messageBuffer);
        System.out.println(recvMessage);

        // 2: rootId 송신
        sendMessage = ROOT_ID + EOL;
        os.write(sendMessage.getBytes());

        // 3: Password message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);
        is.read(messageBuffer);
        recvMessage = new String(messageBuffer);
        System.out.println(recvMessage);

        // 4: rootPassword 송신
        sendMessage = ROOT_PASSWORD + EOL;
        os.write(sendMessage.getBytes());

        // 5: welcome message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);
        // if (is.available() > 0) {
        is.read(messageBuffer);
        recvMessage = new String(messageBuffer);
        System.out.println(recvMessage);

        if (recvMessage.contains("Welcome")) {
            returnVal = true;
        } else {
            returnVal = false;
        }
        return returnVal;
    }

    public boolean quit() {
        byte[] messageBuffer = new byte[1024];
        boolean status = false;
        // quit
        try {
            // 1: quit 명령 송신
            String quitCommand = "quit" + EOL;
            os.write(quitCommand.getBytes());
            // 2: quit 명령에 대한 응답 수신
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            //if (is.available() > 0) {
            is.read(messageBuffer);
            // 3: 메시지 분석
            String recvMessage = new String(messageBuffer);
            System.out.println(recvMessage);
            if (recvMessage.contains("closed")) {
                status = true;
            } else {
                status = false;
            }
        } catch (IOException ex) {
            System.err.println("UserAdminAgent.quit() " + ex);
        } finally {
            return status;
        }
    }
}
