/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import cse.maven_webmail.model.Pop3Agent;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.UserAdminAgent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 *
 * @author jongmin - 수정 : 김광민
 */
public class UserAdminHandler extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    //제임스 서버 변수
    String server = "127.0.0.1";
    int port = 4555;
    //db 변수
    final String JdbcDriver = "com.mysql.cj.jdbc.Driver"; //cj추가
    final String JdbcUrl = "jdbc:mysql://localhost:3306/webmail?serverTimezone=Asia/Seoul"; //중요
    final String User = "jdbctester";
    final String Password = "43319521";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            String userid = (String) session.getAttribute("userid");

            request.setCharacterEncoding("UTF-8");
            int select = Integer.parseInt((String) request.getParameter("menu"));

            switch (select) {
                case CommandType.ADD_USER_COMMAND:
                    if (userid == null || !userid.equals("admin")) {
                        out.println("현재 사용자(" + userid + ")의 권한으로 수행 불가합니다.");
                        out.println("<a href=/WebMailSystem/> 초기 화면으로 이동 </a>");
                        return;
                    } else {
                        addUser(request, response, out);
                        break;
                    }

                case CommandType.DELETE_USER_COMMAND:
                    if (userid == null || !userid.equals("admin")) {
                        out.println("현재 사용자(" + userid + ")의 권한으로 수행 불가합니다.");
                        out.println("<a href=/WebMailSystem/> 초기 화면으로 이동 </a>");
                        return;
                    } else {
                        deleteUser(request, response, out);
                        break;
                    }

                case CommandType.JOIN:
                    joinUser(request, response, out);
                    break;

                case CommandType.SECESSION:
                    secessionUser(request, response, out);
                    break;

                default:
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                    break;

            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

    //(관리자 메뉴) 사용자 추가
    private void addUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            UserAdminAgent agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String userid = request.getParameter("userid");  // for test
            String password = request.getParameter("password");// for test
            String username = request.getParameter("username");
            String birth = request.getParameter("birth");
            String phone = request.getParameter("phone");

            out.println("userid = " + userid + "<br>");
            out.println("password = " + password + "<br>");
            out.println("username = " + username + "<br>");
            out.println("birth = " + birth + "<br>");
            out.println("phone = " + phone + "<br>");
            out.flush();
            // if (addUser successful)  사용자 등록 성공 팦업창
            // else 사용자 등록 실패 팝업창
            if (userid.equals("") || userid == null || password.equals("") || password == null
                    || username.equals("") || username == null || birth.equals("") || birth == null || phone.equals("") || phone == null) {
                out.println(getPopUp("모든 정보를 입력해주세요.", "add_user.jsp"));
            } else if (userid != null && userid.length() > 4 && password != null && password.length() > 5 && username != null
                    && username.length() > 2 && birth != null && birth.length() == 6 && phone != null && phone.length() > 11) {
                if (agent.addUser(userid, password)) {
                    addDBUser(request, response, out); //DB추가함수
                    out.println(getPopUp("정보 추가에 성공했습니다.", "admin_menu.jsp"));
                } else {
                    out.println(getPopUp("사용자 등록에 실패했습니다.", "add_user.jsp"));
                }
            } else {
                //양식에 맞지않음
                out.println(getPopUp("정보를 정확하게 입력해주세요.", "add_user.jsp"));
            }
            out.flush();
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }

    //회원가입
    private void joinUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            UserAdminAgent agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String userid = request.getParameter("userid");  // for test
            String password = request.getParameter("password");
            String password_check = request.getParameter("password_check");
            String username = request.getParameter("username");
            String birth = request.getParameter("birth");
            String phone = request.getParameter("phone");

            out.println("회원 정보" + "<br>");
            out.println("userid = " + userid + "<br>");
            out.println("password = " + password + "<br>");
            out.println("password_check = " + password_check + "<br>");
            out.println("username = " + username + "<br>");
            out.println("birth = " + birth + "<br>");
            out.println("phone = " + phone + "<br>");
            out.flush();

            if (userid.equals("") || userid == null || password.equals("") || password == null
                    || username.equals("") || username == null || birth.equals("") || birth == null || phone.equals("") || phone == null) {
                out.println(getPopUp("모든 정보를 입력해주세요.", "join.jsp"));
            } else if (userid != null && userid.length() > 4 && password != null && password.length() > 5 && password_check != null && username != null
                    && username.length() > 2 && birth != null && birth.length() == 6 && phone != null && phone.length() > 11) {
                if (!password.equals(password_check)) {
                    out.println(getPopUp("암호가 일치하지 않습니다.", "join.jsp"));
                } else if (agent.addUser(userid, password)) {
                    addDBUser(request, response, out); //DB추가함수
                    out.println(getPopUp("회원가입에 성공했습니다.", "index.jsp"));
                } else {
                    out.println(getPopUp("회원가입에 실패했습니다.", "join.jsp"));
                }
            } else {
                out.println(getPopUp("정보를 정확하게 입력해주세요.", "join.jsp"));
            }
            out.flush();
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }

    //(관리자 메뉴) DB 사용자 추가
    private void addDBUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            //1. JDBC 드라이버 객체
            Class.forName(JdbcDriver);

            //2. DB 연결
            Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);

            //3. PreparedStatement 생성
            String sql = "INSERT INTO webmail.userinfo values(?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            //4. SQL문 완성
            request.setCharacterEncoding("UTF-8"); // 한글 인식
            String userid = request.getParameter("userid"); // 주키
            if (!(userid == null) && !userid.equals("")) {
                String username = request.getParameter("username");
                String birth = request.getParameter("birth");
                String phone = request.getParameter("phone");
                pstmt.setString(1, userid);
                pstmt.setString(2, username);
                pstmt.setString(3, birth);
                pstmt.setString(4, phone);
                //5. 실행
                pstmt.executeUpdate();

            }
            //6. 자원해제
            pstmt.close();
            conn.close();
        } catch (Exception ex) {
            out.println("오류 : " + ex.getMessage());
        }

    }

    //(관리자 메뉴) 유저 삭제
    private void deleteUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            UserAdminAgent agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String[] deleteUserList = request.getParameterValues("selectedUsers");
            if (agent.deleteUsers(deleteUserList)) {
                delListDBUser(request, response, out, deleteUserList);
            }
            response.sendRedirect("admin_menu.jsp");
        } catch (Exception ex) {
            System.out.println(" UserAdminHandler.deleteUser : exception = " + ex);
        }
    }

    //(관리자 메뉴) DB 삭제
    private void delListDBUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String[] userList) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            //1. JDBC 드라이버 객체
            Class.forName(JdbcDriver);

            //2. DB 연결
            Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);

            //3. PreparedStatement 생성
            String sql = "DELETE FROM webmail.userinfo WHERE USER_ID=(?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            //4. SQL문 완성
            for (String userid : userList) {
                request.setCharacterEncoding("UTF-8"); // 한글 인식
                if (!(userid == null) && !userid.equals("")) {
                    pstmt.setString(1, userid);
                    //5. 실행
                    pstmt.executeUpdate();
                }
            }
            //6. 자원해제
            pstmt.close();
            conn.close();
        } catch (Exception ex) {
            out.println("오류 : " + ex.getMessage());
        }

    }

    //회원탈퇴
    private void secessionUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String userid = request.getParameter("userid");  // for test
        try {
            UserAdminAgent agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            if (checkPassword(request, response, out)) {
                if (agent.secessionUser(userid)) {
                    delDBUser(request, response, out);
                    out.println(getPopUp("회원탈퇴가 완료됐습니다.", "index.jsp"));
                } else {
                    out.println(getPopUp("회원탈퇴를 실패했습니다.", "main_menu.jsp"));
                }
            } else {
                out.println(getPopUp("회원탈퇴를 실패했습니다.", "main_menu.jsp"));
            }
        } catch (Exception ex) {
            System.out.println(" UserAdminHandler.deleteUser : exception = " + ex);
        }
    }

    //(회원탈퇴)DB에서 삭제
    private void delDBUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            //1. JDBC 드라이버 객체
            Class.forName(JdbcDriver);

            //2. DB 연결
            Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);

            //3. PreparedStatement 생성
            String sql = "DELETE FROM webmail.userinfo WHERE USER_ID=(?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            //4. SQL문 완성
            request.setCharacterEncoding("UTF-8"); // 한글 인식
            String userid = request.getParameter("userid"); // 주키
            if (!(userid == null) && !userid.equals("")) {
                pstmt.setString(1, userid);
                //5. 실행
                pstmt.executeUpdate();

            }
            //6. 자원해제
            pstmt.close();
            conn.close();
        } catch (Exception ex) {
            out.println("오류 : " + ex.getMessage());
        }

    }

    //탈퇴 전 비밀번호 체크
    private boolean checkPassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String host = (String) request.getSession().getAttribute("host");
        String userid = request.getParameter("userid");
        String password = request.getParameter("password");
        out.println(userid); //test
        out.println(password);
        // Check the login information is valid using <<model>>Pop3Agent.
        Pop3Agent pop3Agent = new Pop3Agent(host, userid, password);
        boolean status = pop3Agent.validate();

        return status;
    }

    private String getPopUp(String alertMessage, String send) {
        StringBuilder successPopUp = new StringBuilder();
        successPopUp.append("<html>");
        successPopUp.append("<head>");

        successPopUp.append("<title>메일 전송 결과</title>");
        successPopUp.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successPopUp.append("</head>");
        successPopUp.append("<body onload=\"goMainMenu()\">");
        successPopUp.append("<script type=\"text/javascript\">");
        successPopUp.append("function goMainMenu() {");
        successPopUp.append("alert(\"");
        successPopUp.append(alertMessage);
        successPopUp.append("\"); ");
//        successPopUp.append("window.location = \"admin_menu.jsp\"; ");
        successPopUp.append("window.location = \"");
        successPopUp.append(send);
        successPopUp.append("\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
