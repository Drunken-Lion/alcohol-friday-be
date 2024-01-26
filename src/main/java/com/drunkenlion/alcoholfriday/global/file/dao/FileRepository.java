package com.drunkenlion.alcoholfriday.global.file.dao;

import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<NcpFile, Long> {
	NcpFile findByEntityIdAndEntityType(Long id, String type);
}
