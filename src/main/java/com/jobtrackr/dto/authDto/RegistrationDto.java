package com.jobtrackr.dto.authDto;

public class RegistrationDto {
    private String email;
    private String firstname;
    private String lastname;
    private String job_title_target;
    private String password;
    private String confirmPassword;

    public RegistrationDto() {
    }

    public RegistrationDto(String email, String firstname, String lastname, String job_title_target, String password, String confirmPassword) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.job_title_target = job_title_target;
        this.password = password;
        this.confirmPassword = confirmPassword;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "RegistrationDto{" +
                "email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", job_title_target='" + job_title_target + '\'' +
                '}';
    }
}
