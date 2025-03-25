package com.jobtrackr.service;

import com.jobtrackr.database.TaskDb;
import com.jobtrackr.dto.taskDto.TaskSummaryDto;
import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.errorHandling.SuccessResponseHelper;
import com.jobtrackr.model.Task;
import com.jobtrackr.util.JsonMapperUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;

public class TaskService {
    private final AuthService authUtil = new AuthService();
    private final JsonMapperUtil jsonMapperUtil = new JsonMapperUtil();
    private final TaskDb taskDb = new TaskDb();
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public void createTask(UUID job_id, HttpServletRequest request, HttpServletResponse response){
        Task task = jsonMapperUtil.getRequestBody(request,Task.class);
        if (task.getDescription()==null||task.getDescription().trim().isEmpty()||task.getPriority()==null||
        task.getPriority().trim().isEmpty()){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "All fields required");
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving token");
            return;
        }
        UUID task_id = UUID.randomUUID();
        UUID user_id = authUtil.getIdFromToken(authHeader);
        boolean taskCreated = taskDb.createNewTask(task_id,job_id,user_id,task);
        if (!taskCreated){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Database error");
            return;
        }
        logger.info("Task created");
        new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response, HttpServletResponse.SC_CREATED,"Task created successfully");
    }

    public void getTasks(UUID job_id, HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving token");
            return;
        }
        List<TaskSummaryDto> tasks = taskDb.getTasks(job_id);
        logger.info("Tasks retrieved successfully");
        logger.info(tasks.toString());
            new SuccessResponseHelper<TaskSummaryDto>().sendSuccessResponseWithListData(response, HttpServletResponse.SC_OK, "Job retrieved successfully", tasks);
        }


    public void  getTask(UUID task_id, HttpServletRequest request, HttpServletResponse response)  {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving token");
            return;
        }
        Task task = taskDb.getTask(task_id);
        logger.info("Tasked retrieved successfully");
        new SuccessResponseHelper<Task>().sendSuccessResponseWithData(response, HttpServletResponse.SC_OK,"Job retrieved successfully", task);
    }

    public void updateTask(UUID id, HttpServletRequest request, HttpServletResponse response){
        Task task = jsonMapperUtil.getRequestBody(request, Task.class);
        boolean updateTask = taskDb.updateTask(id,task);
        if (!updateTask){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error updating task");
            return;
        }
        logger.info("Task updated successfully");
        new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response, HttpServletResponse.SC_OK,"Task updated successfully");
    }

    public void deleteTask(UUID id, HttpServletResponse response) {
    boolean taskDeleted = taskDb.deleteTask(id, response);
        if (!taskDeleted){
        new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Task id not found");
        return;
    }
        logger.info("Task deleted successfully");
        new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response, HttpServletResponse.SC_OK,"Task deleted successfully");
    }
}
