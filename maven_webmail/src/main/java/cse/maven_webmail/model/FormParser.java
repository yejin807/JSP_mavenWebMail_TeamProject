/*

- To change this template, choose Tools | Templates
- and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * - 책임: enctype이 multipart/form-data인 HTML 폼에 있는 각 필드와 파일 정보 추출 - - @author
 * jongmin
 */
public class FormParser {

    private HttpServletRequest request;
    private String toAddress = null;
    private String ccAddress = null;
    private String subject = null;
    private String body = null;
    private String fileName = "";
    private final String UPLOAD_DIR = "WEB-INF/upload/";
    private final String UPLOAD_TEMP_DIR = "WEB-INF/temp/";
    private final int MAX_MEMORY_SIZE = 20 * 1024 * 1024;
    private final int MAX_UPLOAD_SIZE = 100 * 1024 * 1024; // 100MB

    public FormParser(HttpServletRequest request) {
        this.request = request;
    }

    private void checkFolder(String baseFolder) {
        System.out.println("baseFolder = " + baseFolder);

        File uf = new File(baseFolder + UPLOAD_DIR);
        if (!uf.exists()) {
            uf.mkdir();
        }

        File tf = new File(baseFolder + UPLOAD_TEMP_DIR);
        if (!tf.exists()) {
            tf.mkdir();
        }

    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void parse() {
        try {

            request.setCharacterEncoding("UTF-8");
            String currentFolder = request.getServletContext().getRealPath("/");

            if (currentFolder.matches(".*\\.*")) {
                currentFolder = currentFolder.replace("\\", "/");
            }

            checkFolder(currentFolder);
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (isMultipart) {

                // 1. 디스크 기반 파일 항목에 대한 팩토리 생성
                DiskFileItemFactory diskFactory = new DiskFileItemFactory();
                // 2. 팩토리 제한사항 설정
                diskFactory.setSizeThreshold(MAX_MEMORY_SIZE);
                diskFactory.setRepository(new File(UPLOAD_TEMP_DIR));
                // 3. 파일 업로드 핸들러 생성
                ServletFileUpload upload = new ServletFileUpload(diskFactory);
                upload.setSizeMax(MAX_UPLOAD_SIZE);

                // 4. request 객체 파싱
                List<FileItem> fileItems = upload.parseRequest(request); // List -> List<FileItem>
                Iterator i = fileItems.iterator();
                while (i.hasNext()) {
                    FileItem fi = (FileItem) i.next();

                    if (fi.isFormField()) {  // 5. 폼 필드 처리
                        System.out.println("filename = " + fi.getFieldName());
                        System.out.println(":" + fi.getString("UTF-8") + "<br>");
                        String fieldName = fi.getFieldName();

                        //todo 주석처리
                        String item = fi.getString("UTF-8");

                        if (fieldName.equals("to")) {
                            setToAddress(item);  // 200102 LJM - @ 이후의 서버 주소 제거
                        } else if (fieldName.equals("cc")) {
                            setCcAddress(item);
                        } else if (fieldName.equals("subj")) {
                            setSubject(item);
                        } else if (fieldName.equals("body")) {
                            setBody(item);
                        }
                    } else {  // 6. 첨부 파일 처리
                        System.out.println("첨부파일  처리 시작");
                        if (fi.getName() != null && !fi.getName().equals("")) {
                            System.out.println("file name  = " + fi.getName());
                            // 절대 경로 저장
                            String abpath = currentFolder + UPLOAD_DIR + fi.getName() + "?";
                            fileName += abpath; // 파일에 추가된 전체 경로  
                            System.out.println("full path = " + fileName);

                            File file = new File(currentFolder + UPLOAD_DIR + fi.getName());
                            System.out.println("파일 저장 경로 = " + file.getCanonicalPath());
                            // upload 완료. 추후 메일 전송후 해당 파일을 삭제하도록 해야 함.
                            fi.write(file);
                        } else {
                            fileName = null;
                        }

                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("FormParser.parse() : exception = " + ex);
        }

    }  // parse()
}
