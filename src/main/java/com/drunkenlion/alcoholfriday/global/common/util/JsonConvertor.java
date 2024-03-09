package com.drunkenlion.alcoholfriday.global.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.mock.web.MockMultipartFile;

public class JsonConvertor {
    public static <T> String build(T t) throws IllegalAccessException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(t);
    }

    public static <T> MockMultipartFile mockBuild(T t) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(t);
        return new MockMultipartFile("request", "request", "application/json", data.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> MockMultipartFile mockBuild(T t, String requestParamName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(t);
        return new MockMultipartFile(requestParamName, "request", "application/json", data.getBytes(StandardCharsets.UTF_8));
    }

    public static MockMultipartFile getMockImg() {
        return new MockMultipartFile("files", "create-test1.txt", "text/plain",
                "create-test1 file".getBytes(StandardCharsets.UTF_8));
    }

    public static MockMultipartFile getMockImg(String requestParamName) {
        return new MockMultipartFile(requestParamName, "create-test1.txt", "text/plain",
                "create-test1 file".getBytes(StandardCharsets.UTF_8));
    }
}
