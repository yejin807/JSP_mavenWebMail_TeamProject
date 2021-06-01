package cse.maven_webmail.model;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

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
    private static final String UPLOADDIR = "WEB-INF/upload/";
    private static final String UPLOADTEMPDIR = "WEB-INF/temp/";
    private static final int MAXMEMORYSIZE = 20 * 1024 * 1024;
    private static final int MAXUPLOADSIZE = 50 * 1024 * 1024; // 50MB
    private static final String CHENCODE = "UTF-8";
    static Logger log = Logger.getLogger(FormParser.class);
    
    public FormParser(HttpServletRequest request) {
        this.request = request;
    }

    private void checkFolder(String baseFolder) {
        log.info("baseFolder = " + baseFolder);

        File uf = new File(baseFolder + UPLOADDIR);
        if (!uf.exists()) {
            uf.mkdir();
        }

        File tf = new File(baseFolder + UPLOADTEMPDIR);
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

            request.setCharacterEncoding(CHENCODE);
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
                diskFactory.setSizeThreshold(MAXMEMORYSIZE);
                diskFactory.setRepository(new File(UPLOADTEMPDIR));
                // 3. 파일 업로드 핸들러 생성
                ServletFileUpload upload = new ServletFileUpload(diskFactory);
                upload.setSizeMax(MAXUPLOADSIZE);

                // 4. request 객체 파싱
                List<FileItem> fileItems = upload.parseRequest(request); // List -> List<FileItem>
                Iterator i = fileItems.iterator();
                while (i.hasNext()) {
                    FileItem fi = (FileItem) i.next();

                    if (fi.isFormField()) {  // 5. 폼 필드 처리
                        log.info("filename = " + fi.getFieldName());
                        log.info(":" + fi.getString(CHENCODE) + "<br>");
                        String fieldName = fi.getFieldName();

                        String item = fi.getString(CHENCODE);

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
                        log.info("첨부파일  처리 시작");
                        if (fi.getName() != null && !fi.getName().equals("")) {
                            log.info("읽어 들인 파일 이름  = " + fi.getName());
                            // 절대 경로 저장
                            String abpath = currentFolder + UPLOADDIR + fi.getName() + "?";
                            fileName += abpath; // 파일에 추가된 전체 경로  
                            log.info("파일 경로 앞에 파일과 이어서 저장 = " + fileName);

                            File file = new File(currentFolder + UPLOADDIR + fi.getName());
                            log.info("파일 저장 경로 = " + file.getCanonicalPath());
                            // upload 완료. 추후 메일 전송후 해당 파일을 삭제하도록 해야 함.
                            fi.write(file);
                        } else {
                            fileName = null;
                        }

                    }
                }
            }

        } catch (Exception ex) {
            log.error("FormParser.parse() : exception = " + ex);
        }

    }  // parse()
}
