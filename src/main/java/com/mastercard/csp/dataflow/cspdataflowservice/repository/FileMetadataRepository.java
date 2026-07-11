package com.mastercard.csp.dataflow.cspdataflowservice.repository;

import com.mastercard.csp.dataflow.cspdataflowservice.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    
}
