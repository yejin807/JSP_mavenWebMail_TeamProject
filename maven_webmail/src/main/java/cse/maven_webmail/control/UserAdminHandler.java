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
import cse.maven_webmail.model.UserJoinAgent;
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
    final String JdbcUrl = "jdbc:mysql://localhost:3306/webmail_system?serverTimezone=Asia/Seoul"; //중요
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
            if (userid.equals("null") || userid == null || password.equals("null") || password == null
                    || username.equals("null") || username == null || birth.equals("null") || birth == null || phone.equals("null") || phone == null) {
                out.println(getEmptyFailurePopUp());
            } else if (userid != null && userid.length() > 4 && password != null && password.length() > 5 && username != null
                    && username.length() > 2 && birth != null && birth.length() == 6 && phone != null && phone.length() > 11) {
                if (agent.addUser(userid, password)) {
                    addDBUser(request, response, out); //DB추가함수
                    out.println(getUserRegistrationSuccessPopUp());
                } else {
                    out.println(getUserRegistrationFailurePopUp());
                }
            } else {
                out.println(getAccurateFailurePopUp());
            }
            out.flush();
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }

    //회원가입
    private void joinUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            UserJoinAgent agent = new UserJoinAgent(server, port, this.getServletContext().getRealPath("."));
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

            if (userid.equals("null") || userid == null || password.equals("null") || password == null
                    || username.equals("null") || username == null || birth.equals("null") || birth == null || phone.equals("null") || phone == null) {
                out.println(getEmptyFailurePopUp());
            } else if (userid != null && userid.length() > 4 && password != null && password.length() > 5 && password_check != null && username != null
                    && username.length() > 2 && birth != null && birth.length() == 6 && phone != null && phone.length() > 11) {
                if (!password.equals(password_check)) {
                    out.println(getDifferentFailurePopUp());
                } else if (agent.joinUser(userid, password)) {
                    addDBUser(request, response, out); //DB추가함수
                    out.println(getUserJoinSuccessPopUp());
                } else {
                    out.println(getUserJoinFailurePopUp());
                }
            } else {
                out.println(getAccurateFailurePopUp());
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
            String sql = "INSERT INTO webmail_system.userinfo values(?,?,?,?)";
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
            String sql = "DELETE FROM webmail_system.userinfo WHERE USER_ID=(?)";
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
            UserJoinAgent agent = new UserJoinAgent(server, port, this.getServletContext().getRealPath("."));
            if (checkPassword(request, response, out)) {
                if (agent.secessionUser(userid)) {
                    //탈퇴 완료 팝업
                    delDBUser(request, response, out);
                    out.println(getSecessionPopUp());
                } else {
                    //탈퇴 실패 팝업
                    out.println(getSecessionFailPopUp());
                }
            } else {
                out.println(getSecessionFailPopUp());
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
            String sql = "DELETE FROM webmail_system.userinfo WHERE USER_ID=(?)";
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

    private String getUserRegistrationSuccessPopUp() {
        String alertMessage = "사용자 등록이 성공했습니다.";
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
        successPopUp.append("window.location = \"admin_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getUserRegistrationFailurePopUp() {
        String alertMessage = "사용자 등록이 실패했습니다.";
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
        successPopUp.append("window.location = \"admin_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getUserJoinSuccessPopUp() {
        String alertMessage = "회원가입에 성공했습니다.";
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
        successPopUp.append("window.location = \"index.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getUserJoinFailurePopUp() {
        String alertMessage = "회원가입에 실패했습니다. 관리자에게 문의해주세요.";
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
        successPopUp.append("window.location = \"join.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getEmptyFailurePopUp() {
        String alertMessage = "모든 정보를 입력해주세요.";
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
        successPopUp.append("window.location = \"join.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getDifferentFailurePopUp() {
        String alertMessage = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
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
        successPopUp.append("window.location = \"join.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getAccurateFailurePopUp() {
        String alertMessage = "모든 정보를 정확하게 입력해주세요.";
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
        successPopUp.append("window.location = \"join.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getSecessionPopUp() {
        String alertMessage = "회원탈퇴가 완료 됐습니다.";
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
        successPopUp.append("window.location = \"index.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getSecessionFailPopUp() {
        String alertMessage = "비밀번호를 정확하게 입력해주세요.";
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
        successPopUp.append("window.location = \"secession.jsp\"; ");
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
