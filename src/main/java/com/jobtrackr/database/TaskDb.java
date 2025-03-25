package com.jobtrackr.database;

import com.jobtrackr.dto.emailDto.EmailReminderDto;
import com.jobtrackr.dto.taskDto.TaskSummaryDto;
import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.model.Task;
import com.jobtrackr.util.DatabaseUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskDb {
    private static final Logger logger = LoggerFactory.getLogger(TaskDb.class);
    private static final String SQLErrorMessage = "A SQL error has occurred ";

    public boolean createNewTask(UUID task_id, UUID job_id, UUID user_id, Task task){
        logger.info("Creating task..........................................");
        String sql = "INSERT INTO tasks (id, job_id, user_id, description, due_date, priority, completed, created_at) VALUES (?,?,?,?,?,?::task_priority,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setObject(1, task_id);
            stmt.setObject(2, job_id);
            stmt.setObject(3, user_id);
            stmt.setString(4, task.getDescription());
            stmt.setDate(5, task.getDue_date());
            stmt.setObject(6, task.getPriority());
            stmt.setBoolean(7,   task.isCompleted());
            stmt.setObject(8, LocalDateTime.now());
            stmt.executeUpdate();

            logger.info("Task successfully created {}", task);
            return true;
        }catch (SQLException e){
            logger.error(SQLErrorMessage+"creating job");
            throw new RuntimeException("Failed to create task: " + e.getMessage(), e);
        }
    }

    public List<TaskSummaryDto> getTasksWithMailReminders() throws IOException {
        logger.info("Returning tasks with email reminders.............");
        List<TaskSummaryDto> tasks = new ArrayList<>();
        String sql = "select * from tasks where set_reminder = true and completed = false";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaskSummaryDto task = new TaskSummaryDto();
                    task.setId((UUID) rs.getObject("id"));
                    task.setUser_id((UUID) rs.getObject("user_id"));
                    task.setDescription(rs.getString("description"));
                    task.setPriority(rs.getString("priority"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"returning tasks");
            throw new RuntimeException("Failed to retrieve task with reminder mail: " + e.getMessage(), e);
        }
        return tasks;
    }
    public List<TaskSummaryDto> getTasks(UUID job_id) {
        logger.info("Returning tasks.............");
        List<TaskSummaryDto> tasks = new ArrayList<>();
        String sql = "Select * from tasks where job_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, job_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaskSummaryDto task = new TaskSummaryDto();
                    task.setId((UUID) rs.getObject("id"));
                    task.setUser_id((UUID) rs.getObject("user_id"));
                    task.setDescription(rs.getString("description"));
                    task.setPriority(rs.getString("priority"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"returning tasks for mail reminders");
            throw new RuntimeException("Failed to fetch document: " + e.getMessage(), e);
        }
        return tasks;
    }

    public Task getTask(UUID id){
        logger.info("Returning task details....................................");
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task();
                    task.setDescription(rs.getString("description"));
                    task.setPriority(rs.getString("priority"));
                    task.setDue_date(rs.getDate("due_date"));
                    task.setCompleted(rs.getBoolean("completed"));
                    logger.info("Job detail returned {}", task);
                    return task;
                }
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"getting task detail with id {}",id);
            throw new RuntimeException("Failed to fetch task: " + e.getMessage(), e);
        }

        return null;
    }

    public boolean updateTask(UUID taskId, Task task) {
        logger.info("Updating tasks......................................");
        StringBuilder sql = new StringBuilder("UPDATE tasks SET ");
        boolean hasUpdates = false;

        if (task.getDescription()!=null) {
            sql.append("description = ?, ");
            hasUpdates = true;
        }
        if (task.getDue_date() != null) {
            sql.append("due_date = ?, ");
            hasUpdates = true;
        }
        if (task.getPriority() != null) {
            sql.append("priority = ?::task_priority, ");
            hasUpdates = true;
        }
        if (task.isCompleted()) {
            sql.append("completed = ?, ");
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

            if (task.getDescription()!=null) {
                stmt.setString(paramIndex++, task.getDescription());
            }
            if (task.getDue_date() != null) {
                stmt.setDate(paramIndex++, task.getDue_date());
            }
            if (task.getPriority() != null) {
                stmt.setString(paramIndex++,task.getPriority());
            }
            if (task.isCompleted()) {
                stmt.setBoolean(paramIndex++, task.isCompleted());
            }
            stmt.setObject(paramIndex, taskId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated>0) {
                logger.info("Task with id {} has been updated", taskId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating task with id {}",taskId);
            throw new RuntimeException("Failed to update task: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean deleteTask(UUID id, HttpServletResponse response) {
        logger.info("Deleting task........................................................");
        String sql = "Delete from tasks where id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Task with id {} has been deleted",id);
                return true;
            } else {
                logger.debug("Task id {} not found",id);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found");
            }
        } catch (IOException | SQLException e) {
            logger.error(SQLErrorMessage+"deleting task.....................................");
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        }
        return false;
    }

    public void setTaskCompletedTrue(UUID task_id, HttpServletResponse response) {
        logger.info("Setting task completed to true..................................");
        String sql = "UPDATE tasks SET completed = true WHERE id = ? ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, task_id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            logger.error(SQLErrorMessage+"setting task to completed to true");
            throw new RuntimeException("Set task completed to true: " + e.getMessage(), e);
        }
    }
    public EmailReminderDto getTaskDetails(UUID taskId) {
        String sql = """
                SELECT u.email, u.name, t.id AS task_id, t.description, t.priority,
                    j.title, j.company FROM tasks t JOIN users u ON t.user_id = u.id
                JOIN jobs j ON t.job_id = j.id WHERE t.id = ?""";

        EmailReminderDto taskDetails = null;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the task ID parameter
            stmt.setObject(1, taskId);

            // Execute the query
            ResultSet resultSet = stmt.executeQuery();

            // Map the result to the TaskDetails object
            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String name = resultSet.getString("name");
                UUID task_id = UUID.fromString(resultSet.getString("task_id"));
                String description = resultSet.getString("description");
                String priority = resultSet.getString("priority");
                String jobTitle = resultSet.getString("title");
                String company = resultSet.getString("company");

                taskDetails = new EmailReminderDto(email, name, task_id, description, priority, jobTitle, company);
                logger.info("User info : {}", taskDetails);
            }

        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"getting task details");
            throw new RuntimeException("Failed to get tasks details: " + e.getMessage(), e);
        }

        return taskDetails;
    }
}

