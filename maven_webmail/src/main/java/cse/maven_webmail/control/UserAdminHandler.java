/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

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
 * @author jongmin
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            String userid = (String) session.getAttribute("userid");
            if (userid == null || !userid.equals("admin")) {
                out.println("현재 사용자(" + userid + ")의 권한으로 수행 불가합니다.");
                out.println("<a href=/WebMailSystem/> 초기 화면으로 이동 </a>");
                return;
            } else {

                request.setCharacterEncoding("UTF-8");
                int select = Integer.parseInt((String) request.getParameter("menu"));

                switch (select) {
                    case CommandType.ADD_USER_COMMAND:
                        addUser(request, response, out);
                        break;

                    case CommandType.DELETE_USER_COMMAND:
                        deleteUser(request, response, out);
                        break;

                    default:
                        out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                        break;
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String server = "127.0.0.1";
        int port = 4555;
        try {
            UserAdminAgent agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String userid = request.getParameter("id");  // for test
            String password = request.getParameter("password");// for test
            out.println("userid = " + userid + "<br>");
            out.println("password = " + password + "<br>");
            out.flush();
            // if (addUser successful)  사용자 등록 성공 팦업창
            // else 사용자 등록 실패 팝업창
            if (agent.addUser(userid, password)) {
                addDBUser(request, response, out); //DB추가함수
                out.println(getUserRegistrationSuccessPopUp());
            } else {
                out.println(getUserRegistrationFailurePopUp());
            }
            out.flush();
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }

    private void addDBUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        //db 변수
        final String JdbcDriver = "com.mysql.cj.jdbc.Driver"; //cj추가
        final String JdbcUrl = "jdbc:mysql://localhost:3306/webmail_system?serverTimezone=Asia/Seoul"; //중요
        final String User = "jdbctester";
        final String Password = "43319521";

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
            String id = request.getParameter("id"); // 주키
            if (!(id == null) && !id.equals("")) {
                String username = request.getParameter("username");
                String birth = request.getParameter("birth");
                String phone = request.getParameter("phone");
                pstmt.setString(1, id);
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

    private void deleteUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String server = "127.0.0.1";
        int port = 4555;
        try {
            UserAdminAgent agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String[] deleteUserList = request.getParameterValues("selectedUsers");
            agent.deleteUsers(deleteUserList);
            response.sendRedirect("admin_menu.jsp");
        } catch (Exception ex) {
            System.out.println(" UserAdminHandler.deleteUser : exception = " + ex);
        }
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
