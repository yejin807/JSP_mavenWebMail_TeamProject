<%-- 
    Document   : write_mail.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>

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
        });
        </script>

        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <div id="sidebar">
            <jsp:include page="sidebar_previous_menu.jsp" />
        </div>
            
        <div id="main">
            <form id="frm" enctype="multipart/form-data" method="POST"
                  action="WriteMail.do?menu=<%= CommandType.SEND_MAIL_COMMAND%>" >
                <table>
                    <tr>
                        <td> 수신 </td>
                        <td> <input type="text" name="to" size="80"
                                    value=<%=request.getParameter("recv") == null ? "" : request.getParameter("recv")%>>  </td>
                    </tr>
                    <tr>
                        <td>참조</td>
                        <td> <input type="text" name="cc" size="80">  </td>
                    </tr>
                    <tr>
                        <td> 메일 제목 </td>
                        <td> <input type="text" name="subj" size="80"  >  </td>
                    </tr>
                    <tr>
                        <td colspan="2">본  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 문</td>
                    </tr>
                    <tr>  <%-- TextArea    --%>
                        <td colspan="2">
                            <textarea rows="10" cols="30" id="cont" name="body" style="width:675px; height:350px; "></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>첨부 파일</td>
                        <td> <input type="file" name="file1"  size="80">  </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" id="submit" value="메일 보내기">
                            <input type="reset" value="다시 입력">
                        </td>
                    </tr>
                </table>
            </form>
        </div>

        <jsp:include page="footer.jsp" />
    </body>
</html>
