package com.jobtrackr.dto.jobDto;

import com.jobtrackr.model.Task;

import java.util.UUID;

public class JobDetailsDto {
    private UUID id;
    private String title;
    private String company;
    private String location;
    private String status;
    private String appliedAt;
    private String jobUrl;
    private String notes;

    public JobDetailsDto() {
    }

    public JobDetailsDto(UUID id, String title, String company, String location, String status, String appliedAt, String jobUrl, String notes) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.status = status;
        this.appliedAt = appliedAt;
        this.jobUrl = jobUrl;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(String appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
