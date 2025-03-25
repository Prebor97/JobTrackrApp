package com.jobtrackr.servlet;

import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.service.DocumentService;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

@WebServlet("/api/documents/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 5, // 5 MB
        maxRequestSize = 1024 * 1024 * 5 * 5 // 25 MB
)
public class DocumentServlet extends HttpServlet {
    private static final String UPLOAD_DIRECTORY = "upload";
    private static final DocumentService service = new DocumentService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String path = request.getPathInfo();
            if (path == null || path.isEmpty()) {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
                return;
            }

            String[] paths = path.split("/");
            if (paths.length == 3 && Objects.equals(paths[1], "upload")){
                UUID jobId = UUID.fromString(paths[2]);
                service.uploadDocument(request, response, uploadPath, jobId);
            }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String path = request.getPathInfo();
            if (path == null || path.isEmpty()) {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
                return;
            }

            String[] paths = path.split("/");
            if (paths.length == 3 && Objects.equals(paths[1], "download")) {
                UUID documentId = UUID.fromString(paths[2]);
                String filePath =service.downloadDocument(documentId);
                if (filePath == null){
                    new ErrorResponseHandler().sendErrorResponse(response,HttpServletResponse.SC_NOT_FOUND,"Document not found");
                    return;
                }
                File file = new File((filePath));
                if (!file.exists()){
                    new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "File not found");
                    return;
                }
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                response.setContentLength((int) file.length());

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream outputStream = response.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            } else if (paths.length==2&&paths[1]!=null) {
                UUID jobId = UUID.fromString(paths[1]);
                System.out.println("Job ID: " + jobId);
                service.getDocuments(response,jobId);
            }
    }
}
