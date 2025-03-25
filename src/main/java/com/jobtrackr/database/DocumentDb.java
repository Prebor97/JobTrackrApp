package com.jobtrackr.database;

import com.jobtrackr.dto.document.DocumentSummaryDto;
import com.jobtrackr.model.Document;
import com.jobtrackr.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentDb {
    private static final Logger logger = LoggerFactory.getLogger(DocumentDb.class);
    private static final String SQLErrorMessage = "A SQL error has occurred ";

    public void createNewDocument(UUID id, UUID job_id, UUID user_id, String file_name, String file_type, String file_url) {
        logger.info("Creating file..........................................");
        String sql = "INSERT INTO documents (id, job_id, user_id, file_name, file_type, file_url, created_at) VALUES (?,?,?,?,?::file_type,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setObject(2, job_id);
            stmt.setObject(3, user_id);
            stmt.setString(4, file_name);
            stmt.setString(5, file_type);
            stmt.setString(6, file_url);
            stmt.setObject(7, LocalDateTime.now());
            stmt.executeUpdate();

            logger.info("Document successfully created");
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"creating document..................................................");
            throw new RuntimeException("Failed to create document: " + e.getMessage(), e);
        }
    }
    public List<DocumentSummaryDto> getDocuments(UUID job_id) {
        logger.info("Returning documents.............");
        List<DocumentSummaryDto> documents = new ArrayList<>();
        String sql = "Select * from documents where job_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, job_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DocumentSummaryDto dto = new DocumentSummaryDto();
                    dto.setId((UUID) rs.getObject("id"));
                    dto.setFile_name(rs.getString("file_name"));
                    documents.add(dto);
                }
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"retrieving task.....................................");
            throw new RuntimeException("Failed to create document: " + e.getMessage(), e);
        }
        return documents;
    }

    public Document getDocument(UUID id){
        logger.info("Returning document details....................................");
        String sql = "SELECT * FROM documents WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Document dto = new Document();
                    dto.setId(id);
                    dto.setFile_name(rs.getString("file_name"));
                    dto.setFile_type(rs.getString("file_type"));
                    dto.setFile_url(rs.getString("file_url"));
                    logger.info("Job detail returned {}", dto);

                    return dto;
                }
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"getting document detail with id {}",id);
            throw new RuntimeException("Failed to fetch document: " + e.getMessage(), e);
        }

        return null;
    }

}
