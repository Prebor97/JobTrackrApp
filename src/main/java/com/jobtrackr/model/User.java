package com.jobtrackr.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;
    private String email;
    private String firstname;
    private String lastname;
    private String job_title_target;
    private LocalDateTime created_at;
    private List<String> roletypes;
    private boolean isActivated;
    private String password;

    public User() {
    }

    public User(UUID id, String email, String firstname, String lastname, String job_title_target, LocalDateTime created_at, List<String> roletypes, boolean isActivated, String password) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.job_title_target = job_title_target;
        this.created_at = created_at;
        this.roletypes = roletypes;
        this.isActivated = isActivated;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public void setRoletype(List<String> roletypes) {
            this.roletypes = roletypes;
    }



    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public List<String> getRoleTypes(){
        return roletypes;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", job_title_target='" + job_title_target + '\'' +
                ", created_at=" + created_at +
                ", roletypes='" + roletypes+ '\'' +
                '}';
    }
}
