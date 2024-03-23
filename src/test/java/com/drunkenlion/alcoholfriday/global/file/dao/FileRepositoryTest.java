package com.drunkenlion.alcoholfriday.global.file.dao;

import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    // test를 위한 임의 변수
    private final String key_name = "test";
    private final String path = "src/test/java/com/drunkenlion/alcoholfriday/test.jpg";
    private final List<Long> entityIds = List.of(1L, 2L, 3L);
    private final String entityType = "item";

    @BeforeEach
    @Transactional
    void beforeEach() {
        for (Long id: entityIds) {
            List<Map<String, Object>> s3Files = new ArrayList<>();
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("key_name", key_name);
            fileInfo.put("path", path);
            fileInfo.put("seq", id);
            s3Files.add(fileInfo);

            NcpFile ncpFile = NcpFile.builder()
                    .entityId(id)
                    .entityType(entityType)
                    .s3Files(s3Files)
                    .build();

            this.fileRepository.save(ncpFile);
        }
    }

    @AfterEach
    @Transactional
    void afterEach() {
        this.fileRepository.deleteAll();
    }
}