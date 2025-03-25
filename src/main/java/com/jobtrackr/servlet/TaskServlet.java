package com.jobtrackr.servlet;

import com.jobtrackr.database.TaskDb;
import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.errorHandling.SuccessResponseHelper;
import com.jobtrackr.service.TaskService;
import com.jobtrackr.util.HeaderUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet {
    private static final TaskService taskUtil = new TaskService();
    private static final TaskDb taskDb = new TaskDb();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
        String [] paths = path.split("/");
        if (Objects.equals(paths[1], "create-task")) {
            if (!paths[2].isEmpty()){
               UUID job_id = UUID.fromString(paths[2]);
               taskUtil.createTask(job_id,request,response);
            }
        } else if (paths.length==3) {
            UUID task_id = UUID.fromString(paths[2]);
            taskDb.setTaskCompletedTrue(task_id,response);
            new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response,HttpServletResponse.SC_OK,"Task updated successfully");
        } else {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid url");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
        String[] paths = path.split("/");
        if (paths.length == 3) {
            if (Objects.equals(paths[1], "job")) {
                UUID job_id = UUID.fromString(paths[2]);
                taskUtil.getTasks(job_id, request, response);
            } else  {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid url");
            }
        } else if (paths.length==2) {
            UUID task_id = UUID.fromString(paths[1]);
            taskUtil.getTask(task_id, request, response);
        }else {
            new ErrorResponseHandler().sendErrorResponse(response,HttpServletResponse.SC_BAD_REQUEST, "invalid url");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request,HttpServletResponse response){
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
        String [] paths = path.split("/");
        if (Objects.equals(paths[1], "update-task")){
            if (!paths[2].isEmpty()) {
                UUID task_id = UUID.fromString(paths[2]);
                taskUtil.updateTask(task_id, request, response);
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
        if (!Objects.equals(paths[1], "delete-task")){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid url");
            return;
        }
        UUID task_id = UUID.fromString(paths[2]);
        taskUtil.deleteTask(task_id,response);
    }
}
