package com.drunkenlion.alcoholfriday.global.file.dao;

import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<NcpFile, Long> {
    Optional<NcpFile> findByEntityIdAndEntityType(Long id, String type);

    List<NcpFile> findAllByEntityIdInAndEntityType(List<Long> entityIds, String entityType);
}
