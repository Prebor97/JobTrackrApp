package com.jobtrackr.dto.responseDto;

public class SuccessResponseWithData <T>{
    public int status;
    public String message;
    public T data;

    public SuccessResponseWithData() {
    }

    public SuccessResponseWithData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
