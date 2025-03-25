package com.jobtrackr.servlet;

import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.util.HeaderUtil;
import com.jobtrackr.service.JobService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

@WebServlet("/api/jobs/*")
public class JobServlet extends HttpServlet {
    private final JobService jobUtil = new JobService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
        if (!Objects.equals(path, "/create-job")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid url");
        } else {
            try {
                jobUtil.createJob(request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            jobUtil.getJobs(request, response);
        } else {
            String[] pathParts = path.split("/");
            if (pathParts.length == 2 && !pathParts[1].isEmpty()) {
                UUID id = UUID.fromString(pathParts[1]);
                jobUtil.getJobDetails(id, response);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request,HttpServletResponse response){
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
        String [] paths = path.split("/");
        if (Objects.equals(paths[1], "update-job")){
            if (!paths[2].isEmpty()) {
                UUID id = UUID.fromString(paths[2]);
                jobUtil.updateJob(id, request, response);
            }
        }else {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid url");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response){
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
        String [] paths = path.split("/");
        if (!Objects.equals(paths[1], "delete-job")){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid url");
        return;
        }
        UUID id = UUID.fromString(paths[2]);
        jobUtil.deleteJob(id,response);
    }
}
