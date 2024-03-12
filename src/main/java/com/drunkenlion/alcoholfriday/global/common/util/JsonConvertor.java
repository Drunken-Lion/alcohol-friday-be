package com.drunkenlion.alcoholfriday.global.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

public class JsonConvertor {
    public static <T> String build(T t) throws IllegalAccessException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(t);
    }

    public static <T> MockMultipartFile mockBuild(T t) throws JsonProcessingException, IllegalAccessException {
        String data = build(t);
        return new MockMultipartFile("request", "request", "application/json", data.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> MockMultipartFile mockBuild(T t, String requestParamName) throws JsonProcessingException, IllegalAccessException {
        String data = build(t);
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

    public static MockMultipartFile getMockImg(String requestParamName, String originalFileName, String content) {
        return new MockMultipartFile(requestParamName, originalFileName, "text/plain",
                content.getBytes(StandardCharsets.UTF_8));
    }
}
