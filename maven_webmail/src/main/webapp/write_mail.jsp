<%-- 
    Document   : write_mail.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>
<%@page import="java.sql.*"%>
<%@page import="javax.servlet.*"%>

<%
    String ctx = request.getContextPath();    //콘텍스트명 얻어오기.
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%-- @taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" --%>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>메일 쓰기 화면</title>
        
        <!-- SmartEditor를 사용하기 위해서 다음 js파일을 추가 (경로 확인) -->
        <script type="text/javascript" src="<%=ctx %>/SE2/js/HuskyEZCreator.js" charset="utf-8"></script>
        <!-- jQuery를 사용하기위해 jQuery라이브러리 추가 -->
        <script type="text/javascript" src="http://code.jquery.com/jquery-1.9.0.min.js"></script>

        <script type="text/javascript">
        var oEditors = [];
        $(function(){
              nhn.husky.EZCreator.createInIFrame({
                  oAppRef: oEditors,
                  elPlaceHolder: "cont", //textarea에서 지정한 id와 일치해야 합니다. 
                  //SmartEditor2Skin.html 파일이 존재하는 경로
                  sSkinURI: "/maven_webmail/SE2/SmartEditor2Skin.html",  
                  htParams : {
                      // 툴바 사용 여부 (true:사용/ false:사용하지 않음)
                      bUseToolbar : true,             
                      // 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
                      bUseVerticalResizer : false,     
                      // 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
                      bUseModeChanger : false,
                      
                      fOnBeforeUnload : function(){
                          
              }
                  },
                  fCreator: "createSEditor2"
              });

              //버튼 클릭시 form 전송
              $("#submit").click(function(){
                  oEditors.getById["cont"].exec("UPDATE_CONTENTS_FIELD", []);
                  $("#main").submit();
              });    
              $("#save").click(function(){
                  oEditors.getById["cont"].exec("UPDATE_CONTENTS_FIELD", []);
                  $("#main").submit();
              });    
        });
        
        function check_file_size(){
            var size_limit = 50*1024*1024; //50MB
            var oFiles = document.getElementById("input_file").files;
            var nFiles = oFiles.length;
            var nBytes = 0;
            for (var nFileId = 0; nFileId < nFiles; nFileId++) {
                nBytes += oFiles[nFileId].size;
            }
            if( nBytes > size_limit){
                alert('파일 크기가 50MB를 초과하여 전송을 할 수 없습니다.');
                return false;
            }else{
                return true;
            }
        }
        </script>
        
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%
        String userid = (String) session.getAttribute("userid");
        final String JdbcDriver = "com.mysql.jdbc.Driver";
        String JdbcUrl = "jdbc:mysql://localhost:3306/webmail?useUnicode=true&characterEncoding=utf8";
        final String User = "jdbctester";
        final String Password = "0000";
        response.setContentType("text/html;charset=UTF-8");
        String DBemail = null;
        String DBtitle = null;
        String DBcont = null;

        try{
            Class.forName(JdbcDriver);
            Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM tempmail WHERE user='"+userid+"';";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                DBemail = rs.getString("email");
                DBtitle = rs.getString("title");
                DBcont = rs.getString("content");
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception ex) {
            out.println("오류가 발생했습니다. (발생 오류: "+ ex.getMessage() + ")");
          }
        %>
        <jsp:include page="header.jsp" />

        <div id="sidebar">
            <jsp:include page="sidebar_previous_menu.jsp" />
            
        </div>
            
        <div id="main">
            <form id="frm" enctype="multipart/form-data" method="POST" name="frm"
<<<<<<< Updated upstream
                  action="WriteMail.do?menu=<%= CommandType.SEND_MAIL_COMMAND%>" 
                  onsubmit ="return check_file_size()">
                <table>
=======
                  action="WriteMail.do?menu=<%= CommandType.SEND_MAIL_COMMAND%>" >
                <table> <!-- 메일쓰기 폼 설정 -->
>>>>>>> Stashed changes
                    <%
                        String to = request.getParameter("to") == null ? "" : request.getParameter("to");
                        String cc = request.getParameter("cc") == null ? "" : request.getParameter("cc");
                        String subj = request.getParameter("subj") == null ? "" : request.getParameter("subj");
                        String text = request.getParameter("text") == null ? "" : request.getParameter("text");
                        String temp = request.getParameter("temp") == null ? "" : request.getParameter("temp");
                        %>
                        
                    <tr>
                        <td> 수신 </td>
                        <td> <input type="text" id="to" name="to" size="80"
                                    value=<%=to%>>  </td>
                    </tr>
                    <tr>
                        <td>참조</td>
                        <td> <input type="text" id="cc" name="cc" size="80" value="<%=cc%>">  </td>
                    </tr>
                    <tr>
                        <td> 메일 제목 </td>
                        <td> <input type="text" id="subj" name="subj" size="80" value="<%=subj%>">  </td>
                    </tr>
                    <tr>
                        <td colspan="2">본  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 문</td>
                    </tr>
                    <tr>  <%-- TextArea    --%>
                        <td colspan="2">
                            <textarea rows="10" cols="30" id="cont" name="body" style="width:675px; height:350px; " ><%=text%><%=DBcont%></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>첨부 파일</td>
                        <td> <input type="file" name="file1" id="input_file" multiple size="80" > 
                    </tr>
                    <input type="text" name="temp" id="temp" value="temp" value="<%=temp%>" hidden>
                    <tr>
                        <td colspan="4">
                            <input type="submit" id="submit" value="메일 보내기" onclick="sendSubmit()">
                            <input type="reset" value="다시 입력">
                            <input type="submit" id="save" value="임시저장" onclick="saveSubmit();">
                            <input type="button" value="불러오기" onclick="loadMail();">
                        </td>
                    </tr>
                </table>
            </form>

            <script type="text/javascript">
                var DBemail = "<%=DBemail%>";
                var DBtitle = "<%=DBtitle%>";
                var DBcontent = "<%=DBcont%>";
                var text = "<%=text%>";
                
                function saveSubmit() {
                    frm.action = "temp_mail_save.jsp";
                    frm.encoding = "application/x-www-form-urlencoded";
                    frm.submit();
                }

                function sendSubmit() {
                    frm.action = "WriteMail.do?menu=<%= CommandType.SEND_MAIL_COMMAND%>";
                    frm.encoding = "multipart/form-data";
                    frm.submit();
                }
                
                function loadMail(){
                    document.getElementById("to").value = DBemail;
                    document.getElementById("subj").value = DBtitle;
                    document.getElementById("cont").value = DBcont;
                    text = DBcontent;
                    frm.submit();
                }
            </script>   
        </div>
                    
                    
        <jsp:include page="footer.jsp" />
    </body>
</html>
