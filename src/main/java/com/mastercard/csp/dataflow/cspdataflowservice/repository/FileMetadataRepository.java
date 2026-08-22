package com.mastercard.csp.dataflow.cspdataflowservice.repository;

import com.mastercard.csp.dataflow.cspdataflowservice.entity.FileMetadata;
import com.mastercard.csp.dataflow.cspdataflowservice.entity.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update FileMetadata fileMetadata set fileMetadata.status = :status where fileMetadata.id = :id")
    int updateStatusById(@Param("id") UUID id, @Param("status") FileStatus status);
}
