package com.jobtrackr.service;

import com.jobtrackr.database.DocumentDb;
import com.jobtrackr.dto.document.DocumentSummaryDto;
import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.errorHandling.SuccessResponseHelper;
import com.jobtrackr.model.Document;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class DocumentService {
    private static final AuthService authUtil = new AuthService();
    private static final DocumentDb documentDb = new DocumentDb();
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    public void uploadDocument(HttpServletRequest request, HttpServletResponse response, String file_path, UUID job_id){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving token");
            return;
        }

        UUID user_id = authUtil.getIdFromToken(authHeader);
        try {
            Part filePart = request.getPart("file");
            if (filePart == null) {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No file part found in the request");
                return;
            }

            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "File name is empty or invalid");
                return;
            }

            String fileType = getFileType(fileName);
            if (!"PDF".equals(fileType) && !"WORD".equals(fileType)) {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Unsupported file type");
                return;
            }

            UUID fileId = UUID.randomUUID();
            String filePath = file_path + File.separator + fileName;
            logger.info("File Name: {}", fileName);
            logger.info("File Type: {}", fileType);
            logger.info("File Path: {}", filePath);
            filePart.write(filePath);

            documentDb.createNewDocument(fileId, job_id, user_id, fileName, fileType, filePath);
            new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response, HttpServletResponse.SC_OK, "Document uploaded successfully");
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

//    public Document getDocument(UUID id) {
//        return documentDb.getDocument(id);
//    }

    public String downloadDocument(UUID id) {
        Document document = documentDb.getDocument(id);
        if (document != null) {
            return document.getFile_url();
        }
        return null;
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null || contentDisp.isEmpty()) {
            return null;
        }

        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }

        return null;
    }

    private String getFileType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();

        if ("PDF".equals(extension)) {
            return "PDF";
        } else if ("DOC".equals(extension) || "DOCX".equals(extension)) {
            return "WORD";
        } else {
            return extension;
        }
    }

    public void getDocuments( HttpServletResponse response, UUID job_id){
        System.out.println("Job id is "+job_id);
        List<DocumentSummaryDto> dtos = documentDb.getDocuments(job_id);
        new SuccessResponseHelper<DocumentSummaryDto>().sendSuccessResponseWithListData(response, HttpServletResponse.SC_OK,"Documents retrieved successfully", dtos);
    }
}
