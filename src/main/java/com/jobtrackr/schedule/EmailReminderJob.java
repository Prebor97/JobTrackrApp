package com.jobtrackr.schedule;

import com.jobtrackr.database.TaskDb;
import com.jobtrackr.dto.emailDto.EmailReminderDto;
import com.jobtrackr.dto.taskDto.TaskSummaryDto;
import com.jobtrackr.util.EmailUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class EmailReminderJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(EmailReminderJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Starting email reminder job...............................................");

        TaskDb taskDb = new TaskDb();
        EmailUtil emailUtil = new EmailUtil();

        List<TaskSummaryDto> tasks;
        try {
            tasks = taskDb.getTasksWithMailReminders();
            System.out.println("Fetched " + tasks.size() + " tasks with reminders.");
        } catch (IOException e) {
            logger.error("Failed to fetch tasks with reminders: {}", e.getMessage());
            throw new JobExecutionException("Failed to fetch tasks with reminders", e);
        }

        for (TaskSummaryDto task : tasks) {
            if (task == null) {
                logger.info("Skipping null task............................................");
                continue;
            }

            try {
                EmailReminderDto emailReminderDto = taskDb.getTaskDetails(task.getId());

                String email = emailReminderDto.getEmail();
                String [] name = emailReminderDto.getName().split(" ");
                String firstName = name[0];
                UUID task_id = task.getId();
                String description = emailReminderDto.getDescription();
                String priority = emailReminderDto.getPriority();
                String job_title = emailReminderDto.getJobTitle();
                String company = emailReminderDto.getCompany();

                logger.info("Preparing to send email to: {}", email);
                String mailTemplate = emailUtil.loadReminderMailTemplate(company,job_title,email,firstName, description, priority, task_id);

                emailUtil.sendEmail(email, "Reminder: Don't Forget Your Task!", mailTemplate);
                logger.info("Email sent successfully to: {}", email);
            } catch (Exception e) {
                logger.error("Failed to process task {}: {}", task.getId(), e.getMessage());
                throw new RuntimeException("Failed to process task: " + e.getMessage(), e);
            }
        }

        logger.info("Email reminder job completed................................................");
    }
}
