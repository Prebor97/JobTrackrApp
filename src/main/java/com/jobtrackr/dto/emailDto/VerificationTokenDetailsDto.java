package com.jobtrackr.dto.emailDto;

public class VerificationTokenDetailsDto {
    private String email;
    private String token;

    public VerificationTokenDetailsDto(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public VerificationTokenDetailsDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
