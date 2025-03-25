package com.jobtrackr.dto.userDto;

public class MyProfile {
    private String email;
    private String firstname;
    private String lastname;
    private String job_title_target;

    public MyProfile() {
    }

    public MyProfile(String email, String firstname, String lastname, String job_title_target) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.job_title_target = job_title_target;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getJob_title_target() {
        return job_title_target;
    }

    public void setJob_title_target(String job_title_target) {
        this.job_title_target = job_title_target;
    }
}
