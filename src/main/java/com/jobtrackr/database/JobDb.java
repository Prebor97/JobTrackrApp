package com.jobtrackr.database;

import com.jobtrackr.dto.jobDto.JobDetailsDto;
import com.jobtrackr.dto.jobDto.JobSummaryDto;
import com.jobtrackr.model.Job;
import com.jobtrackr.util.DatabaseUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobDb {
    private static final Logger logger = LoggerFactory.getLogger(JobDb.class);
    private static final String SQLErrorMessage = "A SQL error has occurred ";

    public boolean createNewJob(UUID id, UUID user_id, Job job){
        logger.info("Creating job..........................................");
        String sql = "INSERT INTO jobs (id, user_id, title, company, location, status, applied_at, job_url, notes, created_at) VALUES (?,?,?,?,?,?::job_status,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setObject(1, id);
            stmt.setObject(2, user_id);
            stmt.setString(3, job.getTitle());
            stmt.setString(4, job.getCompany());
            stmt.setObject(5, job.getLocation());
            stmt.setObject(6, job.getStatus());
            stmt.setDate(7,   job.getAppliedAt());
            stmt.setString(8, job.getJobUrl());
            stmt.setString(9, job.getNotes());
            stmt.setObject(10, LocalDateTime.now());
            stmt.executeUpdate();

            logger.info("Job successfully created {}", job);
            return true;
        }catch (SQLException e){
            logger.debug(SQLErrorMessage+"creating job");
            throw new RuntimeException(e);
        }
    }

    public List<JobSummaryDto> getJobs(UUID user_id, String status, String date) {
        logger.info("Returning jobs.............");
        List<JobSummaryDto> jobs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("Select * from jobs where user_id = ?");
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?::job_status");
        }

        if (date != null && !date.isEmpty()) {
            sql.append(" AND applied_at = ?");
        }

        sql.append(" ORDER BY applied_at DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setObject(1, user_id);
            int paramIndex = 2;
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }

            if (date != null && !date.isEmpty()) {
                stmt.setDate(paramIndex++, Date.valueOf(date));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JobSummaryDto job = new JobSummaryDto();
                    job.setId((UUID) rs.getObject("id"));
                    job.setCompany(rs.getString("company"));
                    job.setTitle(rs.getString("title"));
                    jobs.add(job);
                }
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"returning jobs");
            throw new RuntimeException(e);
        }
        return jobs;
    }
    public JobDetailsDto getJob(UUID id){

        logger.info("Returning job detail....................................");
        String sql = "SELECT * FROM jobs WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JobDetailsDto job = new JobDetailsDto();
                    job.setId((UUID) rs.getObject("id"));
                    job.setTitle(rs.getString("title"));
                    job.setCompany(rs.getString("company"));
                    job.setLocation(rs.getString("location"));
                    job.setStatus(rs.getString("status"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate appliedAt = rs.getDate("applied_at").toLocalDate();
                    job.setAppliedAt(appliedAt.format(formatter));
                    job.setJobUrl(rs.getString("job_url"));
                    job.setNotes(rs.getString("notes"));

                    logger.info("Job detail returned {}", job);

                    return job;
                }
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"getting job detail with id {}",id);
            throw new RuntimeException("Failed to fetch job: " + e.getMessage(), e);
        }

        return null;
    }


    public boolean updateJob(UUID jobId, Job updatedJob) {
        StringBuilder sql = new StringBuilder("UPDATE jobs SET ");
        boolean hasUpdates = false;

        if (updatedJob.getTitle() != null) {
            sql.append("title = ?, ");
            hasUpdates = true;
        }
        if (updatedJob.getCompany() != null) {
            sql.append("company = ?, ");
            hasUpdates = true;
        }
        if (updatedJob.getLocation() != null) {
            sql.append("location = ?, ");
            hasUpdates = true;
        }
        if (updatedJob.getStatus() != null) {
            sql.append("status = ?::job_status, ");
            hasUpdates = true;
        }
        if (updatedJob.getAppliedAt() != null) {
            sql.append("applied_at = ?, ");
            hasUpdates = true;
        }
        if (updatedJob.getJobUrl() != null) {
            sql.append("job_url = ?, ");
            hasUpdates = true;
        }
        if (updatedJob.getNotes() != null) {
            sql.append("notes = ?, ");
            hasUpdates = true;
        }

        if (!hasUpdates) {
            return false;
        }

        sql.delete(sql.length() - 2, sql.length());

        sql.append(" WHERE id = ?");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (updatedJob.getTitle() != null) {
                stmt.setString(paramIndex++, updatedJob.getTitle());
            }
            if (updatedJob.getCompany() != null) {
                stmt.setString(paramIndex++, updatedJob.getCompany());
            }
            if (updatedJob.getLocation() != null) {
                stmt.setString(paramIndex++, updatedJob.getLocation());
            }
            if (updatedJob.getStatus() != null) {
                stmt.setString(paramIndex++, updatedJob.getStatus());
            }
            if (updatedJob.getAppliedAt() != null) {
                stmt.setDate(paramIndex++, updatedJob.getAppliedAt());
            }
            if (updatedJob.getJobUrl() != null) {
                stmt.setString(paramIndex++, updatedJob.getJobUrl());
            }
            if (updatedJob.getNotes() != null) {
                stmt.setString(paramIndex++, updatedJob.getNotes());
            }

            stmt.setObject(paramIndex, jobId);

            int rowsUpdated = stmt.executeUpdate();
            logger.info("Job with id {} has been updated", jobId);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"updating job with id {}",jobId);
            throw new RuntimeException("Failed to update job: " + e.getMessage(), e);
        }
    }

    public boolean deleteJob(UUID id, HttpServletResponse response){
        String sql = "Delete from jobs where id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Job with id {} has been deleted",id);
                return true;
            } else {
                logger.debug("Job id {} not found",id);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
            }
        } catch (SQLException e) {
            logger.info(SQLErrorMessage+"deleting job with id {}",id);
            throw new RuntimeException(e);
        }catch (IOException e) {
            logger.error("An IOException occurred sending error response");
            throw new RuntimeException(e);
        }
        return false;
    }
}
