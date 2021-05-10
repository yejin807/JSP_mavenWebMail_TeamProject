/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import cse.maven_webmail.model.UserJoinAgent;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kwangmin 회원가입, 탈퇴 핸들러
 */
public class JoinHandler extends HttpServlet {

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

            request.setCharacterEncoding("UTF-8");
            int select = Integer.parseInt((String) request.getParameter("menu"));

            switch (select) {
                case CommandType.JOIN:
                    addUser(request, response, out);
                    break;

                case CommandType.SECESSION:
//                        deleteUser(request, response, out);
                    break;

                default:
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                    break;
            }

        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String server = "127.0.0.1";
        int port = 4555;
        try {
            UserJoinAgent agent = new UserJoinAgent(server, port, this.getServletContext().getRealPath("."));
            String userid = request.getParameter("id");  // for test
            String password = request.getParameter("password");// for test
            String password_check = request.getParameter("password_check");
            String username = request.getParameter("username");
            String age = request.getParameter("age");
            String phone = request.getParameter("phone");
            out.println("userid = " + userid + "<br>");
            out.println("password = " + password + "<br>");
            out.println("password_check = " + password_check + "<br>");
            out.println("username = " + username + "<br>");
            out.println("age = " + age + "<br>");
            out.println("phone = " + phone + "<br>");
            out.flush();
            // if (addUser successful)  사용자 등록 성공 팦업창
            // else 사용자 등록 실패 팝업창
            if (userid != null && userid.length() > 4 && password != null && password.length() > 5 && password_check != null
                    && username != null && username.length() > 6 && age != null && phone != null && phone.length() > 12) {
                //db에 한글, 영문 저장이 몇 글자?? 혹은 그 차이 알기 db 연결하고 확인하기
                if (agent.joinUser(userid, password)) {
                    out.println(getUserRegistrationSuccessPopUp());
                } else {
                    out.println(getUserRegistrationFailurePopUp());
                }
            } else if (userid.equals("") || password.equals("") || password_check.equals("") || username.equals("")
                    || age.equals("") || phone.equals("")) {
                out.println(getEmptyFailurePopUp());
            } else if (!password.equals(password_check)) {
                out.println(getDifferentFailurePopUp());
            } else {
                out.println(getAccurateFailurePopUp());
            }

            out.flush();
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }

    private String getUserRegistrationSuccessPopUp() {
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

    private String getUserRegistrationFailurePopUp() {
        String alertMessage = "회원가입에 실패했습니다.";
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
//    private void deleteUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//        String server = "127.0.0.1";
//        int port = 4555;
//        try {
//            UserJoinAgent agent = new UserJoinAgent(server, port, this.getServletContext().getRealPath("."));
//            String[] deleteUserList = request.getParameterValues("selectedUsers");
//            agent.deleteUsers(deleteUserList);
//            response.sendRedirect("admin_menu.jsp");
//        } catch (Exception ex) {
//            System.out.println(" UserAdminHandler.deleteUser : exception = " + ex);
//        }
//    }
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
