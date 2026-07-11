package com.mastercard.csp.dataflow.cspdataflowservice.ziparchiveservice;

import com.mastercard.csp.dataflow.cspdataflowservice.entity.FileMetadata;
import com.mastercard.csp.dataflow.cspdataflowservice.entity.FileStatus;
import com.mastercard.csp.dataflow.cspdataflowservice.model.FlowFileAttribute;
import com.mastercard.csp.dataflow.cspdataflowservice.repository.FileMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class ZipMetadata {

    private static final Logger LOG = LoggerFactory.getLogger(ZipMetadata.class);

    private ZipArchiveService next;
    private FileMetadataRepository fileMetadataRepository;

    public ZipMetadata(ZipArchiveService zipArchiveService,  FileMetadataRepository fileMetadataRepository) {
        this.next = zipArchiveService;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public boolean process(FlowFileAttribute flowFileAttribute) {

        String filename = flowFileAttribute.getAttributes().get("filename");
        LOG.info("ZipArchiveService : Processing file : {}" , filename);

        try {
            insertSubmitted(flowFileAttribute);
            next.process(flowFileAttribute);
        } catch (Exception ex) {
            LOG.error("ZipArchiveService : Error in inserting submitted file : {}", filename, ex);
        }


        return false;
    }

    private void insertSubmitted(FlowFileAttribute flowFileAttribute) {

        String fileName = flowFileAttribute.getAttributes().get("filename");
        UUID id = UUID.fromString(flowFileAttribute.getAttributes().get("file.id"));

        String parentIdStr = flowFileAttribute.getAttributes().get("parent.id");
        UUID parentId = Objects.isNull(parentIdStr) ||
                parentIdStr.isBlank() ? null :
                UUID.fromString(parentIdStr);

        UUID stakeholderId = flowFileAttribute.getAttributes().get("stakeholder.id") == null ||
                flowFileAttribute.getAttributes().get("stakeholder_id").isBlank() ?
                null :
                UUID.fromString(flowFileAttribute.getAttributes().get("stakeholder.id") );

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setName(fileName);
        fileMetadata.setId(id);
        fileMetadata.setParentId(parentId);
        fileMetadata.setStakeholderId(stakeholderId);
        fileMetadata.setType("ZIP");
        fileMetadata.setStatus(FileStatus.SUBMITTED);
        fileMetadata.setSize(flowFileAttribute.getSize());
        fileMetadata.setChecksum(flowFileAttribute.getAttributes().get("checksum"));
        fileMetadata.setSubmittedAt(LocalDateTime.now());

        fileMetadataRepository.save(fileMetadata);

    }
}
