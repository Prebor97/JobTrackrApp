package com.jobtrackr.dto.responseDto;

public class ErrorResponseDto {
    public int status;
    public String message;

    public ErrorResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
