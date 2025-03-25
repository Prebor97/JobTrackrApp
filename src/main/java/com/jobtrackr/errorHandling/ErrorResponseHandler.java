package com.jobtrackr.errorHandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtrackr.dto.responseDto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class ErrorResponseHandler {
    public void sendErrorResponse(HttpServletResponse response, int responseCode, String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(responseCode);
        try {
            objectMapper.writeValue(response.getWriter(), new ErrorResponseDto(responseCode, message));
        }catch (IOException e){
            throw new RuntimeException("Error sending response message: " + e.getMessage(), e);
        }
    }
}