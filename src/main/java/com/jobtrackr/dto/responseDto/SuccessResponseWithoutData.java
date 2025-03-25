package com.jobtrackr.dto.responseDto;

public class SuccessResponseWithoutData {
    public int status;
    public String message;

    public SuccessResponseWithoutData() {
    }

    public SuccessResponseWithoutData(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
