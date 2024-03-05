package com.drunkenlion.alcoholfriday.global.file.application;

import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.dao.FileRepository;
import com.drunkenlion.alcoholfriday.global.ncp.application.NcpS3ServiceImpl;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
class FileServiceImplTest {
    @InjectMocks
    private FileServiceImpl fileService;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private NcpS3ServiceImpl ncpS3Service;

    // test를 위한 임의 변수
    private final String keyName = "test";
    private final String path = "src/test/java/com/drunkenlion/alcoholfriday/test.jpg";
    private final List<Long> entityIds = List.of(1L, 2L, 3L);

    @Test
    @DisplayName("여러 EntityId를 가진 file이 있을 경우의 테스트")
    void listTest() {
        // given
        when(this.fileRepository.findAllByEntityIdInAndEntityType(anyList(), anyString())).thenReturn(
                this.getList(entityIds, EntityType.ITEM.getEntityName()));
        // when
        List<NcpFileResponse> files = this.fileService.findAllByEntityIds(entityIds, EntityType.ITEM.getEntityName());
        // then
        assertThat(files.isEmpty()).isFalse();
        for (NcpFileResponse fileResponse : files) {
            assertThat(fileResponse.getFile()).isNotNull();
            assertThat(fileResponse.getFile()).isInstanceOf(List.class);
            assertThat(fileResponse.getFile().get(0).getKeyName()).isEqualTo(keyName);
            assertThat(fileResponse.getFile().get(0).getPath()).isEqualTo(path);
            assertThat(fileResponse.getFile().get(0).getSeq()).isEqualTo(1L);
        }
    }

    // @Test
    @DisplayName("여러 EntityId를 가진 file이 없을 경우 테스트")
    void listEmptyTest() {
        // given
        when(this.fileRepository.findAllByEntityIdInAndEntityType(anyList(), anyString())).thenReturn(
                this.getEmptyList());
        // when
        List<NcpFileResponse> files = this.fileService.findAllByEntityIds(entityIds, EntityType.ITEM.getEntityName());
        // then
        assertThat(files.isEmpty()).isFalse();
        for (NcpFileResponse fileResponse : files) {
            assertThat(fileResponse.getFile()).isNull();
        }
    }

    // @Test
    @DisplayName("하나의 EntityId를 가진 file이 있을 경우의 테스트")
    void getTest() {
        // given
        when(this.fileRepository.findByEntityIdAndEntityType(anyLong(), anyString()))
                .thenReturn(Optional.ofNullable(this.getNcpFile(entityIds.get(0), EntityType.ITEM.getEntityName())));
        // when
        NcpFileResponse ncpFileResponse = this.fileService.findByEntityId(entityIds.get(0),
                EntityType.ITEM.getEntityName());
        // then
        assertThat(ncpFileResponse.getFile().get(0).getKeyName()).isEqualTo(keyName);
        assertThat(ncpFileResponse.getFile().get(0).getPath()).isEqualTo(path);
        assertThat(ncpFileResponse.getFile().get(0).getSeq()).isEqualTo(1L);
    }

    // @Test
    @DisplayName("하나의 EntityId를 가진 file이 없을 경우의 테스트")
    void getEmptyTest() {
        // given
        when(this.fileRepository.findByEntityIdAndEntityType(anyLong(), anyString()))
                .thenReturn(
                        Optional.ofNullable(this.getEmptyNcpFile(entityIds.get(0), EntityType.ITEM.getEntityName())));
        // when
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> this.fileService.findByEntityId(entityIds.get(0), EntityType.ITEM.getEntityName()));
        // then
        assertThat(businessException.getStatus()).isEqualTo(HttpResponse.Fail.NOT_FOUND_FILE.getStatus());
        assertThat(businessException.getMessage()).isEqualTo(HttpResponse.Fail.NOT_FOUND_FILE.getMessage());
    }

    private List<NcpFile> getEmptyList() {
        List<NcpFile> list = new ArrayList<>();
        list.add(null);

        return list;
    }

    private List<NcpFile> getList(List<Long> entityIds, String entityType) {
        List<NcpFile> list = new ArrayList<>();

        int count = 0;
        while (count < 3) {
            list.add(this.getNcpFile(entityIds.get(count), entityType));
            count++;
        }

        return list;
    }

    private NcpFile getEmptyNcpFile(Long entityId, String entityType) {
        return null;
    }

    private NcpFile getNcpFile(Long entityId, String entityType) {
        List<Map<String, Object>> s3Files = new ArrayList<>();

        for (int seq = 1; seq < 3; seq++) {
            Map<String, Object> map = new HashMap<>();
            map.put("keyName", keyName);
            map.put("path", path);
            map.put("seq", seq);
            s3Files.add(map);
        }

        return NcpFile.builder()
                .entityId(entityId)
                .entityType(entityType)
                .s3Files(s3Files)
                .build();
    }
}