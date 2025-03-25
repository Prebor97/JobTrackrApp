package com.jobtrackr.service;

import com.jobtrackr.dto.jobDto.JobDetailsDto;
import com.jobtrackr.dto.jobDto.JobSummaryDto;
import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.errorHandling.SuccessResponseHelper;
import com.jobtrackr.model.Job;
import com.jobtrackr.database.JobDb;
import com.jobtrackr.util.JsonMapperUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class JobService {
    private final AuthService authUtil = new AuthService();
    private final JobDb jobDbUtil = new JobDb();
    private final JsonMapperUtil jsonMapperUtil = new JsonMapperUtil();
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    public void createJob(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        Job job = jsonMapperUtil.getRequestBody(request,Job.class);
        if (job.getTitle() == null || job.getTitle().trim().isEmpty() ||
                job.getJobUrl() == null || job.getJobUrl().trim().isEmpty() ||
                job.getAppliedAt() == null || job.getCompany() == null ||
                job.getCompany().trim().isEmpty() || job.getLocation() == null ||
                job.getLocation().trim().isEmpty() || job.getNotes() == null ||
                job.getNotes().trim().isEmpty() || job.getStatus() == null)
        {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "All fields required");
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving token");
            return;
        }
        UUID job_id = UUID.randomUUID();
        UUID user_id = authUtil.getIdFromToken(authHeader);
        boolean jobCreated = jobDbUtil.createNewJob(job_id, user_id, job);
        if (!jobCreated){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Database error");
            return;
        }
        logger.info("Job successfully created");
        new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response, HttpServletResponse.SC_CREATED,"Job created successfully");
  }

    public void getJobs(HttpServletRequest request, HttpServletResponse response){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving token");
            return;
        }
        UUID user_id = authUtil.getIdFromToken(authHeader);
        if (user_id==null){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving user id");
            return;
        }
        String status = request.getParameter("status");
        String date = request.getParameter("date");
        List<JobSummaryDto> jobs = jobDbUtil.getJobs(user_id,status,date);
        logger.info("Jobs successfully retrieved");
        new SuccessResponseHelper<JobSummaryDto>().sendSuccessResponseWithListData(response, HttpServletResponse.SC_OK,"Job retrieved successfully", jobs);
    }

    public void getJobDetails(UUID id, HttpServletResponse response) {
        JobDetailsDto job = jobDbUtil.getJob(id);
        if (job == null){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Job not found for id");
            return;
        }
        logger.info("Job details successfully retrieved");
        new SuccessResponseHelper<JobDetailsDto>().sendSuccessResponseWithData(response, HttpServletResponse.SC_OK,"Job retrieved successfully", job);
    }

    public void updateJob(UUID id, HttpServletRequest request, HttpServletResponse response){
        Job job = jsonMapperUtil.getRequestBody(request,Job.class);
        boolean jobUpdated = jobDbUtil.updateJob(id, job);
        if (!jobUpdated){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error updating job");
            return;
        }
        logger.info("Job updated successfully");
        new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response, HttpServletResponse.SC_CREATED,"Job updated successfully");
    }

    public void deleteJob(UUID id, HttpServletResponse response){
        boolean jobDeleted = jobDbUtil.deleteJob(id, response);
        if (!jobDeleted){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Job id not found");
            return;
        }
        logger.info("Job deleted successfully");
        new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response, HttpServletResponse.SC_OK,"Job deleted successfully");
    }
}
