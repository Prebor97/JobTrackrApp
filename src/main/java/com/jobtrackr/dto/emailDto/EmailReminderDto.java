package com.jobtrackr.dto.emailDto;

import java.util.UUID;

public class EmailReminderDto {
    private String email;
    private String name;
    private UUID taskId;
    private String description;
    private String priority;
    private String jobTitle;
    private String company;

    public EmailReminderDto() {
    }

    public EmailReminderDto(String email, String name, UUID taskId, String description, String priority, String jobTitle, String company) {
        this.email = email;
        this.name = name;
        this.taskId = taskId;
        this.description = description;
        this.priority = priority;
        this.jobTitle = jobTitle;
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "EmailReminderDto{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", taskId=" + taskId +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}
