package com.jobtrackr.errorHandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtrackr.dto.responseDto.ListSuccessDto;
import com.jobtrackr.dto.responseDto.SuccessResponseWithData;
import com.jobtrackr.dto.responseDto.SuccessResponseWithoutData;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class SuccessResponseHelper<T>{
    public void sendSuccessResponseWithData(HttpServletResponse response, int responseCode, String message, T data){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(responseCode);
        try {
            objectMapper.writeValue(response.getWriter(), new SuccessResponseWithData<T>(responseCode, message, data));
        }catch (IOException e){
            throw new RuntimeException("Failed to send success response: " + e.getMessage(), e);
        }
        }

    public void sendSuccessResponseWithListData(HttpServletResponse response, int responseCode, String message, List<T> data){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(responseCode);
        try {
            objectMapper.writeValue(response.getWriter(), new ListSuccessDto<>(responseCode, message, data));
        }catch (IOException e){
            throw new RuntimeException("Failed to send success response: " + e.getMessage(), e);
        }
        }

    public void sendSuccessResponseWithoutData(HttpServletResponse response, int responseCode, String message){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(responseCode);
        try {

            objectMapper.writeValue(response.getWriter(), new SuccessResponseWithoutData(responseCode, message));
        }catch (IOException e){
            throw new RuntimeException("Error sending success response: " + e.getMessage(), e);
        }
    }
}
