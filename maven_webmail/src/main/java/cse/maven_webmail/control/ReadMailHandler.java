/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.Pop3Agent;
import cse.maven_webmail.model.BookmarkMessageAgent;
import javax.mail.Message;

/**
 *
 * @author jongmin
 */
public class ReadMailHandler extends HttpServlet {

    private BookmarkMessageAgent bookmarkMessageAgent = BookmarkMessageAgent.getInstance();

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

        request.setCharacterEncoding("UTF-8");
        int select = Integer.parseInt((String) request.getParameter("menu"));
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");

        switch (select) {
            case CommandType.DELETE_MAIL_COMMAND:
                try (PrintWriter out = response.getWriter()) {
                deleteMessage(request);
                response.sendRedirect("main_menu.jsp");
            }
            break;
            //-------------
            case CommandType.MAIL_REMOVE_COMMAND:
                try (PrintWriter out = response.getWriter()) {
                moveMsgBin(request);
                response.sendRedirect("main_menu.jsp"); //수행 후 돌아가는 화면
            }
            break;
            //-----
            case CommandType.DOWNLOAD_COMMAND: // 파일 다운로드 처리
                download(request, response);
                break;

            case CommandType.SET_BOOKMARK: // 북마크설정
                try (PrintWriter out = response.getWriter()) {
                    int msgid = Integer.parseInt((String) request.getParameter("msgid"));
                    if (bookmarkMessageAgent.addBookmarking(userid, msgid)){
                        //bookmarking 성공
                        out.println(/*"userid : "+userid+"님, "+msgid+"번 메일*/"<script>alert('북마크 설정이 되었습니다.');</script>");
                        out.println(bookmarkMessageAgent.showBookmarkingList());
                    }
                    else{
                        out.println("<script>alert('북마크 설정이 실패했습니다.');</script>");
                    }
                    //response.sendRedirect("main_menu.jsp");
                    }catch (Exception ex) {
                        PrintWriter out = response.getWriter();
                        out.println("ReadmailHandler.cancelBookmarking error : " + ex);
                }
                break;
            case CommandType.CANCLE_BOOKMARK: // 북마크취소
                try (PrintWriter out = response.getWriter()) {
                int msgid = Integer.parseInt((String) request.getParameter("msgid"));
                System.out.println("request.getParameter msgid  : " + Integer.toString(msgid));
                if (bookmarkMessageAgent.cancelBookmarking(userid, msgid)) {
                    //bookmarking 성공
                    out.println("<script>alert('북마크 설정이 취소되었습니다.');</script>");
                    out.println(bookmarkMessageAgent.showBookmarkingList());
                } else {
                    out.println("<script>alert('북마크 취소가 실패했습니다.');</script>");
                }
                //response.sendRedirect("main_menu.jsp");
            } catch (Exception ex) {
                PrintWriter out = response.getWriter();
                out.println("ReadmailHandler.cancelBookmarking error : " + ex);
            }
            //response.sendRedirect("bookmarked_mail.jsp");
            break;
            default:
                try (PrintWriter out = response.getWriter()) {
                out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
            }
            break;

        }
    }

    private void download(HttpServletRequest request, HttpServletResponse response) { //throws IOException {
        response.setContentType("application/octet-stream");

        ServletOutputStream sos = null;

        try {
            /* TODO output your page here */
            request.setCharacterEncoding("UTF-8");
            // LJM 041203 - 아래와 같이 해서 한글파일명 제대로 인식되는 것 확인했음.
            String fileName = request.getParameter("filename");
            System.out.println(">>>>>> DOWNLOAD: file name = " + fileName);

            String userid = request.getParameter("userid");
            //String fileName = URLDecoder.decode(request.getParameter("filename"), "utf-8");

            // download할 파일 읽기
            // 윈도우즈 환경 사용시
            String downloadDir = request.getServletContext().getRealPath("/WEB-INF")
                        + File.separator + "download";
                File f = new File(downloadDir);
                if (!f.exists()) {
                    f.mkdir();
                }

            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, "UTF-8") + ";");

            File file = new File(downloadDir + File.separator + userid + File.separator + fileName);
            byte[] b = new byte[(int) file.length()];
            // try-with-resource 문은 fis를 명시적으로 close해 주지 않아도 됨.
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(b);
            };

            // 다운로드
            sos = response.getOutputStream();
            sos.write(b);
            sos.flush();
            sos.close();
        } catch (Exception ex) {
            System.out.println("====== DOWNLOAD exception : " + ex);
        }
    }

    private boolean deleteMessage(HttpServletRequest request) {
        int msgid = Integer.parseInt((String) request.getParameter("msgid"));

        HttpSession httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        boolean status = pop3.deleteMessage(msgid, true);
        return status;
    }

    //-----
    //딜리트 플래그 잇는ㄱ애를 기봄메시지 테이블에서 빼야하는데 안빼줘서 모든게 다뜬다
    //무브 빈에서는 그냥 플래그만 주면됨. 무브빈 역할이 끝나고 테이블을 다시 불러오면 
    // 딜리트 플래그ㄹ가 있는 애들이 안뜰테니 삭제할 필요는 없음
    // 무브 빈에서 스트링 배열에 저장하는것은 해주ㅓ야함 저장되는 형태가
    //메시지 자체여야함. 디비를 연결하든 배열을 연결하든 연결연결을 해야됨
    // 휴지통테이블 불러오는 데서는  스트링 배열도는 디비을 가져와서 테이블로 뿌려주면됨 
    //----------------------------------------
    // 메일을 디비로 보내고 메인화면에있는건 삭제
    //void->boolean으로 해줘야함
   private String moveMsgBin(HttpServletRequest request) {

        int msgid = Integer.parseInt((String) request.getParameter("msgid"));

        HttpSession httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        Message binMessage = pop3.Go_to_trash(msgid);
        System.out.println(pop3.checkMsgAlive(binMessage));
        return pop3.checkMsgAlive(binMessage);
        //return newMsg;


    }
    //----------
/*
    
    case CommandType.SET_BOOKMARK: // 북마크설정
                try (PrintWriter out = response.getWriter()) {
                bookmarkMessage(request);
                //response.sendRedirect("main_menu.jsp");
            } catch (Exception ex) {
                PrintWriter out = response.getWriter();
                out.println("ReadmailHandler.cancelBookmarking error : " + ex);
            }
            break;

    private boolean bookmarkMessage(HttpServletRequest request) {
        int msgid = Integer.parseInt((String) request.getParameter("msgid"));

        HttpSession httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        boolean status = pop3.bookmarkMessage(msgid);
        return status;
        //return false;
    }

    
    private boolean cancelBookmarking(HttpServletRequest request) {
        int msgid = Integer.parseInt((String) request.getParameter("msgid"));

        HttpSession httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        //boolean status = pop3.cancelBookmarking(msgid);
        //return status;
        return false;
    }

     */
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
