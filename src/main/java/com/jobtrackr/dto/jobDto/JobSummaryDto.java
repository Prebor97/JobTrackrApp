package com.jobtrackr.dto.jobDto;

import java.util.UUID;

public class JobSummaryDto {
    private UUID id;
    private String company;
    private String title;

    public JobSummaryDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
