package com.jobtrackr.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Collectors;

public class JsonMapperUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonMapperUtil.class);
    public <T> T getRequestBody(HttpServletRequest request, Class<T> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            logger.error("Error retrieving request body..................................");
            throw new RuntimeException(e);
        }
        try {
            return objectMapper.readValue(requestBody, tClass);
        }catch (JsonProcessingException j){
            logger.error("Error mapping request to json");
            throw new RuntimeException(j);
        }
        }
}
