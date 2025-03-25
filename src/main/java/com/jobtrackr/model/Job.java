package com.jobtrackr.model;

import java.sql.Date;

public class Job {
    private String title;
    private String company;
    private String location;
    private String status;
    private Date appliedAt;
    private String jobUrl;
    private String notes;

    public Job() {
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public Date getAppliedAt() {
        return appliedAt;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public String getNotes() {
        return notes;
    }

}
