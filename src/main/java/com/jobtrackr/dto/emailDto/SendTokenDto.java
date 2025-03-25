package com.jobtrackr.dto.emailDto;

public class SendTokenDto {
    private String email;

    public SendTokenDto() {
    }

    public SendTokenDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
